package com.free.funds.portfolio.impl;

public class TataMFPortfolioInitializer extends BaseMFPortfolioInitializer {

	@Override
	public String getMFName() {
		return "TATA";
	}

	@Override
	public int getSheetStartIndex() {
		return 1;
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
		return 6;
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
	public String getPortfolioDatePrefix() {
		return "as on";
	}

	@Override
	public String getPortfolioDateFormat() {
		return "dd/MM/yy";
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
		new TataMFPortfolioInitializer().initialize();
	}
}
