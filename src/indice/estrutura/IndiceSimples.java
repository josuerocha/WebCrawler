package indice.estrutura;

import crawler.PrintColor;
import crawler.URLAddress;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IndiceSimples extends Indice {

    /**
     * Versao - para gravação do arquivo binário
     */
    private static final long serialVersionUID = 1L;

    private Map<String, List<Ocorrencia>> mapIndice = new HashMap<>();
    /**
     * Construtor da classe IndiceSimples
     *
     * @param 
     * @return
     */
    public IndiceSimples() {

    }
    
    @Override
    public void index(String termo, int docId, int freqTermo) {
        if (!mapIndice.containsKey(termo)) {
            List<Ocorrencia> list = new ArrayList<>();
            list.add(new Ocorrencia(docId, freqTermo));
            mapIndice.put(termo, list);
        } else {
            mapIndice.get(termo).add(new Ocorrencia(docId, freqTermo));
        }
    }
    /**
     * Faz uma busca do termo no map de indices e retorna um Map com o numero de Documentos por Termo
     *
     * @param 
     * @return Map<String, Integer>
     */
    @Override
    public Map<String, Integer> getNumDocPerTerm() {

        Map<String, Integer> map = new HashMap<>();
        for (String termo : mapIndice.keySet()) {
            map.put(termo, mapIndice.get(termo).size());
        }
        return map;

    }
    /**
     * Faz uma busca do termo no map de indices para cada ocorrencia ele busca na lista de ocorrencias
     * se o id da ocorrencia nao estiver adicionado no Documento, então ele adiciona. Por fim, retorna 
     * o tamanho do Documento.
     *
     * @param 
     * @return setDocumento.size()
     */
    @Override
    public int getNumDocumentos() {

        Set<Integer> setDocumento = new HashSet<>();

        for (String termo : mapIndice.keySet()) {

            List<Ocorrencia> listOcorrencia = mapIndice.get(termo);
            for (Ocorrencia ocorrencia : listOcorrencia) {

                if (!setDocumento.contains(ocorrencia.getDocId())) {
                    setDocumento.add(ocorrencia.getDocId());
                }
            }
        }

        return setDocumento.size();
    }
    /**
     * Faz uma busca do termo no map de indices e adiciona no Hash o termo, por fim retorna o Hash da 
     * lista de termos.
     *
     * @param 
     * @return Set<String> set
     */
    @Override
    public Set<String> getListTermos() {

        Set<String> set = new HashSet<>();
        for (String termo : mapIndice.keySet()) {
            set.add(termo);
        }
        return set;
    }
    /**
     * Retorna a lista de ocorrencia dado um termo.
     *
     * @param 
     * @return List<Ocorrencia> mapIndice
     */
    @Override
    public List<Ocorrencia> getListOccur(String termo) {
        return mapIndice.get(termo);
    }
    
    @Override
    public void saveToFile(String filename){
        BufferedWriter fileWriter = null;
        try{
            fileWriter = new BufferedWriter(new FileWriter(filename,false));
            
            int cont = 0;
            for(String key : mapIndice.keySet()){
                
                fileWriter.write( key + " ");
                for(Ocorrencia occur : mapIndice.get(key)){
                    fileWriter.write("<" + occur.getDocId() + "," + occur.getFreq() + "> ");
                }
                
                fileWriter.newLine();
            }
            fileWriter.close();
        }
        catch(IOException ex){
            System.out.println(PrintColor.RED + "ERROR SAVING FILE" + PrintColor.RESET);
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

}
