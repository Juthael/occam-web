package com.tregouet.occamweb.problem.figures;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import com.tregouet.occam.alg.displayers.formatters.FormattersAbstractFactory;
import com.tregouet.occam.alg.displayers.formatters.differentiae.DifferentiaeLabeller;
import com.tregouet.occam.alg.displayers.visualizers.descriptions.DescriptionViz;
import com.tregouet.occam.data.problem_space.states.descriptions.IDescription;
import com.tregouet.occam.data.problem_space.states.descriptions.properties.ADifferentiae;
import com.tregouet.tree_finder.data.Tree;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

public class BasicDescriptionViz implements DescriptionViz {
	private Path directory;

	public BasicDescriptionViz(Path directory) {
		this.directory = directory;
	}

	@Override
	public String apply(IDescription description, String fileName) {
		DifferentiaeLabeller diffDisplayer = FormattersAbstractFactory.INSTANCE.getDifferentiaeDisplayer();
		Tree<Integer, ADifferentiae> descGraph = description.asGraph();
		// convert in DOT format
		DOTExporter<Integer, ADifferentiae> exporter = new DOTExporter<>();
		exporter.setVertexAttributeProvider((v) -> {
			Map<String, Attribute> map = new LinkedHashMap<>();
			map.put("label", DefaultAttribute.createAttribute(v.toString()));
			return map;
		});
		exporter.setEdgeAttributeProvider((e) -> {
			Map<String, Attribute> map = new LinkedHashMap<>();
			map.put("label", DefaultAttribute.createAttribute(diffDisplayer.apply(e)));
			return map;
		});
		Writer writer = new StringWriter();
		exporter.exportGraph(descGraph, writer);
		String dOTFile = writer.toString();
		// display graph
		try {
			MutableGraph dotGraph = new Parser().read(dOTFile);
			String filePath =   directory.resolve(fileName).toString();
			Graphviz.fromGraph(dotGraph).render(Format.PNG).toFile(new File(filePath));
			return filePath + ".png";
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
