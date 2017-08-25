/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trigonic.jrobotx;

import static com.trigonic.jrobotx.Constants.HTTP;
import static com.trigonic.jrobotx.Constants.HTTPS;
import static com.trigonic.jrobotx.Constants.ROBOTS_TXT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.trigonic.jrobotx.util.DefaultURLInputStreamFactory;
import com.trigonic.jrobotx.util.URLInputStreamFactory;

public class RobotExclusion {
	private static final Logger LOG = Logger.getLogger(RobotExclusion.class);
	
	private static final Set<String> SUPPORTED_PROTOCOLS = new HashSet<String>(Arrays.asList(HTTP, HTTPS));
	
	private URLInputStreamFactory urlInputStreamFactory;
	
	public RobotExclusion(URLInputStreamFactory urlInputStreamFactory) {
		this.urlInputStreamFactory = urlInputStreamFactory;
	}
	
	public RobotExclusion() {
		this(new DefaultURLInputStreamFactory());
	}

	/**
	 * Get a robot exclusion {@link RecordIterator} for the server in the specified {@link URL}, or null if none is
	 * available. If the protocol is not supported--that is, not HTTP-based--null is returned.
	 */
	public RecordIterator get(URL url) {
		RecordIterator recordIter = null;
		
		if (!SUPPORTED_PROTOCOLS.contains(url.getProtocol().toLowerCase())) {
			return null;
		}

		try {
			// TODO: this should support error conditions as described in the protocol draft
			// TODO: use some kind of caching
			URL robotsUrl = new URL(url, ROBOTS_TXT);
			//URL robotsUrl = new URL("http://www2.dcc.ufmg.br/projetos/greenwiki/teste.txt");
			//System.out.println("Robots na url: "+robotsUrl.toString());
			 BufferedReader in = new BufferedReader(
				        new InputStreamReader(robotsUrl.openStream()));
			 			/*
				        String inputLine;
				        while ((inputLine = in.readLine()) != null)
				            System.out.println(inputLine);
				        in.close();
				        */
			recordIter = new RecordIterator(in);//urlInputStreamFactory.openStream(robotsUrl));
		} catch (IOException e) {
			LOG.info("Failed to fetch " + url, e);
		}

		return recordIter;
	}

	/**
	 * Get a robot exclusion {@link Record} for the specified {@link URL}, or null if none is available.  This uses {@link #get(URL)}
	 * and iterates through the {@link RecordIterator} to find a matching {@link Record}.
	 */
	public Record get(URL url, String userAgentString) {
		Record result = null;
		RecordIterator recordIter = get(url);
		if (recordIter != null) {
			while (recordIter.hasNext()) {
				Record record = recordIter.next();
				if (record.matches(userAgentString)) {
					result = record;
					break;
				}
			}
			if (result == null) {
				result = recordIter.getDefaultRecord();
			}
		}
		return result;
	}

	/**
	 * Determine whether the specified {@link URL} is allowed for the specified user agent string.  This uses {@link #get(URL, String)}
	 * and returns whether the matching record allows the {@link URL#getPath() path} specified in the URL.  
	 */
	public boolean allows(URL url, String userAgentString) {
	    // shortcut - /robots.txt might not exist, but it must be allowed
        if (Record.ruleMatches(ROBOTS_TXT, url.getFile())) {
            return true;
        }
            
		Record record = get(url, userAgentString);
		return record == null || record.allows(url.getPath());
	}
}
