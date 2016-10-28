package com.free.beans;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.free.funds.portfolio.impl.PortfolioUtils;
import com.free.pojos.funds.MutualFund;

@Path("/funds")
@WebService
@Stateless
public class FundManager {

	@POST
	@Consumes ({"text/xml", "application/json"})
	@Produces ({"text/xml", "application/json"})
	public MutualFund createMutualFund(MutualFund fund) {
		return fund;
	}

	@GET
	@Path("get-fund")
	@Produces ({"text/xml", "application/json"})
	public MutualFund getFund(@QueryParam("name") String name, @QueryParam("plan") String plan, @QueryParam("options") String options) {
		MutualFund fund = new MutualFund();
		fund.setName(name);
//		fund.setPlan(plan);
//		fund.setOptions(options);

		return fund;
	}

	@GET
	@Path("initialize-funds")
	public void initializeFunds() {
		PortfolioUtils.triggerInitialization();
	}
	
}
