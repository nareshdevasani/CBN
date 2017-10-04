package com.free.dao.funds;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.free.dao.CassandraWrapper;
import com.free.dao.DatabaseInitializer;
import com.free.interfaces.dao.CRUD;
import com.free.pojos.funds.MutualFund;

public class NavHistoryCRUD {

	public void create(Map<String, Map<Date, Float>> navHistory) {
		String query = "INSERT INTO "
				+ DatabaseInitializer.FUND_NAV_HISTORY_TABLE
				+ " (schemecode, nav, navdate) VALUES ($${0}$$,{1},$${2}$$)";

		for (Entry<String, Map<Date, Float>> entry : navHistory.entrySet()) {
			for (Entry<Date, Float> navEntry : entry.getValue().entrySet()) {
				CassandraWrapper.executeQuery(MessageFormat.format(query, entry.getKey(), navEntry.getValue(), navEntry.getKey()));
			}
		}
	}

	public void deleteBeforeTheYear(int year) {
		
	}

	public MutualFund get(String schemecode) {
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
		return fund;
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
