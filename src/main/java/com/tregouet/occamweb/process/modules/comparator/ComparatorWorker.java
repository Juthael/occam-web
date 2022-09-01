package com.tregouet.occamweb.process.modules.comparator;

import java.io.IOException;
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
import com.tregouet.occam.data.structures.representations.classifications.concepts.IContextObject;
import com.tregouet.occam.data.structures.representations.descriptions.IDescription;
import com.tregouet.occamweb.process.figures.BasicDescriptionViz;
import com.tregouet.occamweb.process.modules.AWorker;
import com.tregouet.occamweb.process.modules.WorkerMessage;
import com.tregouet.occamweb.process.modules.WorkerMessage.StateType;

public class ComparatorWorker extends AWorker implements IComparatorWorker {

	private IComparator comparator;
	private final static Logger LOGGER = LoggerFactory.getLogger(ComparatorWorker.class);

	public ComparatorWorker(Path directory) {
		super();
		this.directory = directory;
	}

	@Override
	public void reset() {
		if (Files.exists(getModuleFile())) {
			try {
				read(Files.readString(getModuleFile()));
			} catch (IOException e) {
				this.comparator = null;
				LOGGER.error("Unable to read problem space", e);
			}
		}
	}

	@Override
	public IComparator getModule() {
		return comparator;
	}

	@Override
	public WorkerMessage compare(int id1, int id2) {
		if (comparator == null)
			return new WorkerMessage(StateType.ERROR, "Comparator has not been initialized.");
		Boolean result = comparator.display(id1, id2);
		if (result == null)
			return new WorkerMessage(StateType.ERROR, "Unknown ID.");
		else if (!result)
			return new WorkerMessage(StateType.WARNING, "This comparison is displayed already.");
		generateFigures();
		return new WorkerMessage(StateType.OK, "Comparison has been displayed");
	}

	@Override
	protected Path getModuleFile() {
		return directory.resolve("comparator.txt");
	}

	@Override
	protected void generateFigures() {
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
				descriptionViz.setUp(conceptID2ExtentID, DescriptionFormat.EXHAUSTIVE).apply(activeDiffDescription, "differences_exh");
				descriptionViz.setUp(conceptID2ExtentID, DescriptionFormat.OPTIMAL).apply(activeDiffDescription, "differences_opt");
			}
		}
	}

	@Override
	protected void initializeModule(List<IContextObject> objects) {
		this.comparator = new Comparator().process(objects);
	}

}
