package com.free.funds.portfolio.impl;

public class EscortsMFPortfolioInitializer extends BaseMFPortfolioInitializer {

	private int fundNameCellNum = 2;
	private int percentCellNum = 7;

	@Override
	public String getMFName() {
		return "Escorts";
	}

	@Override
	public boolean initializeSheet(String sheetName) {
		switch(sheetName) {
		case "ESDF":
		case "EGILT":
			percentCellNum = 6;
			break;
		case "ELP":
			fundNameCellNum = 0;
			percentCellNum = 6;
			break;
		default:
			percentCellNum = 7;
			fundNameCellNum = 2;
			break;
		}
		return true;
	}

	@Override
	public int getFundNameRowNumber() {
		return 0;
	}

	@Override
	public int getFundNameCellNumber() {
		return fundNameCellNum;
	}

	@Override
	public int getInstrumentNameCellNumber() {
		return 2;
	}

	@Override
	public int getInstrumentPercentCellNumber() {
		return percentCellNum;
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
		return 2;
	}

	@Override
	public String getPortfolioDatePrefix() {
		return "MONTHLY PORTFOLIO STATEMENT AS ON";
	}

	@Override
	public String getPortfolioDateFormat() {
		return "dd-MM-yyyy";
	}

	public static void main(String[] args) {
		new EscortsMFPortfolioInitializer().initialize();
	}
}
