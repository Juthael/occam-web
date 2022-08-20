package com.tregouet.occamweb.problem.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.tregouet.occam.data.problem_space.IProblemSpace;
import com.tregouet.occam.data.problem_space.states.classifications.concepts.IContextObject;

public class ContextTableModel {

	public static class Row {

		private int cardinal = 0;
		private List<ContextConstructs> list = new ArrayList<>();

		public Row() {
		}

		public boolean add(ContextConstructs constructs) {
			if (cardinal < 3) {
				list.add(constructs);
				cardinal++;
				return true;
			}
			else return false;
		}

		public List<ContextConstructs> getList(){
			return list;
		}

	}

	public static class ContextConstructs {
		private String title;
		private String subtitle;
		private List<String> values;

		public ContextConstructs(String title, String subtitle, List<String> values) {
			super();
			this.title = title;
			this.subtitle = subtitle;
			this.values = values;
		}

		public List<String> getValues() {
			return values;
		}

		public void setSubtitle(String subtitle) {
			this.subtitle = subtitle;
		}

		@Nullable
		public String getSubtitle() {
			return subtitle;
		}

		public String getTitle() {
			return title;
		}

	}

	private List<Row> rows = new ArrayList<>();
	private String caption = "Context table";

	public ContextTableModel(IProblemSpace space) {
		rows.add(new Row());
		for (IContextObject object : space.getContext()) {
			String title = Integer.toString(object.iD());
			String subtitle = null;
			String objName = object.getName();
			if (objName != null && !objName.equals(title))
				subtitle = objName;
			ContextConstructs constructs = new ContextConstructs(title, subtitle,
					object.getConstructs().stream().map(c -> c.toString()).collect(Collectors.toList()));
			if (!rows.get(rows.size() - 1).add(constructs)) {
				Row row = new Row();
				row.add(constructs);
				rows.add(row);
			}
		}
	}


	public boolean hasSubtitle() {
		for (Row row : rows) {
			for (ContextConstructs constructs : row.getList())
				if (constructs.getSubtitle() != null)
					return true;
		}
		return false;
	}

	public String getCaption() {
		return caption;
	}

	public List<Row> getRows() {
		return rows;
	}
}
