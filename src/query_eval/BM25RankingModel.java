package query_eval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indice.estrutura.Ocorrencia;

public class BM25RankingModel implements RankingModel {

    private IndicePreCompModelo idxPrecompVals;
    private double b;
    private int k1;

    public BM25RankingModel(IndicePreCompModelo idxPrecomp, double b, int k1) {
        this.idxPrecompVals = idxPrecomp;
        this.b = b;
        this.k1 = k1;
    }

    /**
     * Calcula o idf (adaptado) do bm25
     *
     * @param numDocs
     * @param numDocsArticle
     * @return
     */
    public double idf(int numDocs, int numDocsArticle) {
        return Math.log((numDocs - numDocsArticle + 0.5) / (numDocsArticle + 0.5));
    }

    /**
     * Calcula o beta_{i,j}
     *
     * @param freqTerm
     * @return
     */
    public double beta_ij(int docId, int freqTermDoc) {
        int docLength = this.idxPrecompVals.getDocumentLength(docId);
        double docsAvg = this.idxPrecompVals.getAvgLenPerDocument();
        return ((k1 + 1) * freqTermDoc) / (k1 * ((1 - b) + (b * docLength / docsAvg)) + freqTermDoc);
    }

    /**
     * Retorna a lista ordenada de documentos usando o modelo do BM25. para
     * isso, em dj_weight calcule o peso do documento j para a consulta. Para
     * cada termo, calcule o Beta_{i,j} e o idf e acumule o pesso desse termo
     * para o documento. Logo após, utilize UtilQuery.getOrderedList para
     * ordenar e retornar os docs ordenado mapQueryOcour: Lista de ocorrencia de
     * termos na consulta lstOcorrPorTermoDocs: Lista de ocorrencia dos termos
     * nos documentos (apenas termos que ocorrem na consulta)
     */
    @Override
    public List<Integer> getOrderedDocs(Map<String, Ocorrencia> mapQueryOcur,
            Map<String, List<Ocorrencia>> lstOcorrPorTermoDocs) {

        Map<Integer, Double> dj_weight = new HashMap<Integer, Double>();

        double idf;
        double bij;
        Ocorrencia ocur;
        for (String term : mapQueryOcur.keySet()) {
            ocur = mapQueryOcur.get(term);
            idf = idf(idxPrecompVals.getNumDocumentos(), lstOcorrPorTermoDocs.get(term).size());
            System.out.println("SAIU LINHA 1");

            for (Ocorrencia docOcur : lstOcorrPorTermoDocs.get(term)) {
                bij = beta_ij(docOcur.getDocId(), docOcur.getFreq());
                dj_weight.put(docOcur.getDocId(), bij * idf);
            }
            System.out.println("SAIU FOR 1");
        }
        System.out.println("SAIU FOR 2");

        return UtilQuery.getOrderedList(dj_weight);

    }

}
