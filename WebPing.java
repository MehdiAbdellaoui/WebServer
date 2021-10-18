package http.client;

import java.net.InetAddress;
import java.net.Socket;

public class WebPing {
	public static void main(String[] args) {

		String httpServerHost = "0.0.0.0";
		int httpServerPort = 80;

		try {
			InetAddress addr;
			Socket sock = new Socket(httpServerHost, httpServerPort);
			addr = sock.getInetAddress();
			System.out.println("Connected to " + addr);
			sock.close();
		} catch (java.io.IOException e) {
			System.out.println("Can't connect to " + httpServerHost + ":" + httpServerPort);
			System.out.println(e);
		}
	}
}