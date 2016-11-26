package com.free.funds.portfolio.impl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.free.dao.funds.MutualFundPortfolioCRUD;
import com.free.interfaces.funds.portfolio.PortfolioInitializer;
import com.free.pojos.funds.InstrumentAllocation;
import com.free.pojos.funds.MutualFundPortfolio;

public class FTMFPortfolioInitializer implements PortfolioInitializer {

	@Override
	public boolean initialize() {
		System.out.println("Franklin Templeton MF portfolio Initialize called.");
		try {
			List<MutualFundPortfolio> folios = getDataFromFile();
			MutualFundPortfolioCRUD crud = new MutualFundPortfolioCRUD();
			for (MutualFundPortfolio fund : folios) {
				crud.modify(fund);
			}
			System.out.println("All " + folios.size() +" Franklin Templeton funds are initialized");
		} catch (IOException e) {
			System.out.println("Failed to initialize Franklin Templeton MF portfilios");
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	private List<MutualFundPortfolio> getDataFromFile() throws IOException, InvalidFormatException {
		String folder = Thread.currentThread().getContextClassLoader().getResource("resources/FT/").getFile();
		File[] files = new File(folder).listFiles();
		Workbook wb = null;

		List<MutualFundPortfolio> funds = new ArrayList<>();
		for (File file : files) {
			try {
				wb = WorkbookFactory.create(file);

				int count = wb.getNumberOfSheets();
				// igonre 1st workbook and read from second
				int index = 0;
				while(index < count) {
					Sheet sheet = wb.getSheetAt(index);

					// identify fund name
					String fundName = sheet.getRow(0).getCell(1).getStringCellValue();
					if (fundName.isEmpty()) {
						fundName = sheet.getRow(0).getCell(0).getStringCellValue();
					}

					int dateStart = fundName.indexOf("As of -");
					int dateIndex = dateStart + 7;
					if (dateIndex < 7) {
						dateStart = fundName.indexOf("As of Date - ");
						dateIndex = dateStart + 13;
					}
					String dateStr = fundName.substring(dateIndex).trim();
					fundName = fundName.substring(0, dateStart).trim();
					System.out.println(sheet.getSheetName() + " -> " + fundName + ", date: " + dateStr);

					DateFormat formatter = new SimpleDateFormat("ddMMMyyy");
					Date portfolioDate = null;
					try {
						portfolioDate = (Date)formatter.parse(dateStr);
					} catch (ParseException e) {
						portfolioDate = new Date();
					}

					MutualFundPortfolio fundPortfolio = new MutualFundPortfolio();
					fundPortfolio.setName(fundName);
					fundPortfolio.setDate(portfolioDate);
					List<InstrumentAllocation> instruments = new ArrayList<>();
					fundPortfolio.setPortfolio(instruments);

					//iterator of all the rows in current sheet
					Iterator<Row> rowIterator = sheet.iterator();
					while(rowIterator.hasNext()) {
						Row row = rowIterator.next();
						Cell nameCell = row.getCell(1);
						if (null == nameCell || nameCell.getCellTypeEnum() != CellType.STRING) {
							continue;
						}
						String name = nameCell.getStringCellValue();
						if (name.isEmpty()) {
							continue;
						}

						//						System.out.println("name: " + name);
						Cell percentCell = row.getCell(5);
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

						Cell isinCell = row.getCell(0);
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
		new FTMFPortfolioInitializer().initialize();
	}
}
