package query_eval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indice.estrutura.Indice;
import indice.estrutura.Ocorrencia;
import java.util.ArrayList;
import java.util.Collections;
import com.objectplanet.chart.*; 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.FileWriter;


public class IndicePreCompModelo {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int numDocumentos = 0;
    private double avgLenPerDocument = 0;
    private Map<Integer, Integer> tamPorDocumento = new HashMap<>();
    private Map<Integer, Double> normaPorDocumento = new HashMap<>();
    private Map<String,Double> termPerIdf = new HashMap<>();

    private Indice idx;

    public IndicePreCompModelo(Indice idx) {
        this.idx = idx;
        precomputeValues(idx);
        computeTermData();
    }
    

    /**
     * Acumula o (tfxidf)^2 de mais uma ocorrencia (oc) no somatorio para
     * calcular a norma por documento Usar a propria norma para acumular o
     * somatorio
     *
     * @param numDocsTerm
     * @param lstOcc
     * @param oc
     */
    public void updateSumSquaredForNorm(int numDocsTerm, Ocorrencia oc) {
        double tfidfSquared = Math.pow(VectorRankingModel.tfIdf(this.numDocumentos, oc.getFreq(), numDocsTerm), 2);
        if (normaPorDocumento.containsKey(oc.getDocId())) {
            normaPorDocumento.put(oc.getDocId(), normaPorDocumento.get(oc.getDocId()) + tfidfSquared);
        } else {
            normaPorDocumento.put(oc.getDocId(), tfidfSquared);
        }
    }
    
     /**
     * Calcula a frequencia e IDF de cada termo 
     */
    private void computeTermData(){
        List<TermData> termData = new ArrayList<>();
        
        for(String term : idx.getListTermos()){
            List<Ocorrencia> ocurrences = idx.getListOccur(term);
            int ni = ocurrences.size();
            double idf = VectorRankingModel.idf(numDocumentos,ni);
            double tf = 0;
            int freqTermo = 0;
            
            for(Ocorrencia occur : ocurrences){
                freqTermo += occur.getFreq();
                tf = VectorRankingModel.tf(occur.getFreq());
            }
            
            
            termData.add(new TermData(term,idf,freqTermo,tf));    
        }
        
        Collections.sort(termData);
        
        System.out.println("LOWER IDFs (LOWEST TO HIGHEST):");
        for(int i=0; i<13; i++){
            System.out.println("Termo: " + termData.get(i).getTerm() + " IDF: " + termData.get(i).getIdf());
        }
        
        System.out.println("MEDIUM IDFs (LOWEST TO HIGHEST):");
        for(int i=30000; i<30011; i++){
            System.out.println("Termo: " + termData.get(i).getTerm() + " IDF: " + termData.get(i).getIdf());
        }
        
        System.out.println("");
        System.out.println("HIGHER IDFs (LOWEST TO HIGHEST)");
        for(int i=termData.size()-11; i< termData.size()-1; i++){
            System.out.println("Termo: " + termData.get(i).getTerm() + " IDF: " + termData.get(i).getIdf());
        }
        
        TermData.orderByFrequency();
        
        Collections.sort(termData);
        
        System.out.println(" ");
        
        System.out.println("LOWER FREQUENCIES (LOWEST TO HIGHEST):");
        for(int i=0; i<10; i++){
            System.out.println("Termo: " + termData.get(i).getTerm() + " FREQ: " + termData.get(i).getGlobalFreq());
        }
        
        System.out.println("");
        System.out.println("HIGHER FREQUENCIES (LOWEST TO HIGHEST)");
        for(int i=termData.size()-11; i< termData.size()-1; i++){
            System.out.println("Termo: " + termData.get(i).getTerm() + " FREQ: " + termData.get(i).getGlobalFreq());
        }
        
        TermData.orderByTf();
        Collections.sort(termData);
        
        System.out.println("LOWER TFs (LOWEST TO HIGHEST):");
        for(int i=0; i<13; i++){
            System.out.println("Termo: " + termData.get(i).getTerm() + " IDF: " + termData.get(i).getTf());
        }
        
        System.out.println("MEDIUM TFs (LOWEST TO HIGHEST):");
        for(int i=termData.size()-2000; i< termData.size()-1991; i++){
            System.out.println("Termo: " + termData.get(i).getTerm() + " IDF: " + termData.get(i).getTf());
        }
        
        System.out.println("");
        System.out.println("HIGHER TFs (LOWEST TO HIGHEST)");
        for(int i=termData.size()-11; i< termData.size()-1; i++){
            System.out.println("Termo: " + termData.get(i).getTerm() + " IDF: " + termData.get(i).getTf());
        }
        
        saveToFile(termData);
        
    }
   
    
    /**
     * Atualiza o tamPorDocumento com mais uma cocorrencia
     *
     * @param oc
     */
    public void updateDocTam(Ocorrencia oc) {
        if (tamPorDocumento.containsKey(oc.getDocId())) {
            Integer count = tamPorDocumento.get(oc.getDocId());
            count += oc.getFreq();
            tamPorDocumento.put(oc.getDocId(), count);
        } else {
            tamPorDocumento.put(oc.getDocId(), oc.getFreq());
        }

    }

