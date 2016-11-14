package com.free.instruments.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.free.dao.funds.InstrumentCRUD;
import com.free.interfaces.funds.portfolio.PortfolioInitializer;
import com.free.pojos.funds.Instrument;
import com.free.pojos.funds.Instrument.Segment;

public class InstrumentInitializer implements PortfolioInitializer {

	@Override
	public boolean initialize() {
		// TODO Auto-generated method stub
		// 1. create data provider
		// download from (old url https://www.nseindia.com/content/equities/EQUITY_L.csv)
		// http://www.bseindia.com/corporates/List_Scrips.aspx?expandable=1%3fexpandable%3d1

		// 2. fetch data
		// now fetch from the local excel
		List<String> lines = getDataFromFile("resources/ISIN/ListOfScrips.csv");
		// 3. parse data
		Collection<Instrument> instruments = parseData(lines);
		// 4. persist data
		persistData(instruments);

//		for (Instrument i : instruments) {
//			System.out.println(i);
//		}

		System.out.println(instruments.size() + " Instruments are initialized successfully.");

		// initialize commercial papers
		instruments = getMoneyMarketData("CP", "Commercial Paper");
		// 4. persist data
		persistData(instruments);
		System.out.println(instruments.size() + " Commercial Papers are initialized successfully.");

		// initialize Certificate of Deposits
		instruments = getMoneyMarketData("CD", "Certificate of Deposits");
		// 4. persist data
		persistData(instruments);
		System.out.println(instruments.size() + " Certificate of Deposits are initialized successfully.");

		instruments = getMoneyMarketData("debt", "Bonds or Debentures", Segment.OTHER_DEBT);
		// 4. persist data
		persistData(instruments);
		System.out.println(instruments.size() + " Bonds or Debentures are initialized successfully.");

		instruments = getGSecData();
		// 4. persist data
		persistData(instruments);
		System.out.println(instruments.size() + " Government Securities are initialized successfully.");

		instruments = getTreasuryData();
		// 4. persist data
		persistData(instruments);
		System.out.println(instruments.size() + " Treasury Bills are initialized successfully.");

		instruments = getMutualFundData("MF", 0, 2, 4);
		// 4. persist data
		persistData(instruments);
		System.out.println(instruments.size() + " Mutual Funds are initialized successfully.");

		instruments = getMutualFundData("MF-ETF", 0, 2, 5);
		// 4. persist data
		persistData(instruments);
		System.out.println(instruments.size() + " Mutual Fund ETFs are initialized successfully.");

		return true;
	}

	private void persistData(Collection<Instrument> instruments) {
		InstrumentCRUD instCrud = new InstrumentCRUD();
		for (Instrument inst : instruments) {
			instCrud.modify(inst);
			//System.out.println(inst.getName() + " - " + inst.getIsin());
		}
	}

	private List<Instrument> getMoneyMarketData(String folder, String sector) {
		return getMoneyMarketData(folder, sector, Instrument.Segment.MONEY_MARKET);
	}

