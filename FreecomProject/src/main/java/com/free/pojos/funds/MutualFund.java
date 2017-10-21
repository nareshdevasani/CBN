package com.free.pojos.funds;

import java.util.Date;

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

	public Float getExpense() {
		return expense;
	}
	
	public void setExpense(Float expense) {
		this.expense = expense;
	}
	
	public Float getNav() {
		return nav;
	}
	
	public void setNav(Float nav) {
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

	public Float getRePurchagePrice() {
		return rePurchagePrice;
	}

	public void setRePurchagePrice(Float rePurchagePrice) {
		this.rePurchagePrice = rePurchagePrice;
	}

	public Float getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Float salePrice) {
		this.salePrice = salePrice;
	}

	public String getSchemeCode() {
		return schemeCode;
	}

	public void setSchemeCode(String schemeCode) {
		this.schemeCode = schemeCode;
	}

	public String getFundCategory() {
		return fundCategory;
	}

	public void setFundCategory(String fundCategory) {
		this.fundCategory = fundCategory;
	}

	public Date getNavDate() {
		return navDate;
	}
	
	public void setNavDate(Date navDate) {
		this.navDate = navDate;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(); 
		b.append(schemeCode).append(";")
		.append(isin).append(";")
		.append(isinReinvest).append(";")
		.append(name).append(";")
		.append(plan).append(";")
		.append(options).append(";")
		.append(fundType).append(";")
		.append(fundCategory).append(";")
		.append(fundHouse).append(";")
		.append(nav).append(";")
		.append(rePurchagePrice).append(";")
		.append(salePrice).append(";")
		.append(navDate);
		return b.toString();
	}

	private String name;
	// direct/regular
	private String plan;
	// dividend/growth
	private String options;
	private String schemeCode;
	private String isin; // growth/Div-payout
	private String isinReinvest;

	// open-ended/close-ended etc.
	private String fundType;
	// balanced/ELSS/floating-rate/growth etc...
	private String fundCategory;

	private String fundHouse;
	private String manager;
	private Float expense;
	private Float nav;
	private Date navDate;
	private Float rePurchagePrice;
	private Float salePrice;
}
