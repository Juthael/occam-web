package com.tregouet.occamweb.process.models;

import java.util.ArrayList;
import java.util.List;

import com.tregouet.occam.data.modules.comparison.IComparator;
import com.tregouet.occam.data.structures.representations.classifications.concepts.IContextObject;

public class ComparisonModel {
	
	private MatrixModel simMatrix;
	private MatrixModel asymmSimMatrix;
	private VectorModel typicality;
	private MatrixModel difference;
	
	public ComparisonModel(IComparator comparator) {
		List<String> headers = setHeaders(comparator);
		simMatrix = new MatrixModel(headers, comparator.getSimilarityStringMatrix());
		asymmSimMatrix = new MatrixModel(headers, comparator.getAsymmetricalSimilarityStringMatrix());
		typicality = new VectorModel(headers, comparator.getTypicalityVector());
		difference = new MatrixModel(headers, comparator.getDifferenceStringMatrix());
	}
	
	private List<String> setHeaders(IComparator comparator) {
		List<String> headers = new ArrayList<>();
		for (IContextObject obj : comparator.getContext())
			headers.add(Integer.toString(obj.iD()));
		return headers;
	}
	
	public MatrixModel getSimMatrix() {
		return simMatrix;
	}
	
	public MatrixModel getAsymmSimMatrix() {
		return asymmSimMatrix;
	}
	
	public VectorModel getTypicality() {
		return typicality;
	}
	
	public MatrixModel getDifference() {
		return difference;
	}

}
