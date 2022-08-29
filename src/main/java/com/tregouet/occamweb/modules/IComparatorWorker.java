package com.tregouet.occamweb.modules;

import com.tregouet.occam.data.modules.comparison.IComparator;

public interface IComparatorWorker extends IWorker {
	
	IComparator getComparator();
	
	WorkerMessage compare(int obj1, int obj2);

}
