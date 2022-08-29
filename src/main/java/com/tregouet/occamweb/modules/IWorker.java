package com.tregouet.occamweb.modules;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public interface IWorker {
	
	void read(String input) throws IOException;

	void reset();

	Optional<Path> getResource(String fileName);

}
