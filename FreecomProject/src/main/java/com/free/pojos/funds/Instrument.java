package com.free.pojos.funds;

public class Instrument {
	public String getIsin() {
		return isin;
	}
	public void setIsin(String isin) {
		this.isin = isin;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public float getPersent() {
		return persent;
	}
	public void setPersent(float persent) {
		this.persent = persent;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}

	private String isin;
	private String name;
	private String symbol;
	private float persent;
	private String sector;
}