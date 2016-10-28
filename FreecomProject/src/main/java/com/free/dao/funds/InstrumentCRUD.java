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
				+ " (name, symbol, series, sector, listingDate, isin) VALUES ("
				+ "$$" + object.getName() + "$$,"
				+ "$$" + object.getSymbol() + "$$,"
				+ "$$" + object.getSeries() + "$$,"
				+ "$$" + object.getSector() + "$$,"
				+ "$$" + object.getListingDate() + "$$,"
				+ "$$" + object.getIsin() + "$$"
				+ ")";

		CassandraWrapper.executeQuery(query);

		return object;
	}

	@Override
	public Instrument modify(Instrument object) {
		String query = "select name, symbol, series, sector, listingDate, isin from "
				+ DatabaseInitializer.INSTRUMENT_TABLE
				+ " where isin=?";
		ResultSet rs = CassandraWrapper.getResultSet(query, object.getIsin());
		List<Row> rows = rs.all();
		if (!rows.isEmpty()) {
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
		String query = "select name, symbol, series, sector, listingDate, isin from "
				+ DatabaseInitializer.INSTRUMENT_TABLE
				+ " where isin=?";
		ResultSet rs = CassandraWrapper.getResultSet(query, isin);
		List<Row> rows = rs.all();
		if (!rows.isEmpty()) {
			Row r = rows.get(0);
			Instrument inst = new Instrument();
			inst.setIsin(r.getString("isin"));
			inst.setName(r.getString("name"));
			inst.setSymbol(r.getString("symbol"));
			inst.setIsin(r.getString("isin"));
			inst.setSector(r.getString("sector"));
			inst.setListingDate(r.getString("listingDate"));
			return inst;
		}
		return null;
	}

}
