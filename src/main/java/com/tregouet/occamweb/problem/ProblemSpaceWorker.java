package com.tregouet.occamweb.problem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tregouet.occam.data.problem_space.IProblemSpace;
import com.tregouet.occam.data.problem_space.impl.ProblemSpace;
import com.tregouet.occam.data.problem_space.states.IRepresentation;
import com.tregouet.occam.data.problem_space.states.classifications.concepts.IConcept;
import com.tregouet.occam.data.problem_space.states.classifications.concepts.IContextObject;
import com.tregouet.occam.io.input.impl.GenericFileReader;
import com.tregouet.occamweb.problem.ProblemSpaceMessage.State;
import com.tregouet.occamweb.problem.figures.BasicConceptGraphViz;
import com.tregouet.occamweb.problem.figures.BasicDescriptionViz;
import com.tregouet.occamweb.problem.figures.BasicProblemSpaceViz;
import com.tregouet.occamweb.problem.figures.BasicTransitionFunctionViz;

public class ProblemSpaceWorker {
	private Path directory;
	private IProblemSpace problemSpace;
	private final static Logger LOGGER = LoggerFactory.getLogger(ProblemSpaceWorker.class);

	public ProblemSpaceWorker(Path directory) {
		super();
		this.directory = directory;

	}

	public void read(String input) throws IOException {
		Files.createDirectories(directory);
		Files.writeString(getProblemFile(), input);
		try (BufferedReader reader = new BufferedReader(new StringReader(input))) {

			//XXX:this should not be necessary, risk of race condition
			IContextObject.initializeIDGenerator();
			IConcept.initializeIDGenerator();
			IRepresentation.initializeIDGenerator();

			List<IContextObject> objects = GenericFileReader.getContextObjects(reader);
			this.problemSpace = new ProblemSpace(new HashSet<>(objects));
			generateFigures();
		}
	}

	public IProblemSpace getProblemSpace() {
		return problemSpace;
	}

	private Path getProblemFile() {
		return directory.resolve("problem.txt");
	}

	public void reset() {
		if (Files.exists(getProblemFile())) {
			try {
				read(Files.readString(getProblemFile()));
			} catch (IOException e) {
				this.problemSpace = null;
				LOGGER.error("Unable to read problem space", e);
			}
		}
	}

	public ProblemSpaceMessage displayRepresentation(int id) {
		if (problemSpace == null) {
			return new ProblemSpaceMessage(State.ERROR, "Problem space has not been initialized");
		}
		Boolean result = problemSpace.display(id);
		if (result == null) {
			return new ProblemSpaceMessage(State.ERROR, "No representation has this ID.");
		} else if (!result) {
			return new ProblemSpaceMessage(State.WARNING, "This representation is already displayed.");
		}
		generateRepresentationFigures();
		return new ProblemSpaceMessage(State.OK, "Representation has been displayed");

	}

	public ProblemSpaceMessage developRepresentation(int id) {
		if (problemSpace == null) {
			return new ProblemSpaceMessage(State.ERROR, "Problem space has not been initialized");
		}
		Boolean result = problemSpace.develop(id);
		if (result == null) {
			return new ProblemSpaceMessage(State.ERROR, "No representation has this ID.");
		} else if (!result) {
			return new ProblemSpaceMessage(State.WARNING, "This representation is fully developed already. ");
		}
		generateProblemSpaceFigure();
		return new ProblemSpaceMessage(State.OK, "Representation has been developed");
	}

	public ProblemSpaceMessage developRepresentations(List<Integer> iDs) {
		if (problemSpace == null) {
			return new ProblemSpaceMessage(State.ERROR, "Problem space has not been initialized");
		}
		Boolean result = problemSpace.develop(iDs);
		if (result == null) {
			return new ProblemSpaceMessage(State.ERROR, "No representation has been found.");
		} else if (!result) {
			return new ProblemSpaceMessage(State.WARNING, "The specified representations are fully developed already. ");
		}
		generateProblemSpaceFigure();
		return new ProblemSpaceMessage(State.OK, "Representations have been developed");
	}

	public ProblemSpaceMessage restrictToRepresentations(List<Integer> iDs) {
		if (problemSpace == null) {
			return new ProblemSpaceMessage(State.ERROR, "Problem space has not been initialized");
		}
		Boolean result = problemSpace.restrictTo(new HashSet<>(iDs));
		if (result == null) {
			return new ProblemSpaceMessage(State.ERROR, "No representation has been found.");
		} else if (!result) {
			return new ProblemSpaceMessage(State.WARNING, "The problem space has not been modified. ");
		}
		generateProblemSpaceFigure();
		return new ProblemSpaceMessage(State.OK, "The problem space has been restricted.");
	}

	public ProblemSpaceMessage fullyExpandProblemSpace() {
		if (problemSpace == null) {
			return new ProblemSpaceMessage(State.ERROR, "Problem space has not been initialized");
		}
		Boolean result = problemSpace.develop();
		if (result == null) {
			return new ProblemSpaceMessage(State.ERROR, "The problem space cannot be fully developed.");
		}
		generateProblemSpaceFigure();
		return new ProblemSpaceMessage(State.OK, "The problem space has been fully developed.");
	}

	private void generateFigures() {
		new BasicConceptGraphViz(directory).apply(problemSpace.getLatticeOfConcepts(), "concept_lattice");
		generateProblemSpaceFigure();
		generateRepresentationFigures();
	}

	private void generateProblemSpaceFigure() {
		if (problemSpace != null) {
			new BasicProblemSpaceViz(directory).apply(problemSpace.getProblemSpaceGraph(), "problem_space");
		}
	}

	private void generateRepresentationFigures() {
		if (problemSpace != null) {
			IRepresentation activeRepresentation = problemSpace.getActiveRepresentation();
			if (activeRepresentation != null) {
				new BasicDescriptionViz(directory).apply(activeRepresentation.getDescription(), "description");
				new BasicTransitionFunctionViz(directory).apply(activeRepresentation.getTransitionFunction(), "trans_func");
				new BasicConceptGraphViz(directory).apply(activeRepresentation.getClassification().asGraph(), "classification_tree");
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
