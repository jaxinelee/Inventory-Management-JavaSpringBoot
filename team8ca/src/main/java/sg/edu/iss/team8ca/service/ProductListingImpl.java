package sg.edu.iss.team8ca.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.iss.team8ca.model.Brand;
import sg.edu.iss.team8ca.model.Category;
import sg.edu.iss.team8ca.model.Inventory;
import sg.edu.iss.team8ca.model.Subcategory;
import sg.edu.iss.team8ca.repo.BrandRepo;
import sg.edu.iss.team8ca.repo.CategoryRepo;
import sg.edu.iss.team8ca.repo.InventoryRepo;
import sg.edu.iss.team8ca.repo.SubcategoryRepo;
import sg.edu.iss.team8ca.repo.SupplierRepo;

@Service
@Transactional
public class ProductListingImpl implements ProductListingInterface {
	@Autowired
	BrandRepo brepo;
	@Autowired
	CategoryRepo crepo;
	@Autowired
	InventoryRepo irepo;
	@Autowired
	SubcategoryRepo srepo;

	public ProductListingImpl(BrandRepo brepo, CategoryRepo crepo, InventoryRepo irepo, SubcategoryRepo srepo) {
		super();
		this.brepo = brepo;
		this.crepo = crepo;
		this.irepo = irepo;
		this.srepo = srepo;
	}
	
	@Override
	@Transactional
	public void saveProduct(Inventory inventory) {
		irepo.save(inventory);
	}
	
	@Override
	public void addProduct(Inventory inventory) {
		irepo.save(inventory);
	}
	
	@Override
	public void deleteProduct(Inventory inventory) {
		irepo.delete(inventory);
	}

	@Override
	public Inventory findProductById(Long id) {
		return irepo.findById(id).get();
	}

	@Override
	public void editProduct(Inventory inventory) {
	
	}	
	@Override
	public void editProductQuantity(Long id, int newQty) {

		
	}

	@Override
	public List<Inventory> list(String keyword) {
		if (keyword == null) {
			return irepo.findAll();
		}
		return irepo.invSearch(keyword);
	}

	@Override
	public List<Brand> listBrand() {
		return brepo.findAll();
	}
	
	@Override
	public void addBrand(Brand brand) {
		brepo.save(brand);
	}

	@Override
	public void deleteBrand(Brand brand) {
		brepo.delete(brand);
		
	}
	
	@Override
	public ArrayList<String> findAllBrandNames() {
		List<Brand> brands = brepo.findAll();
		ArrayList<String> names = new ArrayList<String>();
		for (Iterator<Brand> iterator = brands.iterator(); iterator.hasNext();) {
			Brand brand = (Brand) iterator.next();
			names.add(brand.getBrandName());
		}
		return names;
	}
	
	@Override
	public Brand findBrandByName(String name) {
		return brepo.findBrandByName(name).get(0);
	}

	@Override
	public List<Subcategory> listSubcategory() {
		return srepo.findAll();
	}
	
	@Override
	public void addSubcategory(Subcategory subcategory) {
		srepo.save(subcategory);	
	}
	
	@Override
	public void deleteSubcategory(Subcategory subcategory) {
		srepo.delete(subcategory);
	}

	@Override
	public ArrayList<String> findAllSubcatNames() {
		List<Subcategory> subcats = srepo.findAll();
		ArrayList<String> names = new ArrayList<String>();
		for (Iterator<Subcategory> iterator = subcats.iterator(); iterator.hasNext();) {
			Subcategory subcat = (Subcategory) iterator.next();
			names.add(subcat.getSubcategoryName());
		}
		return names;
	}

	@Override
	public Subcategory findSubcatByName(String name) {
		return srepo.findSubcatByName(name).get(0);
	}

	@Override
	public List<Category> listCategory() {
		return crepo.findAll();
	}

	@Override
	public ArrayList<String> findAllCategoryNames() {
		List<Category> categories = crepo.findAll();
		ArrayList<String> names = new ArrayList<String>();
		for (Iterator<Category> iterator = categories.iterator(); iterator.hasNext();) {
			Category category = (Category) iterator.next();
			names.add(category.getCategoryName());
			}
		return names;
	}

	@Override
	public Category findCatByName(String name) {
		return crepo.findCatByName(name).get(0);
	}

	@Override
	public void addCategory(Category category) {
		crepo.save(category);
	}
	
	@Override
	public void deleteCategory(Category category) {
		crepo.delete(category);
	}

	@Override
	public Brand findBrandById(Long id) {
		return brepo.findById(id).get();
	}

	@Override
	public Subcategory findSubcatById(Long id) {
		return srepo.findById(id).get();
	}

	@Override
	public Category findCategoryById(Long id) {
		return crepo.findById(id).get();
	}

	@Override
	public List<Inventory> list() {
		return irepo.findAll();
	}


	
}
