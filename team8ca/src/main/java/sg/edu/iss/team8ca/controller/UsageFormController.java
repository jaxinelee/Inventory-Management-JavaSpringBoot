package sg.edu.iss.team8ca.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import sg.edu.iss.team8ca.model.Customer;
import sg.edu.iss.team8ca.model.InvUsage;
import sg.edu.iss.team8ca.model.Inventory;
import sg.edu.iss.team8ca.model.TransHistory;
import sg.edu.iss.team8ca.model.TransType;
import sg.edu.iss.team8ca.model.UsageDetails;
import sg.edu.iss.team8ca.model.UsageReportStatus;
import sg.edu.iss.team8ca.model.User;
import sg.edu.iss.team8ca.service.CustomerImpl;
import sg.edu.iss.team8ca.service.InvUsageImpl;
import sg.edu.iss.team8ca.service.ProductListingImpl;
import sg.edu.iss.team8ca.service.SendEmailService;
import sg.edu.iss.team8ca.service.TransHistoryImpl;
import sg.edu.iss.team8ca.service.UserService;

@Controller
@RequestMapping("/invusage")
public class UsageFormController {

	@Autowired
	private InvUsageImpl iuservice;

	@Autowired
	private UserService uservice;

	@Autowired
	private ProductListingImpl pservice;

	@Autowired
	private SendEmailService sendEmailService;

	@Autowired
	private TransHistoryImpl thservice;

	@Autowired
	private CustomerImpl cuservice;

	@RequestMapping(value = "/showlisting", method = RequestMethod.GET)
	public String showListing(Model model) {
//		String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
//		User user = uservice.findUserByUserName(currentUserName);
//		InvUsage invUsage = new InvUsage(LocalDate.now(), UsageReportStatus.InProgress, user);
		User user = uservice.findUserByUserName("admin");
		model.addAttribute("user", user);
		List<InvUsage> usageList = iuservice.listAllUsageRecord();
		model.addAttribute("usageList", usageList);
		return "iulisting";
	}

//	New usage report
	@RequestMapping(value = "/addforms/addformdescription/{id}", method = RequestMethod.GET)
	public String addUsageReport(Model model, @PathVariable("id") Long id) {
		User user = uservice.findUserById(id);
		model.addAttribute("user", user);
		return "UsageReportDescription";
	}

	@RequestMapping(value = "/addforms/user/{userid}/addusagecustomer", method = RequestMethod.GET)
	public String addUsageCustomer(Model model, @PathVariable("userid") Long id, @RequestParam("tasks") String tasks) {
		User user = uservice.findUserById(id);
		InvUsage usageform = new InvUsage(LocalDate.now(), UsageReportStatus.InProgress, user);
		iuservice.addUsage(usageform);
		List<Customer> customers = cuservice.findAllCustomer();
		model.addAttribute("usageform", usageform);
		model.addAttribute("customers", customers);
		model.addAttribute("customer", new Customer());
		usageform.setTasks(tasks);
		iuservice.addUsage(usageform);
		return "UsageReportCustomer";
	}

	@RequestMapping(value = "/addforms/customersearch/{usageformid}", method = RequestMethod.GET)
	public String addUsageCustomerSearch(Model model, @PathVariable("usageformid") Long id,
			@Param("keyword") String keyword) {
		InvUsage usageform = iuservice.findUsageById(id);
		List<Customer> customers = cuservice.cusSearch(keyword);
		model.addAttribute("usageform", usageform);
		model.addAttribute("customers", customers);
		model.addAttribute("customer", new Customer());
		return "UsageReportCustomer";
	}

//	new cutomer
	@RequestMapping(value = "/addforms/usageform/{id}/savecustomer")
	public String addNewCustomerToUsageReport(@PathVariable("id") Long id,
			@ModelAttribute("customer") @Valid Customer customer, BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			return "UsageReportCustomer";
		}
		InvUsage usageform = iuservice.findUsageById(id);
		cuservice.saveCustomer(customer);
		usageform.setCustomer(customer);
		iuservice.addUsage(usageform);
		return "forward:/invusage/usageforms/" + id;
	}

//	existing customer
	@RequestMapping(value = "/addforms/usageform/{id}/addcustomer/{custid}", method = RequestMethod.GET)
	public String addExistingCustomerToUsageReport(@PathVariable("id") Long id, @PathVariable("custid") Long custid,
			Model model) {
		InvUsage usageform = iuservice.findUsageById(id);
		Customer customer = cuservice.findCustomerById(custid);
		usageform.setCustomer(customer);
		iuservice.addUsage(usageform);
		List<Inventory> invList = iuservice.listAllInventory();
		List<UsageDetails> udList = iuservice.listDetailsForUdId(id);
		model.addAttribute("usageform", usageform);
		model.addAttribute("udList", udList);
		model.addAttribute("invList", invList);
		return "usage-details";
	}

