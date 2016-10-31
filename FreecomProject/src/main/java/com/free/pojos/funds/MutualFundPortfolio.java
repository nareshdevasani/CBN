package com.free.pojos.funds;

import java.util.Date;
import java.util.List;

import com.free.interfaces.dao.DataObject;

public class MutualFundPortfolio implements DataObject {
	private String name;
	private Date date;
	private List<InstrumentAllocation> portfolio;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<InstrumentAllocation> getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(List<InstrumentAllocation> portfolio) {
		this.portfolio = portfolio;
	}
}
