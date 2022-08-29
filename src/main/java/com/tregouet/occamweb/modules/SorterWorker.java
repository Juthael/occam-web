package com.tregouet.occamweb.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tregouet.occam.alg.displayers.visualizers.descriptions.DescriptionFormat;
import com.tregouet.occam.data.modules.sorting.ISorter;
import com.tregouet.occam.data.modules.sorting.impl.Sorter;
import com.tregouet.occam.data.structures.representations.IRepresentation;
import com.tregouet.occam.data.structures.representations.classifications.concepts.IConcept;
import com.tregouet.occam.data.structures.representations.classifications.concepts.IContextObject;
import com.tregouet.occam.data.structures.representations.descriptions.IDescription;
import com.tregouet.occam.io.input.impl.GenericFileReader;
import com.tregouet.occamweb.modules.WorkerMessage.State;
import com.tregouet.occamweb.modules.figures.BasicConceptGraphViz;
import com.tregouet.occamweb.modules.figures.BasicDescriptionViz;
import com.tregouet.occamweb.modules.figures.BasicProblemSpaceViz;
import com.tregouet.occamweb.modules.figures.BasicTransitionFunctionViz;

public class SorterWorker extends AWorker implements ISorterWorker {
	
	private ISorter sorter;
	private final static Logger LOGGER = LoggerFactory.getLogger(SorterWorker.class);

	public SorterWorker(Path directory) {
		super();
		this.directory = directory;

	}

	@Override
	public void read(String input) throws IOException {
		Files.createDirectories(directory);
		Files.writeString(getSorterFile(), input);
		try (BufferedReader reader = new BufferedReader(new StringReader(input))) {
			
			//XXX:this should not be necessary, risk of race condition
			IContextObject.initializeIDGenerator();
			IConcept.initializeIDGenerator();
			IRepresentation.initializeIDGenerator();
			
			List<IContextObject> objects = GenericFileReader.getContextObjects(reader);
			this.sorter = new Sorter().process(objects);
			generateFigures();
		}
	}

	@Override
	public ISorter getSorter() {
		return sorter;
	}

	private Path getSorterFile() {
		return directory.resolve("sorter.txt");
	}

	@Override
	public void reset() {
		if (Files.exists(getSorterFile())) {
			try {
				read(Files.readString(getSorterFile()));
			} catch (IOException e) {
				this.sorter = null;
				LOGGER.error("Unable to read problem space", e);
			}
		}
	}

	@Override
	public WorkerMessage displayRepresentation(int id) {
		if (sorter == null) {
			return new WorkerMessage(State.ERROR, "Sorter has not been initialized");
		}
		Boolean result = sorter.display(id);
		if (result == null) {
			return new WorkerMessage(State.ERROR, "No representation has this ID.");
		} else if (!result) {
			return new WorkerMessage(State.WARNING, "This representation is displayed already.");
		}
		generateRepresentationFigures();
		return new WorkerMessage(State.OK, "Sorter has been displayed");

	}

	@Override
	public WorkerMessage developRepresentation(int id) {
		if (sorter == null) {
			return new WorkerMessage(State.ERROR, "Problem space has not been initialized");
		}
		Boolean result = sorter.develop(id);
		if (result == null) {
			return new WorkerMessage(State.ERROR, "No representation has this ID.");
		} else if (!result) {
			return new WorkerMessage(State.WARNING, "This representation is fully developed already. ");
		}
		generateProblemSpaceFigure();
		return new WorkerMessage(State.OK, "Representation has been developed");
	}
	
	@Override
	public WorkerMessage developRepresentations(List<Integer> iDs) {
		if (sorter == null) {
			return new WorkerMessage(State.ERROR, "Sorter has not been initialized");
		}
		Boolean result = sorter.develop(iDs);
		if (result == null) {
			return new WorkerMessage(State.ERROR, "No representation has been found.");
		} else if (!result) {
			return new WorkerMessage(State.WARNING, "The specified representations are fully developed already. ");
		}
		generateProblemSpaceFigure();
		return new WorkerMessage(State.OK, "Representations have been developed");
	}	
	
	@Override
	public WorkerMessage restrictToRepresentations(List<Integer> iDs) {
		if (sorter == null) {
			return new WorkerMessage(State.ERROR, "Sorter has not been initialized");
		}
		Boolean result = sorter.restrictTo(new HashSet<>(iDs));
		if (result == null) {
			return new WorkerMessage(State.ERROR, "No representation has been found.");
		} else if (!result) {
			return new WorkerMessage(State.WARNING, "The problem space has not been modified. ");
		}
		generateProblemSpaceFigure();
		return new WorkerMessage(State.OK, "The problem space has been restricted.");
	}
	
	@Override
	public WorkerMessage fullyExpandProblemSpace() {
		if (sorter == null) {
			return new WorkerMessage(State.ERROR, "Sorter has not been initialized");
		}
		Boolean result = sorter.develop();
		if (result == null) {
			return new WorkerMessage(State.ERROR, "The problem space cannot be fully developed.");
		}
		generateProblemSpaceFigure();
		return new WorkerMessage(State.OK, "The problem space has been fully developed.");
	}
	
	private void generateFigures() {
		new BasicConceptGraphViz(directory).apply(sorter.getLatticeOfConcepts(), "concept_lattice", true);
		generateProblemSpaceFigure();
		generateRepresentationFigures();
	}	

	private void generateProblemSpaceFigure() {
		if (sorter != null) {
			new BasicProblemSpaceViz(directory).apply(sorter.getProblemSpaceGraph(), "problem_space");
		}
	}
	
	private void generateRepresentationFigures() {
		if (sorter != null) {
			IRepresentation activeRepresentation = sorter.getActiveRepresentation();
			if (activeRepresentation != null) {
				Map<Integer, List<Integer>> conceptID2ExtentID = activeRepresentation.getClassification().mapConceptID2ExtentIDs();
				IDescription activeDescription = activeRepresentation.getDescription();
				BasicDescriptionViz descrViz = new BasicDescriptionViz(directory);
				descrViz.setUp(conceptID2ExtentID, DescriptionFormat.EXHAUSTIVE)
					.apply(activeDescription, "description_exh");
				descrViz.setUp(conceptID2ExtentID, DescriptionFormat.OPTIMAL)
					.apply(activeDescription, "description_opt");
				new BasicTransitionFunctionViz(directory).apply(activeRepresentation.getTransitionFunction(), "trans_func");
				new BasicConceptGraphViz(directory).apply(activeRepresentation.getClassification().asGraph(), "classification_tree", true);
			}
		}
	}

}
