package query_eval;

import java.util.List;
import java.util.Map;

import indice.estrutura.Ocorrencia;


public interface RankingModel {

	public abstract List<Integer> getOrderedDocs(Map<String,Ocorrencia> mapQueryOcur, Map<String,List<Ocorrencia>> lstOcorrPorTermoDocs);
}
