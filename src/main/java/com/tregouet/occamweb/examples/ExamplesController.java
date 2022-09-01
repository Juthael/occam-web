package com.tregouet.occamweb.examples;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ExamplesController {

	@Autowired
	private ExampleService examples;

	@GetMapping("/api/examples")
	public List<String> examples() {
		return examples.getExamples();
	}


	@GetMapping(value="/api/examples/{name}",produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getExampleContent(@PathVariable("name") String name){
		return ResponseEntity.of( examples.getExample(name));
	}

}
