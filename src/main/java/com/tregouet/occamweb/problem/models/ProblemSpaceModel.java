package com.tregouet.occamweb.problem.models;

import java.util.ArrayList;
import java.util.List;

import com.tregouet.occam.data.problem_space.IProblemSpace;
import com.tregouet.occam.data.problem_space.states.classifications.concepts.IContextObject;

public class ProblemSpaceModel {

	private MatrixModel similarity;
	private MatrixModel similarityRef;
	private MatrixModel asymmetricalSimilarity;
	private VectorModel typicality;
	private MatrixModel difference;

	public ProblemSpaceModel(IProblemSpace problemSpace) {
		super();
		List<String> smh = new ArrayList<>();
		for (IContextObject obj : problemSpace.getContext()) {
			smh.add(Integer.toString(obj.iD()));
		}
		similarity = new MatrixModel(smh, problemSpace.getSimilarityMatrix());
		similarityRef = new MatrixModel(smh, problemSpace.getReferenceMatrix());
		asymmetricalSimilarity = new MatrixModel(smh, problemSpace.getAsymmetricalSimilarityMatrix());
		typicality = new VectorModel(smh, problemSpace.getTypicalityVector());
		difference = new MatrixModel(smh, problemSpace.getDifferenceMatrix());
	}

	public MatrixModel getSimilarity() {
		return similarity;
	}

	public MatrixModel getSimilarityRef() {
		return similarityRef;
	}

	public MatrixModel getAsymmetricalSimilarity() {
		return asymmetricalSimilarity;
	}

	public VectorModel getTypicality() {
		return typicality;
	}

	public MatrixModel getDifference() {
		return difference;
	}

}
