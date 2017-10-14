package com.charlesbot.slack;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class PercentRanges {

	private RangeMap<Double, String> percentRanges;
	
	public PercentRanges() {
		// initialize the percent ranges
		percentRanges = TreeRangeMap.create();
		percentRanges.put(Range.atLeast(10d), "green5");       // [10, +∞)
		percentRanges.put(Range.closedOpen(6d,10d), "green4"); // [6, 10)
		percentRanges.put(Range.closedOpen(3d,6d), "green3");  // [3, 6)
		percentRanges.put(Range.closedOpen(1d,3d), "green2");  // [1, 3)
		percentRanges.put(Range.open(0d,1d), "green1");        // (0, 1)
		percentRanges.put(Range.closed(0d,0d), "black");       // [0, 0]
		percentRanges.put(Range.open(-1d,0d), "red1");         // (-1, 0)
		percentRanges.put(Range.openClosed(-3d,-1d), "red2");  // (-3, -1]
		percentRanges.put(Range.openClosed(-6d,-3d), "red3");  // (-6, -3]
		percentRanges.put(Range.openClosed(-10d,-6d), "red4"); // (-10, -6]
		percentRanges.put(Range.atMost(-10d), "red5");         // (-∞, -10]
	}

	public String get(double amount) {
		return percentRanges.get(amount);
	}
	
	
	
}
