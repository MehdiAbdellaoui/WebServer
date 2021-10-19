package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class HttpServlet {

	private final static String METHOD_GET = "GET";
	private final static String METHOD_POST = "POST";
	private final static String[] WEB_EXTENSIONS = {"html"} ;
	private static final String[] IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif"};

	protected void processRequest(String request, Socket response) throws IOException {
		try (PrintWriter out = new PrintWriter(response.getOutputStream())) {
			/* TODO output your page here. You may use following sample code. */

			// Send the response
			// Send the headers
			out.println("HTTP/1.0 200 OK");
			out.println("Content-Type: text/html");
			out.println("Server: Bot");
			// this blank line signals the end of the headers
			out.println("");
			// Send the HTML page
			out.println(request);
			out.flush();
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
	// + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	public void doMethod(String request, Socket response) throws IOException {
		if(request != null) {
		
			int positionFirstSpace = request.indexOf(" ");
			String method ="";
			String resource = "";
			if(positionFirstSpace > 0) {
				method = request.substring(0, positionFirstSpace);
				resource = request.substring(positionFirstSpace + 1);
			}
			switch(method) {
				case METHOD_GET:
					doGet(resource, response);
					break;

				case METHOD_POST:
					doPost(resource, response);
					break;
					
				}
			}
		}

	private void doGet(String request, Socket response) throws IOException {
		int positionFirstSpace = request.indexOf(' ') ;
		String path = request.substring(1, positionFirstSpace);
		String reader = "";
		try{
			File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader (file));
			
			String extension = getExtension(path) ;
			if(isExtensionOf(extension, WEB_EXTENSIONS)) {
				String newLine = null;
				while ((newLine = br.readLine()) != null) {
					reader += newLine + System.lineSeparator();
				}
			} else if(isExtensionOf(extension, IMAGE_EXTENSIONS)) {
				reader += "<img src=\"" + (new java.io.File(".").getCanonicalPath()) + '/' + path + "\" alt=\"image chargee\" />" + System.lineSeparator() ;
				System.out.println("---> " + reader);
			} else {
				reader = "<em>Erreur 422</em> unprocessable entity" ;
			}

			br.close();
			
		} catch(FileNotFoundException ex) {
			reader = "<em>Erreur 404</em> file not found at path -> " + path ;
		}
		processRequest(reader, response);
	}

	private boolean isExtensionOf(String extension, String[] webExtensions) {
		for(int i = 0 ; i < webExtensions.length ; i++) {
			if(webExtensions[i].equals(extension)) return true ;
		}
		return false;
	}

	private String getExtension(String str) {
		String reverseExtension = "" ;
		for(int i = str.length() - 1 ; i >= 0 && str.charAt(i) != '.' ; i--) {
			reverseExtension += str.charAt(i) ;
		}
		
		String extension = "" ;
		for(int i = reverseExtension.length() - 1 ; i >= 0 ; i--) {
			extension += reverseExtension.charAt(i) ;
		}
		
		return extension;
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */

	protected void doPost(String request, Socket response) throws IOException {
		System.out.println("POST Request :" + request);
		//System.out.println(request.indexOf("Content-Disposition:"));
		String tmpStr = request.substring(request.indexOf("name="));
		
		System.out.println(tmpStr);
		
		String[] parameters = tmpStr.split("name");

		for (String s : parameters) {
			System.out.println(s);
			//System.out.println(s.substring(0,s.indexOf("-")));
		}
		// processRequest(request, response);
	}

}
