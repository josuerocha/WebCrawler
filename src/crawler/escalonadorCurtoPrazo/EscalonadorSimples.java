package crawler.escalonadorCurtoPrazo;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.trigonic.jrobotx.Record;
import com.trigonic.jrobotx.RobotExclusion;

import crawler.Servidor;
import crawler.URLAddress;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import util.PrintColor;

public class EscalonadorSimples implements Escalonador {
    //CONSTANTS
    static final int MAXPROFUNDIDADE = 4;
    static final int LIMITE_PAGINAS = 500;
    static final int SERVER_ACCESS_PAUSE = 30000;
    
    //VARIABLES
    private int contadorPaginas;
    public Set<String> pagVisitada = new HashSet<>();
    public Map<Servidor, List<URLAddress>> fila = new LinkedHashMap<>();
    public Map<String, Record> mapRobots = new HashMap<>();
    public List<URLAddress> collectedURL = new ArrayList<>();

    
    /**
     *  Construtor da Classe, ele tenta adicionar cada URL que esta no vetor mandado a ele na fila de 
     *  URLs.
     * 
     *
     * @param seeds
     * @return
     */
    public EscalonadorSimples(String[] seeds) throws MalformedURLException {
        for (String url : seeds) {
            this.adicionaNovaPagina(new URLAddress(url, 1));
        }
    }
    
    
    /**
     *  Coletar as URL da pagina. Enquanto não finalizar a coleta as threads iram 
     *  acessar a lista de servidor, desse modo a pagina será adicionada as paginas 
     *  visitadas e retornará uma URL caso ela não tenha sido adicionada. Tudo respeitando a 
     *  politica de boas maneiras.
     *
     * @param 
     * @return url
     */
    @Override
    public synchronized URLAddress getURL() {
        URLAddress url = null;
        while (!this.finalizouColeta()) {
            try {
                for (Servidor s : fila.keySet()) {
                    if (s.getTimeSinceLastAcess() >= SERVER_ACCESS_PAUSE) {
                        List<URLAddress> urlList = fila.get(s);
                        if (!urlList.isEmpty()) {
                            url = urlList.remove(0);
                            pagVisitada.add(url.toString());
                            s.acessadoAgora();
                            return url;
                        }
                    }
                }

                wait(1000);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        return url;
    }
    
    /**
     *  Verifica se a urlAdd fornecida já foi visitada, caso não tenha sido, ela é adicionada
     *  a um novo servidor e verificada se já foi adicionada a lista de URLs, caso não tenha sido
     *  ela é adicionada.
     *
     * @param urlAdd
     * @return boolean
     */
    @Override
    public synchronized boolean adicionaNovaPagina(URLAddress urlAdd) {
        // TODO Auto-generated method stub
        if (!pagVisitada.contains(urlAdd.toString()) && urlAdd.getDepth() < MAXPROFUNDIDADE) {
            Servidor servidor = new Servidor(urlAdd.getDomain());
            if (!fila.containsKey(servidor)) {
                List<URLAddress> lista = new ArrayList<>();
                lista.add(urlAdd);
                fila.put(servidor, lista);
            } else {
                fila.get(servidor).add(urlAdd);
            }
            notifyAll();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public synchronized Record getRecordAllowRobots(URLAddress url) {

        return mapRobots.get(url.getDomain());
    }

    @Override
    public synchronized void putRecorded(String domain, Record domainRec) {
        mapRobots.put(domain, domainRec);
    }

    @Override
    public boolean finalizouColeta() {
        return contadorPaginas >= LIMITE_PAGINAS;
    }

    public synchronized void addCollectedURL(URLAddress url){
        collectedURL.add(url);
    }
    
    @Override
    public synchronized void countFetchedPage() {
        contadorPaginas++;

    }
    /**
     * Função que salva a lista de URLs em um arquivo em disco.
     *
     * @param filename
     * @return 
     */
    public synchronized void saveToFile(String filename){
        BufferedWriter fileWriter = null;
        try{
            fileWriter = new BufferedWriter(new FileWriter(filename,false));
            
            int cont = 0;
            for(URLAddress url : collectedURL){
                
                fileWriter.write(url.getAddress() );
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
