package com.free.pojos.funds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PortfolioMatrix {

	private List<MatrixHeader> header;

	private Map<String, PercentList> percentMatrix;

	public PortfolioMatrix() {
	}

	public PortfolioMatrix(List<MatrixHeader> header) {
		setHeader(header);
		percentMatrix = new HashMap<String, PortfolioMatrix.PercentList>();
	}

	public void setPercent(String isin, String name, float percent, int schemeIndex) {
		PercentList percentList = percentMatrix.get(isin);
		if (null == percentList) {
			percentList = new PercentList();
			percentMatrix.put(isin, percentList);
			percentList.setInstName(name);
			percentList.setPercent(new Float[header.size()]);
		}

		percentList.getPercent()[schemeIndex] = percent;
	}

	@XmlElementWrapper(name="header-data")
	@XmlElement(name="header")
	public List<MatrixHeader> getHeader() {
		return header;
	}

	private void setHeader(List<MatrixHeader> header) {
		this.header = header;
	}

	@XmlElementWrapper(name="percentMatrix")
	public Map<String, PercentList> getPercentMatrix() {
		return percentMatrix;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Portfolio Matrix: \n");
		for (MatrixHeader head : header) {
			b.append(head.getSchemeCode() + " -> " + head.getFundName() + " \n");
		}
		b.append("ISIN \t\t");
		for (MatrixHeader head : header) {
			b.append(head.getSchemeCode() + " \t ");
		}
		b.append(" Inst. Name \t\t\n");

		for(Entry<String, PercentList> entry : percentMatrix.entrySet()) {
			b.append(entry.getKey());
			PercentList percentList = entry.getValue();
			float total = 0;
			for (Float per : percentList.getPercent()) {
				if (null != per) {
					b.append("\t" + per);
					total += per;
				} else {
					b.append("\t\t");
				}
			}

			b.append("\t" + percentList.getInstName() + "(" + total/percentList.getPercent().length + ") \n");
		}

		b.append("Total percent\t");
		for (MatrixHeader head : header) {
			b.append(head.getTotalPercent() + " \t ");
		}
		return b.toString();
	}

	@XmlRootElement
	public static class PercentList {
		private String instName;
		private Float[] percent;

		public String getInstName() {
			return instName;
		}

		public void setInstName(String fundName) {
			this.instName = fundName;
		}

		public Float[] getPercent() {
			return percent;
		}

		public void setPercent(Float[] percent) {
			this.percent = percent;
		}
	}

	@XmlRootElement
	public static class MatrixHeader {
		private float totalPercent;
		private String schemeCode;
		private String fundName;

		public float getTotalPercent() {
			return totalPercent;
		}

		public void setTotalPercent(float totalPercent) {
			this.totalPercent = totalPercent;
		}

		public String getSchemeCode() {
			return schemeCode;
		}

		public void setSchemeCode(String schemeCode) {
			this.schemeCode = schemeCode;
		}

		public String getFundName() {
			return fundName;
		}

		public void setFundName(String fundName) {
			this.fundName = fundName;
		}
	}
}

