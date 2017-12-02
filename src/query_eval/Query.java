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
import util.StringUtil;

public class Query {
    private static Stemmer ptStemmer;
    private Indexer indexer;
    public static void main(String[] args) {
        try {
            ptStemmer = new OrengoStemmer();
        } catch (PTStemmerException ex) {
            Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
        }        
        String wikipath = "dataset/wikiSample"; 
        Indexer indexer = new Indexer(wikipath);
        indexer.inicialize();

        String docsTitlesPath = "dataset/titlePerDoc.dat";
        indexer.getTitlePerDocs(docsTitlesPath);

        String query = "ciclotimia popolazione";
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
        Scanner scan = new Scanner(System.in);
        System.out.println(PrintColor.BLUE + "ESCOLHA O MODELO DE RANKING DESEJADO:");
        System.out.println("1 - BooleanRankingModel \t 2 - VectorRankingModel \t 3 - BM25RankingModel \n" + PrintColor.RESET);
        int model = scan.nextInt();
        long initTime = System.currentTimeMillis();  
        switch(model){
            case 1: rank = new BooleanRankingModel(BooleanRankingModel.OPERATOR.OR);
                    break;
           // case 2: IndicePreCompModelo idxPrecomp = new IndicePreCompModelo(indexer.getIndice());
           //         rank = new VectorRankingModel(idxPrecomp);
           //        break;
           // case 3: rank = new BM25RankingModel(idxPrecomp, model, model);
           //         break;
        }
        List<Integer> resultsIds = rank.getOrderedDocs(mapQueryOcur, lstOcorrPorTermoDocs);
        List<String> resultsTitles = indexer.getResultsTitles(resultsIds);
        long finalTime = System.currentTimeMillis();
        
        for (String docTitle : resultsTitles) {
            System.out.println(docTitle);
        }
        
        System.out.println("Execution time: " + ((finalTime - initTime) / 1000) + " s");
        

    }

}
