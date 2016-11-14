package com.free.funds.portfolio.impl;

public class IDBIMFPortfolioInitializer extends BaseMFPortfolioInitializer {

	@Override
	public String getMFName() {
		return "IDBI";
	}

	@Override
	public int getPortfolioDateCellNumber() {
		return 0;
	}

	@Override
	public int getSheetStartIndex() {
		return 1;
	}

	@Override
	public int getFundNameRowNumber() {
		return 3;
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
		return 6;
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
		return "Portfolio Statement as on";
	}

	@Override
	public String getPortfolioDateFormat() {
		return "MMM dd, yyyy";
	}

	@Override
	public String normalizeFundName(String name) {
		return name.substring("Scheme :".length());
	}

	public static void main(String[] args) {
		new IDBIMFPortfolioInitializer().initialize();
	}
}
