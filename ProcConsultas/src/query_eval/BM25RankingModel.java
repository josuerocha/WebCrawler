package query_eval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indice.estrutura.Ocorrencia;

public class BM25RankingModel implements RankingModel 
{
	private 

	private IndicePreCompModelo idxPrecompVals;
	private double b;
	private int k1;
	
	public BM25RankingModel(IndicePreCompModelo idxPrecomp,double b,int k1)
	{
		this.idxPrecompVals = idxPrecomp;
		this.b = b;
		this.k1 = k1;
	}
	/**
	 * Calcula o idf (adaptado) do bm25
	 * @param numDocs
	 * @param numDocsArticle
	 * @return
	 */
	public double idf(int numDocs,int numDocsArticle)
	{
		return 0.0;
	}
	/**
	 * Calcula o beta_{i,j}
	 * @param freqTerm
	 * @return
	 */
	public double beta_ij(int freqTermDoc) {
		return 0;
	}
	
	/**
	 * Retorna a lista ordenada de documentos usando o modelo do BM25.
	 * para isso, em dj_weight calcule o peso do documento j para a consulta. 
	 * Para cada termo, calcule o Beta_{i,j} e o idf e acumule o pesso desse termo para o documento. 
	 * Logo após, utilize UtilQuery.getOrderedList para ordenar e retornar os docs ordenado
	 * mapQueryOcour: Lista de ocorrencia de termos na consulta
	 * lstOcorrPorTermoDocs: Lista de ocorrencia dos termos nos documentos (apenas termos que ocorrem na consulta)
	 */
	@Override
	public List<Integer> getOrderedDocs(Map<String, Ocorrencia> mapQueryOcur,
			Map<String, List<Ocorrencia>> lstOcorrPorTermoDocs) {
		
		
		Map<Integer,Double> dj_weight = new HashMap<Integer,Double>();
		
		
		

	}
	

}
