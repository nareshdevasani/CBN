package com.free.funds.analyze.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.free.dao.funds.FundCRUD;
import com.free.dao.funds.MutualFundPortfolioCRUD;
import com.free.dao.funds.api.MutualFundReader;
import com.free.datahealth.FundToPortfolioMapper;
import com.free.pojos.funds.FolioCompareResult;
import com.free.pojos.funds.InstrumentAllocation;
import com.free.pojos.funds.MutualFund;
import com.free.pojos.funds.MutualFundPortfolio;
import com.free.pojos.funds.MutualFundSnapshot;
import com.free.pojos.funds.PortfolioMatrix;
import com.free.pojos.funds.UserPortfolio;
import com.free.pojos.funds.PortfolioMatrix.MatrixHeader;
import com.free.pojos.funds.PortfolioMatrix.PercentList;

public class PortfolioAnalyzer {

	public static MutualFundPortfolio aggregateMutualFundPortfolio(List<MutualFundPortfolio> mfPortfolios) {
		MutualFundPortfolio mfFolio = new MutualFundPortfolio();
		StringBuilder nameBuff = new StringBuilder();
		List<InstrumentAllocation> allPortfolio = new ArrayList<>();
		Map<String, InstrumentAllocation> instrumentLookup = new HashMap<>();

		for (MutualFundPortfolio oneMfFolio : mfPortfolios) {
			nameBuff.append(oneMfFolio.getName()).append(":");

			for(InstrumentAllocation nextAlloc : oneMfFolio.getPortfolio()) {
				InstrumentAllocation instAlloc = instrumentLookup.get(nextAlloc.getIsin());
				if (null == instAlloc) {
					instAlloc = new InstrumentAllocation();
					instAlloc.setIsin(nextAlloc.getIsin());
					instrumentLookup.put(nextAlloc.getIsin(), instAlloc);

					allPortfolio.add(instAlloc);
				}
				instAlloc.setPercent(Float.sum(instAlloc.getPercent(), nextAlloc.getPercent()));
			}
		}

		for (InstrumentAllocation allAlloc : allPortfolio) {
			allAlloc.setPercent(allAlloc.getPercent() / mfPortfolios.size());
		}

		mfFolio.setName(nameBuff.toString());
		mfFolio.setPortfolio(allPortfolio);
		return mfFolio;
	}

	public static UserPortfolio aggregateUserPortfolio(List<UserPortfolio> userPortfolios) {
		UserPortfolio userFolio = new UserPortfolio();
		StringBuilder nameBuff = new StringBuilder();
		float totalValue = 0;
		MutualFundPortfolio mfPortfolio = new MutualFundPortfolio();
		// isin to value lookup
		Map<String, Float> instrumentLookup = new HashMap<>();

		for (UserPortfolio oneUserFolio : userPortfolios) {
			totalValue = Float.sum(totalValue, oneUserFolio.getValue());
			nameBuff.append(oneUserFolio.getMfPortfolio().getName()).append(":");

			for (InstrumentAllocation nextAlloc : oneUserFolio.getMfPortfolio().getPortfolio()) {
				Float existingValue = instrumentLookup.get(nextAlloc.getIsin());
				float value = (nextAlloc.getPercent() * oneUserFolio.getValue())/100;
				if (null == existingValue) {
					instrumentLookup.put(nextAlloc.getIsin(), value);
				} else {
					instrumentLookup.put(nextAlloc.getIsin(), Float.sum(existingValue, value));
				}
			}
		}

		List<InstrumentAllocation> instruments = new ArrayList<>();
		for (Entry<String, Float> entry : instrumentLookup.entrySet()) {
			InstrumentAllocation instAlloc = new InstrumentAllocation();
			instAlloc.setIsin(entry.getKey());
			instAlloc.setPercent(entry.getValue() / totalValue * 100);
			instruments.add(instAlloc);
		}

		mfPortfolio.setName(nameBuff.toString());
		mfPortfolio.setPortfolio(instruments);

		userFolio.setValue(totalValue);
		userFolio.setMfPortfolio(mfPortfolio);

		return userFolio;
	}

	public static FolioCompareResult compareFolios(List<MutualFundPortfolio> leftFolio, List<MutualFundPortfolio> rightFolio) {
		FolioCompareResult result = new FolioCompareResult();

		MutualFundPortfolio leftAggregate = aggregateMutualFundPortfolio(leftFolio);
		MutualFundPortfolio rightAggregate = aggregateMutualFundPortfolio(rightFolio);
		
		Map<String, InstrumentAllocation> rightLookup = new HashMap<>();
		for (InstrumentAllocation rightInstAlloc : rightAggregate.getPortfolio()) {
			rightLookup.put(rightInstAlloc.getIsin(), rightInstAlloc);
		}

		for (InstrumentAllocation leftAlloc : leftAggregate.getPortfolio()) {
			if (null == rightLookup.get(leftAlloc.getIsin())) {
				result.getLeft().add(leftAlloc);
			} else {
				InstrumentAllocation matched = rightLookup.remove(leftAlloc.getIsin());
				result.getIntersectionLeft().add(leftAlloc);
				result.getIntersectionRight().add(matched);
			}
		}

		result.getRight().addAll(rightLookup.values());

		return result;
	}

	public static PortfolioMatrix getPortfolioMatrix(List<String> schemeCodes) {
		List<MatrixHeader> header = new ArrayList<>();
		for (String schemeCode : schemeCodes) {
			MutualFund fund = new FundCRUD().get(schemeCode);
			MatrixHeader head = new MatrixHeader();
			head.setFundName(fund.getName());
			head.setSchemeCode(schemeCode);
			header.add(head);
		}

		PortfolioMatrix matrix = new PortfolioMatrix(header);

		MutualFundPortfolioCRUD portCrud = new MutualFundPortfolioCRUD();
		for (int i = 0; i < header.size(); i++) {
			MatrixHeader head = header.get(i);
			String portfolioName = FundToPortfolioMapper.getPortfolioFundNameForFundName(head.getSchemeCode(), head.getFundName());
			MutualFundPortfolio portfolio = portCrud.get(portfolioName);
			List<InstrumentAllocation> instruments = portfolio.getPortfolio();
			for (InstrumentAllocation inst : instruments) {
				matrix.setPercent(inst.getIsin(), inst.getName(), inst.getPercent(), i);
			}
		}

		Map<String, PercentList> percentMatrix = matrix.getPercentMatrix();
		for (int i = 0; i < header.size(); i++) {
			float totalPercent = 0;
			for (Entry<String, PercentList> entry : percentMatrix.entrySet()) {
				if (null != entry.getValue().getPercent()[i]) {
					totalPercent += entry.getValue().getPercent()[i];
				}
			}
			MatrixHeader head = header.get(i);
			head.setTotalPercent(totalPercent);
		}
		return matrix;
	}
}
