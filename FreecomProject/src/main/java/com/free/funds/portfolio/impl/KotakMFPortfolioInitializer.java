package com.free.funds.portfolio.impl;

public class KotakMFPortfolioInitializer extends BaseMFPortfolioInitializer {

	private String fundName = "";

	@Override
	public String getMFName() {
		return "Kotak";
	}

	@Override
	public int getFundNameRowNumber() {
		return 0;
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
		return 7;
	}

	@Override
	public int getInstrumentIsinCellNumber() {
		return 3;
	}

	@Override
	public int getInstrumentPercentMultiplier() {
		return 1;
	}

	@Override
	public int getPortfolioDateCellNumber() {
		return 2;
	}

	@Override
	public String getPortfolioDatePrefix() {
		return "Portfolio of " + fundName + " as on";
	}

	@Override
	public String getPortfolioDateFormat() {
		return "dd-MMM-yyyy";
	}

	@Override
	public String normalizeFundName(String name) {
		int end = name.indexOf("as on");
		int start = "Portfolio of ".length();
		if(end >= 0) {
			fundName = name.substring(start, end).trim();
		} else {
			fundName = name;
		}

		return fundName;
	}

	public static void main(String[] args) {
		new KotakMFPortfolioInitializer().initialize();
	}
}
