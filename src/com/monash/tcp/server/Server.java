package com.monash.tcp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
//import java.net.SocketException;

//import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import com.monash.cmdlineparser.CmdLineValues;

public class Server {

	public static void main(String[] args) throws IOException {
		Thread client = null;
		ServerSocket serverSocket = null;
		// TODO Auto-generated method stub
		
		CmdLineValues values = new CmdLineValues();
		CmdLineParser parser = new CmdLineParser(values);
		
		try {
			parser.parseArgument(args);
			int port = values.getPort();
			serverSocket = new ServerSocket(port);
			System.out.println("Server is listening...");

			while (true) {
				// accept new connections to this socket, new socket for each connection
				Socket socket = serverSocket.accept();
				System.out.println("Client Connected!");

				// create thread per client
				client = new Thread(new ClientThread(socket));
				client.start();
				
				System.out.println(client.getState());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		/*catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CmdLineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */finally {
			if (serverSocket != null) {
				serverSocket.close();
			}
		}
	}

}
