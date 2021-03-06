package com.tregouet.occamweb.problem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.tregouet.occam.data.problem_space.IProblemSpace;
import com.tregouet.occamweb.examples.ExampleService;
import com.tregouet.occamweb.problem.models.ContextTableModel;
import com.tregouet.occamweb.problem.models.ExampleModel;
import com.tregouet.occamweb.problem.models.ProblemSpaceModel;
import com.tregouet.occamweb.problem.models.RepresentationModel;

@Controller
@SessionAttributes("state")
public class ProblemSpaceController {
	private final static Logger LOGGER = LoggerFactory.getLogger(ProblemSpaceController.class);

	@Autowired
	private ProblemSpaceService problems;

	@Autowired
	private ExampleService examples;

	@PostMapping("process/action")
	public String action(@ModelAttribute("state") final ProblemState state, final Model model,
			@RequestParam("submit") final String action, @RequestParam("representationIDs") final String repIDs, 
			@RequestParam("representation") final String repID) {
		ProblemSpaceWorker worker = this.problems.getOrCreateWorker(state.getId());
		IProblemSpace problemSpace = worker.getProblemSpace();
		if (problemSpace != null) {
			try {
				if (action.equals("develop") || action.equals("restrict")) {
					List<Integer> iDs = new ArrayList<>();
					String iDString = repIDs.replaceAll(" ", "");
					String[] idStringArray = iDString.split(",");
					for (String iDStr : idStringArray) {
						Integer nextID = null;
						try {
							nextID = Integer.parseInt(iDStr);
						}
						catch (NumberFormatException e) {
							//do nothing
						}
						if (nextID != null)
							iDs.add(nextID);
					}
					if (!iDs.isEmpty()) {
						if (action.equals("develop"))
							worker.developRepresentations(iDs);
						else worker.restrictToRepresentations(iDs);
						model.addAttribute("space", new ProblemSpaceModel(worker.getProblemSpace()));
					}
				}
				else if (action.equals("fully-expand")) {
					worker.fullyExpandProblemSpace();
					model.addAttribute("space", new ProblemSpaceModel(worker.getProblemSpace()));
				}
				else if (action.equals("display-representation")) {
					//HERE
					worker.displayRepresentation(Integer.parseInt(repID));
				}
			} catch (Exception e) {
				LOGGER.error("Unable to proceed action {} on representation {}",action,repIDs,e);
			}

		}
		return "redirect:/process.html";
	}

	private void fill(final Model model, final IProblemSpace problemSpace) {
		ContextTableModel context = new ContextTableModel(problemSpace);
		model.addAttribute("context", context);
		model.addAttribute("representation", new RepresentationModel(problemSpace));
		model.addAttribute("space", new ProblemSpaceModel(problemSpace));
	}

	@RequestMapping(value = "/figures/{file_name}", method = RequestMethod.GET,produces =  MediaType.IMAGE_PNG_VALUE)
	@ResponseBody
	public FileSystemResource getFigureImage(@ModelAttribute("state") final ProblemState state, 
			@PathVariable("file_name") final String fileName) {
		ProblemSpaceWorker worker = this.problems.getOrCreateWorker(state.getId());
		IProblemSpace problemSpace = worker.getProblemSpace();
		if (problemSpace != null) {
			Optional<Path> resource = worker.getResource(fileName);
			if(resource.isPresent()) {
				return new FileSystemResource(resource.get()); 
			}

		}
		return null;
	}
	
	@GetMapping("")
	public String toIndex() {
		return "redirect:/index.html";
	}

	@GetMapping("index.html")
	public String index(final Model model) {
		List<ExampleModel> exampleModels = new ArrayList<>();
		int position = 1;
		for (String name : this.examples.getExamples()) {
			exampleModels.add(new ExampleModel(name, position++));
		}
		model.addAttribute("examples", exampleModels);
		return "index";
	}

	@GetMapping("process.html")
	public String load(@ModelAttribute("state") final ProblemState state, final Model model) {
		ProblemSpaceWorker worker = this.problems.getOrCreateWorker(state.getId());
		IProblemSpace problemSpace = worker.getProblemSpace();
		if (problemSpace != null) {
			fill(model, problemSpace);
		}
		return "process";
	}

	@PostMapping("open")
	public String process(@ModelAttribute("state") final ProblemState state, @RequestParam("input") final String input,
			final Model model) {
		ProblemSpaceWorker worker = this.problems.getOrCreateWorker(state.getId());
		try {
			worker.read(input);
			return "redirect:/process.html";
		} catch (IOException e) {
			LOGGER.error("Unable to read input", e);
			return "redirect:/index.html";
		}

	}

	@ModelAttribute("state")
	public ProblemState state() {
		return new ProblemState(UUID.randomUUID().toString());
	}
}