//	Update inventory details
	@RequestMapping(value = "/usageforms/{id}", method = RequestMethod.GET)
	public String mapInvInvUsage(@PathVariable("id") Long id, Model model) {
		List<Inventory> invList = iuservice.listAllInventory();
		List<UsageDetails> udList = iuservice.listDetailsForUdId(id);
		InvUsage iu = iuservice.findUsageById(id);
		model.addAttribute("usageform", iu);
		model.addAttribute("udList", udList);
		model.addAttribute("invList", invList);
		return "usage-details";
	}

//	Update inventory details with search parameters
	@RequestMapping(value = "/usageforms/search/{id}", method = RequestMethod.GET)
	public String invSearch(@PathVariable("id") Long id, @Param("keyword") String keyword, Model model) {
		List<Inventory> invList = iuservice.invSearch(keyword);
		List<UsageDetails> udList = iuservice.listDetailsForUdId(id);
		InvUsage iu = iuservice.findUsageById(id);
		model.addAttribute("usageform", iu);
		model.addAttribute("udList", udList);
		model.addAttribute("invList", invList);
		return "usage-details";
	}

//	Adding inventory items to the usage listing
	@RequestMapping(value = "/usageforms/{id1}/addinvtoform/{id2}", method = RequestMethod.GET)
	public String addListingInv(@PathVariable("id1") Long id1, @PathVariable("id2") Long id2, Model model) {
		Inventory inv = iuservice.findInvById(id1);
		UsageDetails ud = new UsageDetails(inv, iuservice.findUsageById(id2), LocalDate.now(), 0);
		iuservice.addUsageDetails(ud);
		return "forward:/invusage/usageforms/" + id2;
	}

//	Delete usage details
	@RequestMapping(value = "/delete/usageforms/{id1}/ud/{id2}", method = RequestMethod.GET)
	public String deleteUd(@PathVariable("id1") Long id1, @PathVariable("id2") Long id2, Model model) {
//		String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
//		User user = uservice.findUserByUserName(currentUserName);
//		InvUsage invUsage = new InvUsage(LocalDate.now(), UsageReportStatus.InProgress, user);	
		User user = uservice.findUserByUserName("admin");
		UsageDetails ud = iuservice.findUsageDetailsById(id2);
		Inventory inventory = pservice.findProductById(ud.getInventory().getId());
		inventory.setStockQty(inventory.getStockQty() + Math.toIntExact(ud.getQuantity()));
		pservice.saveProduct(inventory);
		iuservice.deleteUsageDetails(ud);
		TransHistory trans = new TransHistory(TransType.DebitBack, Math.toIntExact(ud.getQuantity()), inventory,
				LocalDate.now(), LocalTime.now(ZoneId.of("Asia/Tokyo")), user);
		thservice.saveTrans(trans);
		return "forward:/invusage/usageforms/" + id1;
	}

//	update usage quantity
	@RequestMapping(value = "/usage/{id1}/ud/{id2}", method = RequestMethod.GET)
	public String usageQuantity(@PathVariable("id1") Long id1, @PathVariable("id2") Long id2,
			@RequestParam("ud_quantity") Long quantity) {
//			String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
//			User user = uservice.findUserByUserName(currentUserName);
//			InvUsage invUsage = new InvUsage(LocalDate.now(), UsageReportStatus.InProgress, user1);
		User user = uservice.findUserByUserName("admin");
		UsageDetails ud = iuservice.findUsageDetailsById(id2);
		long udQuantity = ud.getQuantity();
		long newUdQuantity = udQuantity + quantity;
		Inventory inventory = pservice.findProductById(ud.getInventory().getId());
		int invQuantity = inventory.getStockQty();
		int newQuantity = invQuantity - Math.toIntExact(quantity);

		if (newQuantity >= 0 && quantity >= 0) {
			ud.setQuantity(newUdQuantity);
			ud.setDate(LocalDate.now());
			iuservice.addUsageDetails(ud);

			inventory.setStockQty(newQuantity);
			pservice.saveProduct(inventory);

			TransHistory trans = new TransHistory(TransType.Usage, -Math.toIntExact(quantity), inventory,
					LocalDate.now(), LocalTime.now(ZoneId.of("Asia/Tokyo")), user);
			thservice.saveTrans(trans);

			if (inventory.getStockQty() < inventory.getReorderLevel()) {
				String id = String.valueOf(inventory.getId());
				String name = inventory.getProductName();
				sendEmailService.sendEmail("team8caproject@gmail.com",
						"Inventory ID " + id + " quantity on hand is below the re-order level! Please restock!",
						"NOTIFCATION: stock level below re-order level for part number " + id + " - " + name);
			}

			return "forward:/invusage/usageforms/" + id1;
		} else {
			return "forward:/invusage/usageforms/" + id1;
		}
	}

}
