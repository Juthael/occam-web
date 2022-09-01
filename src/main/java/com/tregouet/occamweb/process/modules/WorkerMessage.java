package com.tregouet.occamweb.process.modules;

public class WorkerMessage {
	public static enum StateType {
		OK, ERROR, WARNING
	}

	private StateType stateType;
	private String message;

	public WorkerMessage(StateType stateType, String message) {
		super();
		this.stateType = stateType;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public StateType getState() {
		return stateType;
	}
}
