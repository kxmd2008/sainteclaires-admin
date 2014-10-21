package org.luis.sainteclaires.admin.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.luis.basic.domain.FilterAttributes;
import org.luis.basic.rest.model.DatatableBean;
import org.luis.basic.rest.model.SimpleMessage;
import org.luis.basic.rest.model.SimpleMessageHead;
import org.luis.basic.util.SpringContextFactory;
import org.luis.sainteclaires.base.INameSpace;
import org.luis.sainteclaires.base.bean.Account;
import org.luis.sainteclaires.base.bean.Category;
import org.luis.sainteclaires.base.bean.Config;
import org.luis.sainteclaires.base.bean.Product;
import org.luis.sainteclaires.base.bean.ProductVo;
import org.luis.sainteclaires.base.bean.service.ProductVoService;
import org.luis.sainteclaires.base.bean.service.ServiceFactory;
import org.luis.sainteclaires.base.util.BaseUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/auth")
// @Secured("ROLE_ADMIN")
public class AdminRest {
	
	@RequestMapping("admin")
	public String login() {
		return "admin/login";
	}
 
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String login(String loginName, String password, ModelMap map,
			HttpServletRequest req) {
		SimpleMessage<Account> sm = BaseUtil.getAccountService().login(loginName, password);
		if (!sm.getHead().getRep_code().equals(SimpleMessageHead.REP_OK)) {
			map.put("errorMsg", sm.getHead().getRep_message());
			return "admin/login";
		}
		req.getSession().setAttribute(INameSpace.KEY_SESSION_ADMIN, sm.getItem());
		map.put("account", sm.getItem().getLoginName());
		map.put("active", "unsettledOrders");
		map.put("collapse", "order");
		BaseUtil.setSessionAttr(req, INameSpace.KEY_SESSION_LOCALE, "zh_CN");
		return "admin/unsettledOrders";
	}

	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest req) {
		req.getSession().removeAttribute(INameSpace.KEY_SESSION_ADMIN);
		return "redirect:/auth/admin";
	}

	@RequestMapping("categorys")
	public String categorys(ModelMap map) {
		setCats(map);
		return "admin/categorys";
	}
	
	@RequestMapping(value = "category/find", method = RequestMethod.GET)
	@ResponseBody
	public DatatableBean<Category> findCates(HttpServletRequest req , ModelMap map) {
		int start = Integer.parseInt(req.getParameter("start") == null ? "0" : req.getParameter("start"));
		int length = Integer.parseInt(req.getParameter("length") == null ? "10" : req.getParameter("length"));
		int draw = Integer.parseInt(req.getParameter("draw") == null ? "10" : req.getParameter("draw"));
		FilterAttributes fa = FilterAttributes.blank();
		List<Category> categories = ServiceFactory.getCategoryService().findPaginationByAttributes(fa, start, length);
		int count = ServiceFactory.getCategoryService().findCountByAttributes(fa);
		DatatableBean<Category> tb = new DatatableBean<Category>();
		tb.setData(categories);
		tb.setDraw(draw + 1);
		tb.setRecordsFiltered(count);
		tb.setRecordsTotal(count);
		return tb;
	}
	
	@RequestMapping(value = "category/find/{parentId}", method = RequestMethod.GET)
	@ResponseBody
	public DatatableBean<Category> findCatesByPid(@PathVariable("parentId") Long parentId, HttpServletRequest req) {
		int draw = Integer.parseInt(req.getParameter("draw") == null ? "10" : req.getParameter("draw"));
		FilterAttributes fa = FilterAttributes.blank().add("parentId", parentId);
		List<Category> categories = ServiceFactory.getCategoryService().findByAttributes(fa);
		DatatableBean<Category> tb = new DatatableBean<Category>();
		tb.setData(categories);
		tb.setDraw(draw + 1);
		tb.setRecordsFiltered(categories.size());
		tb.setRecordsTotal(categories.size());
		return tb;
	}

	@RequestMapping(value = "category/find", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage<Category> findCates() {
		SimpleMessage<Category> sm = new SimpleMessage<Category>();
		List<Category> list = ServiceFactory.getCategoryService().findAll();
		for (Category category : list) {
			sm.addRecord(category);
		}
		return sm;
	}

	@RequestMapping(value = "parentCats/find", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage<Category> findParentCates() {
		SimpleMessage<Category> sm = new SimpleMessage<Category>();
		List<Category> list = BaseUtil.getParentCates();
		for (Category category : list) {
			sm.addRecord(category);
		}
		return sm;
	}

	// @RequestMapping(value = "categoryAdd", method = RequestMethod.GET)
	// public String categoryAdd(ModelMap map) {
	// setCats(map);
	// return "admin/categoryItem";
	// }

	@RequestMapping("products")
	public String products(ModelMap map) {
//		List<Product> list = ServiceFactory.getProductService().findAll();
//		List<Category> parents = BaseUtil.getParentCates();
//		Map<Long, List<Category>> subcatMap = BaseUtil.getSubCatsMap();
//		map.put("products", list);
		map.put("active", "products");
		map.put("collapse", "product");
//		map.put("parents", parents);
//		map.put("subcatMap", subcatMap);
		return "admin/products";
	}
	@RequestMapping(value = "products/find" , method = RequestMethod.GET)
	@ResponseBody
	public DatatableBean<ProductVo> findProducts(HttpServletRequest req , ModelMap map){
		int start = Integer.parseInt(req.getParameter("start") == null ? "0" : req.getParameter("start"));
		int length = Integer.parseInt(req.getParameter("length") == null ? "10" : req.getParameter("length"));
		int draw = Integer.parseInt(req.getParameter("draw") == null ? "10" : req.getParameter("draw"));
		
//		List<Product> list = ServiceFactory.getProductService().findAll();
//		List<Category> parents = BaseUtil.getParentCates();
//		Map<Long, List<Category>> subcatMap = BaseUtil.getSubCatsMap();
		
		FilterAttributes fa = FilterAttributes.blank();
//		List<Product> products = ServiceFactory.getProductService().findPaginationByAttributes(fa, start, length);
		Map<String,Integer> parameters = new HashMap<String,Integer>();
		parameters.put("start",start);
		parameters.put("length", length);
		List<ProductVo> productVOs = BaseUtil.getProductVoService().getProductByPage(parameters);
		
		int count = ServiceFactory.getProductService().findCountByAttributes(fa);
		DatatableBean<ProductVo> tb = new DatatableBean<ProductVo>();
		tb.setData(productVOs);
		tb.setDraw(draw + 1);
		tb.setRecordsFiltered(count);
		tb.setRecordsTotal(count);
		return tb;
	}

	@RequestMapping(value = "productAdd", method = RequestMethod.GET)
	public String productAdd(ModelMap map) {
		List<Category> parents = BaseUtil.getParentCates();
		Map<Long, List<Category>> subcatMap = BaseUtil.getSubCatsMap();
		map.put("active", "productAdd");
		map.put("collapse", "product");
		map.put("parents", parents);
		map.put("subcatMap", subcatMap);
		return "admin/productItem";
	}
	
	@RequestMapping(value = "productEdit", method = RequestMethod.GET)
	public String productEdit(HttpServletRequest req, ModelMap map) {
		List<Category> parents = BaseUtil.getParentCates();
		Map<Long, List<Category>> subcatMap = BaseUtil.getSubCatsMap();
		Long id = Long.valueOf(req.getParameter("id"));
		ProductVo vo = productVoService.get(id);
		Iterator<Entry<Long, List<Category>>> it = subcatMap.entrySet().iterator();
		while(it.hasNext()){
			Entry<Long, List<Category>> e = it.next();
			for (Category category : e.getValue()) {
				for (Category selected : vo.getCategorys()) {
					if(selected.getId().equals(category.getId())){
						category.setSelected(true);
						break;
					}
				}
			}
		}
		map.put("active", "productAdd");
		map.put("collapse", "product");
		map.put("parents", parents);
		map.put("subcatMap", subcatMap);
		map.put("vo", vo);
		return "admin/productItem";
	}

	// ////// 页面跳转END /////////

	// /业务
	@RequestMapping(value = "category/save", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage<Category> saveCategory(Category category, ModelMap map) {
		SimpleMessage<Category> sm = new SimpleMessage<Category>();
		if(category.getParentId() != null){
			List<Category> list = BaseUtil.getParentCates();
			for (Category category2 : list) {
				if(category2.getId().equals(category.getParentId())){
					category.setParentName(category2.getName());
					category.setParentNameEn(category2.getNameEn());
					break;
				}
			}
		}
		boolean b = ServiceFactory.getCategoryService().save(category);
		if (!b) {
			sm.getHead().setRep_code("101");
			sm.getHead().setRep_message("保存错误");
			return sm;
		} else {
			cleanCategory(category);
			sm.setItem(category);
		}
		return sm;
	}

	@RequestMapping(value = "category/delete/{id}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage<Category> deleteCategory(@PathVariable("id") Long id) {
		SimpleMessage<Category> sm = new SimpleMessage<Category>();
		boolean b = ServiceFactory.getCategoryService().deleteById(id);
		if (!b) {
			sm.getHead().setRep_code("101");
			sm.getHead().setRep_message("保存出错");
		} else {
			BaseUtil.clearParentCats();
			BaseUtil.clearAllSubCats();
			sm.getHead().setRep_code("200");
			sm.getHead().setRep_message("删除成功");
		}
		return sm;
	}

	@RequestMapping(value = "product/save", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage<Product> saveProduct(ProductVo productVo, ModelMap map) {
		JSONArray json = JSONArray.fromObject(productVo.getCategoryId());
		Object[] ids = json.toArray();
		List<Long> idList = new ArrayList<Long>();
		for (int i = 0; i < ids.length; i++) {
			idList.add(Long.valueOf(ids[i].toString()));
		}
		productVo.setCateIds(idList);
		boolean b = productVoService.save(productVo);
//		ProductVo vo = productVoService.get(productVo.getId());
//		for (Object id : ids) {
//			Long cateId = Long.valueOf(id.toString());
//			if(!vo.getCateIds().contains(cateId)){
//				CategoryProduct cp = new CategoryProduct();
//				cp.setCategoryId(cateId);
//				cp.setProductId(productVo.getId());
//				ServiceFactory.getCateProductService().save(cp);
//			}
//		}
		SimpleMessage<Product> sm = new SimpleMessage<Product>();
		if (!b) {
			map.put("error", "保存出错");
			sm.getHead().setRep_code("-100");
		} else {
			sm.getHead().setRep_code(SimpleMessageHead.REP_OK);
		}
		return sm;
	}

//	@RequestMapping(value = "product/edit", method = RequestMethod.POST)
//	@ResponseBody
//	public SimpleMessage editProduct(ProductVo productVo) {
//		SimpleMessage sm = new SimpleMessage();
//		boolean b = ServiceFactory.getProductService().update(product);
//		if (!b) {
//			sm.getHead().setRep_code("101");
//			sm.getHead().setRep_message("保存出错");
//		}
//		return sm;
//	}

	@RequestMapping(value = "product/delete", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage<?> deleteProduct(Long id) {
		SimpleMessage<?> sm = new SimpleMessage<Object>();
		boolean b = ServiceFactory.getProductService().deleteById(id);
		if (!b) {
			sm.getHead().setRep_code("101");
			sm.getHead().setRep_message("保存出错");
		} else {
			sm.getHead().setRep_code("200");
			sm.getHead().setRep_message("删除成功");
		}
		return sm;
	}
	
	@RequestMapping(value = "quarter/change/{quarter}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage<?> quarterChange(@PathVariable("quarter")Integer quarter) {
		SimpleMessage<?> sm = new SimpleMessage<Object>();
		Config config = BaseUtil.getCurrQuarter();
		if(config == null){
			config = new Config();
			config.setKey("quarter");
			config.setDescription("");
			config.setType("QUARTER");
		} 
		config.setValue(quarter.toString());
		boolean b = ServiceFactory.getConfigService().save(config);
		if(b){
			BaseUtil.setCurrQuarter(config);
		}
		return sm;
	}

	private void cleanCategory(Category category) {
		BaseUtil.clearParentCats();
		BaseUtil.clearAllSubCats();
	}

	private void setCats(ModelMap map) {
		// List<Category> list = ServiceFactory.getCategoryService().findAll();
		// map.put("cats", list);
		// map.put("parents", BaseUtil.getParentCates());
		map.put("active", "categorys");
		map.put("collapse", "category");
	}

	@SuppressWarnings("unused")
	private void setOrders(ModelMap map) {
		map.put("active", "orders");
		map.put("collapse", "order");
	}

	private ProductVoService productVoService = SpringContextFactory
			.getSpringBean(ProductVoService.class);

}
