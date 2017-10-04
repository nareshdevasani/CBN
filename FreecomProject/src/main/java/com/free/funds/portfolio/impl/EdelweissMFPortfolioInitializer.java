package com.free.funds.portfolio.impl;

public class EdelweissMFPortfolioInitializer extends BaseMFPortfolioInitializer {

	private String fundName = "";

	@Override
	public String getMFName() {
		return "Edelweiss";
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
	public int getInstrumentNameCellNumber() {
		return 0;
	}

	@Override
	public int getInstrumentPercentCellNumber() {
		return 5;
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
	public int getPortfolioDateCellNumber() {
		return 0;
	}

	@Override
	public String getPortfolioDatePrefix() {
		return "PORTFOLIO STATEMENT OF " + fundName + " AS ON";
	}

	@Override
	public String normalizeFundName(String name) {
		int end = name.indexOf("AS ON");
		int start = "PORTFOLIO STATEMENT OF".length();
		if(end >= 0) {
			fundName = name.substring(start, end).trim();
		} else {
			fundName = name;
		}

		return fundName;
	}

	@Override
	public String getPortfolioDateFormat() {
		return "MMM dd, yyyy";
	}

	public static void main(String[] args) {
		new EdelweissMFPortfolioInitializer().initialize();
	}
}