	private List<Instrument> getMoneyMarketData(String folder, String sector, Segment segment) {
		List<Instrument> instruments = new ArrayList<>();

		Workbook wb = null;
		try {
			URI instrumentsUri = Thread.currentThread().getContextClassLoader().getResource("resources/ISIN/" + folder).toURI();
			File[] files = Paths.get(instrumentsUri).toFile().listFiles();

			for (File file : files) {
				wb = WorkbookFactory.create(file);
				Sheet sheet = wb.getSheetAt(0);
	
				Iterator<Row> rowIterator = sheet.rowIterator();
				rowIterator.next(); // ignore first row
				Instrument inst = null;
				while(rowIterator.hasNext()) {
					Row row = rowIterator.next();
					inst = new Instrument();
					Cell nameCell = row.getCell(0);
					if (null == nameCell || nameCell.getCellTypeEnum() != CellType.STRING) {
						continue;
					}
					inst.setName(nameCell.getStringCellValue());
	
					Cell isinCell = row.getCell(1);
					if (null == isinCell || isinCell.getCellTypeEnum() != CellType.STRING) {
						continue;
					}

					inst.setIsin(isinCell.getStringCellValue());

					if ("debt".equals(folder)) {
						Cell couponCell = row.getCell(8);
						if (null != couponCell) {
							if (couponCell.getCellTypeEnum() == CellType.NUMERIC) {
								inst.setSymbol(couponCell.getNumericCellValue() + "");
							} else {
								inst.setSymbol(couponCell.getStringCellValue());
							}
						}
					}
					inst.setSector(sector);
					inst.setSegment(segment);
	
					instruments.add(inst);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	    return instruments;
	}

	private List<Instrument> getGSecData() {
		List<Instrument> instruments = new ArrayList<>();

		Workbook wb = null;
		try {
			URI instrumentsUri = Thread.currentThread().getContextClassLoader().getResource("resources/ISIN/" + "GS").toURI();
			File[] files = Paths.get(instrumentsUri).toFile().listFiles();

			for (File file : files) {
				wb = WorkbookFactory.create(file);
				Sheet sheet = wb.getSheetAt(0);
	
				Iterator<Row> rowIterator = sheet.rowIterator();
				Instrument inst = null;
				while(rowIterator.hasNext()) {
					Row row = rowIterator.next();
					Cell isinCell = row.getCell(4);
					if (null == isinCell || isinCell.getCellTypeEnum() != CellType.STRING) {
						continue;
					}

					String isin = isinCell.getStringCellValue().trim();
					if ("ISIN".equalsIgnoreCase(isin)) {
						continue;
					}
					inst = new Instrument();
					inst.setIsin(isin);
					Cell nameCell = row.getCell(5);
					if (null == nameCell || nameCell.getCellTypeEnum() != CellType.STRING) {
						continue;
					}
					inst.setName(nameCell.getStringCellValue() + " - Government of India");

					inst.setSector("Government Securities");
					inst.setSegment(Instrument.Segment.OTHER_DEBT);
	
					instruments.add(inst);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	    return instruments;
	}

	private Collection<Instrument> getMutualFundData(String folder, int symbolIndex, int nameIndex, int isinIndex) {
		Map<String, Instrument> instruments = new HashMap<>();

		try {
			URI instrumentsUri = Thread.currentThread().getContextClassLoader().getResource("resources/ISIN/" + folder).toURI();
			File[] files = Paths.get(instrumentsUri).toFile().listFiles();

			for (File file : files) {
				List<String> lines = getDataFromFile("resources/ISIN/" + folder + "/"+ file.getName());

				Instrument inst = null;
				for(int i = 1; i < lines.size(); i++) {
					String[] cells = lines.get(i).split(",");
					String isin = cells[isinIndex];
					if (null == isin || isin.isEmpty()) {
						continue;
					}

					inst = new Instrument();
					inst.setIsin(isin);
					String name = cells[nameIndex];
					if (null == name || name.trim().isEmpty()) {
						continue;
					}
					inst.setName(name.trim());
					inst.setSymbol(cells[symbolIndex]);

					inst.setSector("Mutual Funds");
					inst.setSegment(Instrument.Segment.MF);
	
					instruments.put(isin, inst);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	    return instruments.values();
	}

	private List<Instrument> getTreasuryData() {
		List<Instrument> instruments = new ArrayList<>();

		try {
			URI instrumentsUri = Thread.currentThread().getContextClassLoader().getResource("resources/ISIN/TBills").toURI();
			File[] files = Paths.get(instrumentsUri).toFile().listFiles();

			List<String> lines = getDataFromFile("resources/ISIN/TBills/" + files[0].getName());
			Instrument inst = null;
			for (String line : lines) {
					String[] cells = line.split(",");
					if (cells.length < 17) {
						continue;
					}
					String secType = cells[2];
					String sector = "";
					switch(secType) {
					case "TB":
						sector = "Treasury Bills";
						break;
					case "SG":
						sector = "State Government Securities";
						break;
					case "GF":
						sector = "Government Floating Loans";
						break;
					case "GS":
					case "GI":
						sector = "Government Securities";
						break;
					case "GZ":
						sector = "Government Coupons";
						break;
					}

					if (sector.isEmpty()) {
						continue;
					}

					String isin = cells[16];
					inst = new Instrument();
					inst.setIsin(isin);
					inst.setName(cells[3]);
					inst.setSector(sector);
					inst.setSegment(Instrument.Segment.OTHER_DEBT);
	
					instruments.add(inst);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	    return instruments;
	}

	private List<String> getDataFromFile(String fileName) {
		List<String> lines = new ArrayList<>();

//		URL instrumentsUrl = Thread.currentThread().getContextClassLoader().getResource("resources/EQUITY_L.csv");
		URL instrumentsUrl = Thread.currentThread().getContextClassLoader().getResource(fileName);
		BufferedReader reader = null;
	    try {
			reader = new BufferedReader(new FileReader(instrumentsUrl.getFile()));
			String line;
			while((line = reader.readLine()) != null) {
				//System.out.println(line);
				lines.add(line);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (null != reader) {
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	    return lines;
	}

	private List<Instrument> parseData(List<String> lines) {
		List<Instrument> instruments = new ArrayList<>();
		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			String[] details = line.split(",");

			// only add active instruments
			if (!"Active".equalsIgnoreCase(details[3]) 
					|| details.length < 9 
					|| details[6].isEmpty()
					|| "NA".equals(details[6].trim())) {
				continue;
			}

			Segment segment = Instrument.Segment.findByName(details[8]);

			Instrument instrument = new Instrument();
			try {
				instrument.setSecurityCode(Integer.parseInt(details[0]));
			} catch (NumberFormatException nfe) {
				System.out.println("Security code cannot be converted into integer.");
			}

			instrument.setSymbol(details[1]);

			String name = details[2];
			name = name.replace("&amp;", "&").replace("-$", "");
			instrument.setName(name);
			instrument.setIsin(details[6]);
			String sector = details[7];
			sector = sector.replace("&amp;", "&");
			instrument.setSector(sector);
			instrument.setSegment(segment);

			instruments.add(instrument);
		}
		return instruments;
	}

	public static void main(String[] args) {
		new InstrumentInitializer().initialize();
	}
}
