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

		return true;
	}

	private void persistData(List<Instrument> instruments) {
		// TODO Auto-generated method stub
		InstrumentCRUD instCrud = new InstrumentCRUD();
		for (Instrument inst : instruments) {
			instCrud.modify(inst);
			System.out.println(inst.getName() + " - " + inst.getIsin());
		}
	}

	private List<String> getDataFromFile() {
		List<String> lines = new ArrayList<>();

		URL instrumentsUrl = Thread.currentThread().getContextClassLoader().getResource("resources/EQUITY_L.csv");
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

			Instrument instrument = new Instrument();
			instrument.setSymbol(details[0]);
			instrument.setName(details[1]);
			instrument.setSeries(details[2]);
			instrument.setListingDate(details[3]);
			instrument.setIsin(details[6]);

			instruments.add(instrument);
		}
		return instruments;
	}

	public static void main(String[] args) {
		new InstrumentInitializer().initialize();
	}
}
