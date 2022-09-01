package com.tregouet.occamweb.process.figures;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import com.tregouet.occam.alg.displayers.formatters.descriptions.differentiae.DifferentiaeFormatter;
import com.tregouet.occam.alg.displayers.formatters.descriptions.genus.GenusFormatter;
import com.tregouet.occam.alg.displayers.visualizers.descriptions.DescriptionFormat;
import com.tregouet.occam.alg.displayers.visualizers.descriptions.DescriptionViz;
import com.tregouet.occam.data.structures.representations.descriptions.IDescription;
import com.tregouet.occam.data.structures.representations.descriptions.differentiae.ADifferentiae;
import com.tregouet.tree_finder.data.Tree;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

public class BasicDescriptionViz implements DescriptionViz {

	private Path directory;
	private Map<Integer, List<Integer>> conceptID2ExtentIDs = null;
	private DescriptionFormat format = DescriptionFormat.EXHAUSTIVE;

	public BasicDescriptionViz(Path directory) {
		this.directory = directory;
	}

	@Override
	public String apply(IDescription description, String fileName) {
		DifferentiaeFormatter diffFormatter = DescriptionViz.differentiaeFormatter();
		GenusFormatter genusFormatter = DescriptionViz.genusFormatter().setUp(conceptID2ExtentIDs);
		Tree<Integer, ADifferentiae> descGraph = description.asGraph();
		// convert in DOT format
		DOTExporter<Integer, ADifferentiae> exporter = new DOTExporter<>();
		exporter.setVertexAttributeProvider((v) -> {
			Map<String, Attribute> map = new LinkedHashMap<>();
			map.put("label", DefaultAttribute.createAttribute(genusFormatter.apply(v)));
			return map;
		});
		exporter.setEdgeAttributeProvider((e) -> {
			Map<String, Attribute> map = new LinkedHashMap<>();
			map.put("label", DefaultAttribute.createAttribute(diffFormatter.apply(e, format)));
			return map;
		});
		Writer writer = new StringWriter();
		exporter.exportGraph(descGraph, writer);
		String dOTFile = writer.toString();
		// display graph
		try {
			MutableGraph dotGraph = new Parser().read(dOTFile);
			String filePath = directory.resolve(fileName).toString();
			Graphviz.fromGraph(dotGraph).render(Format.PNG).toFile(new File(filePath));
			return filePath + ".png";
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BasicDescriptionViz setUp(Map<Integer, List<Integer>> conceptID2ExtentIDs, DescriptionFormat format) {
		this.conceptID2ExtentIDs = conceptID2ExtentIDs;
		this.format = format;
		return this;
	}

}
