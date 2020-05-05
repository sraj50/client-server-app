package com.monash.json;

import org.json.JSONException;
import org.json.JSONObject;
import com.monash.jhttp.JHTTPprotocol;

public class Jsonifier {
	// client
	private String clientRequestType;
	private String clientRequestTarget;
	private String clientRequestSource;

	// server
	private String serverResponseStatuscode;
	private String serverResponseContent;

	public String getClientRequestSource() {
		return clientRequestSource;
	}

	public void setClientRequestSource(String clientRequestSource) {
		this.clientRequestSource = clientRequestSource;
	}

	public String getClientRequestTarget() {
		return clientRequestTarget;
	}

	public void setClientRequestTarget(String clientRequestTarget) {
		this.clientRequestTarget = clientRequestTarget;
	}

	public String getClientRequestType() {
		return clientRequestType;
	}

	public void setClientRequestType(String clientRequestType) {
		this.clientRequestType = clientRequestType;
	}

	public String getServerResponseStatuscode() {
		return serverResponseStatuscode;
	}

	public void setServerResponseStatuscode(String serverResponseStatuscode) {
		this.serverResponseStatuscode = serverResponseStatuscode;
	}

	public String getServerResponseContent() {
		return serverResponseContent;
	}

	public void setServerResponseContent(String serverResponseContent) {
		this.serverResponseContent = serverResponseContent;
	}

	public static JSONObject jsonifyClient(String type, String content, String target) throws JSONException {
		JSONObject obj = new JSONObject();
		if (type.equals(JHTTPprotocol.GET_REQUEST)) {
			obj.put("message", "request");
			obj.put("type", type);
			obj.put("target", target);
		} else if (type.equals(JHTTPprotocol.PUT_REQUEST)) {
			obj.put("message", "request");
			obj.put("type", type);
			obj.put("target", target);
			obj.put("content", content);
		} else if (type.equals(JHTTPprotocol.DELETE_REQUEST)) {
			obj.put("message", "request");
			obj.put("type", type);
			obj.put("target", target);
		} else if (type.equals(JHTTPprotocol.DISCONNECT_REQUEST)) {
			obj.put("message", "request");
			obj.put("type", type);
		}
		return obj;
	}

	public void deJsonifyServer(String jsonStr) throws JSONException {
		try {
			JSONObject obj = new JSONObject(jsonStr);

			String type = (String) obj.get("type");
			this.setClientRequestType(type);

			String target = (String) obj.get("target");
			this.setClientRequestTarget(target);

			String source = (String) obj.get("content");
			this.setClientRequestSource(source);

		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}

	public JSONObject jsonifyServer(String statuscode, String content) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("message", "response");
		obj.put("statuscode", statuscode);
		obj.put("content", content);
		return obj;
	}

	public static String dejsonifyClient(String jsonStr) throws JSONException {
		String content = null;
		try {
			JSONObject obj = new JSONObject(jsonStr);
			content = (String) obj.get("content");
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		return content;
	}
}
