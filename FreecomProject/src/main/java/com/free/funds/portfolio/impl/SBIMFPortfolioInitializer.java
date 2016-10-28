package com.free.funds.portfolio.impl;

import com.free.interfaces.funds.portfolio.PortfolioInitializer;

public class SBIMFPortfolioInitializer implements PortfolioInitializer {

	@Override
	public boolean initialize() {
		System.out.println("Initialize called.");
		
		return false;
	}

}
