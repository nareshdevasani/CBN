package com.free.funds.portfolio.impl;

import java.util.ArrayList;
import java.util.List;

import com.free.interfaces.funds.portfolio.PortfolioInitializer;

public final class PortfolioUtils {

	public static void triggerInitialization() {
		List<PortfolioInitializer> initializers = getAllInitializers();
		for (PortfolioInitializer initializer : initializers) {
			initializer.initialize();
		}
	}

	private static List<PortfolioInitializer> getAllInitializers() {
		List<PortfolioInitializer> list = new ArrayList<>();
		list.add(new SBIMFPortfolioInitializer());

		return list;
	}
}
