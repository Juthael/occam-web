package com.tregouet.occamweb.modules;

public class WorkerMessage {
	public static enum State {
		OK, ERROR, WARNING
	}

	private State state;
	private String message;

	public WorkerMessage(State state, String message) {
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
