package com.tregouet.occamweb.process.modules;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class AController {

	@Autowired
	protected WorkerService workerService;

	@ModelAttribute("state")
	public State state() {
		return new State(UUID.randomUUID().toString());
	}

}
