package com.free.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.free.dao.funds.api.MutualFundReader;
import com.free.funds.analyze.impl.PortfolioAnalyzer;
import com.free.funds.portfolio.impl.PortfolioUtils;
import com.free.pojos.funds.InstrumentAllocation;
import com.free.pojos.funds.MutualFund;
import com.free.pojos.funds.MutualFundPortfolio;
import com.free.pojos.funds.MutualFundSnapshot;
import com.free.pojos.funds.PortfolioMatrix;

@Path("/funds")
@WebService
@Stateless
public class FundManager {

	@POST
	@Consumes ({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces ({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public MutualFund createMutualFund(MutualFund fund) {
		return fund;
	}

	@GET
	@Path("get-fund")
	@Produces ({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public MutualFund getFund(@QueryParam("name") String name, @QueryParam("plan") String plan, @QueryParam("options") String options) {
		MutualFund fund = new MutualFund();
		fund.setName(name);
//		fund.setPlan(plan);
//		fund.setOptions(options);

		return fund;
	}
 
	@GET
	@Path("all-funds")
	@Produces ({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Collection<MutualFund> getAllFunds() {
		return MutualFundReader.getAllMutualFunds();
	}

	@GET
	@Path("fund-snapshot")
	@Produces ({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public MutualFundSnapshot getFundSnapshot(@QueryParam("schemecode") String schemeCode) {
		System.out.println("Entered....");
		return MutualFundReader.getMutualFundSnapshot(schemeCode);
	}

  @GET
  @Path("fund-portfolio")
 	@Produces ({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Collection<InstrumentAllocation> getFundPortfolio(@QueryParam("schemecode") String schemeCode) {
	  System.out.println("Entered portfolio....");
	  MutualFundPortfolio result = MutualFundReader.getMutualFundPortfolio(schemeCode);
	  return result.getPortfolio();
	}

	@GET
	@Path("portfolio-matrix")
	@Produces ({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public PortfolioMatrix getPortfolioMatrix(@QueryParam("schemecode") List<String> schemeCodes) {
		return PortfolioAnalyzer.getPortfolioMatrix(schemeCodes);
	}

	@GET
	@Path("initialize-funds")
	public void initializeFunds() {
		PortfolioUtils.triggerInitialization();
	}

	public static void main(String[] args) {
		Collection<MutualFund> funds = new FundManager().getAllFunds();
		List<String> schemeCodes = new ArrayList<>();
		String highGrowth = null;
		String sbiBluechip = null;
		String multicap = null;
		for (MutualFund mf : funds) {
			System.out.println(mf);
			if (mf.getName().contains("High Growth Companies")) {
				highGrowth = mf.getSchemeCode();
//			} else if (mf.getName().contains("SBI BLUE CHIP")) {
//				sbiBluechip = mf.getSchemeCode();
//			} else if (mf.getName().contains("SBI Magnum Multicap")) {
//				multicap = mf.getSchemeCode();
			}
//				MutualFundSnapshot snapshot = new FundManager().getFundSnapshot(mf.getSchemeCode());
//				System.out.println(snapshot);
		}

		if (null != highGrowth) {
			schemeCodes.add(highGrowth);
		}
		if (null != sbiBluechip) {
			schemeCodes.add(sbiBluechip);
		}
		if (null != multicap) {
			schemeCodes.add(multicap);
		}
		schemeCodes.add("112093");
		PortfolioMatrix matrix = PortfolioAnalyzer.getPortfolioMatrix(schemeCodes);
		System.out.println(matrix);
	}
}
