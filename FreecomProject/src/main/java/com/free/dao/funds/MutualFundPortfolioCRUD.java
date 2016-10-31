package com.free.dao.funds;

import java.io.IOException;
import java.util.List;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.free.dao.CassandraWrapper;
import com.free.dao.DatabaseInitializer;
import com.free.interfaces.dao.CRUD;
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
				+ " (name, portfolioDate, instruments) VALUES ("
				+ "$$" + object.getName() + "$$,"
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
		ResultSet rs = CassandraWrapper.getResultSet(query, object.getName());
		List<Row> rows = rs.all();
		if (!rows.isEmpty()) {
			delete(object.getName());
		}

		return create(object);
	}

	@Override
	public MutualFundPortfolio delete(String name) {
		String query = "delete from " + DatabaseInitializer.MF_PORTFOLIO_TABLE + " where name=$$" + name + "$$";
		CassandraWrapper.executeQuery(query);
		return null;
	}

	@Override
	public MutualFundPortfolio get(String name) {
		String query = "select name, portfolioDate, instruments from "
				+ DatabaseInitializer.MF_PORTFOLIO_TABLE
				+ " where name=?";
		ResultSet rs = CassandraWrapper.getResultSet(query, name);
		List<Row> rows = rs.all();
		if (rows.isEmpty()) {
			return null;
		}

		Row r = rows.get(0);
		MutualFundPortfolio mfPortfolio = new MutualFundPortfolio();
		mfPortfolio.setName(r.getString("name"));
		mfPortfolio.setDate(r.getTimestamp("portfolioDate"));
		String instrumentJson = r.getString("instruments");
		
		try {
			mfPortfolio.setPortfolio(mapper.readValue(instrumentJson, List.class));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mfPortfolio;
	}

}
