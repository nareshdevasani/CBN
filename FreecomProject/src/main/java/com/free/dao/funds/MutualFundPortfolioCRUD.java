package com.free.dao.funds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.free.dao.CassandraWrapper;
import com.free.dao.DatabaseInitializer;
import com.free.interfaces.dao.CRUD;
import com.free.pojos.funds.InstrumentAllocation;
import com.free.pojos.funds.MutualFundPortfolio;

public class MutualFundPortfolioCRUD implements CRUD<MutualFundPortfolio> {

	ObjectMapper mapper = new ObjectMapper();

	@Override
	public MutualFundPortfolio create(MutualFundPortfolio object) {
		String instruments = "";
		try {
			instruments = mapper.writeValueAsString(object.getPortfolio());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String query = "INSERT INTO "
				+ DatabaseInitializer.MF_PORTFOLIO_TABLE
				+ " (name, lname, portfolioDate, instruments) VALUES ("
				+ "$$" + object.getName() + "$$,"
				+ "$$" + object.getName().toLowerCase() + "$$,"
				+ "$$" + object.getDate().getTime() + "$$,"
				+ "$$" + instruments + "$$"
				+ ")";

		CassandraWrapper.executeQuery(query);

		return object;
	}

	@Override
	public MutualFundPortfolio modify(MutualFundPortfolio object) {
		String query = "select name, portfolioDate, instruments from "
				+ DatabaseInitializer.MF_PORTFOLIO_TABLE
				+ " where name=?";
		ResultSet rs = CassandraWrapper.executeQueryWithParams(query, object.getName());
		List<Row> rows = rs.all();
		if (!rows.isEmpty()) {
//			System.out.println("NEW:" + object);
//			System.out.println("OLD:" + getMutualFundPortfolio(rows.get(0)));
			delete(object.getName());
		}

		return create(object);
	}

//	public MutualFundPortfolio updateSchemeCode(String schemeCode, String name) {
//		String query = "update " + DatabaseInitializer.MF_PORTFOLIO_TABLE
//				+ " set schemecode='" + schemeCode + "' where name=?";
//		CassandraWrapper.executeQueryWithParams(query, name);
//
//		return getBySchemeCode(schemeCode);
//	}

	public MutualFundPortfolio getBySchemeCode(String schemeCode) {
		String query = "select name, lname, portfolioDate, instruments from "
				+ DatabaseInitializer.MF_PORTFOLIO_TABLE
				+ " where schemecode=? allow filtering";
		ResultSet rs = CassandraWrapper.executeQueryWithParams(query, schemeCode);
		List<Row> rows = rs.all();
		if (rows.isEmpty()) {
			return null;
		}

		Row r = rows.get(0);
		return getMutualFundPortfolio(r);
	}

	@Override
	public MutualFundPortfolio delete(String name) {
		String query = "delete from " + DatabaseInitializer.MF_PORTFOLIO_TABLE + " where name=$$" + name + "$$";
		CassandraWrapper.executeQuery(query);
		return null;
	}

	@Override
	public MutualFundPortfolio get(String name) {
		String query = "select name, lname, portfolioDate, instruments from "
				+ DatabaseInitializer.MF_PORTFOLIO_TABLE
				+ " where lname=? allow filtering";
		ResultSet rs = CassandraWrapper.executeQueryWithParams(query, name.toLowerCase());
		List<Row> rows = rs.all();
		if (rows.isEmpty()) {
			return null;
		}

		Row r = rows.get(0);
		return getMutualFundPortfolio(r);
	}

	public Map<String, String> getMutualFundPortfolioNameMap() {
		String query = "select name, lname from " + DatabaseInitializer.MF_PORTFOLIO_TABLE;
		ResultSet rs = CassandraWrapper.executeQuery(query);
		List<Row> rows = rs.all();
		Map<String, String> lNameToNameMap = new HashMap<>();
		if (rows.isEmpty()) {
			return lNameToNameMap;
		}

		for (Row r : rows) {
			lNameToNameMap.put(r.getString(1), r.getString(0));
		}
		return lNameToNameMap;
	}

	private MutualFundPortfolio getMutualFundPortfolio(Row r) {
		MutualFundPortfolio mfPortfolio = new MutualFundPortfolio();
		mfPortfolio.setName(r.getString("name"));
		mfPortfolio.setDate(r.getTimestamp("portfolioDate"));
		String instrumentJson = r.getString("instruments");
		
		try {
			mfPortfolio.setPortfolio(mapper.readValue(instrumentJson, new TypeReference<ArrayList<InstrumentAllocation>>(){}));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mfPortfolio;
	}
}
