package com.tregouet.occamweb.process.modules.sorter;

import java.util.List;

import com.tregouet.occam.data.modules.sorting.ISorter;
import com.tregouet.occamweb.process.modules.IWorker;
import com.tregouet.occamweb.process.modules.WorkerMessage;

public interface ISorterWorker extends IWorker {

	@Override
	ISorter getModule();

	WorkerMessage displayRepresentation(int id);

	WorkerMessage developRepresentation(int id);

	WorkerMessage developRepresentations(List<Integer> iDs);

	WorkerMessage restrictToRepresentations(List<Integer> iDs);

	WorkerMessage fullyExpandProblemSpace();

}