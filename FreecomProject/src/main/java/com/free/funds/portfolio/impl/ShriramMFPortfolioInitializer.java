package com.free.funds.portfolio.impl;

public class ShriramMFPortfolioInitializer extends BaseMFPortfolioInitializer {

	@Override
	public String getMFName() {
		return "Shriram";
	}

	@Override
	public int getFundNameRowNumber() {
		return 0;
	}

	@Override
	public int getFundNameCellNumber() {
		return 0;
	}

	@Override
	public int getPortfolioDateCellNumber() {
		return 3;
	}

	@Override
	public int getInstrumentNameCellNumber() {
		return 3;
	}

	@Override
	public int getInstrumentPercentCellNumber() {
		return 7;
	}

	@Override
	public int getInstrumentIsinCellNumber() {
		return 1;
	}

	@Override
	public int getInstrumentPercentMultiplier() {
		return 1;
	}

	@Override
	public String getPortfolioDatePrefix() {
		return "Portfolio as on";
	}

	@Override
	public String getPortfolioDateFormat() {
		return "MMM dd,yyyy";
	}

	public static void main(String[] args) {
		new ShriramMFPortfolioInitializer().initialize();
	}
}
