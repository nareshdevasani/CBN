package com.free.instruments.impl;

import java.util.List;

import com.free.dao.funds.FundCRUD;
import com.free.interfaces.funds.portfolio.PortfolioInitializer;
import com.free.pojos.funds.MutualFund;
import com.free.utils.HttpDataSourceUtils;

public class MutualFundInitializer implements PortfolioInitializer {

	@Override
	public boolean initialize() {
		List<MutualFund> funds = HttpDataSourceUtils.readNAVFromURL();
		int count = 0;
		long start = System.currentTimeMillis();
		if (null != funds) {
			FundCRUD crud = new FundCRUD();
			for (MutualFund fund : funds) {
				crud.modify(fund);
				count++;
				if (count % 1000 == 0) {
					System.out.println(count + " Mutual funds are loaded. Time taken: " + (System.currentTimeMillis() - start));
				}
			}
		}

		System.out.println("Finished loading mutual fund and their NAVs. Count: " + count);
		return true;
	}

	public static void main(String[] args) {
		new MutualFundInitializer().initialize();
	}
}
