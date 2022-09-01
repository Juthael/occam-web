package com.tregouet.occamweb.process.modules;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import com.tregouet.occam.data.modules.IModule;

public interface IWorker {

	void read(String input) throws IOException;

	void reset();

	Optional<Path> getResource(String fileName);

	IModule getModule();

}
