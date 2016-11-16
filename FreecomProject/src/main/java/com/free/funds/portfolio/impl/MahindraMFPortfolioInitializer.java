package com.free.funds.portfolio.impl;

public class MahindraMFPortfolioInitializer extends BaseMFPortfolioInitializer {

	private int fundNameCellNum = 1;
	private int instNameCellNum = 1;
	private int instPercentCellNum = 6;
	private int instIsinCellNum = 2;

	@Override
	public String getMFName() {
		return "Mahindra";
	}

	@Override
	public boolean initializeSheet(String sheetName, int index) {
		switch(sheetName) {
		case "MMF Kar Bachat Yojana":
			fundNameCellNum = 0;
			instNameCellNum = 0;
			instPercentCellNum = 5;
			instIsinCellNum = 1;
			break;
		case "Mahindra Liquid Fund":
		default:
			fundNameCellNum = 1;
			instNameCellNum = 1;
			instPercentCellNum = 6;
			instIsinCellNum = 2;
			break;
		case "MMF02":
			return false;
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
		return instNameCellNum;
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
	public int getPortfolioDateCellNumber() {
		return -1;
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
		return name.split("\n")[2];
	}

	public static void main(String[] args) {
		new MahindraMFPortfolioInitializer().initialize();
	}
}
