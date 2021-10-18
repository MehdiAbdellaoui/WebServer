package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class HttpServlet {

	private final static String METHOD_GET = "GET";
	private final static String METHOD_POST = "POST";

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
			// out.println("<H1>Welcome to the Ultra Mini-WebServer</H2>");
			out.flush();
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
	// + sign on the left to edit the code.">
	/**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
	public void doMethod(String request, Socket response) throws IOException {
		String[] parameters = request.split(" ");
		String method = parameters[0];
		String resource = parameters[1];
		
		switch(method) {
			case METHOD_GET:
				doGet(resource, response);
				break;
			
			case METHOD_POST:
				break;
		}
		
	}

	private void doGet(String request, Socket response) throws IOException {
		File file = new File(request.substring(1));
		String reader = "";
		BufferedReader br = new BufferedReader(new FileReader (file));
		String newLine = null;
		while ((newLine = br.readLine()) != null) {
			reader += newLine + System.lineSeparator();
		}
		processRequest(reader, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */

	/*
	 * protected void doPost(HttpServletRequest request, HttpServletResponse
	 * response) throws ServletException, IOException { processRequest(request,
	 * response); }
	 */

}
