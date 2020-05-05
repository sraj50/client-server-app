package com.monash.cmdlineparser;

public class CustomCmdLineParser {
	private String command;
	private String arg0;
	private String arg1;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getArg0() {
		return arg0;
	}

	public void setArg0(String arg0) {
		this.arg0 = arg0;
	}

	public String getArg1() {
		return arg1;
	}

	public void setArg1(String arg1) {
		this.arg1 = arg1;
	}

	public void parse(String msg) throws ParseException {
		String[] cmdArgs = msg.split(" ");
		if (cmdArgs[0].equals("connect") && cmdArgs.length == 3) {
			setCommand(cmdArgs[0]);
			setArg0(cmdArgs[1]);
			setArg1(cmdArgs[2]);
		} else if (cmdArgs[0].equals("get") && cmdArgs.length == 2) {
			setCommand(cmdArgs[0]);
			setArg0(cmdArgs[1]);
		} else if (cmdArgs[0].equals("put") && cmdArgs.length == 3) {
			setCommand(cmdArgs[0]);
			setArg0(cmdArgs[1]);
			setArg1(cmdArgs[2]);
		} else if (cmdArgs[0].equals("delete") && cmdArgs.length == 2) {
			setCommand(cmdArgs[0]);
			setArg0(cmdArgs[1]);
		} else if (cmdArgs[0].equals("disconnect") && cmdArgs.length == 1) {
			setCommand(cmdArgs[0]);
		} else {
			throw new ParseException("Incorrect number of arguments");
		}
	}

	public boolean hasCommand(String command) {
		if (getCommand().equals(command)) {
			return true;
		} else
			return false;
	}
}
