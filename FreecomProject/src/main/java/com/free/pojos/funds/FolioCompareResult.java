package com.free.pojos.funds;

import java.util.ArrayList;
import java.util.List;

public class FolioCompareResult {

	public FolioCompareResult() {
		left = new ArrayList<>();
		intersectionLeft = new ArrayList<>();
		intersectionRight = new ArrayList<>();
		right = new ArrayList<>();
	}

	public List<InstrumentAllocation> getLeft() {
		return left;
	}
	public void setLeft(List<InstrumentAllocation> left) {
		this.left = left;
	}
	public List<InstrumentAllocation> getIntersectionLeft() {
		return intersectionLeft;
	}
	public void setIntersectionLeft(List<InstrumentAllocation> intersectionLeft) {
		this.intersectionLeft = intersectionLeft;
	}
	public List<InstrumentAllocation> getIntersectionRight() {
		return intersectionRight;
	}
	public void setIntersectionRight(List<InstrumentAllocation> intersectionRight) {
		this.intersectionRight = intersectionRight;
	}
	public List<InstrumentAllocation> getRight() {
		return right;
	}
	public void setRight(List<InstrumentAllocation> right) {
		this.right = right;
	}

	List<InstrumentAllocation> left;
	List<InstrumentAllocation> intersectionLeft;
	List<InstrumentAllocation> intersectionRight;
	List<InstrumentAllocation> right;
}
