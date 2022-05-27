package com.tregouet.occamweb.problem.figures;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import com.tregouet.occam.alg.displayers.formatters.FormattersAbstractFactory;
import com.tregouet.occam.alg.displayers.formatters.problem_states.ProblemStateLabeller;
import com.tregouet.occam.alg.displayers.formatters.problem_transitions.ProblemTransitionLabeller;
import com.tregouet.occam.alg.displayers.visualizers.problem_spaces.ProblemSpaceViz;
import com.tregouet.occam.data.problem_space.states.IRepresentation;
import com.tregouet.occam.data.problem_space.transitions.AProblemStateTransition;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

public class BasicProblemSpaceViz implements ProblemSpaceViz {


	private Path directory;

	public BasicProblemSpaceViz(Path directory) {
		this.directory = directory;
	}

	@Override
	public String apply(DirectedAcyclicGraph<IRepresentation, AProblemStateTransition> graph, String fileName) {
		// convert in DOT format
		DOTExporter<IRepresentation, AProblemStateTransition> exporter = new DOTExporter<>();
		ProblemStateLabeller stateDisplayer = FormattersAbstractFactory.INSTANCE.getProblemStateDisplayer();
		ProblemTransitionLabeller transitionDisplayer = FormattersAbstractFactory.INSTANCE
				.getProblemTransitionDisplayer();
		exporter.setVertexAttributeProvider((v) -> {
			Map<String, Attribute> map = new LinkedHashMap<>();
			map.put("label", DefaultAttribute.createAttribute(stateDisplayer.apply(v)));
			return map;
		});
		exporter.setEdgeAttributeProvider((e) -> {
			Map<String, Attribute> map = new LinkedHashMap<>();
			map.put("label", DefaultAttribute.createAttribute(transitionDisplayer.apply(e)));
			return map;
		});
		Writer writer = new StringWriter();
		exporter.exportGraph(graph, writer);
		String stringDOT = writer.toString();
		// display graph
		try {
			MutableGraph dotGraph = new Parser().read(stringDOT);
			String filePath =   directory.resolve(fileName).toString();
			Graphviz.fromGraph(dotGraph).render(Format.PNG).toFile(new File(filePath));
			return filePath + ".png";
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
