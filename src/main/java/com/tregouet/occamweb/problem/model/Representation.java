package com.tregouet.occamweb.problem.model;

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

public class Representation {
	public static class Similarity {
		private Matrix similarityMatrix;
		private Matrix asymetricalSimilarityMatrix;
		private Matrix typicalityVector;

		public Similarity(IProblemSpace space, IRepresentation representation) {
			super();
			List<String> smh = new ArrayList<>();
			for (IContextObject obj : space.getContext()) {
				smh.add(Integer.toString(obj.iD()));
			}

			similarityMatrix = new Matrix(smh,
					representation.getDescription().getSimilarityMetrics().getSimilarityMatrix());
			asymetricalSimilarityMatrix = new Matrix(smh,
					representation.getDescription().getSimilarityMetrics().getAsymmetricalSimilarityMatrix());
			typicalityVector = new Matrix(smh,
					representation.getDescription().getSimilarityMetrics().getTypicalityVector());

		}

		public Matrix getAsymetricalSimilarityMatrix() {
			return asymetricalSimilarityMatrix;
		}

		public Matrix getSimilarityMatrix() {
			return similarityMatrix;
		}

		public Matrix getTypicalityVector() {
			return typicalityVector;
		}

	}
	
	public static class Facts{
		private List<String> headers=new ArrayList<>();
		private List<Fact> facts = new ArrayList<>();
		
		public Facts(IProblemSpace space, IRepresentation representation) {
			
			Map<Integer, List<String>> objID2acceptedFacts = 
					representation.mapParticularIDsToFactualDescription(FormattersAbstractFactory.INSTANCE.getFactDisplayer());
			NavigableSet<Integer> objIDs = new TreeSet<>(objID2acceptedFacts.keySet());
	
		
			for (Integer iD : objIDs) {
				headers.add(setHeadOfAcceptedFactsArray(representation, iD));
				Fact fact = new Fact();
	
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
		public List<String> getHeaders() {
			return headers;
		}
	}
	
	public static class Fact  {
		private List<String> values = new ArrayList<>();
		
		public List<String> getValues() {
			return values;
		}
		
		
	}

	private Integer id;
	private Similarity similarity;
	private Facts acceptedFacts;

	public Representation(IProblemSpace space) {
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
