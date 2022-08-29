package com.tregouet.occamweb.modules.models;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class MatrixModel {
	
	private List<String> headers;
	private List<Row> rows= new ArrayList<>();
	private MathContext mathContext = new MathContext(3);
	
	public static class Row {
		private String iD;
		private List<String> columns = new ArrayList<>();
		
		public Row(String iD) {
			this.iD = iD;
		}
		
		
		public List<String> getColumns() {
			return columns;
		}
		
		public String getID() {
			return iD;
		}
	}

	public MatrixModel(List<String>headers, double[][] values) {
		this(headers);
		fill(values);
	}
	
	public MatrixModel(List<String>headers, String[][] values) {
		this(headers);
		fill(values);
	}

	private void fill(double[][] values) {
		for (int i = 0; i < values.length; i++) {
			Row row = new Row(headers.get(i));
			for (int j = 0; j < values[i].length; j++) {
				BigDecimal value = new BigDecimal(values[i][j]).round(mathContext);
				row.columns.add(value.toString());
			}
			rows.add(row);
		}
	}
	
	private void fill(String[][] values) {
		for (int i = 0 ; i < values.length ; i++) {
			Row row = new Row(headers.get(i));
			for (int j = 0 ; j < values[i].length ; j++) {
				row.columns.add(values[i][j]);
			}
			rows.add(row);
		}
	}
	
	
	public MatrixModel(List<String> headers) {
		this.headers = headers;
	}

	public List<String> getHeaders() {
		return headers;
	}
	
	public List<Row> getRows() {
		return rows;
	}
	
}
