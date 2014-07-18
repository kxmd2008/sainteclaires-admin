package org.luis.sainteclaires.admin.rest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.luis.basic.util.BasicUtil;
import org.luis.basic.util.FileUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUpload {

	@ResponseBody
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String upload(String fileName, MultipartFile imgFile) {
		try {
			if (!imgFile.isEmpty()) {
				String file = BasicUtil.getWebAppPath() + "product/imgs/"
						+ imgFile.getOriginalFilename();
				FileUtil.mkdirs(file);
				byte[] bytes = imgFile.getBytes();
				FileOutputStream fos = new FileOutputStream(file); // 上传到写死的上传路径
				fos.write(bytes); // 写入文件
				fos.close();
			}
			String string = new String(imgFile.getBytes(), "UTF-8");
			System.out.println(string);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "OK";
	}
}
