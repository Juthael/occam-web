package com.tregouet.occamweb.problem.model;

import java.util.ArrayList;
import java.util.List;

public class Matrix {
	private List<String> headers;
	private List<Row> rows= new ArrayList<>();
	
	public static class Row {
		private List<String> columns =new ArrayList<>();
		
		
		public List<String> getColumns() {
			return columns;
		}
	}

	public Matrix(List<String>headers,double[][] values) {
		this(headers);
		fill(values);
	}

	private void fill(double[][] values) {
		for (int i = 0; i < values.length; i++) {
			Row row = new Row();
			for (int j = 0; j < values[i].length; j++) {
				row.columns.add(String.valueOf(values[i][j]));
			}
			rows.add(row);
		}
	}
	
	public Matrix(List<String>headers,double[] values) {
		this(headers);
		double [][]m= new double [1][values.length];
		m[0]=values;
		fill(m);
	}
	
	
	public Matrix(List<String> headers) {
		this.headers = headers;
	}

	public List<String> getHeaders() {
		return headers;
	}
	
	public List<Row> getRows() {
		return rows;
	}
	
}
