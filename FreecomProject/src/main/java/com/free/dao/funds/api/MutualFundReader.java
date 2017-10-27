package com.free.dao.funds.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.free.dao.funds.FundCRUD;
import com.free.dao.funds.MutualFundPortfolioCRUD;
import com.free.datahealth.FundToPortfolioMapper;
import com.free.funds.analyze.impl.PortfolioAnalyzer;
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

  public static MutualFundPortfolio getMutualFundPortfolio(List<String> schemeCodes) {
    MutualFundPortfolioCRUD portfolioCrud = new MutualFundPortfolioCRUD();
    FundCRUD fundCrud = new FundCRUD();
    List<MutualFundPortfolio> portfolios = new ArrayList<>();

    for (String schemeCode : schemeCodes) {
      MutualFund fund = fundCrud.get(schemeCode);
      String portfolioName = FundToPortfolioMapper.getPortfolioFundNameForFundName(schemeCode, fund.getName());
  
      portfolios.add(portfolioCrud.get(portfolioName));
    }

    return PortfolioAnalyzer.aggregateMutualFundPortfolio(portfolios);
  }
}
