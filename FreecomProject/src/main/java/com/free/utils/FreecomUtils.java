package com.free.utils;

import java.util.ArrayList;
import java.util.List;

public class FreecomUtils {

  private static void combination(int arr[], int data[], int start, int end, int index, int r, List<int[]> combinations) {
    if (index == r) {
//      for (int j = 0; j < r; j++)
//        System.out.print(data[j] + " ");
//      System.out.println("");
      int[] dest = new int[data.length];
      System.arraycopy(data, 0, dest, 0, data.length);
      combinations.add(dest);
      return;
    }

    for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
      data[index] = arr[i];
      combination(arr, data, i + 1, end, index + 1, r, combinations);
    }
  }

  private static void printCombination(int arr[], int n, int r, List<int[]> combinations) {
    int data[] = new int[r];
    combination(arr, data, 0, n - 1, 0, r, combinations);
  }

  public static List<int[]> getAllCombinations(int size) {
    List<int[]> combinations = new ArrayList<>();
    int[] array = new int[size];
    for (int i = 0; i < size; i++) {
      array[i] = i;
    }

    for (int i = 1; i <= size; i++) {
      printCombination(array, size, i, combinations);
    }
    return combinations;
  }

  public static void main(String[] args) {
    List<int[]> combs= getAllCombinations(2);
    for (int[] index : combs) {
      for (int idx : index) {
        System.out.print(idx + " ");
      }
      System.out.println();
    }

  }
}
