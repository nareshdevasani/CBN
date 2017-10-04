package com.free.dao.funds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.free.dao.CassandraWrapper;
import com.free.dao.DatabaseInitializer;
import com.free.interfaces.dao.CRUD;
import com.free.pojos.funds.MutualFund;

public class FundCRUD implements CRUD<MutualFund> {

	@Override
	public MutualFund create(MutualFund object) {
		String query = "INSERT INTO "
				+ DatabaseInitializer.MUTUAL_FUND_TABLE
				+ " (name, plan, options, schemecode, isin, isinreinvest,"
				+ " fundtype, fundcategory, fundhouse,"
				+ "manager, expense, nav, repurchasingprice, saleprice,"
				+ "navdate) VALUES ("
				+ "$$" + object.getName() + "$$,"
				+ "$$" + object.getPlan() + "$$,"
				+ "$$" + object.getOptions() + "$$,"
			    + "$$" + object.getSchemeCode() + "$$,"
				+ "$$" + object.getIsin() + "$$,"
				+ "$$" + object.getIsinReinvest() + "$$,"
				+ "$$" + object.getFundType() + "$$,"
				+ "$$" + object.getFundCategory() + "$$,"
				+ "$$" + object.getFundHouse() + "$$,"
		        + "$$" + object.getManager() + "$$,"
		        + object.getExpense() + ","
		   		+ object.getNav() + ","
		        + object.getRePurchagePrice() + ","
		        + object.getSalePrice() + ","
		        + "$$" + object.getNavDate().getTime() + "$$"
				+ ")";

		CassandraWrapper.executeQuery(query);

		return object;
	}

	@Override
	public MutualFund modify(MutualFund object) {
		String query = "select name, plan, options, schemecode from "
				+ DatabaseInitializer.MUTUAL_FUND_TABLE
				+ " where schemecode=?";
		ResultSet rs = CassandraWrapper.executeQueryWithParams(query, object.getSchemeCode());
		List<Row> rows = rs.all();
		if (!rows.isEmpty()) {
			String deleteQuery = "delete from " + DatabaseInitializer.MUTUAL_FUND_TABLE + " where name=$$" + rows.get(0).getString("name") 
					+ "$$ and plan=$$" + rows.get(0).getString("plan")
					+ "$$ and options=$$" + rows.get(0).getString("options")
					+ "$$ and schemecode=$$" + object.getSchemeCode()
					+ "$$";
			CassandraWrapper.executeQuery(deleteQuery);

//			String updateQuery = "update "
//					+ DatabaseInitializer.MUTUAL_FUND_TABLE
////					+ " set name=$$" + object.getName() + "$$,"
////					+ " plan=$$" + object.getPlan() + "$$,"
////					+ " options=$$" + object.getOptions() + "$$,"
//					+ " set isin=$$" + object.getIsin() + "$$,"
//					+ " isinreinvest=$$" + object.getIsinReinvest() + "$$,"
//					+ " fundtype=$$" + object.getFundType() + "$$,"
//					+ " fundcategory=$$" + object.getFundCategory() + "$$,"
//					+ " fundhouse=$$" + object.getFundHouse() + "$$,"
//					+ " manager=$$" + object.getManager() + "$$,"
//					+ " expense=" + object.getExpense() + ","
//					+ " nav=" + object.getNav() + ","
//					+ " repurchasingprice=" + object.getRePurchagePrice() + ","
//					+ " saleprice=" + object.getSalePrice() + ","
//					+ " navdate=$$" + object.getNavDate().getTime() + "$$"
//					+ " where schemecode=$$" + object.getSchemeCode() + "$$";
//
//			CassandraWrapper.executeQuery(updateQuery);

			//delete(object.getSchemeCode());
			//return object;
		}
		return create(object);
	}

	@Override
	public MutualFund delete(String schemecode) {
		String query = "delete from " + DatabaseInitializer.MUTUAL_FUND_TABLE + " where schemecode=$$" + schemecode + "$$";
		CassandraWrapper.executeQuery(query);
		return null;
	}

	@Override
	public MutualFund get(String schemecode) {
		String query = "select name, plan, options, isin, isinreinvest, fundtype, fundcategory,"
				+ " fundhouse, manager, expense, nav, repurchasingprice, saleprice, navdate, schemecode from "
				+ DatabaseInitializer.MUTUAL_FUND_TABLE
				+ " where schemecode=$$" + schemecode + "$$";
		ResultSet rs = CassandraWrapper.executeQuery(query);
		List<Row> rows = rs.all();
		if (!rows.isEmpty()) {
			MutualFund fund = getMutualFundFromRow(rows.get(0));
			fund.setSchemeCode(schemecode);
			return fund;
		}
		return null;
	}

	private MutualFund getMutualFundFromRow(Row r) {
		MutualFund fund = new MutualFund();
		fund.setName(r.getString("name"));
		fund.setPlan(r.getString("plan"));
		fund.setOptions(r.getString("options"));
		fund.setIsin(r.getString("isin"));
		fund.setIsinReinvest(r.getString("isinreinvest"));
		fund.setFundType(r.getString("fundtype"));
		fund.setFundCategory(r.getString("fundcategory"));
		fund.setFundHouse(r.getString("fundhouse"));
		fund.setManager(r.getString("manager"));
		fund.setExpense(r.getFloat("expense"));
		fund.setNav(r.getFloat("nav"));
		fund.setRePurchagePrice(r.getFloat("repurchasingprice"));
		fund.setSalePrice(r.getFloat("saleprice"));
		fund.setNavDate(r.getTimestamp("navdate"));
		fund.setSchemeCode(r.getString("schemecode"));
		return fund;
	}

	public Collection<MutualFund> getAllMutualFunds() {
		String query = "select * from " + DatabaseInitializer.MUTUAL_FUND_TABLE;
		ResultSet rs = CassandraWrapper.executeQuery(query);
		List<Row> rows = rs.all();

		List<MutualFund> funds = new ArrayList<>();
		if (!rows.isEmpty()) {
			for (Row row : rows) {
				funds.add(getMutualFundFromRow(row));
			}
		}
		return funds;
	}

	public Collection<String> getAllSchemeCodes() {
		String query = "select schemecode from " + DatabaseInitializer.MUTUAL_FUND_TABLE;
		ResultSet rs = CassandraWrapper.executeQuery(query);
		List<Row> rows = rs.all();

		Set<String> schemeCodes = new HashSet<>();
		if (!rows.isEmpty()) {
			for (Row row : rows) {
				schemeCodes.add(row.getString(0));
			}
		}
		return schemeCodes;
	}
}
