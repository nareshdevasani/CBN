package com.free.funds.portfolio.impl;

public class IndiabullsMFPortfolioInitializer extends BaseMFPortfolioInitializer {

	@Override
	public String getMFName() {
		return "Indiabulls";
	}

	@Override
	public int getPortfolioDateCellNumber() {
		return 2;
	}

	@Override
	public int getFundNameRowNumber() {
		return 1;
	}

	@Override
	public int getFundNameCellNumber() {
		return 2;
	}

	@Override
	public int getInstrumentNameCellNumber() {
		return 2;
	}

	@Override
	public int getInstrumentPercentCellNumber() {
		return 6;
	}

	@Override
	public int getInstrumentIsinCellNumber() {
		return 1;
	}

	@Override
	public int getInstrumentPercentMultiplier() {
		return 100;
	}

	@Override
	public String getPortfolioDatePrefix() {
		return "Portfolio as on";
	}

	@Override
	public String getPortfolioDateFormat() {
		return "dd-MMM-yyyy";
	}

	public static void main(String[] args) {
		new IndiabullsMFPortfolioInitializer().initialize();
	}
}
