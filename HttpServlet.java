package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class HttpServlet {

	private final static String METHOD_GET = "GET";
	private final static String METHOD_POST = "POST";
	private final static String[] TEXT_EXTENSIONS = {"html", "css", "csv", "javascript", "plain", "xml"} ;
	private static final String[] IMAGE_EXTENSIONS = {"jpeg", "png", "gif", "tiff", "vnd.microsoft.icon", "x-icon", "vnd.djvu", "svg+xml"};
	private static final String[] VIDEO_EXTENSIONS = {"mp4", "mpeg", "quicktime", "webm", "x-ms-wmv", "x-msvideo", "x-flv"};
	private static final String[] AUDIO_EXTENSIONS = {"mpeg", "x-ms-wma", "vnd.rn-realaudio", "x-wav"};
	private static final String[] APPLICATION_EXTENSIONS = {"java-archive", "EDI-X12", "EDIFACT", "javascript", "octet-stream", "ogg", "pdf",
			"xhtml+xml", "x-shockwave-flash", "json", "ld+json", "xml", "zip", "x-www-form-urlencoded "};

	protected void processRequest(String header, Object body, Socket response) throws IOException {
		if(body instanceof String) {
			processRequest(header, (String)body, response) ;
		} else if (body instanceof byte[]) {
			processRequest(header, (byte[])body, response) ;
		}
	}

	protected void processRequest(String header, String body, Socket response) throws IOException {
		try(PrintWriter out = new PrintWriter(response.getOutputStream())) {
			out.println(header);
			out.println("");
			out.println(body);
			out.flush();
		}
	}

	protected void processRequest(String header, byte[] body, Socket response) throws IOException {
		try (PrintWriter outHead = new PrintWriter(response.getOutputStream());
			OutputStream outBody = response.getOutputStream()) {
			outHead.println(header) ;
			outHead.println("");
			outBody.write(body);
			outHead.flush() ;
			outBody.flush();
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
		int positionFirstSpace = request.indexOf(' ') ;
		String method = request.substring(0, positionFirstSpace);
		String resource = request.substring(positionFirstSpace + 1);
		//System.out.println("method to do --> " + method) ;
		switch(method) {
			case METHOD_GET:
				doGet(resource, response);
				break;

			case METHOD_POST:
				doPost(resource, response);
				break;
			default:
				doError(501, response);
		}
	}
	
	private void doError(int nbError, Socket response) {
		String header = "HTTP/1.0 " + nbError + " NO" ;
		String body = "Error " + nbError + " --> " ;
		switch(nbError) {
			case 404 :
				body += "File not found error" ;
				break ;
			case 501 :
				body += "Unimplemented method" ;
				break ;
			case 422 :
				body += "Unprocessable entity" ;
				break ;
		}
		try {
			processRequest(header, body, response) ;
		} catch (IOException e) {
			e.printStackTrace();
		}
		}

	private void doGet(String request, Socket response) throws IOException {
		int positionFirstSpace = request.indexOf(' ') ;
		String path = request.substring(1, positionFirstSpace);
		//System.out.println("in get with adress --> " + path) ;
		String header = null ;
		Object body = null ;
		
		String extension = getExtension(path) ;
		try {
			if(isExtensionOf(extension, TEXT_EXTENSIONS)) {
				File file = new File(path);
				BufferedReader br = new BufferedReader(new FileReader (file));
				header = getHeader(200, "text", extension) ;
				String newLine = null;
				body = new String("") ;
				while ((newLine = br.readLine()) != null) {
					body += newLine + System.lineSeparator();
				}
				br.close();
			} else if(isExtensionOf(extension, IMAGE_EXTENSIONS)) {
				header = getHeader(200, "image", extension) ;
				File file = new File(path) ;
				body = Files.readAllBytes(file.toPath()) ;
	
				//reader += "<img src=\"" + path + "\" alt=\"image chargee\" />" + System.lineSeparator() ;
			} else if(isExtensionOf(extension, VIDEO_EXTENSIONS)) {
				header = getHeader(200, "video", extension) ;
				File file = new File(path) ;
				body = Files.readAllBytes(file.toPath()) ;
			} else if(isExtensionOf(extension, AUDIO_EXTENSIONS)) {
				header = getHeader(200, "audio", extension) ;
				File file = new File(path) ;
				body = Files.readAllBytes(file.toPath()) ;
			} else if(isExtensionOf(extension, APPLICATION_EXTENSIONS)) {
				header = getHeader(200, "application", extension) ;
				File file = new File(path) ;
				body = Files.readAllBytes(file.toPath()) ;
			}  else {
				doError(422, response);;
			}
		} catch (NoSuchFileException | FileNotFoundException ex) {
			ex.printStackTrace() ;
			doError(404, response) ;
		}
		
		processRequest(header, body, response) ;
	}

	private String getHeader(int nb, String type, String extension) {
		String header = "HTTP/1.0 " + nb + " OK" + System.lineSeparator() ;
		header += "Content-Type: " + type + "/" + extension + System.lineSeparator() ;
		header += "Server: Bot" + System.lineSeparator() ;
		return header ;
	}
	
	private String getPostHeader(int nb) {
		String header = "HTTP/1.1 " + nb + " Created" + System.lineSeparator();
		return header;
	}
	private String getPostHeader(int nb, String resourceETag, String redirectionPath) {
		String header = "HTTP/1.1 " + nb + " Created" + System.lineSeparator();
		header += "ETag: " + "\"" + resourceETag+ "\"" + System.lineSeparator();
		header += "Location: " + redirectionPath;
		return header;
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
		
		//Récupérer la valeur de boundary
		int boundaryIndex = request.indexOf("boundary=") + "boundary=--------------------------".length();
		String boundaryValue = request.substring(boundaryIndex,boundaryIndex + 24);
		//System.out.println(boundaryValue);
		
		//Parsing de la requête 
		String[] splitBoundary = request.substring(boundaryIndex + 24).split(boundaryValue); //24 x '-' 
		
		int i = 1;
		while (i < splitBoundary.length-1 )  {
			if(splitBoundary[i].startsWith("--")) break;
			String[] splitLine = splitBoundary[i].split(System.lineSeparator());
			String parameter = splitLine[1].split("; ")[1];
			String parameterName = parameter.substring(6, parameter.length() -1 );
			System.out.println(parameterName + " = " + splitLine[3]);
			i++;
		}
		System.out.println("After while");
		String header = getPostHeader(201,"tag","/src/web/pageWebTest.html");
		System.out.println(header);
		String body = "";
		processRequest(header,body, response);
	}

}
