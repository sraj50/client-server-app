package com.monash.tcp.server;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
//import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
//import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//import org.apache.commons.io.FileUtils;
//import org.json.JSONException;
//import org.json.simple.JSONObject;
import org.json.JSONObject;

import com.monash.json.Jsonifier;
import com.monash.jhttp.JHTTPprotocol;
import com.monash.regex.PathChecker;
import com.monash.utils.Utils;

public class ClientThread implements Runnable {
	private Socket socket;
	private String currDir = "." + File.separator;
	private String rootFolderName = "www";
	private String rootFolder = currDir + rootFolderName;

	// constructor, takes socket as parameter
	public ClientThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		synchronized (this) {
			try {
				if (!Utils.isRootFolderExists(rootFolder)) {
					Utils.createFolder(rootFolder);
				}

				// input and output streams of socket
				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());

				String request = null;

				try {
					while (true) {

						try {
							// read input stream from client
							request = Utils.readBytesFromSocket(in);
							System.out.println("Message from client: " + request);
						} catch (SocketException e) {
							e.printStackTrace();
							System.out.println("Client disconnect abruptly, disconnecting...");
							break;
						}

						Jsonifier jsonifier = new Jsonifier();
						JSONObject jsonResponseObj = null;

						// get parameters from client json request
						jsonifier.deJsonifyServer(request);
						String type = jsonifier.getClientRequestType();
						String content = jsonifier.getClientRequestSource();
						String target = jsonifier.getClientRequestTarget();

						System.out.println("type: " + type);
						System.out.println("content: " + content);
						System.out.println("target: " + target);

						// GET request
						if (type.equals(JHTTPprotocol.GET_REQUEST)) { // GET request
							if (!PathChecker.isValidFilePath(target)) { // invalid file path
								// 401 bad request
								jsonifier.setServerResponseStatuscode(JHTTPprotocol.BAD_REQUEST_CODE);
								jsonifier.setServerResponseContent(JHTTPprotocol.BAD_REQUEST_CONTENT);
								jsonResponseObj = jsonifier.jsonifyServer(jsonifier.getServerResponseStatuscode(),
										jsonifier.getServerResponseContent());
							} else if (PathChecker.isValidFilePath(target)
									&& !Utils.isFileExists(Utils.getRelativePath(rootFolder, target))) { // valid file
																											// path and
																											// file does
																											// not exist
								// 400 not found
								jsonifier.setServerResponseStatuscode(JHTTPprotocol.GET_FAIL_CODE);
								jsonifier.setServerResponseContent(JHTTPprotocol.GET_FAIL_CONTENT);
								jsonResponseObj = jsonifier.jsonifyServer(jsonifier.getServerResponseStatuscode(),
										jsonifier.getServerResponseContent());
							} else if (PathChecker.isValidFilePath(target)
									&& Utils.isFileExists(Utils.getRelativePath(rootFolder, target))) { // valid file
																										// path and file
																										// exists
								// 200 Ok, content
								String fileContent = Files.readString(
										Paths.get(Utils.getRelativePath(rootFolder, target)), StandardCharsets.UTF_8);
								jsonifier.setServerResponseStatuscode(JHTTPprotocol.GET_SUCCESS_CODE);
								jsonifier.setServerResponseContent(fileContent);
								jsonResponseObj = jsonifier.jsonifyServer(jsonifier.getServerResponseStatuscode(),
										jsonifier.getServerResponseContent());
							} else { // 402 unknown error
								jsonifier.setServerResponseStatuscode(JHTTPprotocol.UNKNOWN_ERROR_CODE);
								jsonifier.setServerResponseContent(JHTTPprotocol.UNKNOWN_ERROR_CONTENT);
								jsonResponseObj = jsonifier.jsonifyServer(jsonifier.getServerResponseStatuscode(),
										jsonifier.getServerResponseContent());
							}
						} else if (type.equals(JHTTPprotocol.PUT_REQUEST)) { // PUT request
							if (!PathChecker.isValidFilePath(target)) { // invalid file path
								// 401 bad request
								jsonifier.setServerResponseStatuscode(JHTTPprotocol.BAD_REQUEST_CODE);
								jsonifier.setServerResponseContent(JHTTPprotocol.BAD_REQUEST_CONTENT);
								jsonResponseObj = jsonifier.jsonifyServer(jsonifier.getServerResponseStatuscode(),
										jsonifier.getServerResponseContent());
							} else if (PathChecker.isValidFilePath(target)
									&& !Utils.isFileExists(Utils.getRelativePath(rootFolder, target))) { // valid file
																											// path and
																											// file does
																											// not exist

								Path filePath = Paths.get(Utils.getRelativePath(rootFolder, target));
								Path parentDirs = filePath.getParent();

								System.out.println("filepath: " + filePath);
								System.out.println("parentDirs: " + parentDirs);

								// create parent directories
								Utils.createFolder(parentDirs.toString());

								// create file
								File file = new File(filePath.toString());
								BufferedWriter fileOut = new BufferedWriter(new FileWriter(file));
								fileOut.write(content);
								fileOut.close();

								// set response 201 Ok
								jsonifier.setServerResponseStatuscode(JHTTPprotocol.PUT_NEW_FILE_SUCCESS_CODE);
								jsonifier.setServerResponseContent(JHTTPprotocol.PUT_NEW_FILE_SUCCESS_CONTENT);
								jsonResponseObj = jsonifier.jsonifyServer(jsonifier.getServerResponseStatuscode(),
										jsonifier.getServerResponseContent());
							} else if (PathChecker.isValidFilePath(target)
									&& Utils.isFileExists(Utils.getRelativePath(rootFolder, target))) { // valid file
																										// path and file
																										// exists
								// just overwrite
								Path filePath = Paths.get(Utils.getRelativePath(rootFolder, target));
								File file = new File(filePath.toString());
								BufferedWriter fileOut = new BufferedWriter(new FileWriter(file));
								fileOut.write(content);
								fileOut.close();

								// set response 202 modified
								jsonifier.setServerResponseStatuscode(JHTTPprotocol.PUT_OVERWRITE_SUCCESS_CODE);
								jsonifier.setServerResponseContent(JHTTPprotocol.PUT_OVERWRITE_SUCCESS_CONTENT);
								jsonResponseObj = jsonifier.jsonifyServer(jsonifier.getServerResponseStatuscode(),
										jsonifier.getServerResponseContent());
							} else { // 402 unknown error
								jsonifier.setServerResponseStatuscode(JHTTPprotocol.UNKNOWN_ERROR_CODE);
								jsonifier.setServerResponseContent(JHTTPprotocol.UNKNOWN_ERROR_CONTENT);
								jsonResponseObj = jsonifier.jsonifyServer(jsonifier.getServerResponseStatuscode(),
										jsonifier.getServerResponseContent());
							}
						} else if (type.equals(JHTTPprotocol.DELETE_REQUEST)) { // DELETE request
							File file = new File(Utils.getRelativePath(rootFolder, target));

							if (Utils.isFile(file)) { // path a file
								if (!PathChecker.isValidFilePath(target)) { // invalid file path
									// 401 bad request
									jsonifier.setServerResponseStatuscode(JHTTPprotocol.BAD_REQUEST_CODE);
									jsonifier.setServerResponseContent(JHTTPprotocol.BAD_REQUEST_CONTENT);
									jsonResponseObj = jsonifier.jsonifyServer(jsonifier.getServerResponseStatuscode(),
											jsonifier.getServerResponseContent());
								} else if (PathChecker.isValidFilePath(target)
										&& !Utils.isFileExists(Utils.getRelativePath(rootFolder, target))) { // valid
																												// file
																												// path
																												// and
																												// file
																												// does
																												// not
																												// exist
									// 400 not found
									jsonifier.setServerResponseStatuscode(JHTTPprotocol.DELETE_FAIL_CODE);
									jsonifier.setServerResponseContent(JHTTPprotocol.DELETE_FAIL_CONTENT);
									jsonResponseObj = jsonifier.jsonifyServer(jsonifier.getServerResponseStatuscode(),
											jsonifier.getServerResponseContent());
								} else if (PathChecker.isValidFilePath(target)
										&& Utils.isFileExists(Utils.getRelativePath(rootFolder, target))) { // valid
																											// file path
																											// and file
																											// exists

									if (file.delete()) { // delete file
										// 203 Ok
										jsonifier.setServerResponseStatuscode(JHTTPprotocol.DELETE_SUCCESS_CODE);
										jsonifier.setServerResponseContent(JHTTPprotocol.DELETE_SUCCESS__CONTENT);
										jsonResponseObj = jsonifier.jsonifyServer(
												jsonifier.getServerResponseStatuscode(),
												jsonifier.getServerResponseContent());
									}
								}
							} else if (Utils.isDirectory(file)) { // path a folder
								System.out.println("is a directory");
								if (!PathChecker.isValidFolderPath(target)) { // invalid folder path
									// 401 bad request
									System.out.println("invalid folder path");
									jsonifier.setServerResponseStatuscode(JHTTPprotocol.BAD_REQUEST_CODE);
									jsonifier.setServerResponseContent(JHTTPprotocol.BAD_REQUEST_CONTENT);
									jsonResponseObj = jsonifier.jsonifyServer(jsonifier.getServerResponseStatuscode(),
											jsonifier.getServerResponseContent());
								} else if (PathChecker.isValidFolderPath(target)
										&& !Utils.isFileExists(Utils.getRelativePath(rootFolder, target))) { // valid
																												// file
																												// folder
																												// path
																												// but
																												// does
																												// not
																												// exist
									System.out.println("valid folder path and folder does not exist");
									// 400 not found
									jsonifier.setServerResponseStatuscode(JHTTPprotocol.DELETE_FAIL_CODE);
									jsonifier.setServerResponseContent(JHTTPprotocol.DELETE_FAIL_CONTENT);
									jsonResponseObj = jsonifier.jsonifyServer(jsonifier.getServerResponseStatuscode(),
											jsonifier.getServerResponseContent());
								} else if (PathChecker.isValidFolderPath(target)
										&& Utils.isFileExists(Utils.getRelativePath(rootFolder, target))) { // valid
																											// folder
																											// path
																											// and
																											// exists,
																											// perform
																											// delete
																											// operation
									System.out.println("valid folder path and file exists");
									File[] contents = file.listFiles();
									if (contents.length == 0) { // directory is empty
										System.out.println("directory is empty");
										if (file.delete()) { // delete directory
											jsonifier.setServerResponseStatuscode(JHTTPprotocol.DELETE_SUCCESS_CODE);
											jsonifier.setServerResponseContent(JHTTPprotocol.DELETE_SUCCESS__CONTENT);
											jsonResponseObj = jsonifier.jsonifyServer(
													jsonifier.getServerResponseStatuscode(),
													jsonifier.getServerResponseContent());
										} else { // unknown
											System.out.println("unknown error");
											jsonifier.setServerResponseStatuscode(JHTTPprotocol.UNKNOWN_ERROR_CODE);
											jsonifier.setServerResponseContent(JHTTPprotocol.UNKNOWN_ERROR_CONTENT);
											jsonResponseObj = jsonifier.jsonifyServer(
													jsonifier.getServerResponseStatuscode(),
													jsonifier.getServerResponseContent());
										}
									} else { // directory not empty
										// 402 unknown error
										System.out.println("directory is not empty, unknown error");
										jsonifier.setServerResponseStatuscode(JHTTPprotocol.UNKNOWN_ERROR_CODE);
										jsonifier.setServerResponseContent(JHTTPprotocol.UNKNOWN_ERROR_CONTENT);
										jsonResponseObj = jsonifier.jsonifyServer(
												jsonifier.getServerResponseStatuscode(),
												jsonifier.getServerResponseContent());
									}
								}
							} else { // unknown
								jsonifier.setServerResponseStatuscode(JHTTPprotocol.UNKNOWN_ERROR_CODE);
								jsonifier.setServerResponseContent(JHTTPprotocol.UNKNOWN_ERROR_CONTENT);
								jsonResponseObj = jsonifier.jsonifyServer(jsonifier.getServerResponseStatuscode(),
										jsonifier.getServerResponseContent());
							}
						} else if (type.equals(JHTTPprotocol.DISCONNECT_REQUEST)) {
							break;
						} else {
							// unknown
							jsonifier.setServerResponseStatuscode(JHTTPprotocol.UNKNOWN_ERROR_CODE);
							jsonifier.setServerResponseContent(JHTTPprotocol.UNKNOWN_ERROR_CONTENT);
							jsonResponseObj = jsonifier.jsonifyServer(jsonifier.getServerResponseStatuscode(),
									jsonifier.getServerResponseContent());
						}

						System.out.println("Server Response: " + String.valueOf(jsonResponseObj) + "\n");
						// write response to client in bytes
						out.write((String.valueOf(jsonResponseObj) + "\n").getBytes("UTF-8"));
						out.flush();
					}
					in.close();
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
					// if end of steam has reach unexpectedly and the socket is still open, close
					// the socket
					if (socket != null) {
						socket.close();
						System.out.println("Client disconnected");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
