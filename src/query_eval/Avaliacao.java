package query_eval;

import crawler.PrintColor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import util.ArquivoUtil;

public class Avaliacao {

    private double precisao[] = new double[4];
    private double revocacao[] = new double[4];
    private List<Integer> docsRelevantes = new ArrayList<>();

    public void preProcessa(String docsRelevPath) {

        File docsRelevFile = new File(docsRelevPath);
        String content = "";
        if (docsRelevFile.exists()) {
            try {
                content = ArquivoUtil.leTexto(docsRelevFile).trim();
                //System.out.println(PrintColor.PURPLE + content + PrintColor.RESET);
                String vector[] = content.split(",");
                for (String docId : vector) {
                    docsRelevantes.add(Integer.parseInt(docId));
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public void avalia(List<Integer> results) {
        List<Integer> fracaoResults;

        if (results.size() >= 5) {
            //Precisao @5
            fracaoResults = results.subList(0, 4);
            double retorno[] = calcula(fracaoResults);
            precisao[0] = retorno[0];
            revocacao[0] = retorno[1];
            //System.out.println("SAIU IF 1");

            if (results.size() >= 10) {
                //Precisao @10
                fracaoResults = results.subList(0, 9);
                retorno = calcula(fracaoResults);                
                precisao[1] = retorno[0];
                revocacao[1] = retorno[1];
                //System.out.println("SAIU IF 2");                

                if (results.size() >= 25) {
                    //Precisao @25
                    fracaoResults = results.subList(0, 24);
                    retorno = calcula(fracaoResults);
                    precisao[2] = retorno[0];
                    revocacao[2] = retorno[1];
                   // System.out.println("SAIU IF 3");

                    if (results.size() >= 50) {
                        //Precisao @50
                        fracaoResults = results.subList(0, 49);
                        retorno = calcula(fracaoResults);
                        precisao[3] = retorno[0];
                        revocacao[3] = retorno[1];
                        //System.out.println("SAIU IF 4");
                    }
                }
            }
        }
    }

    public double[] calcula(List<Integer> fracaoResults) {
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
