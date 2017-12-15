package query_eval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import indice.estrutura.Ocorrencia;

public class BooleanRankingModel implements RankingModel {

    public enum OPERATOR {
        AND, OR;
    }
    private OPERATOR operator;

    public BooleanRankingModel(OPERATOR op) {
        this.operator = op;
    }

    /**
     * Retorna a lista de documentos (nao eh necessário fazer a ordenação) para
     * a consulta mapQueryOcur e a lista de ocorrencias de documentos
     * lstOcorrPorTermoDocs.
     *
     * mapQueryOcur: Mapa de ocorrencia de termos na consulta
     * lstOcorrPorTermoDocs: lista de ocorrencia dos termos nos documentos
     * (apenas os termos que exitem na consulta)
     */
    @Override
    public List<Integer> getOrderedDocs(Map<String, Ocorrencia> mapQueryOcur,
            Map<String, List<Ocorrencia>> lstOcorrPorTermoDocs) {

        if (this.operator == OPERATOR.AND) {
            return intersectionAll(lstOcorrPorTermoDocs);
        } else {
            return unionAll(lstOcorrPorTermoDocs);
        }
    }

    /**
     * Faz a uniao de todos os elementos
     *
     * @param lstOcorrPorTermoDocs
     * @return
     */
    public List<Integer> unionAll(Map<String, List<Ocorrencia>> lstOcorrPorTermoDocs) {
        Set<Integer> docs = new HashSet<>();
        for (String term : lstOcorrPorTermoDocs.keySet()) {
            List<Ocorrencia> list = lstOcorrPorTermoDocs.get(term);
            for (Ocorrencia ocor : list) {
                if (!docs.contains(ocor.getDocId())) {
                    docs.add(ocor.getDocId());
                }

            }
        }
        
        List<Integer> itg = new ArrayList<>();
        itg.addAll(docs);
        return itg;
    }

    /**
     * Faz a interseção de todos os elementos
     *
     * @param lstOcorrPorTermoDocs
     * @return
     */
    public List<Integer> intersectionAll(Map<String, List<Ocorrencia>> lstOcorrPorTermoDocs) {
        List<Integer> docs = new ArrayList<>();
        Iterator it = lstOcorrPorTermoDocs.keySet().iterator();
        String term = (String) it.next();
        System.out.println(term);
        for (Ocorrencia ocor : lstOcorrPorTermoDocs.get(term)) {
            if(ocor.getDocId() == 53672){System.out.println("PEIDO");}
            docs.add(ocor.getDocId());
        }

        while (it.hasNext()) {
            term = (String) it.next();
            System.out.println(term);
            
            List<Ocorrencia> ocurrencesPerTerm = lstOcorrPorTermoDocs.get(term);
            List<Integer> docsForThisTerm = new ArrayList<>();
            System.out.println("NEWLIST SIZE: " + ocurrencesPerTerm.size());
            
            for (Ocorrencia ocor : lstOcorrPorTermoDocs.get(term)) {
                if(ocor.getDocId() == 53672){System.out.println("PEIDO2");}
                docsForThisTerm.add(ocor.getDocId());
            }
            
            System.out.println("DOCS SIZE ANTES: " + docs.size());
            docs.retainAll(docsForThisTerm);
            System.out.println("DOCS SIZE DEPOIS: " + docs.size());
        }
        return docs;
    }

}
