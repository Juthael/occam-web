package com.tregouet.occamweb.problem.models;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class VectorModel {
	
	private List<String> headers;
	private List<String> values = new ArrayList<>();
	private MathContext mathContext = new MathContext(3);
	
	public VectorModel(List<String> headers, double[] doubleVal) {
		this.headers = headers;
		for (double v : doubleVal) {
			BigDecimal value = new BigDecimal(v).round(mathContext);
			values.add(value.toString());
		}
	}
	
	public List<String> getHeaders(){
		return headers;
	}
	
	public List<String> getValues(){
		return values;
	}

}
