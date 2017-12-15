package query_eval;

import crawler.PrintColor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import util.ArquivoUtil;
import util.StringUtil;

public class Avaliacao {

    private double precisao[] = new double[4];
    private double revocacao[] = new double[4];
    Map<String,Set<Integer>> relevantDocsMap = new HashMap<>();
    /**
     * Função que é executada ao iniciar a tela de consulta, 
     * ler os arquivos (BeloHorizonte.bat, Irlanda.bat e SaoPaulo.bat) e salva em uma lista.
     *
     * @param filepath
     * @param queryname
     * @return
     */
    public void preProcessa(String filepath,String queryname) {
        Set<Integer> docsRelevantes = new HashSet<>();
        
        File file = new File(filepath);
        String content;
        if (file.exists()) {
            try {
                content = ArquivoUtil.leTexto(file).trim();
                //System.out.println(PrintColor.PURPLE + content + PrintColor.RESET);
                String vector[] = content.split(",");
                for (String docId : vector) {
                    int docIdInt = Integer.parseInt(docId);
                    if (!docsRelevantes.contains(docIdInt)) {
                        docsRelevantes.add(docIdInt);
                    }
                }
 
               relevantDocsMap.put(queryname, docsRelevantes);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
    /**
     * Função que realiza a avaliação da precisão da lista de resultados.
     *
     * @param results
     * @return
     */
    public void avalia(List<Integer> results,String query) {
        
        if("irlanda".equals(query) || "sao paulo".equals(query) || "belo horizonte".equals(query)){
            List<Integer> fracaoResults;
            Set<Integer> relevantDocs;

            query = query.toLowerCase();
            query = StringUtil.replaceAcento(query);
            relevantDocs = relevantDocsMap.get(query);

            //Precisao @5
            fracaoResults = results.subList(0, 4);
            System.out.println("fracaoResults SIZE: " + fracaoResults.size());
            double retorno[] = calcula(fracaoResults,relevantDocs);
            precisao[0] = retorno[0];
            revocacao[0] = retorno[1];


            //Precisao @10
            fracaoResults = results.subList(0, 9);
            retorno = calcula(fracaoResults,relevantDocs);
            precisao[1] = retorno[0];
            revocacao[1] = retorno[1];


            //Precisao @25
            fracaoResults = results.subList(0, 24);
            retorno = calcula(fracaoResults,relevantDocs);
            precisao[2] = retorno[0];
            revocacao[2] = retorno[1];


            //Precisao @50
            fracaoResults = results.subList(0, 49);
            retorno = calcula(fracaoResults,relevantDocs);
            precisao[3] = retorno[0];
            revocacao[3] = retorno[1];
        }
    }
    /**
     * Calcula a precisão e revocação dada uma lista de resultados.
     *
     * @param fracaoResults
     * @return
     */
    public double[] calcula(List<Integer> fracaoResults,Set docsRelevantes) {
        List<Integer> intersection = new ArrayList<>();
        double retorno[] = new double[2];
        double precisao, revocacao;
        
        for (Integer docResult : fracaoResults) {
            if (docsRelevantes.contains(docResult)) {
                intersection.add(docResult);
            }
        }
        if (intersection.isEmpty()) {
            System.out.println("VAZIO");
            precisao = 0;
            revocacao = 0;
        } else {
            precisao = (double) intersection.size() / fracaoResults.size();
            revocacao = (double) intersection.size() / docsRelevantes.size();
        }
        retorno[0] = precisao;
        retorno[1] = revocacao;
        return retorno;
    }

    public double[] getPrecisao() {
        return precisao;
    }

    public double[] getRevocacao() {
        return revocacao;
    }
}
