package com.free.dao.funds;

import java.util.List;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.free.dao.CassandraWrapper;
import com.free.dao.DatabaseInitializer;
import com.free.interfaces.dao.CRUD;
import com.free.pojos.funds.Instrument;

public class InstrumentCRUD implements CRUD<Instrument> {

	@Override
	public Instrument create(Instrument object) {
		String query = "INSERT INTO "
				+ DatabaseInitializer.INSTRUMENT_TABLE
				+ " (name, symbol, sector, segment, isin, securitycode, listingdate, marketcap, mcaptype) VALUES ("
				+ "$$" + object.getName() + "$$,"
				+ "$$" + object.getSymbol() + "$$,"
				+ "$$" + object.getSector() + "$$,"
			    + "$$" + object.getSegment() + "$$,"
				+ "$$" + object.getIsin() + "$$,"
				+ object.getSecurityCode() + ","
				+ "$$" + object.getListingDate() + "$$,"
				+ object.getMarketValue() + ","
				+ "$$" + object.getMarketCap() + "$$"
				+ ")";

		CassandraWrapper.executeQuery(query);

		return object;
	}

	@Override
	public Instrument modify(Instrument object) {
		String query = "select name, symbol, sector, segment, isin, securitycode, listingdate, marketcap, mcaptype from "
				+ DatabaseInitializer.INSTRUMENT_TABLE
				+ " where isin=?";
		ResultSet rs = CassandraWrapper.getResultSet(query, object.getIsin());
		List<Row> rows = rs.all();
		if (!rows.isEmpty()) {
			System.out.println("NEW:" + object);
			System.out.println("OLD:" + getInstrument(rows.get(0)));
			delete(object.getIsin());
		}

		return create(object);
	}

	@Override
	public Instrument delete(String isin) {
		String query = "delete from " + DatabaseInitializer.INSTRUMENT_TABLE + " where isin=$$" + isin + "$$";
		CassandraWrapper.executeQuery(query);
		return null;
	}

	@Override
	public Instrument get(String isin) {
		String query = "select name, symbol, sector, segment, isin, securitycode, listingdate, marketcap from "
				+ DatabaseInitializer.INSTRUMENT_TABLE
				+ " where isin=?";
		ResultSet rs = CassandraWrapper.getResultSet(query, isin);
		List<Row> rows = rs.all();
		if (!rows.isEmpty()) {
			return getInstrument (rows.get(0));
		}
		return null;
	}

	private Instrument getInstrument(Row r) {
		Instrument inst = new Instrument();
		inst.setName(r.getString("name"));
		inst.setSymbol(r.getString("symbol"));
		inst.setSector(r.getString("sector"));
		inst.setSegment(Instrument.Segment.valueOf(r.getString("segment")));
		inst.setIsin(r.getString("isin"));
		inst.setSecurityCode(r.getInt("securitycode"));
		inst.setListingDate(r.getString("listingdate"));
		inst.setMarketValue(r.getFloat("marketcap"));
		return inst;
	}

}
