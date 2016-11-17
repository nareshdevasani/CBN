package com.free.funds.portfolio.impl;

public class SaharaMFPortfolioInitializer extends BaseMFPortfolioInitializer {

	private int dateCellNum = 0;
	private int percentMultiplier = 1;

	@Override
	public String getMFName() {
		return "Sahara";
	}

	@Override
	public boolean initializeSheet(String sheetName, int index) {
		switch(sheetName) {
		case "Liquid":
			dateCellNum = 0;
			percentMultiplier = 100;
			break;
		default:
			dateCellNum = 3;
			percentMultiplier = 1;
			break;
		}
		return true;
	}

	@Override
	public int getPortfolioDateCellNumber() {
		return dateCellNum;
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
		return percentMultiplier;
	}

	@Override
	public String getPortfolioDatePrefix() {
		return "Holding as on";
	}

	@Override
	public String getPortfolioDateFormat() {
		return "dd.MM.yyyy";
	}

	public static void main(String[] args) {
		new SaharaMFPortfolioInitializer().initialize();
	}
}
