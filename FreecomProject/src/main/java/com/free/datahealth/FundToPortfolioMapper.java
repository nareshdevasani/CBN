package com.free.datahealth;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.free.dao.funds.FundCRUD;
import com.free.dao.funds.MutualFundPortfolioCRUD;
import com.free.pojos.funds.MutualFund;

public class FundToPortfolioMapper {

	public String getPortfolioFundNameForSchemeCode(String schemeCode) {
		FundCRUD fundCrud = new FundCRUD();
		MutualFundPortfolioCRUD portfolioCrud = new MutualFundPortfolioCRUD();

		return getPortfolioFundNameForSchemeCode(schemeCode, fundCrud, portfolioCrud);
	}

	private String getPortfolioFundNameForSchemeCode(String code, FundCRUD fundCrud,
			MutualFundPortfolioCRUD portfolioCrud) {
		MutualFund fund = fundCrud.get(code);
		if (null == fund) {
			return null;
		}
		String name = fund.getName();
		Map<String, String> lNameToNameMap = portfolioCrud.getMutualFundPortfolioNameMap();

		String finalName = identifyNameFromMap(name, lNameToNameMap);
		if (null == finalName) {
			System.out.println("No portfolio for name: " + name + " - code: " + fund.getSchemeCode());
			// TODO add proxy code to identify it from hard-coded mapping
		}

		return finalName;
	}

	public Map<String, String> getPortfolioFundNameForSchemeCodes(Collection<String> schemeCodes) {
		FundCRUD fundCrud = new FundCRUD();
		MutualFundPortfolioCRUD portfolioCrud = new MutualFundPortfolioCRUD();
		Map<String, String> lNameToNameMap = portfolioCrud.getMutualFundPortfolioNameMap();

		Map<String, String> codeToNameMap = new HashMap<>();
		for (String code : schemeCodes) {
			MutualFund fund = fundCrud.get(code);
			String name = fund.getName();
			String finalName = identifyNameFromMap(name, lNameToNameMap);

			if (null == finalName) {
				System.out.println("No portfolio for name: " + name + " - code: " + fund.getSchemeCode());
			}
			codeToNameMap.put(code, finalName);
		}

		return codeToNameMap;
	}

	private String identifyNameFromMap(String fName, Map<String, String> lNameToNameMap) {
		String finalName = identifyNameFromMapCheckCaseOnly(fName, lNameToNameMap);
		if (null == finalName) {
			int toIndex = fName.indexOf('-', fName.toLowerCase().indexOf("fund"));
			if (toIndex > 0) {
				finalName = identifyNameFromMapCheckCaseOnly(fName.substring(0, toIndex), lNameToNameMap);
//				if (null != finalName) {
//					System.out.println("got it after - check");
//				}
			}
		}
		return finalName;
	}

	private String identifyNameFromMapCheckCaseOnly(String fName, Map<String, String> lNameToNameMap) {
		String finalName = lNameToNameMap.get(fName);

		if (finalName == null) {
			String lowerName = fName.toLowerCase();
			finalName = lNameToNameMap.get(lowerName);
		}
		return finalName;
	}

	public static void main(String[] args) {
		FundCRUD fundCrud = new FundCRUD();
		Collection<String> schemeCodes = fundCrud.getAllSchemeCodes();
		System.out.println("Got all scheme codes. Count: " + schemeCodes.size());
		FundToPortfolioMapper mapper = new FundToPortfolioMapper();

		Map<String, String> map = mapper.getPortfolioFundNameForSchemeCodes(schemeCodes);
		Set<Entry<String, String>> entries = map.entrySet();
		int count = 0;
		for (Entry<String, String> entry : entries) {
			String value = entry.getValue();
			System.out.println(entry.getKey() + " ->" + value);
			if (null == value) {
				count++;
			}
		}

		System.out.println("Not mapped count : " + count);
	}
}
