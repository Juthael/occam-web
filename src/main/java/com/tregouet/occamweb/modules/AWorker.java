package com.tregouet.occamweb.modules;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public abstract class AWorker implements IWorker {
	
	protected Path directory;

	@Override
	public Optional<Path> getResource(String fileName) {
		try {
			Path rp = directory.resolve(fileName).toRealPath();
			if (rp.startsWith(directory.toAbsolutePath()) && Files.exists(rp)) {
				return Optional.of(rp);
			}
		} catch (IOException e) {

		}
		return Optional.empty();
	}

}
