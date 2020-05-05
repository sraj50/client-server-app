package com.monash.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
	public static String readBytesFromSocket(DataInputStream in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte buffer[] = new byte[1024];
		baos.write(buffer, 0, in.read(buffer));
		String msg = baos.toString();
		return msg;
	}

	public static void createFolder(String folderName) {
		File dir = new File(folderName);
		dir.mkdirs();
	}

	public static boolean isRootFolderExists(String path) {
		File file = new File(path);
		if (file.exists()) {
			return true;
		} else
			return false;
	}

	public static String getRelativePath(String rootFolder, String path) {
		return rootFolder + File.separator + path;

	}

	public static boolean isFileExists(String path) {
		File file = new File(path);
		if (file.exists()) {
			return true;
		} else
			return false;
	}

	public static String getFileContents(String path) throws IOException, FileNotFoundException {
		String relativePath = "." + path;
		Path pathLocal = Paths.get(relativePath);
		String content = Files.readString(pathLocal, StandardCharsets.UTF_8);
		return content;
	}

	public static boolean isFile(File file) {
		if (file.getName().lastIndexOf(".") != -1) {
			return true;
		} else
			return false;
	}

	public static boolean isDirectory(File file) {
		if (file.getName().lastIndexOf(".") == -1) {
			return true;
		} else
			return false;
	}
}
