package com.tregouet.occamweb.process.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.tregouet.occam.alg.displayers.formatters.FormattersAbstractFactory;
import com.tregouet.occam.data.modules.sorting.ISorter;
import com.tregouet.occam.data.structures.representations.IRepresentation;

public class RepresentationModel {
	
	public static class Facts{
		
		private List<Fact> facts = new ArrayList<>();
		
		public Facts(ISorter sorter, IRepresentation representation) {
			Map<Integer, List<String>> objID2acceptedFacts = 
					representation.mapParticularIDsToFactualDescription(FormattersAbstractFactory.INSTANCE.getFactDisplayer());
			NavigableSet<Integer> objIDs = new TreeSet<>(objID2acceptedFacts.keySet());
			for (Integer iD : objIDs) {
				String header = setHeadOfAcceptedFactsArray(representation, iD);
				Fact fact = new Fact(header);
				for (String factString : objID2acceptedFacts.get(iD))
					fact.add(factString);
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
		
		public void add(String factString) {
			values.add(factString);
		}
		
	}

	private Integer id;
	private Facts acceptedFacts;

	public RepresentationModel(ISorter sorter) {
		super();
		IRepresentation active = sorter.getActiveRepresentation();
		if (active != null) {
			this.acceptedFacts = new Facts(sorter, active);
			id = active.iD();
		}
	}
	
	public Facts getAcceptedFacts() {
		return acceptedFacts;
	}

	public Integer getId() {
		return id;
	}

}
