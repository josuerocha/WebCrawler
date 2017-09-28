package crawler;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class URLAddress {
	private URL address;
	private int depth;
	public URLAddress(String url,int depth) throws MalformedURLException
	{
		
		this.address =  new URL(formatURL(url));
		this.depth = depth;
	}
	public static String formatURL(String url)
	{
		if(!url.matches("[a-zA-Z]+://.*"))
		{
			url = "http://"+url;
		}
		
		return url;
	}
	public static String getDomain(String address) throws MalformedURLException
	{
		return new URL(formatURL(address)).getHost();
	}
	public String getProtocol()
	{
		return this.address.getProtocol();
	}
	public String getDomain()
	{
	
		return address.getHost();
	}
	public String getAddress() {
		return address.getProtocol()+"://"+address.getHost()+address.getFile();
	}
	public int getDepth() {
		return depth;
	}

	public String getPath() {
		// TODO Auto-generated method stub
		return address.getPath().length()==0?"/":"";
	}
	
	
	
	public static void main(String[] args) throws MalformedURLException, UnknownHostException
	{
		URLAddress url1 = new URLAddress("http://www.terra.com.br/oioi/lala", 0);
		System.out.println("Dominio: "+url1.getDomain());
		System.out.println("Caminho: "+url1.getPath());
		System.out.println("Completo: "+url1.getAddress());
		
		URLAddress url2 = new URLAddress("esportes.terra.com.br/kaak/oioi", 0);
		System.out.println("Dominio: "+url2.getDomain());
		System.out.println("Caminho: "+url2.getPath());
		System.out.println("Completo: "+url2.getAddress());
		
		URL url = new URL("http://www.terra.com.br");
		System.out.println(url.getHost());
		InetAddress address = InetAddress.getByName(url.getHost());
		System.out.println(address.getHostAddress());
		
		url = new URL("http://esportes.terra.com.br");
		System.out.println(url.getHost());
		address = InetAddress.getByName(url.getHost());
		System.out.println(address.getHostAddress());
	}
	public String toString()
	{
		return address.toString();
	}
}
