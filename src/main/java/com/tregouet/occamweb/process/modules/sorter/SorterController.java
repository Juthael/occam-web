package com.tregouet.occamweb.process.modules.sorter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.tregouet.occam.data.modules.sorting.ISorter;
import com.tregouet.occamweb.process.models.ContextTableModel;
import com.tregouet.occamweb.process.models.RepresentationModel;
import com.tregouet.occamweb.process.modules.AController;
import com.tregouet.occamweb.process.modules.State;

@Controller
@SessionAttributes("state")
public class SorterController extends AController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SorterController.class);

	@PostMapping("sort/action")
	public String action(@ModelAttribute("state") final State state, final Model model,
			@RequestParam("submit") final String action, @RequestParam("representationIDs") final String repIDs, 
			@RequestParam("representation") final String repID) {
		ISorterWorker worker = this.workerService.getOrCreateSorterWorker(state.getId());
		ISorter sorter = worker.getModule();
		if (sorter != null) {
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
					}
				}
				else if (action.equals("fully-expand")) {
					worker.fullyExpandProblemSpace();
				}
				else if (action.equals("display-representation")) {
					worker.displayRepresentation(Integer.parseInt(repID));
				}
			} catch (Exception e) {
				LOGGER.error("Unable to proceed action {} on representation {}",action,repIDs,e);
			}

		}
		return "redirect:/sort.html";
	}

	private void fill(final Model model, final ISorter sorter) {
		ContextTableModel context = new ContextTableModel(sorter);
		model.addAttribute("context", context);
		model.addAttribute("representation", new RepresentationModel(sorter));
	}

	@RequestMapping(value = "/figures/{file_name}", method = RequestMethod.GET,produces =  MediaType.IMAGE_PNG_VALUE)
	@ResponseBody
	public FileSystemResource getFigureImage(@ModelAttribute("state") final State state, 
			@PathVariable("file_name") final String fileName) {
		ISorterWorker worker = this.workerService.getOrCreateSorterWorker(state.getId());
		ISorter sorter = worker.getModule();
		if (sorter != null) {
			Optional<Path> resource = worker.getResource(fileName);
			if(resource.isPresent()) {
				return new FileSystemResource(resource.get()); 
			}

		}
		return null;
	}

	@GetMapping("sort.html")
	public String load(@ModelAttribute("state") final State state, final Model model) {
		ISorterWorker worker = this.workerService.getOrCreateSorterWorker(state.getId());
		ISorter sorter = worker.getModule();
		if (sorter != null) {
			fill(model, sorter);
		}
		return "sort";
	}

	@PostMapping("open")
	public String process(@ModelAttribute("state") final State state, @RequestParam("input") final String input,
			final Model model) {
		ISorterWorker worker = this.workerService.getOrCreateSorterWorker(state.getId());
		try {
			worker.read(input);
			return "redirect:/sort.html";
		} catch (IOException e) {
			LOGGER.error("Unable to read input", e);
			return "redirect:/index.html";
		}

	}

	@ModelAttribute("state")
	public State state() {
		return new State(UUID.randomUUID().toString());
	}
}
