package com.free.pojos.funds;

public class MutualFundSnapshot {

	private MutualFund fundHeader;
	private MutualFundPortfolio portfolio;

	public MutualFund getFundHeader() {
		return fundHeader;
	}

	public void setFundHeader(MutualFund fundHeader) {
		this.fundHeader = fundHeader;
	}

	public MutualFundPortfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(MutualFundPortfolio portfolio) {
		this.portfolio = portfolio;
	}

	@Override
	public String toString() {
		return fundHeader.toString() + "\n" + portfolio.toString();
	}
}
