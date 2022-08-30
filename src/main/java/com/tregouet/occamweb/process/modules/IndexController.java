package com.tregouet.occamweb.process.modules;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tregouet.occamweb.examples.ExampleService;
import com.tregouet.occamweb.process.models.ExampleModel;

@Controller
public class IndexController extends AController {
	
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

}
