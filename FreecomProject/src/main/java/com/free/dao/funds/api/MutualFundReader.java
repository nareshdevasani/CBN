package com.free.dao.funds.api;

import java.util.Collection;

import com.free.dao.funds.FundCRUD;
import com.free.dao.funds.MutualFundPortfolioCRUD;
import com.free.datahealth.FundToPortfolioMapper;
import com.free.pojos.funds.MutualFund;
import com.free.pojos.funds.MutualFundPortfolio;
import com.free.pojos.funds.MutualFundSnapshot;

public final class MutualFundReader {

	public static Collection<MutualFund> getAllMutualFunds() {
		return new FundCRUD().getAllMutualFunds();
	}

	public static MutualFundSnapshot getMutualFundSnapshot(String schemeCode) {
		MutualFundSnapshot snapshot = new MutualFundSnapshot();

		MutualFund fund = new FundCRUD().get(schemeCode);
		snapshot.setFundHeader(fund);

		String fundName = fund.getName();
		String portfolioName = FundToPortfolioMapper.getPortfolioFundNameForFundName(schemeCode, fundName);

		snapshot.setPortfolio(new MutualFundPortfolioCRUD().get(portfolioName));

		return snapshot;
	}

  public static MutualFundPortfolio getMutualFundPortfolio(String schemeCode) {
    MutualFund fund = new FundCRUD().get(schemeCode);
    String fundName = fund.getName();
    String portfolioName = FundToPortfolioMapper.getPortfolioFundNameForFundName(schemeCode, fundName);

    return new MutualFundPortfolioCRUD().get(portfolioName);
  }
}
