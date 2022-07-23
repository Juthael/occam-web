package com.tregouet.occamweb.problem.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.tregouet.occam.alg.displayers.formatters.FormattersAbstractFactory;
import com.tregouet.occam.data.problem_space.IProblemSpace;
import com.tregouet.occam.data.problem_space.states.IRepresentation;
import com.tregouet.occam.data.problem_space.states.classifications.concepts.IContextObject;

public class RepresentationModel {
	
	public static class Similarity {
		private MatrixModel similarityMatrix;

		public Similarity(IProblemSpace space, IRepresentation representation) {
			super();
			List<String> smh = new ArrayList<>();
			for (IContextObject obj : space.getContext()) {
				smh.add(Integer.toString(obj.iD()));
			}
			similarityMatrix = new MatrixModel(smh,
					representation.getDescription().getSimilarityMetrics().getSimilarityMatrix());

		}

		public MatrixModel getSimilarityMatrix() {
			return similarityMatrix;
		}

	}
	
	public static class Facts{
		
		private List<Fact> facts = new ArrayList<>();
		
		public Facts(IProblemSpace space, IRepresentation representation) {
			Map<Integer, List<String>> objID2acceptedFacts = 
					representation.mapParticularIDsToFactualDescription(FormattersAbstractFactory.INSTANCE.getFactDisplayer());
			NavigableSet<Integer> objIDs = new TreeSet<>(objID2acceptedFacts.keySet());
			for (Integer iD : objIDs) {
				String header = setHeadOfAcceptedFactsArray(representation, iD);
				Fact fact = new Fact(header);
				for (String factString : objID2acceptedFacts.get(iD))
					fact.values.add(factString);
				facts.add(fact);
			}
		}
		
		private static String setHeadOfAcceptedFactsArray(IRepresentation representation, Integer conceptID) {
			if (representation.isFullyDeveloped())
				return conceptID.toString();
			else {
				TreeSet<Integer> extent = new TreeSet<>(representation.getExtentIDs(conceptID));
				if (extent.size() == 1)
					return conceptID.toString();
				else {
					StringBuilder sB = new StringBuilder();
					sB.append(conceptID.toString() + " = { ");
					Iterator<Integer> extentIte = extent.iterator();
					while (extentIte.hasNext()) {
						sB.append(extentIte.next().toString());
						if (extentIte.hasNext())
							sB.append(", ");
					}
					sB.append(" }");
					return sB.toString();
				}
			}
		}
	
		public List<Fact> getFacts() {
			return facts;
		}

	}
	
	public static class Fact  {
		
		private String header;
		private List<String> values = new ArrayList<>();
		
		public Fact(String header) {
			this.header = header;
		}
		
		public List<String> getValues() {
			return values;
		}
		
		public String getHeader() {
			return header;
		}
		
	}

	private Integer id;
	private Similarity similarity;
	private Facts acceptedFacts;

	public RepresentationModel(IProblemSpace space) {
		super();
		IRepresentation active = space.getActiveRepresentation();
		if (active != null) {
			this.similarity  =new Similarity(space, active);
			this.acceptedFacts = new Facts(space, active);
			id = active.iD();
		}
	}
	
	public Facts getAcceptedFacts() {
		return acceptedFacts;
	}
	
	public Similarity getSimilarity() {
		return similarity;
	}

	public Integer getId() {
		return id;
	}

}