    /**
     * Inicializa os atributos por meio do indice (idx): numDocumentos: o numero
     * de documentos que o indice possui avgLenPerDocument: média do tamanho (em
     * palavras) dos documentos tamPorDocumento: para cada doc id, seu tamanho
     * (em palavras) - use o metodo updateDocTam para auxiliar
     * normaPorDocumento: A norma por documento (cada termo é presentado pelo
     * seu peso (tfxidf) - use o metodo updateSumSquaredForNorm para auxiliar
     *
     * @param idx
     */
    private void precomputeValues(Indice idx) {
        this.numDocumentos = idx.getNumDocumentos();

        for (String termo : idx.getListTermos()) {
            for (Ocorrencia occur : idx.getListOccur(termo)) {
                updateDocTam(occur);
                updateSumSquaredForNorm(idx.getListOccur(termo).size(), occur);
            }
            //System.out.println("SAIU FOR 0");

        }
        //System.out.println("SAIU FOR 1");

        for (Integer tamDoc : tamPorDocumento.keySet()) {
            this.avgLenPerDocument += tamDoc;
        }
        //System.out.println("SAIU FOR 2");
        this.avgLenPerDocument /= this.numDocumentos;

        for (Integer docid : normaPorDocumento.keySet()) {
            normaPorDocumento.put(docid, Math.sqrt(normaPorDocumento.get(docid)));
        }
        //System.out.println("SAIU FOR 3");

    }
    
    private void saveToFile(List<TermData> termData){
        BufferedWriter fileWriter = null;
        try{
            fileWriter = new BufferedWriter(new FileWriter("termdata.csv",false));
            
            int cont = 1;
            for(TermData data : termData){
                
                fileWriter.write( cont + "," + data.getGlobalFreq() + "," + data.getIdf());
                fileWriter.newLine();
                cont++;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public int getDocumentLength(int docId) {
        return this.tamPorDocumento.get(docId);
    }

    public int getNumDocumentos() {
        return numDocumentos;
    }

    public void setNumDocumentos(int numDocumentos) {
        this.numDocumentos = numDocumentos;
    }

    public double getAvgLenPerDocument() {
        return avgLenPerDocument;
    }

    public void setAvgLenPerDocument(double avgLenPerDocument) {
        this.avgLenPerDocument = avgLenPerDocument;
    }

    public Map<Integer, Double> getNormaPorDocumento() {
        return normaPorDocumento;
    }

    public void setNormaPorDocumento(Map<Integer, Double> normaPorDocumento) {
        this.normaPorDocumento = normaPorDocumento;
    }

    public double getNormaDocumento(int docId) {
        return this.normaPorDocumento.get(docId);
    }

}
