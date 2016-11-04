package com.free.funds.portfolio.impl;

public class IDFCMFPortfolioInitializer extends BaseMFPortfolioInitializer {

	@Override
	public String getMFName() {
		return "IDFC";
	}

	@Override
	public int getFundNameRowNumber() {
		return 4;
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
		return 100;
	}

	@Override
	public String getPortfolioDatePrefix() {
		return "Monthly Portfolio Statement as on";
	}

	@Override
	public String getPortfolioDateFormat() {
		return "MMM dd,yyyy";
	}

	@Override
	public String normalizeFundName(String name) {
		int index = name.indexOf('(');
		if(index >= 0) {
			return name.substring(0, index).trim();
		}
		return name;
	}

	public static void main(String[] args) {
		new IDFCMFPortfolioInitializer().initialize();
	}
}
