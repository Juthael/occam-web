package com.tregouet.occamweb.process.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.SessionAttributes;

import com.tregouet.occam.data.structures.representations.IRepresentation;
import com.tregouet.occam.data.structures.representations.classifications.concepts.IConcept;
import com.tregouet.occam.data.structures.representations.classifications.concepts.IContextObject;
import com.tregouet.occam.io.input.impl.GenericFileReader;

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
	
	@Override
	public void read(String input) throws IOException {
		Files.createDirectories(directory);
		Files.writeString(getModuleFile(), input);
		try (BufferedReader reader = new BufferedReader(new StringReader(input))) {
			
			//XXX:this should not be necessary, risk of race condition
			IContextObject.initializeIDGenerator();
			IConcept.initializeIDGenerator();
			IRepresentation.initializeIDGenerator();
			
			List<IContextObject> objects = GenericFileReader.getContextObjects(reader);
			initializeModule(objects);
			generateFigures();
		}
	}	
	
	protected abstract void generateFigures();
	
	protected abstract Path getModuleFile();
	
	protected abstract void initializeModule(List<IContextObject> objects);

}
