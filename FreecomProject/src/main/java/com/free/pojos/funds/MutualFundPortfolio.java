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

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Name: " + getName())
		.append("\n")
		.append("Date: " + getDate())
		.append("\n")
		.append("Portfolio Details: \n")
		.append("ISIN \t\t Percent \t Name \t \n");
		for (InstrumentAllocation alloc : portfolio) {
			b.append(alloc.getIsin() + "\t" + alloc.getPercent() + "\t" + alloc.getName() + "\n");
		}
		return b.toString();
	}
}
