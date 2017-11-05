package indice.estrutura;
import java.io.Serializable;


public class Ocorrencia implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int docId;
	private int freq;
	public Ocorrencia(int docId, int freq) {
		super();
		this.docId = docId;
		this.freq = freq;
	}	
	public int getDocId() {
		return docId;
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}

	public int getFreq() {
		return freq;
	}
	public void setFreq(int freq) {
		this.freq = freq;
	}

	public String toString()
	{
		return this.docId+","+this.freq;
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof Ocorrencia)
		{
			return this.docId == ((Ocorrencia) o).getDocId();
		}
		return false;
	}
	
}
