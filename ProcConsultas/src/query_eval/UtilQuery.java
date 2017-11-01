package query_eval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import query_eval.util.Tupla;

public class UtilQuery {
	public static List<Integer> getOrderedList(Map<Integer,Double> mapDocsWeights)
	{
		ArrayList<Tupla<Integer,Double>> lstDocsWeight = new ArrayList<Tupla<Integer,Double>>();
		
		for(int docId : mapDocsWeights.keySet())
		{
			lstDocsWeight.add(new Tupla<Integer,Double>(docId,mapDocsWeights.get(docId),false));	
		}
		Collections.sort(lstDocsWeight);
		
		ArrayList<Integer> lstResult = new ArrayList<Integer>();
		for(int i = lstDocsWeight.size()-1 ; i >= 0 ; i--)
		{
			lstResult.add(lstDocsWeight.get(i).getX());
		}
		return lstResult;
		
		
	}
}
