package com.free.pojos.funds;

public class UserPortfolio {

	private float value;
	private MutualFundPortfolio mfPortfolio;

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public MutualFundPortfolio getMfPortfolio() {
		return mfPortfolio;
	}

	public void setMfPortfolio(MutualFundPortfolio mfPortfolio) {
		this.mfPortfolio = mfPortfolio;
	}
}
