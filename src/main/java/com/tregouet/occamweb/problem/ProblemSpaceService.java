package com.tregouet.occamweb.problem;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

@Service
public class ProblemSpaceService {

	private Map<String, ProblemSpaceWorker> sessionWorkers = new ConcurrentHashMap<>();

	public ProblemSpaceService() {
		try {
			FileSystemUtils.deleteRecursively(Paths.get("workers"));
		} catch (IOException e) {

		}
	}

	public ProblemSpaceWorker getOrCreateWorker(String sessionProblemId) {
		return sessionWorkers.computeIfAbsent(sessionProblemId, sid -> new ProblemSpaceWorker(getWorkerDirectory(sid)));
	}

	private Path getWorkerDirectory(String sid) {
		return Paths.get("workers").resolve(sid);
	}

}
