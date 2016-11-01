package com.free.funds.portfolio.impl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.free.dao.funds.MutualFundPortfolioCRUD;
import com.free.interfaces.funds.portfolio.PortfolioInitializer;
import com.free.pojos.funds.InstrumentAllocation;
import com.free.pojos.funds.MutualFundPortfolio;

public class DSPBRMFPortfolioInitializer implements PortfolioInitializer {

	@Override
	public boolean initialize() {
		System.out.println("DSP-BR MF portfolio Initialize called.");
		try {
			List<MutualFundPortfolio> folios = getDataFromFile();
			MutualFundPortfolioCRUD crud = new MutualFundPortfolioCRUD();
			for (MutualFundPortfolio fund : folios) {
				crud.modify(fund);
			}
			System.out.println("All " + folios.size() + " DSP-BR funds are initialized");
		} catch (IOException e) {
			System.out.println("Failed to initialize DSP-BR MF portfilios");
		}

		return false;
	}

	private List<MutualFundPortfolio> getDataFromFile() throws IOException {
		String folder = Thread.currentThread().getContextClassLoader().getResource("resources/DSPBR/").getFile();
		File[] files = new File(folder).listFiles();
		NPOIFSFileSystem fs = null;
		HSSFWorkbook wb = null;

		List<MutualFundPortfolio> funds = new ArrayList<>();
		for (File file : files) {
			try {
				fs = new NPOIFSFileSystem(file);
				wb = new HSSFWorkbook(fs.getRoot(), true);

				int count = wb.getNumberOfSheets();
				// igonre 1st workbook and read from second
				int index = 0;
				while(index < count) {
					Sheet sheet = wb.getSheetAt(index);
					String sheetName = sheet.getSheetName();

					// identify fund name
					String fundName = sheet.getRow(0).getCell(1).getStringCellValue();

					Date portfolioDate = getPortfolioDate(sheet);
					System.out.println(sheetName + " -> " + fundName + ", date: " + portfolioDate);

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
						if (name.isEmpty() || name.toLowerCase().endsWith("total")) {
							continue;
						}

						Cell percentCell = row.getCell(6);
						if (null == percentCell || percentCell.getCellTypeEnum() == CellType.BLANK || percentCell.getCellTypeEnum() == CellType.STRING) {
							continue;
						}
						float percent = 0;
						if (null != percentCell) {
							percent = (float)percentCell.getNumericCellValue() * 100;
						}

						if (percent == 0.0) {
							continue;
						}

						Cell isinCell = row.getCell(2);
						String isin = null == isinCell ? "" : isinCell.getStringCellValue();
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
				if (null != fs) {
					fs.close();
				}
				if (null != wb) {
					wb.close();
				}
			}

		}

		return funds;
	}

	private Map<String, String> getSchemeDetails(HSSFWorkbook wb) {
		Map<String, String> result = new HashMap<>();
		Sheet sheet = wb.getSheetAt(0);
		Iterator<Row> rows = sheet.rowIterator();
		while(rows.hasNext()) {
			Row row = rows.next();
			Cell cell = row.getCell(0);
			if (null == cell || cell.getCellTypeEnum() != CellType.STRING) {
				continue;
			}
			String indexName = cell.getStringCellValue();
			if ("Index".equalsIgnoreCase(indexName)) {
				continue;
			}
			String schemeName = row.getCell(1).getStringCellValue();

			//System.out.println("Index: " + indexName + ", Scheme Name: " + schemeName);
			result.put(indexName, schemeName);
		}
		return result;
	}

	private Date getPortfolioDate(Sheet sheet) {
		Iterator<Row> rows = sheet.rowIterator();
		Date portfolioDate = null;
		while(rows.hasNext()) {
			Row row = rows.next();
			Cell dateCell = row.getCell(1);
			if (null == dateCell || dateCell.getCellTypeEnum() != CellType.STRING) {
				continue;
			}
			String dateStr = dateCell.getStringCellValue();
			if (dateStr.startsWith("Portfolio as on")) {
				dateStr = dateStr.replace("Portfolio as on", "").trim();
			}
			DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
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
		new DSPBRMFPortfolioInitializer().initialize();
	}
}
