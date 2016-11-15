package com.free.funds.portfolio.impl;

public class InvescoMFPortfolioInitializer extends BaseMFPortfolioInitializer {

	private int instPercentCellNum = 6;

	@Override
	public String getMFName() {
		return "Invesco";
	}

	@Override
	public boolean initializeSheet(String sheetName, int index) {
		switch(sheetName) {
		case "IIGEIF-holdings":
		case "IIPEEF-holdings":
		case "IIGFOF-holdings":
			instPercentCellNum = 5;
			break;
		default:
			instPercentCellNum = 6;
		}

		return index == 0;
	}

	@Override
	public int getFundNameRowNumber() {
		return 2;
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
		return instPercentCellNum;
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

	@Override
	public String normalizeFundName(String name) {
		int index = name.indexOf('(');
		if(index >= 0) {
			return name.substring(0, index).trim();
		}
		return name;
	}

	public static void main(String[] args) {
		new InvescoMFPortfolioInitializer().initialize();
	}
}
