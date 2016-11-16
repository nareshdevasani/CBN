package com.free.funds.portfolio.impl;

public class QuantumMFPortfolioInitializer extends BaseMFPortfolioInitializer {

	private String fundName = "";
	private int instPercentCellNum = 7;

	@Override
	public String getMFName() {
		return "Quantum";
	}

	@Override
	public int getSheetStartIndex() {
		return 1;
	}

	@Override
	public boolean initializeSheet(String sheetName, int index) {
		switch(sheetName) {
		case "QEFOF":
		case "QGSF":
		case "QMAF":
			instPercentCellNum = 6;
			break;
		default:
			instPercentCellNum = 7;
			break;
		}
		return true;
	}

	@Override
	public int getFundNameRowNumber() {
		return 7;
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
		return instPercentCellNum;
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
		return "Monthly Portfolio Statement of the " + fundName + " for the period ended";
	}

	@Override
	public String getPortfolioDateFormat() {
		return "MMM dd,yyyy";
	}

	@Override
	public String normalizeFundName(String name) {
		int index = name.indexOf('(');
		if(index >= 0) {
			fundName = name.substring(0, index).trim();
		} else {
			fundName = name;
		}
		return fundName;
	}

	public static void main(String[] args) {
		new QuantumMFPortfolioInitializer().initialize();
	}
}
