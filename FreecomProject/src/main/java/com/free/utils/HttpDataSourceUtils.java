package com.free.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.free.pojos.funds.MutualFund;

public final class HttpDataSourceUtils {

	public static List<MutualFund> readNAVFromURL() {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpget = new HttpGet("http://portal.amfiindia.com/spages/NAV0.txt");

			System.out.println("Executing request " + httpget.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());

				// Get hold of the response entity
				HttpEntity entity = response.getEntity();

				// If the response does not enclose an entity, there is no need
				// to bother about connection release
				if (entity != null) {
					InputStream instream = entity.getContent();
					try {
						return getFundHouseToFundDataMap(instream);
						// do something useful with the response
					} catch (IOException ex) {
						// In case of an IOException the connection will be
						// released back to the connection manager automatically
						throw ex;
					} finally {
						// Closing the input stream will trigger connection release
						instream.close();
					}
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			System.out.println("Exception in getting nav details.");
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static List<MutualFund> getFundHouseToFundDataMap(InputStream instream) throws IOException {
		List<MutualFund> funds = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(instream));

		String line = reader.readLine(); // ignore first line.
		String houseName = null;
		String category = null;
		String type = null;
		while((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}

			if (line.contains(";")) {
				String[] details = line.split(";");

				MutualFund fund = new MutualFund();
				fund.setSchemeCode(details[0]);
				fund.setIsin(details[1]);
				fund.setIsinReinvest(details[2]);
				try {
					fund.setNav(Float.parseFloat(details[4]));
					fund.setRePurchagePrice(Float.parseFloat(details[5]));
					fund.setSalePrice(Float.parseFloat(details[6]));
				} catch(NumberFormatException nfe) {
					// continue
					continue;
				}
				Date navDate;
				DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
				try {
					navDate = (Date)formatter.parse(details[7]);
				} catch (ParseException e) {
					navDate = new Date();
				}

				fund.setFundHouse(houseName);
				fund.setFundCategory(category);
				fund.setFundType(type);
				fund.setNavDate(navDate);

				setSchemeNameDetails(fund, details[3]);
				funds.add(fund);
				System.out.println(fund);
			} else if (line.contains("Fund")) {
				houseName = line;
			} else if (line.contains("(")) {
				type = line.substring(0, line.indexOf('('));
				type = type.substring(0, type.length() - "schemes".length()).trim();
				category = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
			}
		}

		return funds;
	}

	private static void setSchemeNameDetails(MutualFund fund, String sname) {
		if (sname.toLowerCase().contains("direct")) {
			fund.setPlan("Direct");
		} else if (sname.toLowerCase().contains("bonus")) {
			fund.setPlan("Bonus");
		} else {
			fund.setPlan("Regular");
		}

		if (sname.toLowerCase().contains("dividend reinvestment")
				|| sname.toLowerCase().contains("div re-invest")
				 || sname.toLowerCase().contains("div reinvest")) {
			fund.setOptions("Dividend Reinvestment");
		} else if (sname.toLowerCase().contains("dividend payout")) {
			fund.setOptions("Dividend Payout");
		} else if (sname.toLowerCase().contains("- growth") || sname.toLowerCase().contains("growth")) {
			fund.setOptions("Growth");
		}
		if (sname.toLowerCase().contains("- dividend") || sname.toLowerCase().contains("dividend")
				|| sname.toLowerCase().contains("div option")) {
			fund.setOptions("Dividend");
		}

		sname = sname.replaceAll("( )+", " ");
		sname = sname.replace("Direct Plan", "");
		sname = sname.replace("DIrect Plan", "");
		sname = sname.replace("Regular Plan", "");
		sname = sname.replace("DIRECT PLAN", "");
		sname = sname.replace("REGULAR PLAN", "");
		sname = sname.replace("Direct Option", "");
		sname = sname.replace("Regular Option", "");
		sname = sname.replace("DIRECT OPTION", "");
		sname = sname.replace("REGULAR OPTION", "");
		sname = sname.replace("(Direct)", "");
		sname = sname.replace("(DIRECT)", "");
		sname = sname.replace("Direct", "");
		sname = sname.replace("Dividend Option", "");
		sname = sname.replace("Dividend Payout Option", "");
		sname = sname.replace("Div Option", "");
		sname = sname.replace("Div Reinvest.", "");
		sname = sname.replace("Div Reinvest", "");
		sname = sname.replace("Growth Option", "");
		sname = sname.replace("GROWTH OPTION", "");
		sname = sname.replace("Growth option", "");
		sname = sname.replace("Bonus Option", "");
		sname = sname.replace("Dividend Option", "");
		sname = sname.replace("dividend option", "");
		sname = sname.replace("DIVIDEND OPTION", "");
		sname = sname.replace("Dividend option", "");
		if (sname.contains("- Dividend Plan")
				|| sname.contains("-Dividend Plan")
				|| sname.contains("-DIVIDEND PLAN")
				|| sname.contains("- Dividend")) {
			sname = sname.replace("- Dividend Plan", "");
			sname = sname.replace("-Dividend Plan", "");
			sname = sname.replace("-DIVIDEND PLAN", "");
			sname = sname.replace("- Dividend", "");
		} else {
			sname = sname.replace("Dividend", "");
		}
		if (sname.contains("- Growth Plan") || sname.contains("- Growth")) {
			sname = sname.replace("- Growth Plan", "");
			sname = sname.replace("- Growth", "");
		} else if ("Growth".equals(fund.getOptions())) {
			sname = sname.replace("Growth", "");
		}
		sname = sname.replace("DIVIDEND", "");
		sname = sname.replace("GROWTH", "");
		sname = sname.replaceAll("( )+", " ");
		sname = sname.replaceAll("(-)+", "-");
		sname = sname.replaceAll("(_)+", "_");
		sname = sname.replaceAll("(- )+", "- ");
		sname = sname.replaceAll("( -)+", " -");
		while(true) {
			sname = sname.trim();
			if (sname.endsWith("-")) {
				sname = sname.substring(0, sname.length() - 1);
			} else {
				break;
			}
		}

		fund.setName(sname.trim());
	}

	public static void main(String[] args) throws Exception {
		readNAVFromURL();
	}
}
