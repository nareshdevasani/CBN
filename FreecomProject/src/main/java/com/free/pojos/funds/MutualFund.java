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

	private String name;
	private String plan;
	private String options;
	private String isin;

	private String manager;
	private float expense;
	private float nav;
}
