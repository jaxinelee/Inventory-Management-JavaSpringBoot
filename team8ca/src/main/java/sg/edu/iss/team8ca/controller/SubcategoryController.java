package sg.edu.iss.team8ca.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import sg.edu.iss.team8ca.model.Category;
import sg.edu.iss.team8ca.model.Subcategory;
import sg.edu.iss.team8ca.service.ProductListingImpl;

@Controller
@RequestMapping("/subcategory")
public class SubcategoryController {
	
	@Autowired
	private ProductListingImpl plService;
	
	@RequestMapping(value = "/add")
	public String addSubcategory(Model model) {
		Subcategory subcategory = new Subcategory();
		List<Subcategory> slist = plService.listSubcategory();
		ArrayList<String> clist = plService.findAllCategoryNames();
		model.addAttribute("subcategory", subcategory);
		model.addAttribute("cnames", clist); 
		model.addAttribute("slist", slist); 
		return "add-subcategory";	
}

	@RequestMapping(value = "/save")
	public String saveSubcat(@ModelAttribute("subcategory") Subcategory subcategory, Model model, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "add-subcategory";
		}
		Category category = plService.findCatByName(subcategory.getCategory().getCategoryName());
		subcategory.setCategory(category);
		plService.addSubcategory(subcategory);
		return "redirect:/subcategory/add"; 
	}
	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)		
	public String deleteSubcategory(@PathVariable Long id) {
		plService.deleteSubcategory(plService.findSubcatById(id));
	return "redirect:/subcategory/add";
	}
}
