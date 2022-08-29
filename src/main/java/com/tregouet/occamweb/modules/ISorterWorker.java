package com.tregouet.occamweb.modules;

import java.util.List;

import com.tregouet.occam.data.modules.sorting.ISorter;

public interface ISorterWorker extends IWorker {

	ISorter getSorter();

	WorkerMessage displayRepresentation(int id);

	WorkerMessage developRepresentation(int id);

	WorkerMessage developRepresentations(List<Integer> iDs);

	WorkerMessage restrictToRepresentations(List<Integer> iDs);

	WorkerMessage fullyExpandProblemSpace();

}