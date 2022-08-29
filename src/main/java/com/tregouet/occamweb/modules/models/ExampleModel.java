package com.tregouet.occamweb.modules.models;

public class ExampleModel {
	private String name;
	private int position;

	public ExampleModel(String name, int position) {
		super();
		this.name = name;
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public int getPosition() {
		return position;
	}

}
