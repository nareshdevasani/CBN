package com.free.pojos.funds;

import com.free.interfaces.dao.DataObject;

public class Instrument implements DataObject {

	public enum Segment {
		EQUITY ("Equity"),
		MF ("MF"),
		PREFERENCE_SHARES ("Preference Shares"),
		DEBENTURES_BONDS ("Debentures and Bonds"),
		EQUITY_INSTITUTIONAL ("Equity - Institutional Series"),
		MONEY_MARKET ("Money Market"),
		OTHER_DEBT ("Other Debt");

		private String name;

		Segment(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static Segment findByName(String name) {
			for (Segment s : values()) {
				if (s.getName().equals(name)) {
					return s;
				}
			}
			return null;
		}

		public boolean isDebenturesAndBonds() {
			return this == DEBENTURES_BONDS;
		}
	}

	public enum MCAP {
		// 1-lac crore and above
		GAINT_CAP ("Mega Cap"),
		// 20K crore and less than 1-lac crore
		LARGE_CAP ("Large Cap"),
		// between 5K crore and 20K crore
		MID_CAP ("Mid Cap"),
		// less than 5K crore
		SMALL_CAP ("Small Cap"),
		MICRO_CAP ("Micro Cap");
		private String name;

		MCAP(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

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

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	public String getListingDate() {
		return listingDate;
	}

	public void setListingDate(String listingDate) {
		this.listingDate = listingDate;
	}

	public int getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(int securityCode) {
		this.securityCode = securityCode;
	}

	public float getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(float marketValue) {
		this.marketValue = marketValue;
	}

	public MCAP getMarketCap() {
		return marketCap;
	}
	
	public void setMarketCap(MCAP marketCap) {
		this.marketCap = marketCap;
	}

	@Override
	public String toString() {
		return new StringBuilder("ISIN: ").append(isin).append(", ")
				.append("Name: ").append(name).append(", ")
				.append("Symbol: ").append(symbol).append(", ")
				.append("Sector: ").append(sector).append(", ")
				.append("Segment: ").append(segment).append(", ")
				.append("Security Code: ").append(securityCode).append(", ")
				.append("Market-cap Type: ").append(null == marketCap ? "" : marketCap.name()).append(", ")
				.toString();
	}

	private String name;
	private String symbol;
	private String sector;
	private Segment segment;
	private String isin;
	private int securityCode;
	private String listingDate;
	// in crores
	private float marketValue;
	private MCAP marketCap;
}