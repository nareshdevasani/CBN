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

import org.apache.poi.EncryptedDocumentException;
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

public class UTIMFPortfolioInitializer implements PortfolioInitializer {

	@Override
	public boolean initialize() {
		System.out.println("UTI MF portfolio Initialize called.");
		try {
			List<MutualFundPortfolio> folios = getDataFromFile();
			MutualFundPortfolioCRUD crud = new MutualFundPortfolioCRUD();
			for (MutualFundPortfolio fund : folios) {
				crud.modify(fund);
			}
			System.out.println("All " + folios.size() + " UTI funds are initialized");
		} catch (IOException e) {
			System.out.println("Failed to initialize UTI MF portfilios");
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	private List<MutualFundPortfolio> getDataFromFile() throws IOException, EncryptedDocumentException, InvalidFormatException {
		String folder = Thread.currentThread().getContextClassLoader().getResource("resources/UTI/").getFile();
		File[] files = new File(folder).listFiles();
		Workbook wb = null;

		List<MutualFundPortfolio> funds = new ArrayList<>();
		for (File file : files) {
			try {
				wb = WorkbookFactory.create(file);

				Sheet sheet = wb.getSheetAt(0);

				Date portfolioDate = getPortfolioDate(sheet);

				// iterator of all the rows in current sheet
				Iterator<Row> rowIterator = sheet.iterator();
				String fundName = null;
				MutualFundPortfolio fundPortfolio = null;
				List<InstrumentAllocation> instruments = null;
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					Cell cell = row.getCell(0);
					if (null == cell || cell.getCellTypeEnum() != CellType.STRING) {
						continue;
					}

					String cellValue = cell.getStringCellValue();
					// identify fund name
					if (cellValue.startsWith("SCHEME:")) {
						fundName = cellValue.replace("SCHEME:", "").trim();
						// new fund started
						if (fundPortfolio != null) {
							funds.add(fundPortfolio);
						}
						fundPortfolio = new MutualFundPortfolio();
						fundPortfolio.setDate(portfolioDate);
						instruments = new ArrayList<>();
						fundPortfolio.setPortfolio(instruments);
						fundPortfolio.setName(fundName);
						System.out.println("Fund Name: " + fundName + ", date: " + portfolioDate);
						continue;
					}

					Cell nameCell = row.getCell(0);
					if (null == nameCell || nameCell.getCellTypeEnum() != CellType.STRING) {
						continue;
					}
					String name = nameCell.getStringCellValue();
					if (name.isEmpty() || name.toLowerCase().endsWith("total:")) {
						continue;
					}

					Cell percentCell = row.getCell(4);
					if (null == percentCell || percentCell.getCellTypeEnum() == CellType.BLANK
							|| percentCell.getCellTypeEnum() == CellType.STRING) {
						continue;
					}

					float percent = (float) percentCell.getNumericCellValue();

					if (percent == 0.0) {
						continue;
					}

					Cell isinCell = row.getCell(7);
					String isin = null == isinCell ? "" : isinCell.getStringCellValue();
					if (isin.isEmpty()) {
						continue;
					}

					System.out.println("name: " + name + ", isin: " + isin + ", percent: " + percent);
					InstrumentAllocation alloc = new InstrumentAllocation();
					alloc.setIsin(isin);
					alloc.setPercent(percent);

					if (null != instruments) {
						instruments.add(alloc);
					}
				}
			} finally {
				if (null != wb) {
					wb.close();
				}
			}

		}

		return funds;
	}

	private Date getPortfolioDate(Sheet sheet) {
		Iterator<Row> rows = sheet.rowIterator();
		Date portfolioDate = null;
		while (rows.hasNext()) {
			Row row = rows.next();
			Cell dateCell = row.getCell(0);
			if (null == dateCell || dateCell.getCellTypeEnum() != CellType.STRING) {
				continue;
			}
			String dateStr = dateCell.getStringCellValue();
			if (dateStr.startsWith("PROVISIONAL AND UNAUDITED PORTFOLIO DISCLOSURE AS OF")) {
				dateStr = dateStr.replace("PROVISIONAL AND UNAUDITED PORTFOLIO DISCLOSURE AS OF", "")
						.replace(" (Market value in Lacs)", "").trim();
			}
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			try {
				portfolioDate = (Date) formatter.parse(dateStr);
				break;
			} catch (ParseException e) {
				portfolioDate = new Date();
			}
		}
		return portfolioDate;
	}

	public static void main(String[] args) {
		new UTIMFPortfolioInitializer().initialize();
	}
}
