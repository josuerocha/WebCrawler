package crawler.escalonadorCurtoPrazo;

import com.trigonic.jrobotx.Record;

import crawler.URLAddress;

public interface Escalonador {
	
        
	/**
	 * Metodo para resgatar uma url. Não esquecer que, ao implementar esse método em que os PageFetcher são multithread, 
	 * voce deve implementar este metodo com o termo "synchronized", para utilizar o monitor e deixar
	 * esta classe threadsafe 
	 * @return url
	 */
	public URLAddress getURL();
	
	
	/** 
	 * Adiciona uma nova url ao escalonador. Não esquecer que, ao implementar esse método em que os PageFetcher são multithread, 
	 * voce deve implementar este metodo com o termo "synchronized", para utilizar o monitor e deixar
	 * esta classe threadsafe  
	 * @return adicionado
	 */
	public boolean adicionaNovaPagina(URLAddress urlAdd);
        
        /** 
	 * Adiciona uma nova url ao escalonador ignorando se a mesma está na fila de coletados. Utilizada em casos em que há problemas
         * de conexão e nova tentativa sera feita apos tempo determinado.
	 * @return adicionado
	 */
        public boolean adicionaNovaPaginaSemChecar(URLAddress urlAdd);

	/**
	 * Verifica que finalizou a coleta (pela quantidade de páginas coletadas)
	 */
	public boolean finalizouColeta();
	
	/**
	 * Contabiliza que foi coletada mais uma página.
	 * O PageFetcher que deverá executar este método sempre que uma pagina
	 * for coletada com sucesso.
	 * Este método deve ser threadsafe
	 */
	public void countFetchedPage();

	/**
	 * Retorna a lista do robots.txt do domínio de uma determinada pagina passada como parametro
	 * Caso não encontre esta lista, será retornado null. O PageFetcher é o responsável por criar um novo registro.
	 * @param url
	 * @return
	 */
	public Record getRecordAllowRobots(URLAddress url);

	/**
	 * Adiciona a lista do robots.txt (Record) de um determinado domínio 
	 * @param url
	 * @return
	 */
	public void putRecorded(String domain, Record domainRec);
        
        /**
	 * Adiciona URLs coletadas na em uma lista para posterior relatório de endereços. 
	 * @param url
	 * @return
	 */
        public void addCollectedURL(URLAddress url);
        
        
        /**
	 * Salva as URLs coletadas na em um arquivo. 
	 * @param filename
	 * @return
	 */
        public void saveToFile(String filename);
        

}
