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

public class SundaramMFPortfolioInitializer implements PortfolioInitializer {

	private int fundNameRowNum = 1;
	private int fundNameCellNum = 0;
	private int percentCellNum = 6;
	private String datePrefix = "Monthly Portfolio Statement for the month ended";
	private String dateFormat = "dd MMM yyyy";
	private int dateCellNum = 0;
	private int instNameCellNum = 2;
	private int instIsinCellNum = 1;
	private int percentMul = 100;

	public String getMFName() {
		return "Sundaram";
	}

	public boolean initializeSheet(String sheetName, int index) {
		switch(sheetName) {
		case "GLOBAL":
			percentMul = 1;
			break;
		case "XDO_METADATA":
		case "Derivative Disclosure":
			return false;
		default:
			percentMul = 100;
			break;
		}

		return true;
	}

	public int getFundNameRowNumber() {
		return fundNameRowNum;
	}

	public int getFundNameCellNumber() {
		return fundNameCellNum;
	}

	public int getInstrumentNameCellNumber() {
		return instNameCellNum;
	}

	public int getInstrumentPercentCellNumber() {
		return percentCellNum;
	}

	public int getInstrumentIsinCellNumber() {
		return instIsinCellNum;
	}

	public int getInstrumentPercentMultiplier() {
		return percentMul;
	}

	public String getPortfolioDatePrefix() {
		return datePrefix;
	}

	public String getPortfolioDateFormat() {
		return dateFormat;
	}

	public int getPortfolioDateCellNumber() {
		return dateCellNum;
	}
	
	public int getSheetStartIndex() {
		return 0;
	}

	public String normalizeFundName(String name) {
		return name;
	}

	@Override
	public boolean initialize() {
		System.out.println(getMFName() + " MF portfolio Initialize called.");
		try {
			List<MutualFundPortfolio> folios = getDataFromFile();
			MutualFundPortfolioCRUD crud = new MutualFundPortfolioCRUD();
			int emptyInstCount = 0;
			for (MutualFundPortfolio fund : folios) {
				crud.modify(fund);
				if (fund.getPortfolio().isEmpty()) {
					emptyInstCount++;
				}
			}
			System.out.println("All " + folios.size() + " " + getMFName() +  " funds are initialized. Empty Funds: " + emptyInstCount);
		} catch (IOException e) {
			System.out.println("Failed to initialize " + getMFName() + " MF portfilios");
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
		String mfFolder = getMFName();
		String folder = Thread.currentThread().getContextClassLoader().getResource("resources/" + mfFolder + "/").getFile();
		File[] files = new File(folder).listFiles();
		Workbook wb = null;

		List<MutualFundPortfolio> funds = new ArrayList<>();
		for (File file : files) {
			fundNameRowNum = 1;
			fundNameCellNum = 0;
			percentCellNum = 6;
			datePrefix = "Monthly Portfolio Statement for the month ended";
			dateFormat = "dd MMM yyyy";
			dateCellNum = 0;
			instNameCellNum = 2;
			instIsinCellNum = 1;

			System.out.println("File name currently parsing - " + file.getName());
			try {
				wb = WorkbookFactory.create(file);

				int count = wb.getNumberOfSheets();
				int index = getSheetStartIndex();
				while(index < count) {
					Sheet sheet = wb.getSheetAt(index);
					String sheetName = sheet.getSheetName();
					boolean parseSheet = initializeSheet(sheetName.trim(), index);
					if (!parseSheet) {
						index++;
						continue;
					}

					if ("global".equals(sheetName)) {
						fundNameRowNum = 0;
						percentCellNum = 5;
						datePrefix = "Portfolio Statement for the month ended";
					}
					if ("WBF 1".equals(sheetName) || "WBF 2".equals(sheetName) || "WBF 3".equals(sheetName)) {
						fundNameRowNum = 0;
						fundNameCellNum = 1;
						datePrefix = "Monthly Portfolio Statement as on";
						dateFormat = "MMM dd, yyyy";
						dateCellNum = 1;
						percentCellNum = 6;
						instNameCellNum = 1;
						instIsinCellNum = 2;
					}

					// identify fund name
					Row fundNameRow = sheet.getRow(getFundNameRowNumber());
					if (null == fundNameRow || "NAV Details".equals(sheetName)) { // NAV Details added for Kotak
						index++;
						continue;
					}
					String fundName = fundNameRow.getCell(getFundNameCellNumber())
							.getStringCellValue();
					fundName = normalizeFundName(fundName);

					Date portfolioDate = getPortfolioDate(sheet);
					System.out.println(sheetName + " -> " + fundName + ", date: " + portfolioDate);

//					if (true) {
//						index ++;
//						continue;
//					}
					MutualFundPortfolio fundPortfolio = new MutualFundPortfolio();
					fundPortfolio.setName(fundName);
					fundPortfolio.setDate(portfolioDate);
					List<InstrumentAllocation> instruments = new ArrayList<>();
					fundPortfolio.setPortfolio(instruments);

					//iterator of all the rows in current sheet
					Iterator<Row> rowIterator = sheet.iterator();
					while(rowIterator.hasNext()) {
						Row row = rowIterator.next();
						Cell nameCell = row.getCell(getInstrumentNameCellNumber());
						if (null == nameCell || nameCell.getCellTypeEnum() != CellType.STRING) {
							continue;
						}
						String name = nameCell.getStringCellValue();
						if (name.isEmpty() || name.toLowerCase().endsWith("total")) {
							continue;
						}

						Cell percentCell = row.getCell(getInstrumentPercentCellNumber());
						if (null == percentCell || percentCell.getCellTypeEnum() == CellType.BLANK || percentCell.getCellTypeEnum() == CellType.STRING) {
							continue;
						}
						float percent = 0;
						if (null != percentCell) {
							percent = (float)percentCell.getNumericCellValue() * getInstrumentPercentMultiplier();
						}

						if (percent == 0.0) {
							continue;
						}

						Cell isinCell = row.getCell(getInstrumentIsinCellNumber());
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

	private Date getPortfolioDate(Sheet sheet) {
		Iterator<Row> rows = sheet.rowIterator();
		Date portfolioDate = null;
		String prefix = getPortfolioDatePrefix();
		String dateFormat = getPortfolioDateFormat();
		while(rows.hasNext()) {
			Row row = rows.next();
			Cell dateCell = row.getCell(getPortfolioDateCellNumber());
			if (null == dateCell || dateCell.getCellTypeEnum() != CellType.STRING) {
				continue;
			}
			String dateStr = dateCell.getStringCellValue();
			if (dateStr.startsWith(prefix)) {
				dateStr = dateStr.replace(prefix, "").trim();
				dateStr = dateStr.replace("th", "");
			}
			DateFormat formatter = new SimpleDateFormat(dateFormat);
			try {
				portfolioDate = (Date)formatter.parse(dateStr);
				break;
			} catch (ParseException e) {
				portfolioDate = new Date();
			}
		}
		return portfolioDate;
	}

	public static void main(String[] args) {
		new SundaramMFPortfolioInitializer().initialize();
	}
}
