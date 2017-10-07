package crawler;

import com.trigonic.jrobotx.Constants;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class URLAddress {

    private URL address;
    private int depth;
    private int attempts;
    
    /**
     *  Construtor da Classe, inicialmente verifica se no final da url esta apenas uma "/", 
     *  caso esteja, é retirado a "/" e adicionado a url ao Buffer, essa verificação é 
     *  feita devido a alguns sites que tem apenas essa "/" depois do link o que faz 
     *  com que ele seja igual ao site "original", logo para evitar que o coletor colete 
     *  a mesma pagina é feito essa formatação na url do link.
     *  Logo apos é verificado se a url inicia com "//", casos de urls mal formatadas, é inserido no
     *  inicio dela o padrão "http:".
     *  Caso a verificação acima for falsa, é feita uma outra verificação para saber se a url é absoluta, 
     *  caso ela não seja é concatenado à variavel buffer o dominio do servidor.
     *  No ultimo caso é quando o link já esta todo correto/formatado.
     *  Por fim, é criado criado um novo endereço com essa URL e calculado a profundidade da URL 
     *  no servidor.
     * 
     *
     * @param url,possibleDomain
     * @return
     */
    public URLAddress(String url, String possibleDomain) throws MalformedURLException {
        StringBuffer buffer = new StringBuffer();
        if(url.substring(url.length()-1).equals("/") ){
            buffer.append(url.substring(0, url.length()-1));
        }else{
            buffer.append(url);
        }
        
        if (url.length() > 1 && url.substring(0, 2).equals("//")) {
            buffer.insert(0,Constants.HTTP + ":");
        }else if (!isAbsoluteURL(url)) {
            buffer.insert(0,possibleDomain);
        } else{
            formatBuffer(buffer);
        }
        
        this.address = new URL(formatURL(buffer.toString()));
        this.depth = calculateDepth(this.address.getPath());
        this.attempts = 0;
    }
    /**
     *  Construtor extra criado para setar automaticamente a profundidade
     *
     * @param 
     * @return
     */
    public URLAddress(String url, int depth) throws MalformedURLException {

        this.address = new URL(formatURL(url));
        this.depth = depth;
    }
    
    
    /**
     * Formata a URL do link concatenando a string "http://" à url
     *
     * @param url
     * @return url
     */
    private String formatURL(String url) {
        if (!url.matches("[a-zA-Z]+://.*")) {
            url = "http://" + url;
        }

        return url;
    }
    /**
     * Formata a URL do link porém recebendo um Buffer, foi criado com o intuito 
     * de otimizar o processamento, para evitar a instanciação de um objeto extra na 
     * hora de realizar a concatenação das strings
     *
     * @param buff
     * @return buff
     */
    
    private StringBuffer formatBuffer(StringBuffer buff) {
        if (!buff.toString().matches("[a-zA-Z]+://.*")) {
            buff.insert(0,"http://");
        }

        return buff;
    }
    
    /**
     * Retorna o servidor de domínio do endereço fornecido
     *
     * @param address
     * @return URL
     */
    public String getDomain(String address) throws MalformedURLException {
        return new URL(formatURL(address)).getHost();
    }

    /**
     * Verifica se uma url é relativa ou absoluta
     *
     * @param urlString
     * @return
     */
    public boolean isAbsoluteURL(String urlString) {

        boolean result = false;
        try {
            URL url = new URL(urlString);
            String protocol = url.getProtocol();
            if (protocol != null && protocol.trim().length() > 0) {
                result = true;
            }
        } catch (MalformedURLException e) {
            return false;
        }
        return result;
    }

    public int getAttempts() {
        return attempts;
    }
    
    public String getProtocol() {
        return this.address.getProtocol();
    }

    public String getDomain() {
        return address.getHost();
    }

    public String getAddress() {
        return address.getProtocol() + "://" + address.getHost() + address.getFile();
    }

    public int getDepth() {
        return depth;
    }

    public String getPath() {
        // TODO Auto-generated method stub
        //return address.getPath().length()==0?"/":"";
        return address.getPath();
    }

    public URL getUrlRobotsTxt() throws MalformedURLException {
        return new URL(address.getProtocol() + "://" + address.getHost() + "/robots.txt");
    }

    public URL getUrlObj() {
        return this.address;
    }
    
    public void incrementAttempts(){
        this.attempts++;
    }

    public static void main(String[] args) throws MalformedURLException, UnknownHostException {
        URLAddress url1 = new URLAddress("http://www.terra.com.br/oioi/lala", 0);
        System.out.println("Dominio: " + url1.getDomain());
        System.out.println("Caminho: " + url1.getPath());
        System.out.println("Completo: " + url1.getAddress());

        URLAddress url2 = new URLAddress("esportes.terra.com.br/kaak/oioi", 0);
        System.out.println("Dominio: " + url2.getDomain());
        System.out.println("Caminho: " + url2.getPath());
        System.out.println("Completo: " + url2.getAddress());

        URL url = new URL("http://www.terra.com.br");
        System.out.println(url.getHost());
        InetAddress address = InetAddress.getByName(url.getHost());
        System.out.println(address.getHostAddress());

        url = new URL("http://esportes.terra.com.br");
        System.out.println(url.getHost());
        address = InetAddress.getByName(url.getHost());
        System.out.println(address.getHostAddress());
    }
    /**
     * Calcula a profundidade de um link dado domínio.
     *
     * @param path
     * @return count
     */
    private int calculateDepth(String path){
        
        int count = 0;
        for(char ch : path.toCharArray()){
            if(ch == '/'){
                count++;
            }
        }
        return count;
    }

    public String toString() {
        return address.toString();
    }
}
