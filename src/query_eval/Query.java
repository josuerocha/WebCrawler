package query_eval;

import crawler.PrintColor;
import indice.estrutura.Ocorrencia;
import indice.teste.Indexer;
import static indice.teste.Indexer.usedMemory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import ptstemmer.Stemmer;
import ptstemmer.exceptions.PTStemmerException;
import ptstemmer.implementations.OrengoStemmer;
import query_eval.BooleanRankingModel.OPERATOR;
import util.StringUtil;

public class Query {
    private List<String> resultsTitles;
    private static Stemmer ptStemmer;
    private Indexer indexer;
    private OPERATOR boolOperator = null;
    
    
    public void inicialize(int model, String query) {
        
        try {
            ptStemmer = new OrengoStemmer();
        } catch (PTStemmerException ex) {
            Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String wikipath = "dataset/wikiSample"; 
        Indexer indexer = new Indexer(wikipath);
        //Carregamento completo do índice
        indexer.inicialize();
        
        // Preprocessamento de valores necessários para o modelo vetorial e BM25
        //IndicePreCompModelo idxPrecomp = new IndicePreCompModelo(indexer.getIndice());
        
        //Preprocessamento dos documentos relevantes nas coleções de referência
            
        //Preprocessamento dos títulos por documentos
        String docsTitlesPath = "dataset/titlePerDoc.dat";
        indexer.getTitlePerDocs(docsTitlesPath);     

        // ciclotimia popolazione
        String terms[] = query.split("[\\W^ç]+");

        Map<String, Ocorrencia> mapQueryOcur = new HashMap<>();
        Map<String, List<Ocorrencia>> lstOcorrPorTermoDocs = new HashMap<>();

        for (String term : terms) {
            if (!StringUtil.isStopWord(term)) {
                term = StringUtil.replaceAcento(term);
                term = term.toLowerCase();
                term = ptStemmer.getWordStem(term);
                List<Ocorrencia> listOcur = indexer.getIndice().getListOccur(term);
                lstOcorrPorTermoDocs.put(term, listOcur);
            }
        }      
        
        RankingModel rank = null; 
        
        long initTime = System.currentTimeMillis();  
        switch(model){
            case 1: 
                    rank = new BooleanRankingModel(boolOperator);
                    break;
            case 2:
            //        rank = new VectorRankingModel(idxPrecomp);
                    break;
            case 3: 
                    double b = 0.75; int k = 1;
            //        rank = new BM25RankingModel(idxPrecomp, b, k);
                    break;
        }
        List<Integer> resultsIds = rank.getOrderedDocs(mapQueryOcur, lstOcorrPorTermoDocs);
        resultsTitles = indexer.getResultsTitles(resultsIds);
        long finalTime = System.currentTimeMillis();
        System.out.println(PrintColor.BLUE + "\n\n RESULTADOS" + PrintColor.RESET);
        
        for (String docTitle : resultsTitles) {
            System.out.println(docTitle);
        }
        
        System.out.println("Tempo de busca: " + ((finalTime - initTime) / 1000) + " s");
    }
    
    public List<String> getResults(){
        return resultsTitles;
    }
    
    public void setOperator(OPERATOR op){        
        this.boolOperator = op;
    }

}
