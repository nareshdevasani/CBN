package com.free.datahealth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.free.dao.funds.FundCRUD;
import com.free.dao.funds.MutualFundPortfolioCRUD;
import com.free.pojos.funds.MutualFund;

public class FundToPortfolioMapper {

	public static String getPortfolioFundNameForSchemeCode(String schemeCode) {
		FundCRUD fundCrud = new FundCRUD();

		MutualFund fund = fundCrud.get(schemeCode);
		if (null == fund) {
			return null;
		}
		String name = fund.getName();

		return getPortfolioFundNameForFundName(schemeCode, name);
	}

	public static String getPortfolioFundNameForFundName(String code, String fundName) {
		MutualFundPortfolioCRUD portfolioCrud = new MutualFundPortfolioCRUD();
		Map<String, String> lNameToNameMap = portfolioCrud.getMutualFundPortfolioNameMap();

		return identifyPortfolioName(lNameToNameMap, code, fundName);
	}

	public static Map<String, String> getPortfolioFundNameForSchemeCodes(Collection<String> schemeCodes) {
		FundCRUD fundCrud = new FundCRUD();
		MutualFundPortfolioCRUD portfolioCrud = new MutualFundPortfolioCRUD();
		Map<String, String> lNameToNameMap = portfolioCrud.getMutualFundPortfolioNameMap();

		Map<String, String> codeToNameMap = new HashMap<>();
		for (String code : schemeCodes) {
			MutualFund fund = fundCrud.get(code);
			String name = fund.getName();
			String finalName = identifyPortfolioName(lNameToNameMap, code, name);

			codeToNameMap.put(code, finalName);
		}

		Collection<String> portfolioNames = lNameToNameMap.values();
		List<String> listNames = new ArrayList<>(portfolioNames);
		Collections.sort(listNames);
		System.out.println(" All portfolio names: ");
		for (String portName : listNames) {
			System.out.println(portName);
		}
		return codeToNameMap;
	}

	private static String identifyPortfolioName(Map<String, String> lNameToNameMap, String code, String name) {
		String finalName = identifyNameFromMap(name, lNameToNameMap);

		if (null == finalName) {
			System.out.println("No portfolio for name: " + name + " - code: " + code);
			finalName = mapByWordMatch(name.toLowerCase(), lNameToNameMap.keySet());
			if (null == finalName) {
				System.out.println("Not mapped after word matching " + name + " - code: " + code);
			} else {
				finalName = lNameToNameMap.get(finalName);
				System.out.println("Mapped after word match. Fund Name: " + name + ", Matched Portfolio Name: " + finalName);
			}
		}
		return finalName;
	}

	private static String mapByWordMatch(String lowerCaseFundName, Set<String> keySet) {

		String[] wordsInFundName = lowerCaseFundName.split(" ");
		Map<Integer, List<String>> countToPortName = new TreeMap<>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}
		});
		for (String portName : keySet) {
			if (portName == null) {
				continue;
			}
			int count = 0;
			for (String wordInFund : wordsInFundName) {
				if (!"-".equals(wordInFund) && portName.contains(wordInFund)) {
					count++;
				}
			}
			if (count > 0) {
				List<String> portNames = countToPortName.get(count);
				if (portNames == null) {
					portNames = new ArrayList<>();
					countToPortName.put(count, portNames);
				}
				portNames.add(portName);
			}
		}

		List<String> mappedPortNames = null;
		for (Entry<Integer, List<String>> entry : countToPortName.entrySet()) {
			mappedPortNames = entry.getValue();
			break;
		}

		if (null != mappedPortNames && !mappedPortNames.isEmpty()) {
			if (mappedPortNames.size() > 1) {
				System.out.println("More than one portfolios matched. Selecting the first one." + mappedPortNames);
			}
			return mappedPortNames.get(0);
		}
		return null;
	}

	private static String identifyNameFromMap(String fName, Map<String, String> lNameToNameMap) {
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

	private static String identifyNameFromMapCheckCaseOnly(String fName, Map<String, String> lNameToNameMap) {
		String finalName = lNameToNameMap.get(fName);

		if (finalName == null) {
			String lowerName = fName.toLowerCase();
			finalName = lNameToNameMap.get(lowerName);
		}
		return finalName;
	}

	// also updates scheme codes for all of the portfolios
	public static void main(String[] args) {
		FundCRUD fundCrud = new FundCRUD();
		Collection<String> schemeCodes = fundCrud.getAllSchemeCodes();
		System.out.println("Got all scheme codes. Count: " + schemeCodes.size());
		FundToPortfolioMapper mapper = new FundToPortfolioMapper();

		Map<String, String> map = mapper.getPortfolioFundNameForSchemeCodes(schemeCodes);
		Set<Entry<String, String>> entries = map.entrySet();
		int count = 0;
		// update all portfolios with scheme codes
		MutualFundPortfolioCRUD portfolioCrud = new MutualFundPortfolioCRUD();
		for (Entry<String, String> entry : entries) {
			String value = entry.getValue();
			System.out.println(entry.getKey() + " ->" + value);
			if (null == value) {
				count++;
			} else {
				//portfolioCrud.updateSchemeCode(entry.getKey(), value);
			}
		}

		System.out.println("Total: " + entries.size() + ", Not mapped count : " + count);
	}
}
