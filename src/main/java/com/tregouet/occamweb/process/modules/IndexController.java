package com.tregouet.occamweb.process.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.tregouet.occamweb.examples.ExampleService;
import com.tregouet.occamweb.process.models.ExampleModel;
import com.tregouet.occamweb.process.modules.comparator.IComparatorWorker;
import com.tregouet.occamweb.process.modules.sorter.ISorterWorker;
import com.tregouet.occamweb.process.modules.sorter.SorterController;

@Controller
@SessionAttributes("state")
public class IndexController extends AController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SorterController.class);
	
	@Autowired
	private ExampleService examples;
	
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
	
	@PostMapping("open")
	public String process(@ModelAttribute("state") final State state, @RequestParam("input") final String input,
			@RequestParam("submit") final String action) {
		if (action.equals("sort")){
			ISorterWorker worker = this.workerService.getOrCreateSorterWorker(state.getId());
			try {
				worker.read(input);
				return "redirect:/sort.html";
			} catch (IOException e) {
				LOGGER.error("Unable to read input", e);
			}
		}
		else if (action.equals("compare")) {
			IComparatorWorker worker = this.workerService.getOrCreateComparatorWorker(state.getId());
			try {
				worker.read(input);
				return "redirect:/compare.html";
			}
			catch(IOException e) {
				LOGGER.error("Unable to read input", e);
			}
		}
		return "redirect:/index.html";
	}	

}
