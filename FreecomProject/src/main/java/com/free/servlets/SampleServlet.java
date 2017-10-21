package com.free.servlets;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.free.beans.FundManager;
import com.free.pojos.funds.MutualFund;

/**
 * Servlet implementation class SampleServlet
 */
@WebServlet("/SampleServlet")
public class SampleServlet extends HttpServlet {

//	@EJB
//	private FundManager fundManager;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
//		MutualFund fund = fundManager.getFund("SBI Small and Midcap", "Direct", "Growth");
//		response.getWriter().append("Fund: " + fund.getName());
	}
}
