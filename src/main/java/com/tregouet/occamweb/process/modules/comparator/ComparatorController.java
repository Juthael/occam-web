package com.tregouet.occamweb.process.modules.comparator;

import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.tregouet.occam.data.modules.comparison.IComparator;
import com.tregouet.occam.data.modules.sorting.ISorter;
import com.tregouet.occamweb.process.models.ComparisonModel;
import com.tregouet.occamweb.process.models.ContextTableModel;
import com.tregouet.occamweb.process.modules.AController;
import com.tregouet.occamweb.process.modules.State;
import com.tregouet.occamweb.process.modules.sorter.ISorterWorker;

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
				if (action.equals("display")) {
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
	
	@GetMapping("compare.html")
	public String load(@ModelAttribute("state") final State state, final Model model) {
		IComparatorWorker worker = this.workerService.getOrCreateComparatorWorker(state.getId());
		IComparator comparator = worker.getModule();
		if (comparator != null) {
			fill(model, comparator);
		}
		return "compare";
	}
	
	@RequestMapping(value = "/comparison/figures/{file_name}", method = RequestMethod.GET,produces =  MediaType.IMAGE_PNG_VALUE)
	@ResponseBody
	public FileSystemResource getFigureImage(@ModelAttribute("state") final State state, 
			@PathVariable("file_name") final String fileName) {
		IComparatorWorker worker = this.workerService.getOrCreateComparatorWorker(state.getId());
		IComparator sorter = worker.getModule();
		if (sorter != null) {
			Optional<Path> resource = worker.getResource(fileName);
			if(resource.isPresent()) {
				return new FileSystemResource(resource.get()); 
			}
		}
		return null;
	}	
	
	private void fill(final Model model, final IComparator comparator) {
		ContextTableModel context = new ContextTableModel(comparator);
		model.addAttribute("context", context);
		ComparisonModel comparison = new ComparisonModel(comparator);
		model.addAttribute("comparison", comparison);
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
