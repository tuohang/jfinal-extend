package com.tuohang.jfinal.extend.kit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * HttpKit的加强版，用httpClient实现（主要是用于文件操作）
 * 
 * @author Lims
 * @date 2015年9月20日
 * @version 1.0
 */
public class HttpKitPlus {

	/**
	 * post请求上传单个文件
	 * 
	 * @param url
	 *            url地址
	 * @param filekey
	 *            上传文件对应的请求参数
	 * @param file
	 *            上传文件
	 * @return 返回结果
	 */
	public static String postSingleFile(String url, String filekey, File file) {
		CloseableHttpClient client = null;
		CloseableHttpResponse resp = null;
		try {
			client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			FileBody fb = new FileBody(file);
			HttpEntity reqEntity = MultipartEntityBuilder.create()
					.addPart(filekey, fb).build();
			post.setEntity(reqEntity);
			resp = client.execute(post);
			int sc = resp.getStatusLine().getStatusCode();
			if (sc >= 200 && sc < 300) {
				String json = EntityUtils.toString(resp.getEntity());
				return json;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (client != null)
					client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (resp != null)
					resp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 获取下载文件（单文件）
	 * 
	 * @param url
	 *            url地址
	 * @param f
	 *            下载文件
	 */
	public static void getFile(String url, File f) {
		CloseableHttpClient client = null;
		CloseableHttpResponse resp = null;

		try {
			client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			resp = client.execute(get);
			int sc = resp.getStatusLine().getStatusCode();
			if (sc >= 200 && sc < 300) {
				InputStream is = resp.getEntity().getContent();
				FileUtils.copyInputStreamToFile(is, f);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (client != null)
					client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (resp != null)
					resp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
