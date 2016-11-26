package com.free.funds.portfolio.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.free.dao.funds.InstrumentCRUD;
import com.free.dao.funds.MutualFundPortfolioCRUD;
import com.free.interfaces.funds.portfolio.PortfolioInitializer;
import com.free.pojos.funds.Instrument;
import com.free.pojos.funds.InstrumentAllocation;
import com.free.pojos.funds.MutualFundPortfolio;

public class SBIMFPortfolioInitializer implements PortfolioInitializer {

	@Override
	public boolean initialize() {
		System.out.println("SBI MF portfolio Initialize called.");
		try {
			List<MutualFundPortfolio> folios = getDataFromFile();
			MutualFundPortfolioCRUD crud = new MutualFundPortfolioCRUD();
			for (MutualFundPortfolio fund : folios) {
				crud.modify(fund);
				// validatePortfolio
				validatePortfolio(fund);
			}
			System.out.println("All " + folios.size() + " SBI funds are initialized");
		} catch (IOException e) {
			System.out.println("Failed to initialize SBI MF portfilios");
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	private void validatePortfolio(MutualFundPortfolio fund) {
		InstrumentCRUD instCrud = new InstrumentCRUD();
		for (InstrumentAllocation inst : fund.getPortfolio()) {
			String isin = inst.getIsin();
			Instrument instrument = instCrud.get(isin);
			if (null == instrument) {
				System.out.println(isin + " not found in MF - " + fund.getName());
			}
		}
	}

	private List<MutualFundPortfolio> getDataFromFile() throws IOException, EncryptedDocumentException, InvalidFormatException {
		String folder = Thread.currentThread().getContextClassLoader().getResource("resources/SBI/").getFile();
		File[] files = new File(folder).listFiles();
		Workbook wb = null;

		List<MutualFundPortfolio> funds = new ArrayList<>();
		for (File file : files) {
			try {
				wb = WorkbookFactory.create(file);

				int count = wb.getNumberOfSheets();
				// igonre 1st workbook and read from second
				int index = 2;
				while(index < count) {
					Sheet sheet = wb.getSheetAt(index);

					// identify fund name
					String fundName = sheet.getRow(1).getCell(1).getStringCellValue();
					System.out.println(sheet.getSheetName() + " -> " + fundName);
					Date portfolioDate = sheet.getRow(2).getCell(1).getDateCellValue();

					MutualFundPortfolio fundPortfolio = new MutualFundPortfolio();
					fundPortfolio.setName(fundName);
					fundPortfolio.setDate(portfolioDate);
					List<InstrumentAllocation> instruments = new ArrayList<>();
					fundPortfolio.setPortfolio(instruments);

					//iterator of all the rows in current sheet
					Iterator<Row> rowIterator = sheet.iterator();
					while(rowIterator.hasNext()) {
						Row row = rowIterator.next();
						Cell nameCell = row.getCell(0);
						if (null == nameCell) {
							continue;
						}
						String name = nameCell.getStringCellValue();
						if (name.isEmpty() || name.toLowerCase().endsWith("total")) {
							continue;
						}

						//						System.out.println("name: " + name);
						Cell percentCell = row.getCell(7);
						if (null == percentCell || percentCell.getCellTypeEnum() == CellType.BLANK || percentCell.getCellTypeEnum() == CellType.STRING) {
							continue;
						}
						float percent = 0;
						if (null != percentCell) {
							percent = (float)percentCell.getNumericCellValue();
						}

						if (percent == 0.0) {
							continue;
						}

						Cell isinCell = row.getCell(1);
						String isin = null == isinCell ? "" : isinCell.getStringCellValue();
						if (isin.isEmpty()) {
							continue;
						}

						System.out.println("name: " + name + ", isin: " + isin + ", percent: " + percent);
						InstrumentAllocation alloc = new InstrumentAllocation();
						alloc.setIsin(isin);
						alloc.setName(name);
						alloc.setPercent(percent);

						instruments.add(alloc);
					}

					funds.add(fundPortfolio);

					index++;
				}
			} finally {
				if (null != wb) {
					wb.close();
				}
			}

		}

		return funds;
	}

	public static void main(String[] args) {
		new SBIMFPortfolioInitializer().initialize();
	}
}
