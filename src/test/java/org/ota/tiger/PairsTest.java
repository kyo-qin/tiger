package org.ota.tiger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PairsTest {

    public int longestChain(int[][] pairs) {
        Arrays.sort(pairs, (a, b) -> a[0] == b[0] ? a[1] - b[1] : a[0] - b[0]);
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        int longest = 0;
        for (int i = 0; i < pairs.length; i++) {
            int count = longest(pairs, i, map);
            if (count > longest) {
                longest = count;
            }
        }
        return longest;
    }

    public int longest(int[][] pairs, int index, Map<Integer, Integer> map) {
        if (index == pairs.length) {
            return 0;
        }
        if (map.get(index) != null) {
            return map.get(index);
        }
        int end = pairs[index][1];
        int pointer = index + 1;
        while (pointer < pairs.length && pairs[pointer][0] <= end) {
            pointer++;
        }
        int nextLongest = 0;
        for (int i = pointer; i < pairs.length; i++) {
            int theLong = longest(pairs, i, map);
            if (theLong > nextLongest) {
                nextLongest = theLong;
            }
        }
        int count = 1 + nextLongest;
        map.put(index, count);
        return count;
    }

    public static void main(String[] args) {
        PairsTest m = new PairsTest();
        int[][] pairs = new int[][] { { 5, 24 }, { 39, 60 }, { 15, 25 }, { 27, 40 }, { 50, 90 } };
        System.out.println(m.longestChain(pairs));
    }

}