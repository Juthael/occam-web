package com.tregouet.occamweb.process.modules.comparator;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.alg.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.tregouet.occam.data.modules.comparison.IComparator;
import com.tregouet.occamweb.process.modules.AController;
import com.tregouet.occamweb.process.modules.State;

@Controller
@SessionAttributes("state")
public class ComparatorController extends AController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ComparatorController.class);
	
	@PostMapping("compare/action")
	public String action(@ModelAttribute("state") final State state, final Model model, 
			@RequestParam("submit") final String action, 
			@RequestParam("particularIDs") final String iDsAsString) {
		IComparatorWorker worker = this.workerService.getOrCreateComparatorWorker(state.getId());
		IComparator comparator = worker.getModule();
		if (comparator != null) {
			try {
				if (action.equals("doCompare")) {
					Integer[] iDs = getIDs(iDsAsString);
					if (iDs != null)
						worker.compare(iDs[0], iDs[1]);
				}
			}
			catch (Exception e) {
				LOGGER.error("Unable to proceed comparison of {}", iDsAsString, e);
			}
		}
		return "redirect:/compare.html";
	}
	
	private Integer[] getIDs(String particularIDs){
		Integer[] iDs = new Integer[2];
		String stringOfIDs = particularIDs.replaceAll(" ", "");
		String[] idStringArray = stringOfIDs.split(",");
		if (idStringArray.length != 2)
			return null;
		for (int i = 0 ; i < 2 ; i++) {
			Integer iID = null;
			try {
				iID = Integer.parseInt(idStringArray[i]);
				iDs[i] = iID;
			}
			catch (NumberFormatException e) {
				//do nothing
			}
		}
		if (iDs[0] != null && iDs[1] != null) {
			return iDs;
		}
		return null;
	}

}
