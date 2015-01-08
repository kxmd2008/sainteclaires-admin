package org.luis.sainteclaires.admin.rest;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.luis.basic.rest.model.SimpleMessage;
import org.luis.basic.util.SpringContextFactory;
import org.luis.sainteclaires.base.bean.Category;
import org.luis.sainteclaires.base.bean.PicShow;
import org.luis.sainteclaires.base.bean.service.PicShowService;
import org.luis.sainteclaires.base.util.BaseUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 图片展示REST
 * @author Guoliang.Li
 *
 */
@Controller
@RequestMapping("/auth")
public class PicShowAdminRest {
	private static final Logger logger = Logger.getLogger(PicShowAdminRest.class);

	@RequestMapping("showPic")
	public String toUploadPicPage(ModelMap map){
		List<Category> parents = BaseUtil.getParentCates();
		Map<Long, List<Category>> subcatMap = BaseUtil.getSubCatsMap();
		map.put("parents", parents);
		map.put("subcatMap", subcatMap);
		return "admin/picShowUpload";
	}
	
	@RequestMapping(value="picShow/save", method=RequestMethod.POST)
	@ResponseBody
	public SimpleMessage<Object> picSave(PicShow ps){
		SimpleMessage<Object> sm = new SimpleMessage<Object>();
		try {
			picShowService.save(ps);
		} catch (Exception e){
			sm.getHead().setRep_code("100");
			logger.error(e.getMessage(), e);
		}
		return sm;
	}
	
	private PicShowService picShowService = SpringContextFactory
			.getSpringBean(PicShowService.class);
}
