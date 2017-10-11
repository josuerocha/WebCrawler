package indice.estrutura;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Indice implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Retorna o número de documentos existentes neste índice
	 */
	public abstract int getNumDocumentos();

	/**
	 * Retorna a lista de termos (vocabulário) deste índice
	 */
	public abstract Set<String> getListTermos();
	
	/**
	 * Indexa um terminado termo que ocorreu freqTermo vezes em um determinado documento docId
	 * @param termo
	 * @param docId
	 * @param freqTermo
	 */
	public abstract void index(String termo,int docId,int freqTermo);
	
	/**
	 * Retorna, para cada termo, o número de documentos que este termo ocorreu.
	 * Exemplo:
	 * "casa"=>2
	 * "verde"=>10
	 * "vermelha"=>3
	 * Ou seja, o termo casa ocorreu em 2 documentos, 
	 * o termo verde em 10 documentos 
	 * e, o termo vermelha, ocorreu em 3 documentos
	 * 
	 * @return
	 */
	public abstract Map<String,Integer> getNumDocPerTerm();
	
	/**
	 * Retorna a lista de ocorrencias de um determinado termo.
	 *  
	 * Ou seja, indica a ocorrencia de um determinado termo retornando 
	 * uma lista em que cada item é uma tupla <docId, freq> indicando a ocorrencia 
	 * deste item no documento docId com a frequencia "freq". 
	 * @param termo
	 * @return
	 */
	public abstract List<Ocorrencia> getListOccur(String termo);

	/**
	 * Conclui a indexação, fazendo algum processamento (se necessário)
	 */
	public void concluiIndexacao(){
		
	}
	
	/**
	 * Grava o índice em arquivo 
	 */
	public void gravarIndice(File arq){
		
	}
	
	/**
	 * Le o indice em um determinado arquivo
	 * @param arq
	 * @return
	 */
	public static Indice leIndice(File arq){
		return null;
	}
	public String toString(){
		StringBuffer str = new StringBuffer();
		for(String termo : getListTermos()){
			str.append(termo+": ");
			for(Ocorrencia o : getListOccur(termo)){
				str.append("<"+o+">; ");
			}
			str.append("\n");
			
			
		}
		
		return str.toString();
	}
}
