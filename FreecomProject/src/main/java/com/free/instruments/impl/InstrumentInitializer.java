package com.free.instruments.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.free.dao.funds.InstrumentCRUD;
import com.free.interfaces.funds.portfolio.PortfolioInitializer;
import com.free.pojos.funds.Instrument;

public class InstrumentInitializer implements PortfolioInitializer {

	@Override
	public boolean initialize() {
		// TODO Auto-generated method stub
		// 1. create data provider
		// download from https://www.nseindia.com/content/equities/EQUITY_L.csv

		// 2. fetch data
		// now fetch from the local excel
		List<String> lines = getDataFromFile();

		// 3. parse data
		List<Instrument> instruments = parseData(lines);

		// 4. persist data
		persistData(instruments);

//		for (Instrument i : instruments) {
//			System.out.println(i);
//		}

		System.out.println(instruments.size() + " Instruments are initialized successfully.");
		return true;
	}

	private void persistData(List<Instrument> instruments) {
		InstrumentCRUD instCrud = new InstrumentCRUD();
		for (Instrument inst : instruments) {
			instCrud.modify(inst);
			System.out.println(inst.getName() + " - " + inst.getIsin());
		}
	}

	private List<String> getDataFromFile() {
		List<String> lines = new ArrayList<>();

//		URL instrumentsUrl = Thread.currentThread().getContextClassLoader().getResource("resources/EQUITY_L.csv");
		URL instrumentsUrl = Thread.currentThread().getContextClassLoader().getResource("resources/ListOfScrips.csv");
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
					|| details.length < 7 
					|| details[6].isEmpty()
					|| "NA".equals(details[6])) {
				continue;
			}

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
			instrument.setSegment(Instrument.Segment.findByName(details[8]));

			instruments.add(instrument);
		}
		return instruments;
	}

	public static void main(String[] args) {
		new InstrumentInitializer().initialize();
	}
}
