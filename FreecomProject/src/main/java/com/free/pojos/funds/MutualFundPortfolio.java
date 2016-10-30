package com.free.pojos.funds;

import java.util.List;

public class MutualFundPortfolio {
	private String name;
	private List<InstrumentAllocation> portfolio;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<InstrumentAllocation> getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(List<InstrumentAllocation> portfolio) {
		this.portfolio = portfolio;
	}
}
