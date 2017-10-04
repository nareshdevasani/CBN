package com.free.funds.portfolio.impl;

public class MotilalOswalMFPortfolioInitializer extends BaseMFPortfolioInitializer {

	private int instPercentCellNum = 9;
	private int instIsinCellNum = 4;

	@Override
	public String getMFName() {
		return "MotilalOswal";
	}

	@Override
	public boolean initializeSheet(String sheetName, int index) {
		switch(sheetName) {
		case "USTBF":
		case "M50":
		case "MCAP100":
			instPercentCellNum = 8;
			break;
		case "N100":
			instPercentCellNum = 6;
			instIsinCellNum = 2;
			break;
		default:
			instPercentCellNum = 9;
			instIsinCellNum = 4;
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
		return instIsinCellNum;
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
		new MotilalOswalMFPortfolioInitializer().initialize();
	}
}
