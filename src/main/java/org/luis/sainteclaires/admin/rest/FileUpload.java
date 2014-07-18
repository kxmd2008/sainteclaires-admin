package org.luis.sainteclaires.admin.rest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.luis.basic.util.BasicUtil;
import org.luis.basic.util.FileUtil;
import org.luis.basic.util.ImageUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUpload {
	private static final Logger logger = Logger.getLogger(FileUpload.class);
	@ResponseBody
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String upload(String fileName, MultipartFile ufile) {
		try {
			if (!ufile.isEmpty()) {
				// 文件上传路径
				String file = BasicUtil.getWebAppPath() + "product/imgs/"
						+ ufile.getOriginalFilename();
				logger.info("上传文件：" + file);
				FileUtil.mkdirs(file);
				byte[] bytes = ufile.getBytes();
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(bytes); // 写入文件
				fos.close();
				logger.info("上传文件成功！");
				String previewFile = BasicUtil.getWebAppPath()
						+ "product/preview/" + ufile.getOriginalFilename();
				ImageUtil.createPreviewImage(file, previewFile);
				logger.info("生成缩略图"+previewFile);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "OK";
	}
}
