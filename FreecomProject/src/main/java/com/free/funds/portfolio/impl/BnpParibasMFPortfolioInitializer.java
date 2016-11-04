package com.free.funds.portfolio.impl;

public class BnpParibasMFPortfolioInitializer extends BaseMFPortfolioInitializer {
	@Override
	public String getMFName() {
		return "BNPParibas";
	}

	@Override
	public int getFundNameRowNumber() {
		return 0;
	}

	@Override
	public int getFundNameCellNumber() {
		return 1;
	}

	@Override
	public int getInstrumentNameCellNumber() {
		return 1;
	}

	@Override
	public int getInstrumentPercentCellNumber() {
		return 6;
	}

	@Override
	public int getInstrumentIsinCellNumber() {
		return 2;
	}

	@Override
	public int getInstrumentPercentMultiplier() {
		return 1;
	}

	@Override
	public String getPortfolioDatePrefix() {
		return "Monthly Portfolio Statement as on";
	}

	@Override
	public String getPortfolioDateFormat() {
		return "MMM dd, yyyy";
	}

	public static void main(String[] args) {
		new BnpParibasMFPortfolioInitializer().initialize();
	}
}
