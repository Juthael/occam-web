package com.tregouet.occamweb.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.tregouet.occamweb.modules.SorterMessage.State;
import com.tregouet.occamweb.modules.figures.BasicConceptGraphViz;
import com.tregouet.occamweb.modules.figures.BasicDescriptionViz;
import com.tregouet.occamweb.modules.figures.BasicProblemSpaceViz;
import com.tregouet.occamweb.modules.figures.BasicTransitionFunctionViz;

public class SorterWorker {
	private Path directory;
	private ISorter sorter;
	private final static Logger LOGGER = LoggerFactory.getLogger(SorterWorker.class);

	public SorterWorker(Path directory) {
		super();
		this.directory = directory;

	}

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

	public ISorter getSorter() {
		return sorter;
	}

	private Path getSorterFile() {
		return directory.resolve("sorter.txt");
	}

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

	public SorterMessage displayRepresentation(int id) {
		if (sorter == null) {
			return new SorterMessage(State.ERROR, "Sorter has not been initialized");
		}
		Boolean result = sorter.display(id);
		if (result == null) {
			return new SorterMessage(State.ERROR, "No representation has this ID.");
		} else if (!result) {
			return new SorterMessage(State.WARNING, "This representation is already displayed.");
		}
		generateRepresentationFigures();
		return new SorterMessage(State.OK, "Sorter has been displayed");

	}

	public SorterMessage developRepresentation(int id) {
		if (sorter == null) {
			return new SorterMessage(State.ERROR, "Problem space has not been initialized");
		}
		Boolean result = sorter.develop(id);
		if (result == null) {
			return new SorterMessage(State.ERROR, "No representation has this ID.");
		} else if (!result) {
			return new SorterMessage(State.WARNING, "This representation is fully developed already. ");
		}
		generateProblemSpaceFigure();
		return new SorterMessage(State.OK, "Representation has been developed");
	}
	
	public SorterMessage developRepresentations(List<Integer> iDs) {
		if (sorter == null) {
			return new SorterMessage(State.ERROR, "Sorter has not been initialized");
		}
		Boolean result = sorter.develop(iDs);
		if (result == null) {
			return new SorterMessage(State.ERROR, "No representation has been found.");
		} else if (!result) {
			return new SorterMessage(State.WARNING, "The specified representations are fully developed already. ");
		}
		generateProblemSpaceFigure();
		return new SorterMessage(State.OK, "Representations have been developed");
	}	
	
	public SorterMessage restrictToRepresentations(List<Integer> iDs) {
		if (sorter == null) {
			return new SorterMessage(State.ERROR, "Sorter has not been initialized");
		}
		Boolean result = sorter.restrictTo(new HashSet<>(iDs));
		if (result == null) {
			return new SorterMessage(State.ERROR, "No representation has been found.");
		} else if (!result) {
			return new SorterMessage(State.WARNING, "The problem space has not been modified. ");
		}
		generateProblemSpaceFigure();
		return new SorterMessage(State.OK, "The problem space has been restricted.");
	}
	
	public SorterMessage fullyExpandProblemSpace() {
		if (sorter == null) {
			return new SorterMessage(State.ERROR, "Sorter has not been initialized");
		}
		Boolean result = sorter.develop();
		if (result == null) {
			return new SorterMessage(State.ERROR, "The problem space cannot be fully developed.");
		}
		generateProblemSpaceFigure();
		return new SorterMessage(State.OK, "The problem space has been fully developed.");
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
				new BasicDescriptionViz(directory).setUp(conceptID2ExtentID, DescriptionFormat.EXHAUSTIVE)
					.apply(activeDescription, "description_exh");
				new BasicDescriptionViz(directory).setUp(conceptID2ExtentID, DescriptionFormat.OPTIMAL)
					.apply(activeDescription, "description_opt");
				new BasicTransitionFunctionViz(directory).apply(activeRepresentation.getTransitionFunction(), "trans_func");
				new BasicConceptGraphViz(directory).apply(activeRepresentation.getClassification().asGraph(), "classification_tree", true);
			}
		}
	}

	public Optional<Path> getResource(String fileName) {
		try {
			Path rp = directory.resolve(fileName).toRealPath();
			if (rp.startsWith(directory.toAbsolutePath()) && Files.exists(rp)) {
				return Optional.of(rp);
			}
		} catch (IOException e) {

		}
		return Optional.empty();
	}

}
