package com.elevenquest.sol.upnp.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.StringTokenizer;

import com.elevenquest.sol.upnp.common.Logger;

/**
 * When to receive http request from client,we should parse and process it.
 * This class has capabilities of divide request int two parts - header parts & body parts -.
 * 
 * @author kyungpyo.park
 *
 */
public class HttpParser {
	
	InputStream inputStream = null;
	
	public HttpParser(InputStream is) {
		this.inputStream = is;
	}
	
	public HttpParser(byte[] contents) {
		this.inputStream = new ByteArrayInputStream(contents);
	}
	
	private String readLine() throws Exception {
		int curByte = -1;
		String rtn = null;
		ByteArrayOutputStream baos = null;
		boolean isPrevCharCr = false;
		try {
			baos = new ByteArrayOutputStream();
			while( ( curByte = this.inputStream.read() ) != -1 ) {
				if ( curByte == '\n' && isPrevCharCr )
					break;
				if ( curByte == '\r' )
					isPrevCharCr = true;
				else
					isPrevCharCr = false;
				baos.write(curByte);
			}
			if ( baos.size() == 0 && curByte == -1 )
				rtn = null;
			else
				rtn = baos.toString().trim();
		} finally {
			if ( baos != null ) try { baos.close(); } catch ( Exception e1 ) { e1.printStackTrace(); } 
		}
		Logger.println(Logger.DEBUG, "a line read from packet.:" + rtn);
		return rtn;
	}

	private byte[] getBody() throws Exception {
		ByteArrayOutputStream baos = null;
		byte[] rtn = null;
		try {
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = -1;
			while ( ( len = this.inputStream.read(buffer, 0, 1024)) != -1 ) {
				baos.write(buffer,0,len);
			}
			baos.flush();
			rtn = baos.toByteArray();
		} finally {
			if ( baos != null ) try { baos.close(); } catch ( Exception e1 )  { e1.printStackTrace(); }
		}
		return rtn;
	}
	
	private void close() {
		if ( inputStream != null ) try { inputStream.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
	}

	public HttpRequest parseHTTPRequest() throws Exception {
		
		HttpRequest request = new HttpRequest();
		String aLine = "";
		boolean isHeader = true;
		while ( ( aLine = readLine() ) != null ) {
			if ( aLine.trim().length() == 0 ) {		// Header / Body separated by knew line character.
				isHeader = false;
			}
			if ( isHeader) {
				String key = "";
				String value = "";
				for ( int pos = 0 ; pos < aLine.length() ; pos++ ) {
					if ( aLine.charAt(pos) == ':' )
						key = aLine.substring(0,pos);
						value = (pos + 1 < aLine.length()) ? aLine.substring(pos+1) : ""; 
				}
				// start line
				if ( key.length() == 0 ) {
					StringTokenizer st = new StringTokenizer(aLine, " ");
					boolean isValidRequest = true;
					if ( isValidRequest && st.hasMoreTokens() )
						request.setCommand(st.nextToken());
					else {
						isValidRequest = false;
						Logger.println(Logger.ERROR, "In http request, there is no url path. command is " + request.getCommand() + "." );
					}
					if ( isValidRequest && st.hasMoreTokens() )
						request.setUrlPath(st.nextToken());
					else {
						isValidRequest = false;
						Logger.println(Logger.ERROR, "In http request, there is no url path. command is " + request.getCommand() + "." );
					}
					if ( isValidRequest && st.hasMoreTokens() )
						request.setHttpVer(st.nextToken());
					else {
						isValidRequest = false;
						Logger.println(Logger.ERROR, "In http request, there is no url path. command is " + request.getCommand() + "." );
					}
				} else if ( key.length() > 0 ) {
					request.addHeader(key, value);
				}
			} else {
				request.setBodyArray(this.getBody());
			}
		}
		this.close();
		return request;
	}

	public HttpResponse parseHTTPResponse() throws Exception {
		
		HttpResponse response = new HttpResponse();
		String aLine = "";
		boolean isHeader = true;
		while ( ( aLine = readLine() ) != null ) {
			if ( aLine.trim().length() == 0 ) {		// Header / Body separated by knew line character.
				isHeader = false;
			}
			if ( isHeader) {
				String key = "";
				String value = "";
				for ( int pos = 0 ; pos < aLine.length() ; pos++ ) {
					if ( aLine.charAt(pos) == ':' )
						key = aLine.substring(0,pos);
						value = (pos + 1 < aLine.length()) ? aLine.substring(pos+1) : ""; 
				}
				// status code line
				if ( key.length() == 0 ) {
					StringTokenizer st = new StringTokenizer(aLine, " ");
					boolean isValidResponse = true;
					if ( isValidResponse && st.hasMoreTokens() )
						response.setHttpVer(st.nextToken());
					else {
						isValidResponse = false;
						Logger.println(Logger.ERROR, "In http response, there is no http version. status line is " + aLine + "." );
					}
					if ( isValidResponse && st.hasMoreTokens() ) {
						String strStatusCode = st.nextToken();
						try {
							int statusCode = Integer.parseInt(strStatusCode);
							response.setStatusCode(strStatusCode);
						} catch (NumberFormatException nfe) {
							isValidResponse = false;
							Logger.println(Logger.ERROR, "In http response, there is invalid status code. status code is "+ strStatusCode + ".");
						}
					}
					else {
						isValidResponse = false;
						Logger.println(Logger.ERROR, "In http response, there is no status code. status line is " + aLine + "." );
					}
					if ( isValidResponse && st.hasMoreTokens() )
						response.setReasonPhrase(st.nextToken());
					else {
						isValidResponse = false;
						Logger.println(Logger.ERROR, "In http response, there is status reason. status line is " + aLine + "." );
					}
				} else if ( key.length() > 0 ) {
					response.addHeader(key, value);
				}
			} else {
				response.setBodyArray(this.getBody());
			}
		}
		this.close();
		return response;
	}
}
