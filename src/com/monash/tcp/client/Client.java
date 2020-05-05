package com.monash.tcp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.NoSuchFileException;
import java.util.Scanner;

import org.json.JSONObject;

import com.monash.jhttp.JHTTPprotocol;
import com.monash.json.Jsonifier;
import com.monash.cmdlineparser.CustomCmdLineParser;
import com.monash.cmdlineparser.ParseException;
import com.monash.utils.Utils;

public class Client {
	public static void main(String[] args) {
		try {
			Scanner cmdIn = new Scanner(System.in);
			CustomCmdLineParser parser = new CustomCmdLineParser();

			Socket socket = null;
			DataInputStream in = null;
			DataOutputStream out = null;

			while (true) {
				String inputStream = cmdIn.nextLine();

				try {
					parser.parse(inputStream);
				} catch (ParseException e) {
					continue;
				}

				if (parser.hasCommand("connect")) {
					try {
						String host = parser.getArg0();
						int port = Integer.parseInt(parser.getArg1());

						socket = new Socket(host, port);
						System.out.println("Successfully connected");

						in = new DataInputStream(socket.getInputStream());
						out = new DataOutputStream(socket.getOutputStream());
					} catch (Exception e) {
						System.out.println("No Server");
//						e.printStackTrace();
						continue;
					}
				} else if (parser.hasCommand("get")) {
					try {
						String type = JHTTPprotocol.GET_REQUEST;
						String target = parser.getArg0();
						JSONObject jsonRequestObj = Jsonifier.jsonifyClient(type, null, target);

						// write bytes to socket in UTF-8 format
						out.write((String.valueOf(jsonRequestObj) + "\n").getBytes("UTF-8"));

						// read response from server
						String response = Utils.readBytesFromSocket(in);
						String responseContent = Jsonifier.dejsonifyClient(response);
						System.out.println(responseContent);
					} catch (SocketException e) {
						break;
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				} else if (parser.hasCommand("put")) {
					try {
						String type = JHTTPprotocol.PUT_REQUEST;
						String srcFile = parser.getArg0();
						String target = parser.getArg1();

						String srcContent = Utils.getFileContents(srcFile);
						JSONObject jsonRequestObj = Jsonifier.jsonifyClient(type, srcContent, target);

						// write bytes to socket in UTF-8 format
						out.write((String.valueOf(jsonRequestObj) + "\n").getBytes("UTF-8"));

						// read response from server
						String response = Utils.readBytesFromSocket(in);
						String responseContent = Jsonifier.dejsonifyClient(response);
						System.out.println(responseContent);
					} catch (NoSuchFileException e) {
						System.out.println("Source file not found.");
//						e.printStackTrace();
						continue;
					} catch (SocketException e) {
						break;
					} catch (Exception e) {
//						e.printStackTrace();
						continue;
					}

				} else if (parser.hasCommand("delete")) {
					try {
						String type = JHTTPprotocol.DELETE_REQUEST;
						String target = parser.getArg0();
						JSONObject jsonRequestObj = Jsonifier.jsonifyClient(type, null, target);

						// write bytes to socket in UTF-8 format
						out.write((String.valueOf(jsonRequestObj) + "\n").getBytes("UTF-8"));

						// read response from server
						String response = Utils.readBytesFromSocket(in);
						String responseContent = Jsonifier.dejsonifyClient(response);
						System.out.println(responseContent);
					} catch (SocketException e) {
						break;
					} catch (Exception e) {
//						e.printStackTrace();
						continue;
					}

				}

				else if (parser.hasCommand("disconnect")) {
					try {
						String type = JHTTPprotocol.DISCONNECT_REQUEST;
						JSONObject jsonRequestObj = Jsonifier.jsonifyClient(type, null, null);

						// write to TCP socket output stream in UTF-8 format
						out.write((String.valueOf(jsonRequestObj) + "\n").getBytes("UTF-8"));
						System.out.println("Successfully disconnected");
						break;
					} catch (SocketException e) {
						break;
					} catch (Exception e) {
//						e.printStackTrace();
						continue;
					}
				}
			}
			in.close();
			out.close();
			socket.close();
			cmdIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
