package com.free.pojos.funds;

import javax.xml.bind.annotation.XmlRootElement;
import com.free.interfaces.dao.DataObject;

@XmlRootElement
public class MutualFund implements DataObject {

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

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

	public String getFundHouse() {
		return fundHouse;
	}

	public void setFundHouse(String fundHouse) {
		this.fundHouse = fundHouse;
	}

	public String getFundType() {
		return fundType;
	}

	public void setFundType(String fundType) {
		this.fundType = fundType;
	}

	public String getIsinReinvest() {
		return isinReinvest;
	}

	public void setIsinReinvest(String isinReinvest) {
		this.isinReinvest = isinReinvest;
	}

	public float getRePurchagePrice() {
		return rePurchagePrice;
	}

	public void setRePurchagePrice(float rePurchagePrice) {
		this.rePurchagePrice = rePurchagePrice;
	}

	public float getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(float salePrice) {
		this.salePrice = salePrice;
	}

	public String getSchemeCode() {
		return schemeCode;
	}

	public void setSchemeCode(String schemeCode) {
		this.schemeCode = schemeCode;
	}

	private String name;
	private String plan;
	private String options;
	private String schemeCode;
	private String isin; // growth/Div-payout
	private String isinReinvest;

	// open-ended/close-ended etc.
	private String fundType;

	private String fundHouse;
	private String manager;
	private float expense;
	private float nav;
	private float rePurchagePrice;
	private float salePrice;
}
