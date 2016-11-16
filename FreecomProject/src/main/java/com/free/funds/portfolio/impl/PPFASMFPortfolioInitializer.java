package com.free.funds.portfolio.impl;

public class PPFASMFPortfolioInitializer extends BaseMFPortfolioInitializer {

	@Override
	public String getMFName() {
		return "PPFAS";
	}

	@Override
	public boolean initializeSheet(String sheetName, int index) {
		return 0 == index;
	}

	@Override
	public int getFundNameRowNumber() {
		return 9;
	}

	@Override
	public int getFundNameCellNumber() {
		return 1;
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
		return 100;
	}

	@Override
	public String getPortfolioDatePrefix() {
		return "Monthly Portfolio Statement of the Scheme/s of PPFAS MUTUAL FUND as on";
	}

	@Override
	public String getPortfolioDateFormat() {
		return "MMM dd, yyyy";
	}

	@Override
	public String normalizeFundName(String name) {
		int end = name.indexOf("(");
		int start = "Name of the Scheme: ".length();
		if(end >= 0) {
			return name.substring(start, end).trim();
		} else {
			return name;
		}
	}

	public static void main(String[] args) {
		new PPFASMFPortfolioInitializer().initialize();
	}
}
