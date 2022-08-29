package com.tregouet.occamweb.modules;

public class SorterMessage {
	public static enum State {
		OK, ERROR, WARNING
	}

	private State state;
	private String message;

	public SorterMessage(State state, String message) {
		super();
		this.state = state;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public State getState() {
		return state;
	}
}
