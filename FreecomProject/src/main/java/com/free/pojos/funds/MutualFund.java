package com.free.pojos.funds;

import java.util.List;

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

	public List<Options> getOptions() {
		return options;
	}

	public void setOptions(List<Options> options) {
		this.options = options;
	}

	public List<Instrument> getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(List<Instrument> portfolio) {
		this.portfolio = portfolio;
	}

	private String name;
	private String manager;

	private List<Options> options;
	private List<Instrument> portfolio;
}
