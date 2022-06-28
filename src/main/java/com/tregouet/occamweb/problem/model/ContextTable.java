package com.tregouet.occamweb.problem.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.tregouet.occam.data.problem_space.IProblemSpace;
import com.tregouet.occam.data.problem_space.states.classifications.concepts.IContextObject;

public class ContextTable {

	public static class ContextConstructs {
		private List<String> values;

		public ContextConstructs(List<String> values) {
			super();
			this.values = values;
		}
		
		public List<String> getValues() {
			return values;
		}

	}

	public static class ContextHeader {
		private String title;
		private String subtitle;

		public ContextHeader(String title) {
			super();
			this.title = title;
		}

		public ContextHeader(String title, @Nullable String subtitle) {
			super();
			this.title = title;
			this.subtitle = subtitle;
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

	private List<ContextHeader> headers = new ArrayList<>();
	private List<ContextConstructs> constructs = new ArrayList<>();
	private String caption = "Context table";

	public ContextTable(IProblemSpace space) {
		
		for (IContextObject object : space.getContext()) {
			ContextHeader header = new ContextHeader(String.valueOf(object.iD()));
			if ((object.getName() != null)) {
				header.setSubtitle(object.getName());
			}
			headers.add(header);
			ContextConstructs constructs = new ContextConstructs(
					object.getConstructs().stream().map(c -> c.toString()).collect(Collectors.toList()));
			this.constructs.add(constructs);
		}

	}
	
	
	public boolean hasSubtitle() {
		return headers.stream().anyMatch(h->h.getSubtitle()!=null);
	}

	public String getCaption() {
		return caption;
	}

	public List<ContextHeader> getHeaders() {
		return headers;
	}

	public List<ContextConstructs> getConstructs() {
		return constructs;
	}
}
