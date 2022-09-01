package com.tregouet.occamweb.process.models;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.alg.util.UnorderedPair;

import com.tregouet.occam.data.modules.comparison.IComparator;
import com.tregouet.occam.data.structures.representations.classifications.concepts.IContextObject;

public class ComparisonModel {

	private MatrixModel simMatrix;
	private MatrixModel asymmSimMatrix;
	private VectorModel typicality;
	private MatrixModel difference;
	private UnorderedPair<Integer, Integer> comparedPair;

	public ComparisonModel(IComparator comparator) {
		List<String> headers = setHeaders(comparator);
		simMatrix = new MatrixModel(headers, comparator.getSimilarityStringMatrix());
		asymmSimMatrix = new MatrixModel(headers, comparator.getAsymmetricalSimilarityStringMatrix());
		typicality = new VectorModel(headers, comparator.getTypicalityVector());
		difference = new MatrixModel(headers, comparator.getDifferenceStringMatrix());
		comparedPair = comparator.getComparedPair();
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

	public VectorModel getTypicalityVector() {
		return typicality;
	}

	public MatrixModel getDifferenceMatrix() {
		return difference;
	}

	public UnorderedPair<Integer, Integer> getComparedPair(){
		return comparedPair;
	}

	public String getFirstObjectID() {
		if (comparedPair == null)
			return null;
		return comparedPair.getFirst().toString();
	}

	public String getSecondObjectID() {
		if (comparedPair == null)
			return null;
		return comparedPair.getSecond().toString();
	}

}
