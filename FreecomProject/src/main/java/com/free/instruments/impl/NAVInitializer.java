package com.free.instruments.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.free.interfaces.funds.portfolio.PortfolioInitializer;
import com.free.utils.HttpDataSourceUtils;

public class NAVInitializer implements PortfolioInitializer {

	private static final int MF_MIN = 1;
	private static final int MF_MAX = 100;
	private static final int YEAR_START = 2005;

	@Override
	public boolean initialize() {
		long start = System.currentTimeMillis();

		Calendar cal = Calendar.getInstance();
		int currentYear = cal.get(Calendar.YEAR);

		int mf = MF_MIN;
		int year = YEAR_START;
		while (mf <= MF_MAX) {
			year = YEAR_START;
			while(year <= currentYear) {
				String from = "01-Jan-" + year;
				String to = "31-Dec-" + year;
				year++;

				// open-ended
				Map<String, Map<Date, Float>> navHistory = HttpDataSourceUtils.getNavHistoryForDateRange(mf, 1, from, to);
				// close-ended
				navHistory = HttpDataSourceUtils.getNavHistoryForDateRange(mf, 2, from, to);
				// interval-funds
				navHistory = HttpDataSourceUtils.getNavHistoryForDateRange(mf, 3, from, to);
			}
			mf++;
		}

		System.out.println("Finished loading historical NAVs. Time taken(min): " + ((System.currentTimeMillis() - start)/(60000 * 1.0)));
		return true;
	}

	private void persistNav(Map<String, Map<Date, Float>> navHistory) {
		
	}

	public static void main(String[] args) {
		new NAVInitializer().initialize();
	}
}
