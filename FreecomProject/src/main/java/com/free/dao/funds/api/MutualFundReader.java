package com.free.dao.funds.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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

    System.out.println(schemeCodes);
    int groupCount = 1;
    for (String schemeCode : schemeCodes) {
      String[] schemes = schemeCode.split("-");
      System.out.println("After split by - " + schemes.length);
      MutualFundPortfolio portfolio = null;
      if (schemes.length > 1) {
        List<MutualFundPortfolio> groupPortfolio = new ArrayList<>();
        for (String scheme : schemes) {
          System.out.println(scheme);
          groupPortfolio.add(getPortfolioForSchemeCode(portfolioCrud, fundCrud, scheme));
        }
        portfolio = PortfolioAnalyzer.aggregateMutualFundPortfolio(groupPortfolio);
        portfolio.setName("Group-" + groupCount++);
      } else {
        portfolio = getPortfolioForSchemeCode(portfolioCrud, fundCrud, schemeCode);        
      }

      if (null != portfolio) {
        portfolios.add(portfolio);
      }
    }
    return portfolios;
  }

  private static MutualFundPortfolio getPortfolioForSchemeCode(MutualFundPortfolioCRUD portfolioCrud, FundCRUD fundCrud,
      String schemeCode) {
    MutualFund fund = fundCrud.get(schemeCode);
    String portfolioName = FundToPortfolioMapper.getPortfolioFundNameForFundName(schemeCode, fund.getName());
    MutualFundPortfolio portfolio = portfolioCrud.get(portfolioName);
    return portfolio;
  }

  public static List<PortfolioVennSet> getPortfolioVennSets(List<String> schemeCodes) {
    List<MutualFundPortfolio> portfolios = getPortfolios(schemeCodes);
    List<PortfolioVennSet> result = new ArrayList<>();

    List<int[]> combinations = FreecomUtils.getAllCombinations(portfolios.size());
    for (int[] set : combinations) {
      //System.out.println("SET - " + Arrays.asList(set));
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

  public static String getFundSimilarTo(String fundName) {
    MutualFundPortfolioCRUD portfolioCrud = new MutualFundPortfolioCRUD();
    Map<String, String> lNameToNameMap = portfolioCrud.getMutualFundPortfolioNameMap();

    MutualFundPortfolio srcPortfolio = portfolioCrud.get(fundName);
    Map<String, Float> isinLookup = new HashMap<>();
    for (InstrumentAllocation alloc : srcPortfolio.getPortfolio()) {
      isinLookup.put(alloc.getIsin(), alloc.getPercent());
    }

    Map<Integer, List<String>> matchedMap = new TreeMap<>();
    System.out.println("Total funds to check: " + lNameToNameMap.size() + ", Funds: " + lNameToNameMap.keySet());
    int fcount = 0;
    for (String lname : lNameToNameMap.keySet()) {
      fcount++;
      if (fcount % 200 == 0) {
        System.out.println("Verified fund count: " + fcount);
      }
      if (null != lname && !lname.equalsIgnoreCase(fundName)) {
        MutualFundPortfolio targetPortfolio = portfolioCrud.get(lname);
        float srcPercentMatched = 0;
        float targetPercentMatched = 0;
        int count = 0;
        for (InstrumentAllocation alloc : targetPortfolio.getPortfolio()) {
          Float srcPercent = isinLookup.get(alloc.getIsin());
          if (null != srcPercent) {
            count++;
            srcPercentMatched = Float.sum(srcPercentMatched, srcPercent);
            targetPercentMatched = Float.sum(targetPercentMatched, alloc.getPercent());
          }
        }
        if (count == 0) {
          continue;
        }
        List<String> existing = matchedMap.get(count);
        if (null == existing) {
          existing = new ArrayList<String>();
          matchedMap.put(count, existing);
        }
        existing.add(targetPortfolio.getName() + " (" + srcPercentMatched + "% <> " + targetPercentMatched + ")");
      }
    }

    for (Entry<Integer, List<String>> entry : matchedMap.entrySet()) {
      System.out.println(entry.getKey() + " -> " + entry.getValue());
    }
    return "";
  }

  public static void main(String[] args) {
//    getFundSimilarTo("sbi small and midcap fund");
    getFundSimilarTo("Mirae Asset India Opportunities Fund".toLowerCase());
  }
}
