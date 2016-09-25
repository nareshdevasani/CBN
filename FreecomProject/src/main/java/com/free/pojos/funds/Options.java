package com.free.pojos.funds;

public class Options {
	private float expense;
	private String plan;
	private String options;
	private float nav;
	private String isin;
	
	public String getPlan() {
		return plan;
	}
	
	public void setPlan(String plan) {
		this.plan = plan;
	}
	
	public String getOptions() {
		return options;
	}
	
	public void setOptions(String options) {
		this.options = options;
	}
	public float getExpense() {
		return expense;
	}
	
	public void setExpense(float expense) {
		this.expense = expense;
	}
	
	public float getNav() {
		return nav;
	}
	
	public void setNav(float nav) {
		this.nav = nav;
	}
	
	public String getIsin() {
		return isin;
	}
	
	public void setIsin(String isin) {
		this.isin = isin;
	}
}