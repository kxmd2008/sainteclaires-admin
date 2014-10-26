package org.luis.sainteclaires.admin.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.luis.basic.domain.FilterAttributes;
import org.luis.basic.rest.model.DatatableBean;
import org.luis.basic.rest.model.SimpleMessage;
import org.luis.sainteclaires.base.INameSpace;
import org.luis.sainteclaires.base.bean.Category;
import org.luis.sainteclaires.base.bean.Config;
import org.luis.sainteclaires.base.bean.Picture;
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
public class BackGroundRest {

	@RequestMapping("bgMgt")
	public String bgMgt(ModelMap map){
		return "admin/backgroundMgt";
	}
	
	@RequestMapping("bgAdd")
	public String bgAdd(ModelMap map){
		setParentCate(map);
		return "admin/backgroundItem";
	}
	
	@RequestMapping("bgEdit")
	public String bgEdit(HttpServletRequest req, ModelMap map){
		Long id = Long.valueOf(req.getParameter("id"));
		FilterAttributes fa = FilterAttributes.blank().add("id", id);
		Config config = ServiceFactory.getConfigService().findOneByFilter(fa);
		Picture picture = new Picture();
		picture.setId(config.getId());
		picture.setName(config.getKey());
		picture.setPics(parsePic(config.getValue()));
		map.put("picture", picture);
		setParentCate(map);
		return "admin/backgroundItem";
	}
	
	@RequestMapping("bg/find")
	@ResponseBody
	public DatatableBean<Picture> bgFind(ModelMap map , HttpServletRequest req){
//		int start = Integer.parseInt(req.getParameter("start") == null ? "0" : req.getParameter("start"));
//		int length = Integer.parseInt(req.getParameter("length") == null ? "10" : req.getParameter("length"));
		int draw = Integer.parseInt(req.getParameter("draw") == null ? "10" : req.getParameter("draw"));
//		SimpleMessage<Picture> sm = new SimpleMessage<Picture>();
		FilterAttributes fa = FilterAttributes.blank().add("type", INameSpace.TYPE_BGPIC);
		List<Config> list = ServiceFactory.getConfigService().findByAttributes(fa);
		DatatableBean<Picture> tb = new DatatableBean<Picture>();
		List<Picture> pics = new ArrayList<Picture>();
		for (Config config : list) {
			Picture p = new Picture();
			p.setId(config.getId());
			p.setName(config.getKey());
			p.setPics(parsePic(config.getValue()));
//			sm.addRecord(p);
			pics.add(p);
		}
		tb.setData(pics);
		tb.setDraw(draw + 1);
		tb.setRecordsFiltered(pics.size());
		tb.setRecordsTotal(pics.size());
		return tb;
	}
	
	@RequestMapping(value="bg/save", method=RequestMethod.POST)
	@ResponseBody
	public SimpleMessage<Picture> bgsave(Picture pic, ModelMap map){
		SimpleMessage<Picture> sm = new SimpleMessage<Picture>();
		FilterAttributes fa = FilterAttributes.blank().add("key", pic.getName());
		Config config = ServiceFactory.getConfigService().findOneByFilter(fa);
		if(config != null && pic.getId() == null && config.getId().equals(pic.getId())){
			sm.getHead().setRep_code("2001");
			sm.getHead().setRep_message("保存失败!"+pic.getName() + "背景图片已存在");;
			return sm;
		}
		if(config == null){
			config = new Config();
		}
		StringBuilder sb = new StringBuilder();
		for (String s : pic.getPics()) {
			sb.append(BaseUtil.getBgPath2()).append(s).append(",");
		}
		config.setValue(sb.deleteCharAt(sb.length()-1).toString());
		config.setKey(INameSpace.TYPE_BGPIC);
		config.setType(pic.getName());
		ServiceFactory.getConfigService().save(config);
		BaseUtil.storeBgPic(config.getKey(), BaseUtil.config2Pic(config));
		return sm;
	}
	
	@RequestMapping(value="bg/delete/{id}", method=RequestMethod.GET)
	@ResponseBody
	public SimpleMessage<Picture> bgsave(@PathVariable("id") Long id){
		SimpleMessage<Picture> sm = new SimpleMessage<Picture>();
		Config config = new Config();
		config.setId(id);
		ServiceFactory.getConfigService().delete(config);
		BaseUtil.removeBgPic(id);
		return sm;
	}
	
	private void setParentCate(ModelMap map){
		List<Category> parents = BaseUtil.getParentCates();
		map.put("parents", parents);
	}
	
	private List<String> parsePic(String pics){
		List<String> list = new ArrayList<String>();
		if(pics != null){
			String[] temp = pics.split(",");
			for (String string : temp) {
				list.add(string);
			}
		}
		return list;
	}
	
}
