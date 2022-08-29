package com.tregouet.occamweb.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tregouet.occam.alg.displayers.visualizers.descriptions.DescriptionFormat;
import com.tregouet.occam.data.modules.comparison.IComparator;
import com.tregouet.occam.data.modules.comparison.impl.Comparator;
import com.tregouet.occam.data.structures.representations.IRepresentation;
import com.tregouet.occam.data.structures.representations.classifications.concepts.IConcept;
import com.tregouet.occam.data.structures.representations.classifications.concepts.IContextObject;
import com.tregouet.occam.data.structures.representations.descriptions.IDescription;
import com.tregouet.occam.io.input.impl.GenericFileReader;
import com.tregouet.occamweb.modules.WorkerMessage.State;
import com.tregouet.occamweb.modules.figures.BasicDescriptionViz;

public class ComparatorWorker extends AWorker implements IComparatorWorker {
	
	private IComparator comparator;
	private final static Logger LOGGER = LoggerFactory.getLogger(ComparatorWorker.class);

	@Override
	public void read(String input) throws IOException {
		Files.createDirectories(directory);
		Files.writeString(getComparatorFile(), input);
		try (BufferedReader reader = new BufferedReader(new StringReader(input))) {
			
			//XXX:this should not be necessary, risk of race condition
			IContextObject.initializeIDGenerator();
			IConcept.initializeIDGenerator();
			IRepresentation.initializeIDGenerator();
			
			List<IContextObject> objects = GenericFileReader.getContextObjects(reader);
			this.comparator = new Comparator().process(objects);
			generateComparisonFigures();
		}

	}

	@Override
	public void reset() {
		if (Files.exists(getComparatorFile())) {
			try {
				read(Files.readString(getComparatorFile()));
			} catch (IOException e) {
				this.comparator = null;
				LOGGER.error("Unable to read problem space", e);
			}
		}
	}	

	@Override
	public IComparator getComparator() {
		return comparator;
	}

	@Override
	public WorkerMessage compare(int id1, int id2) {
		if (comparator == null)
			return new WorkerMessage(State.ERROR, "Comparator has not been initialized.");
		Boolean result = comparator.display(id1, id2);
		if (result == null)
			return new WorkerMessage(State.ERROR, "Unknown ID.");
		else if (!result)
			return new WorkerMessage(State.WARNING, "This comparison is displayed already.");
		generateComparisonFigures();
		return new WorkerMessage(State.OK, "Comparison has been displayed");
	}
	
	private Path getComparatorFile() {
		return directory.resolve("comparator.txt");
	}
	
	private void generateComparisonFigures() {
		if (comparator != null) {
			IRepresentation activeRepOfSimilarity = comparator.getActiveRepresentationOfSimilarity();
			IRepresentation activeRepOfDifference = comparator.getActiveRepresentationOfDifferences();
			BasicDescriptionViz descriptionViz = new BasicDescriptionViz(directory);
			if (activeRepOfSimilarity != null) {
				Map<Integer, List<Integer>> conceptID2ExtentID = activeRepOfSimilarity.getClassification().mapConceptID2ExtentIDs();
				IDescription activeSimDescription = activeRepOfSimilarity.getDescription();
				descriptionViz.setUp(conceptID2ExtentID, DescriptionFormat.EXHAUSTIVE).apply(activeSimDescription, "similarity_exh");
				descriptionViz.setUp(conceptID2ExtentID, DescriptionFormat.OPTIMAL).apply(activeSimDescription, "similarity_opt");
			}
			if (activeRepOfDifference != null) {
				Map<Integer, List<Integer>> conceptID2ExtentID = activeRepOfDifference.getClassification().mapConceptID2ExtentIDs();
				IDescription activeDiffDescription = activeRepOfDifference.getDescription();
				descriptionViz.setUp(conceptID2ExtentID, DescriptionFormat.EXHAUSTIVE).apply(activeDiffDescription, "difference_exh");
				descriptionViz.setUp(conceptID2ExtentID, DescriptionFormat.OPTIMAL).apply(activeDiffDescription, "difference_opt");
			}
		}
	}

}
