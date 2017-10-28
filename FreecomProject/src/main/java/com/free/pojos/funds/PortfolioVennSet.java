package com.free.pojos.funds;

public class PortfolioVennSet {

  public int[] getSets() {
    return sets;
  }

  public void setSets(int[] sets) {
    this.sets = sets;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  private int[] sets;
  private String label;
  private int size;
}
