package crawler;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.mozilla.universalchardet.UniversalDetector;

public class ColetorUtil {
	/**
	 * Retorna o input stream de uma url a ser baixada dado o userAgent e a url
	 * @param userAgent
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static InputStream getUrlStream(String userAgent,URL url) throws IOException
	{

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("User-Agent",userAgent);
		return conn.getInputStream();
	}
	
	/**
	 * Coleta a pagina de acordo com o encoding
	 * Extraido de: http://codingwiththomas.blogspot.com.br/2013/01/build-basic-crawler.html
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public static String consumeStream(InputStream stream) throws IOException {
		  try {
		    // setup the universal detector for charsets
		    UniversalDetector detector = new UniversalDetector(null);
		    ReadableByteChannel bc = Channels.newChannel(stream);
		    
		    
		    // 1mb is enough for every usual webpage
		    ByteBuffer buffer = ByteBuffer.allocate(1024*1024);
		    int read = 0;
		    while ((read = bc.read(buffer)) != -1) {
		      // let the detector work on the downloaded chunk
		      detector.handleData(buffer.array(), buffer.position() - read, read);
		      // check if we found a larger site, then resize the buffer
		      buffer = resizeBuffer(buffer);
		    }
		    // finish the sequence
		    detector.dataEnd();
		    
		    // copy the result back to a byte array
		    String encoding = detector.getDetectedCharset();
		    
		    // obtain the encoding, if null fall back to UTF-8
		    return new String(buffer.array(), 0, buffer.position(),
		        encoding == null ? "UTF-8" : encoding);
		  } finally {
		    if (stream != null) {
		      stream.close();
		    }
		  }
		}
	/**
	 * altera o buffer, se necessário
	 * Extraido de: http://codingwiththomas.blogspot.com.br/2013/01/build-basic-crawler.html
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private static ByteBuffer resizeBuffer(ByteBuffer buffer) {
	  ByteBuffer result = buffer;
	  // double the size if we have only 10% capacity left
	  if (buffer.remaining() < (int) (buffer.capacity() * 0.1f)) {
	    result = ByteBuffer.allocate(buffer.capacity() * 2);
	    buffer.flip();
	    result.put(buffer);
	  }
	  return result;
	}
	/**
	 * Verifica se uma url é relativa ou absoluta
	 * @param urlString
	 * @return
	 */
	public static boolean isAbsoluteURL(String urlString)
    {
        boolean result = false;
        try
        {
            URL url = new URL(urlString);
            String protocol = url.getProtocol();
            if (protocol != null && protocol.trim().length() > 0)
                result = true;
        }
        catch (MalformedURLException e)
        {
            return false;
        }
        return result;
    }

}
