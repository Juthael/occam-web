package com.tregouet.occamweb.process.modules.comparator;

import com.tregouet.occam.data.modules.comparison.IComparator;
import com.tregouet.occamweb.process.modules.IWorker;
import com.tregouet.occamweb.process.modules.WorkerMessage;

public interface IComparatorWorker extends IWorker {
	
	@Override
	IComparator getModule();
	
	WorkerMessage compare(int obj1, int obj2);

}
