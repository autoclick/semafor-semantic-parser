package edu.cmu.cs.lti.ark.fn.parsing;

import java.util.Arrays;
import java.util.Comparator;

public class UniqueSpanSlave implements Slave {
	public double[] mObjVals;
	public int mStart;
	public int mEnd;
	public DescComparator mDesc;
	
	public UniqueSpanSlave(double[] objVals, 
						   int start, 
						   int end) {
		mObjVals = new double[end-start];
		for (int i = start; i < end; i++) {
			mObjVals[i-start] = objVals[i];
		}
		mStart = start;
		mEnd = end;
		mDesc = new DescComparator();
	}
	
	@Override
	public double[] makeZUpdate(double rho, 
						   double[] us, 
						   double[] lambdas,
						   double[] zs) {
		Double[] as = new Double[mEnd - mStart];
		for (int i = mStart; i < mEnd; i++) {
			double a = us[i] + (1.0 / rho) * (mObjVals[i-mStart] + lambdas[i]);
			as[i-mStart] = a;
		}
		Arrays.sort(as, mDesc);
		double[] sums = new double[as.length];
		Arrays.fill(sums, 0);
		sums[0] = as[0];
		for (int i = 1; i < as.length; i++) {
			sums[i] = sums[i-1] + as[i];
		}
		int tempRho = 0;
		for (int i = 0; i < as.length; i++) {
			double temp = as[i] - (1.0 / (double)(i+1)) * (sums[i] - 1.0);
			if (temp <= 0) {
				break;
			}
			tempRho = i;
		}
		double tau = (1.0 / (double)(tempRho+1)) * (sums[tempRho] - 1.0);
		double[] updZs = new double[mObjVals.length];
		Arrays.fill(updZs, 0);
		for (int i = mStart; i < mEnd; i++) {
			updZs[i] = Math.max(as[i-mStart] - tau, 0);
		}
		return updZs;
	}
}

class DescComparator implements Comparator<Double> {
	public int compare(Double o1, Double o2) {
		if (o1 > o2) {
			return -1;
		} else if (o1 == o2) {
			return 0;
		} else {
			return 1;
		}
	}
}