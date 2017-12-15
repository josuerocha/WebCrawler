package query_eval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indice.estrutura.Ocorrencia;
import java.util.Collections;

public class VectorRankingModel implements RankingModel {

    private IndicePreCompModelo idxPrecompVals;

    public static double tf(int freqTerm) {
        if (freqTerm > 0) {
            return 1 + Math.log(freqTerm) / Math.log(2);
        } else {
            return 0;
        }
    }

    public static double idf(int numDocs, int numDocsTerm) {
        return Math.log(numDocs / numDocsTerm) / Math.log(2);
    }

    public static double tfIdf(int numDocs, int freqTerm, int numDocsTerm) {
        return tf(freqTerm) * idf(numDocs, numDocsTerm);
    }

    public VectorRankingModel(IndicePreCompModelo idxPrecomp) {
        this.idxPrecompVals = idxPrecomp;
    }

    /**
     * Retorna uma lista ordenada de documentos usando o tf xidf. Para isso,
     * para cada termo da consulta, calcule o w_iq (peso do termo i na consulta
     * q) e o w_ij e, logo apos, acumule o w_iq x w_ij no peso do documento
     * (dj_weight). Depois de ter o peso de cada documento, divida pela norma do
     * documento (use o idxPrecompVals.getNormaDocumento) Apos ter o peso para
     * cada documento, rode o UtilQuery.getOrderedList para retornar a lista de
     * documentos ordenados pela consulta
     * @param mapQueryOcur
     * @param lstOcorrPorTermoDocs
     * @return 
     */
    @Override
    public List<Integer> getOrderedDocs(Map<String, Ocorrencia> mapQueryOcur,Map<String, List<Ocorrencia>> lstOcorrPorTermoDocs) {

        Map<Integer, Double> dj_weight = new HashMap<>();

        
        Ocorrencia ocurQuery;
        double wij;
        double wiq;

        for (String term : mapQueryOcur.keySet()) {
            ocurQuery = mapQueryOcur.get(term);
            wiq = tfIdf(idxPrecompVals.getNumDocumentos(), ocurQuery.getFreq(), lstOcorrPorTermoDocs.get(term).size());
            
            
            for (Ocorrencia docOcur : lstOcorrPorTermoDocs.get(term)) {
                wij = tfIdf(idxPrecompVals.getNumDocumentos(), docOcur.getFreq(),lstOcorrPorTermoDocs.get(term).size());        
                double weight = wij * wiq;
                
                if(dj_weight.containsKey(docOcur.getDocId())){
                    Double previousWeight = dj_weight.get(docOcur.getDocId());
                    Double newWeight = previousWeight + weight;
                    dj_weight.put(docOcur.getDocId(), newWeight);
                    
                }else{
                    dj_weight.put(docOcur.getDocId(), weight);
                }
                
            }
        }
        
        for (Integer djId : dj_weight.keySet()) {
            Double novoPeso = dj_weight.get(djId) / idxPrecompVals.getNormaDocumento(djId);
            dj_weight.put(djId, novoPeso);
        }
   
        List<Integer> rank = UtilQuery.getOrderedList(dj_weight);
        return rank;
    }
   

}
