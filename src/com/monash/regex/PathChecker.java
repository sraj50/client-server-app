package com.monash.regex;

import java.util.regex.*;

public class PathChecker {
	public static boolean isValidFilePath(String path) {
		Pattern p = Pattern.compile("([\\/][a-zA-Z0-9]{1,20}){0,10}[\\/](([a-zA-Z0-9]){1,10}[.]([a-zA-Z0-9]{1,5}))");
		Matcher m = p.matcher(path);
		return m.matches();
	}

	public static boolean isValidFolderPath(String path) {
		Pattern p = Pattern.compile("([\\/][a-zA-Z0-9]{1,20}){1,10}[\\/]");
		Matcher m = p.matcher(path);
		return m.matches();
	}
}
