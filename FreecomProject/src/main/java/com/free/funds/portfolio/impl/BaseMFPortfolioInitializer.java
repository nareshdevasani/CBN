package com.free.funds.portfolio.impl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.free.dao.funds.InstrumentCRUD;
import com.free.dao.funds.MutualFundPortfolioCRUD;
import com.free.interfaces.funds.portfolio.PortfolioInitializer;
import com.free.pojos.funds.Instrument;
import com.free.pojos.funds.InstrumentAllocation;
import com.free.pojos.funds.MutualFundPortfolio;

public abstract class BaseMFPortfolioInitializer implements PortfolioInitializer {

	public abstract String getMFName();
	// true to continue; false to continue to next sheet
	public boolean initializeSheet(String sheetName, int index) {
		return true;
	}
	public abstract int getFundNameRowNumber();
	public abstract int getFundNameCellNumber();

	// instrument details extraction
	public abstract int getInstrumentNameCellNumber();
	public abstract int getInstrumentPercentCellNumber();
	public abstract int getInstrumentIsinCellNumber();
	public abstract int getInstrumentPercentMultiplier();

	// date extraction
	public abstract String getPortfolioDatePrefix();
	public abstract String getPortfolioDateFormat();

	public int getPortfolioDateCellNumber() {
		return 1;
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

				// validatePortfolio
				validatePortfolio(fund);
				if (fund.getPortfolio().isEmpty()) {
					emptyInstCount++;
					System.out.println("Empty: " + fund.getName());
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
		String mfFolder = getMFName();
		String folder = Thread.currentThread().getContextClassLoader().getResource("resources/" + mfFolder + "/").getFile();
		File[] files = new File(folder).listFiles();
		Workbook wb = null;

		List<MutualFundPortfolio> funds = new ArrayList<>();
		for (File file : files) {
			try {
				wb = WorkbookFactory.create(file);

				int count = wb.getNumberOfSheets();
				int index = getSheetStartIndex();
				while(index < count) {
					Sheet sheet = wb.getSheetAt(index);
					String sheetName = sheet.getSheetName();
					boolean parseSheet = initializeSheet(sheetName, index);
					if (!parseSheet) {
						index++;
						continue;
					}

					// identify fund name
					Row fundNameRow = sheet.getRow(getFundNameRowNumber());
					if (null == fundNameRow) {
						index++;
						continue;
					}
					String fundName = fundNameRow.getCell(getFundNameCellNumber())
							.getStringCellValue();
					fundName = normalizeFundName(fundName).trim();

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
						String isin = null == isinCell ? "" : (isinCell.getCellTypeEnum() == CellType.STRING) ? isinCell.getStringCellValue() : "";
						isin = isin.trim();
						if (isin.isEmpty()) {
							continue;
						}

						System.out.println("name: " + name + ", isin: " + isin + ", percent: " + percent);
						InstrumentAllocation alloc = new InstrumentAllocation();
						alloc.setIsin(isin);
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
		int dateCellNum = getPortfolioDateCellNumber();
		if (-1 == dateCellNum) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			return calendar.getTime();
		}
		while(rows.hasNext()) {
			Row row = rows.next();
			Cell dateCell = row.getCell(dateCellNum);
			if (null == dateCell || dateCell.getCellTypeEnum() != CellType.STRING) {
				continue;
			}
			String dateStr = dateCell.getStringCellValue();
			if (dateStr.startsWith(prefix)) {
				dateStr = dateStr.replace(prefix, "").trim();
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
}
