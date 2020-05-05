package com.monash.jhttp;

public class JHTTPprotocol {

	// Requests
	public static final String GET_REQUEST = "GET";
	public static final String PUT_REQUEST = "PUT";
	public static final String DELETE_REQUEST = "DELETE";
	public static final String DISCONNECT_REQUEST = "DISCONNECT";

	// Responses

	// GET
	public static final String GET_SUCCESS_CODE = "200";
	public static final String GET_FAIL_CODE = "400";
	public static final String GET_FAIL_CONTENT = "Not Found";

	// PUT
	public static final String PUT_NEW_FILE_SUCCESS_CODE = "201";
	public static final String PUT_NEW_FILE_SUCCESS_CONTENT = "Ok";
	public static final String PUT_OVERWRITE_SUCCESS_CODE = "202";
	public static final String PUT_OVERWRITE_SUCCESS_CONTENT = "Modified";

	// DELETE
	public static final String DELETE_SUCCESS_CODE = "203";
	public static final String DELETE_SUCCESS__CONTENT = "Ok";
	public static final String DELETE_FAIL_CODE = "400";
	public static final String DELETE_FAIL_CONTENT = "Not Found";

	// Bad request
	public static final String BAD_REQUEST_CODE = "401";
	public static final String BAD_REQUEST_CONTENT = "Bad Request";

	// Unknown
	public static final String UNKNOWN_ERROR_CODE = "402";
	public static final String UNKNOWN_ERROR_CONTENT = "Unknown Error";
}
