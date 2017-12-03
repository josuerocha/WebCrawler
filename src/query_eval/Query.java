package query_eval;

import InterfaceGrafica.TelaConsulta;
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
    private List<Integer> resultsIds;
    private static Stemmer ptStemmer;
    private Indexer indexer;
    private OPERATOR boolOperator = null;
    private IndicePreCompModelo idxPrecomp;
    private Avaliacao avaliacao = new Avaliacao();
    
    public void preprocess(){
        try {
            ptStemmer = new OrengoStemmer();
        } catch (PTStemmerException ex) {
            Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String wikipath = "dataset/wikiSample"; 
        indexer = new Indexer(wikipath);
        //Carregamento completo do índice
        indexer.inicialize();
        
        // Preprocessamento de valores necessários para o modelo vetorial e BM25
        idxPrecomp = new IndicePreCompModelo(indexer.getIndice());
        
        //Preprocessamento dos documentos relevantes nas coleções de referência
        String docsRelevantesIrlanda = "dataset/docsRelevantes/Irlanda.dat";
        avaliacao.preProcessa(docsRelevantesIrlanda);
        String docsRelevantesBh = "dataset/docsRelevantes/Belo Horizonte.dat";
        avaliacao.preProcessa(docsRelevantesBh);
        String docsRelevantesSp = "dataset/docsRelevantes/São Paulo.dat";
        avaliacao.preProcessa(docsRelevantesSp);
        
        //Preprocessamento dos títulos por documentos
        String docsTitlesPath = "dataset/titlePerDoc.dat";
        indexer.getTitlePerDocs(docsTitlesPath);   
    }
    
    
    public void start(int model, String query) {
                
        // ciclotimia popolazione
        String terms[] = query.split("[\\W^ç]+");

        Map<String, Ocorrencia> mapQueryOcur = new HashMap<>();
        Map<String, List<Ocorrencia>> lstOcorrPorTermoDocs = new HashMap<>();
        int count = 0;
        for (String term : terms) {
            count = 0;
            if (!StringUtil.isStopWord(term)) {
                term = StringUtil.replaceAcento(term);
                term = term.toLowerCase();
                term = ptStemmer.getWordStem(term);
                if(!mapQueryOcur.containsKey(term)){
                //docid = -1 -> consulta
                    mapQueryOcur.put(term, new Ocorrencia(1,-1));                    
                }else{
                    count = mapQueryOcur.get(term).getFreq() + 1;
                    mapQueryOcur.get(term).setFreq(count);
                }
                System.out.println(PrintColor.RED + "termo: " + term + PrintColor.RESET);
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
                    rank = new VectorRankingModel(idxPrecomp);
                    break;
            case 3: 
                    double b = 0.75; int k = 1;
                    rank = new BM25RankingModel(idxPrecomp, b, k);
                    break;
        }
        resultsIds = rank.getOrderedDocs(mapQueryOcur, lstOcorrPorTermoDocs);
        resultsTitles = indexer.getResultsTitles(resultsIds);
        long finalTime = System.currentTimeMillis();
        //System.out.println(PrintColor.BLUE + "\n\n RESULTADOS" + PrintColor.RESET);
        
        //for (String docTitle : resultsTitles) {
        //    System.out.println(docTitle);
        //}        
        System.out.println(PrintColor.GREEN + "Tempo de busca: " + ((finalTime - initTime) / 1000) + " s" + PrintColor.RESET);
        
        avaliacao.avalia(resultsIds);
        System.out.print(PrintColor.BLUE + "Precisão: " + avaliacao.getPrecisao()[0] + "\t");
        System.out.print(PrintColor.BLUE + "Precisão: " + avaliacao.getPrecisao()[1] + "\t");
        System.out.print(PrintColor.BLUE + "Precisão: " + avaliacao.getPrecisao()[2] + "\t");
        //System.out.println(PrintColor.RED + "Precisão: " + avaliacao.getPrecisao()[3] + "\t" + PrintColor.RESET);
        
        System.out.print(PrintColor.GREEN + "Revocacao: " + avaliacao.getRevocacao()[0] + "\t");
        System.out.print(PrintColor.GREEN + "Revocacao: " + avaliacao.getRevocacao()[1] + "\t");
        System.out.println(PrintColor.GREEN + "Revocacao: " + avaliacao.getRevocacao()[2] + "\t");
        //System.out.println(PrintColor.GREEN + "Revocacao: " + avaliacao.getRevocacao()[3] + "\t" + PrintColor.RESET);
        System.out.println(PrintColor.RED + "_______________________________________________________________________" + PrintColor.RESET);
    }
    
    public Avaliacao getAvaliacao(){
        return avaliacao;
    }
    
    public List<String> getResults(){
        return resultsTitles;
    }
    
    public void setOperator(OPERATOR op){        
        this.boolOperator = op;
    }

}
