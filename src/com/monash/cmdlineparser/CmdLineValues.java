package com.monash.cmdlineparser;

import org.kohsuke.args4j.Option;

// command line options
public class CmdLineValues {
	@Option(required = true, name = "-p", aliases = { "--port" }, usage = "Port Address")
	private int port = 4444;

	public int getPort() {
		return port;
	}
}
