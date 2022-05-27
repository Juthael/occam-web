package com.tregouet.occamweb.examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;

@Service
public class ExampleService {
	private final static Logger LOGGER = LoggerFactory.getLogger(ExampleService.class);
	
	private Path directory = Paths.get("examples");
	
	
	
	public List<String> getExamples(){
		List<String> examples = new ArrayList<>();
		try {
			for(Path file: listFiles(directory, 1)) {
				examples.add(file.getFileName().toString());
			}
			return examples;
		} catch (IOException e) {
			LOGGER.error("Unable to load examples",e);
		}
		
		return ImmutableList.of();
		
	}
	
	
	public Optional<String> getExample(String name){
		try {
			Path realPath = directory.resolve(name).toRealPath();
			if(realPath.startsWith(directory.toAbsolutePath()) && Files.exists(realPath)) {
				String content = Files.readString(realPath,Charsets.UTF_8);
			
				return Optional.of(content);
			}
		} catch (IOException e) {
		
		}
		
		return Optional.empty();
	}
	
	private List<Path> listFiles(Path dir, int depth) throws IOException {
	    try (Stream<Path> stream = Files.walk(dir, depth)) {
	        return stream
	          .filter(file -> !Files.isDirectory(file))
	          .collect(Collectors.toList());
	    }
	}
}
