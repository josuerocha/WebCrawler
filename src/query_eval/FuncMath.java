package query_eval;

import java.util.ArrayList;
import java.util.List;

public class FuncMath {
	public static double cortaCasasDecimais(double val, int numCasas) {
	
		int numPow = (int) Math.pow(10.0, numCasas);
		return Math.round(val * numPow) / (double) numPow;
	}

	
	public static double log2(double num)
	{
		return (Math.log(num)/Math.log(2));
	} 
}
