package org.luis.sainteclaires.admin.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.log4j.Logger;
import org.luis.basic.rest.model.SimpleMessage;
import org.luis.sainteclaires.base.INameSpace;
import org.luis.sainteclaires.base.util.BaseUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
public class FileUpload {
	private static final Logger logger = Logger.getLogger(FileUpload.class);
	@ResponseBody
	@RequestMapping(value = "auth/upload", method = RequestMethod.POST)
	public SimpleMessage<Object> upload(String fileName, MultipartHttpServletRequest request) {
		SimpleMessage<Object> sm = new SimpleMessage<Object>();
		try {
			String type = (String) request.getParameter("type");
			String filepath = null;
			if(INameSpace.TYPE_BGPIC.equals(type)){
				filepath = BaseUtil.getBgPath();
			} else {
				filepath = BaseUtil.getProductPath();
			}
			List<MultipartFile> files = request.getFiles("files");
			if (!files.isEmpty()) {
				// 文件上传路径
				
				File file = new File(filepath);
				if(!file.exists()){
					file.mkdirs();
				}
				for (int i = 0; i < files.size(); i++) {
					if (!files.get(i).isEmpty()) {
						String path = filepath + files.get(i).getOriginalFilename();
						logger.info("上传文件：" + path);
						byte[] bytes = files.get(i).getBytes();
						FileOutputStream fos = new FileOutputStream(path); // 写入文件
						fos.write(bytes);
						fos.close();
					}
				}
				logger.info("上传文件成功！");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sm;
	}
}
