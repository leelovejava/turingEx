package com.yami.trading.util.json;

public class JsonConvertException extends RuntimeException {
	public JsonConvertException(String message) {
		super(message);
	}

	public JsonConvertException(Throwable cause) {
		super(cause);
	}

	public JsonConvertException(String message, Throwable cause) {
		super(message, cause);
	}
}
