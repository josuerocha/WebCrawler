package indice.estrutura;

import crawler.PrintColor;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IndiceLight extends Indice {

    /**
     * Versao - para gravação do arquivo binário
     */
    private static final long serialVersionUID = 1L;
    private int lastTermId = 0;

    private Map<String, PosicaoVetor> posicaoIndice;
    private int[] arrDocId;
    private int[] arrTermId;
    private int[] arrFreqTermo;

    /**
     * Ultimo indice (com algum valor valido) nos vetores
     */
    private int lastIdx = -1;

    /**
     * Armazena o ultimo id de termo criado. Utilizado para criar um id
     * incremental dos termos.
     */
    public int[] aumentaCapacidadeVetor(int[] vetor, double d) {

        int novoTamanho = (int) (vetor.length * (1.0 + d));
        int[] novoVetor = new int[novoTamanho];

        for (int i = 0; i < vetor.length; i++) {
            novoVetor[i] = vetor[i];
        }

        return novoVetor;
    }

    /**
     * Construtor da classe IndiceLight, ele inicializa os arrays com o valor do
     * initCap
     *
     * @param initCap
     * @return
     */
    public IndiceLight(int initCap) {

        arrDocId = new int[initCap];
        arrTermId = new int[initCap];
        arrFreqTermo = new int[initCap];
        posicaoIndice = new HashMap<String, PosicaoVetor>();
    }

    /**
     * Função que retorna o numero de documentos
     *
     * @param
     * @return int
     */
    @Override
    public int getNumDocumentos() {
        
        Set<Integer> docs = new HashSet<>();
        for (int i : arrDocId) {
            if (!docs.contains(i) && i != 0) {
                docs.add(i);
            }
        }
        
        return docs.size();
    }

    /**
     * Indexa um terminado termo que ocorreu freqTermo vezes em um determinado
     * documento docId. Armazene o novo termo na última posição do vetor (usando
     * o atributo lastIdx). Utilize o posicaoIndice para resgatar o id do termo.
     * Caso este id não exista, crie-o utilizando a variável lastTermId. Caso o
     * vetor já esteja no seu limite, você deve criar um vetor 10% maior e
     * realocar todos os elementos.
     *
     * **Sobre o Map posicaoIndice*** Você irá usar o Map posicaoIndice agora
     * apenas para definir/resgatar o id deste termo passado como parametro. Não
     * se preocupe em definir a posicao inicial no vetor (posInicial) nem o
     * número de documentos que este termo ocorreu (numOcorrencias). Estes dois
     * atributos (posInicial e numOcorrencias) só serão setados ao concluir a
     * indexação (i.e. no método concluiIndexacao), pois, ao concluir, o vetor
     * será devidamente ordenado.
     */
    @Override
    public void index(String termo, int docId, int freqTermo) {

        if (arrDocId.length == lastIdx + 1) {
            arrDocId = aumentaCapacidadeVetor(arrDocId, 1.0);
            arrTermId = aumentaCapacidadeVetor(arrTermId, 1.0);
            arrFreqTermo = aumentaCapacidadeVetor(arrFreqTermo, 1.0);
        }
        int termId = -1;
        if (posicaoIndice.containsKey(termo)) {
            termId = posicaoIndice.get(termo).getIdTermo();
        } else {
            posicaoIndice.put(termo, new PosicaoVetor(++lastTermId));
            termId = lastTermId;
        }
        lastIdx++;
        arrDocId[lastIdx] = docId;
        arrTermId[lastIdx] = termId;
        arrFreqTermo[lastIdx] = freqTermo;

    }

    /**
     * Função que retorna o numero de documentos por termo
     *
     * @param
     * @return Map<String, Integer>
     */
    @Override
    public Map<String, Integer> getNumDocPerTerm() {
        Map<String, Integer> numDocPerTerm = new HashMap<>();

        for (String str : posicaoIndice.keySet()) {
            numDocPerTerm.put(str, posicaoIndice.get(str).getNumDocumentos());
        }

        return numDocPerTerm;
    }

    /**
     * Função que retorna a lista de Termos
     *
     * @param
     * @return Set<String>
     */
    @Override
    public Set<String> getListTermos() {
        Set<String> termList = new HashSet<>();

        for (String str : posicaoIndice.keySet()) {
            termList.add(str);
        }

        return termList;
    }

    /**
     * Função que retorna a lista de Ocorrencias dado um termo
     *
     * @param termo
     * @return List<Ocorrencia>
     */
    @Override
    public List<Ocorrencia> getListOccur(String termo) {

        List<Ocorrencia> listOccur = new ArrayList<>();
        int initialPos = posicaoIndice.get(termo).getPosInicial();
        int qtdDocs = posicaoIndice.get(termo).getNumDocumentos();

        for (int i = initialPos; i < initialPos + qtdDocs; i++) {
            listOccur.add(new Ocorrencia(arrDocId[i], arrFreqTermo[i]));
        }

        return listOccur;
    }

    /**
     * Ao concluir a indexação, deve-se ordenar o indice de acordo com o id do
     * termo. Logo após, atualize a posicaoInicial e numOcorrencia de cada termo
 no Map posicaoIndice.

 Dica: ao percorrer os vetores, para saber qual instancia PosicaoVetor um
 id de termo se refere, crie um vetor que relaciona o id do termo (como
 indice) e a instancia PosicaoVetor que esta no mapa posicaoIndice.
 Percorra o mapa posicaoIndice para obter essa relação. Ou seja, cosidere
 que o PosicaoVetorIndexado é o vetor criado. Este vetor possuirá o tamanho
 lastTermId+1 (pois o id do termo é incremental) você povoará o este vetor
 da seguinte forma: para cada termo pertencente em posicaoIndice:
 PosicaoVetorIndexado[posicaoIndice.get(termo).getIdTermo()] =
 posicaoIndice.get(termo);
     *
     */
    @Override
    public void concluiIndexacao() {
        PosicaoVetor[] PosicaoVetorIndexado = new PosicaoVetor[lastTermId + 1];
        //Map<Integer,PosicaoVetor> idToPos = new HashMap<Integer,PosicaoVetor>();
        ordenaIndice();

        for (String termo : posicaoIndice.keySet()) {
            //idToPos.put(posicaoIndice.get(str).getIdTermo(), posicaoIndice.get(str));
            PosicaoVetorIndexado[posicaoIndice.get(termo).getIdTermo()] = posicaoIndice.get(termo);
        }
        /*
        System.out.println("SIZE: " + PosicaoVetorIndexado.length);
        //int posInicial = 0;
        
        for(PosicaoVetor aff : PosicaoVetorIndexado){
            if(aff != null){
                System.out.print(aff.getIdTermo() + " ");
            }else{
                System.out.print("null" + " ");
            }
            
        }
        System.out.println(" \n");
        System.out.println("ARRTERMID");
        
        for(int aff : arrTermId){
            System.out.print(aff + " ");
        }
        System.out.println(" \n");
        */
        int lastId = arrTermId[0];

        int numDocs = 1;
        int lastIterator = -1;
        int initPos = 0;
        for (int i = 1; i < arrTermId.length; i++) {
            //System.out.println("NUMDOCS " + numDocs + " Iter " + i);
            if(arrTermId[i] == 0){
                lastIterator = i;
                break;
            }else if (lastId == arrTermId[i]) {
                numDocs++;
            }
            else { 
                    
                    initPos = i-numDocs;
                    
                    PosicaoVetorIndexado[lastId].setNumDocumentos(numDocs);
                    PosicaoVetorIndexado[lastId].setPosInicial(initPos);
                    
                    //System.out.println("< " + initPos + " ," + numDocs + ">");
                    numDocs = 1;
                    if(arrTermId[i] != 0){
                        lastId = arrTermId[i];
                    }
                    lastIterator = i;
                    
            }
        }
        
        
        PosicaoVetorIndexado[lastId].setNumDocumentos(numDocs);
        PosicaoVetorIndexado[lastId].setPosInicial(lastIterator-numDocs);
        //System.out.println("< " + (lastIterator-numDocs) + " ," + numDocs + ">");
        
        saveToFile("indice.txt");
    }

    /**
     * Função que ordena o indice
     *
     * @param
     * @return
     */
    public void ordenaIndice() {
        quickSort(0, lastIdx);
        //insertionSort();

    }
////////////////////////////////////////////Algoritmo de Ordenação///////////////////////////////////////

    /**
     * Algoritmo qucksort baseado em Cormen et. al, Introduction to Algorithms e
     * adaptado para utilizar a partição com o pivot aleatório
     *
     * @param p
     * @param r
     */
    private void quickSort(int p, int r) {
        if (p < r) {
            //System.out.println("p: "+p+" r: "+r);
            int q = partition(p, r);
            quickSort(p, q - 1);
            quickSort(q + 1, r);
        }
    }

    private int partition(int p, int r) {
        //partição com pivot aleatório
        int pivot = (int) (p + Math.random() * (r - p));
        exchange(r, pivot);

        int i = p - 1;
        for (int j = p; j <= r - 1; j++) {
            if (compare(j, r) <= 0) {
                i = i + 1;
                exchange(i, j);
            }
        }
        exchange(i + 1, r);
        return i + 1;
    }

    /**
     * Usando os vetores do indice, Retorna >0 se posI>posJ <0 se posI<posJ 0,
     * caso contrário @param posI @param po
     *
     * s
     * J
     * @return
     */
    public int compare(int posI, int posJ) {
        //ordena primeirmente pelo termId
        if (this.arrTermId[posI] != this.arrTermId[posJ]) {
            return this.arrTermId[posI] - this.arrTermId[posJ];
        } else {
            return this.arrDocId[posI] - this.arrDocId[posJ];
        }
    }

    /**
     * Troca a posição dos vetores
     *
     * @param posI
     * @param posJ
     */
    public void exchange(int posI, int posJ) {
        int docAux = this.arrDocId[posI];
        int freqAux = this.arrFreqTermo[posI];
        int termAux = this.arrTermId[posI];

        this.arrDocId[posI] = this.arrDocId[posJ];
        this.arrFreqTermo[posI] = this.arrFreqTermo[posJ];
        this.arrTermId[posI] = this.arrTermId[posJ];

        this.arrDocId[posJ] = docAux;
        this.arrFreqTermo[posJ] = freqAux;
        this.arrTermId[posJ] = termAux;

    }
    
    public void saveToFile(String filename){
        BufferedWriter fileWriter = null;
        try{
            fileWriter = new BufferedWriter(new FileWriter(filename,false));
            
            int cont = 0;
            for(String key : posicaoIndice.keySet()){
                
                fileWriter.write( key + " ");
                PosicaoVetor posicaoVetor = posicaoIndice.get(key);
                fileWriter.write("<" + posicaoVetor.getIdTermo() + "," + posicaoVetor.getPosInicial() + "," + posicaoVetor.getNumDocumentos()+ ">");
                fileWriter.newLine();
            }
            
            fileWriter.newLine();
            fileWriter.write("DOCIDS ");
            for(int id : arrDocId){
                fileWriter.write(id + " ");
            }
            
            fileWriter.newLine();
            fileWriter.write("TERMIDS ");
            for(int id : arrTermId){
                fileWriter.write(id + " ");
            }
            
            fileWriter.newLine();
            fileWriter.write("FREQUENCY ");
            for(int id : arrFreqTermo){
                fileWriter.write(id + " ");
            }
            
            fileWriter.newLine();
            
            fileWriter.close();
        }
        catch(IOException ex){
            System.out.println(PrintColor.RED + "ERROR SAVING FILE" + PrintColor.RESET);
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
////////////////////////////////////////////FIM//////////////////////////////////////////////////////////

///////////////////////////// Funções Set e Get/////////////////////////////////////////////////////////
    public void setArrs(int[] arrDocId, int[] arrTermId, int[] arrFreqTermo) {
        this.arrDocId = Arrays.copyOf(arrDocId, arrDocId.length);
        this.arrTermId = Arrays.copyOf(arrTermId, arrTermId.length);
        this.arrFreqTermo = Arrays.copyOf(arrFreqTermo, arrFreqTermo.length);
        lastIdx = arrFreqTermo.length - 1;

    }

    public int[] getArrDocId() {
        return this.arrDocId;
    }

    public int[] getArrTermId() {
        return this.arrTermId;
    }

    public int[] getArrFreq() {
        return this.arrFreqTermo;
    }
////////////////////////////////////////////FIM//////////////////////////////////////////////////////////
}
