package com.free.dao.funds.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.free.dao.funds.FundCRUD;
import com.free.dao.funds.MutualFundPortfolioCRUD;
import com.free.datahealth.FundToPortfolioMapper;
import com.free.funds.analyze.impl.PortfolioAnalyzer;
import com.free.pojos.funds.InstrumentAllocation;
import com.free.pojos.funds.MutualFund;
import com.free.pojos.funds.MutualFundPortfolio;
import com.free.pojos.funds.MutualFundSnapshot;
import com.free.pojos.funds.PortfolioVennSet;
import com.free.utils.FreecomUtils;

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
    List<MutualFundPortfolio> portfolios = getPortfolios(schemeCodes);

    return PortfolioAnalyzer.aggregateMutualFundPortfolio(portfolios);
  }

  private static List<MutualFundPortfolio> getPortfolios(List<String> schemeCodes) {
    MutualFundPortfolioCRUD portfolioCrud = new MutualFundPortfolioCRUD();
    FundCRUD fundCrud = new FundCRUD();
    List<MutualFundPortfolio> portfolios = new ArrayList<>();

    for (String schemeCode : schemeCodes) {
      MutualFund fund = fundCrud.get(schemeCode);
      String portfolioName = FundToPortfolioMapper.getPortfolioFundNameForFundName(schemeCode, fund.getName());
  
      portfolios.add(portfolioCrud.get(portfolioName));
    }
    return portfolios;
  }

  public static List<PortfolioVennSet> getPortfolioVennSets(List<String> schemeCodes) {
    List<MutualFundPortfolio> portfolios = getPortfolios(schemeCodes);
    List<PortfolioVennSet> result = new ArrayList<>();

    List<int[]> combinations = FreecomUtils.getAllCombinations(portfolios.size());
    for (int[] set : combinations) {
      PortfolioVennSet venn = new PortfolioVennSet();
      venn.setSets(set);

      calculateIntersection(portfolios, set, venn);
      if (venn.getSize() > 0) {
        result.add(venn);
      }
    }

    return result;
  }

  private static void calculateIntersection(List<MutualFundPortfolio> portfolios, int[] set, PortfolioVennSet venn) {
    if (set.length == 1) {
      venn.setLabel(portfolios.get(set[0]).getName());
      venn.setSize(portfolios.get(set[0]).getPortfolio().size());
      return;
    }

    Map<String, Integer> instrumentIdToCount = new HashMap<>();
    for (int index : set) {
      List<InstrumentAllocation> instruments = portfolios.get(index).getPortfolio();
      for (InstrumentAllocation alloc : instruments) {
        Integer count = instrumentIdToCount.get(alloc.getIsin());
        if (null == count) {
          instrumentIdToCount.put(alloc.getIsin(), 1);
        } else {
          instrumentIdToCount.put(alloc.getIsin(), count + 1);
        }
      }
    }

    int vennSize = 0;
    for (Integer count : instrumentIdToCount.values()) {
      if (count.intValue() == set.length) {
        vennSize++;
      }
    }

    venn.setSize(vennSize);
  }
}
