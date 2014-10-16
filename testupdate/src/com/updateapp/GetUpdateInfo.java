package com.updateapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.updateapp.model.CheckVersion;

public class GetUpdateInfo {
	private String downPath = "http://172.24.68.4:8080/apk/";
	private String appName = "layou_home.apk";
	private String appVersion = "check.json";
	HttpResponse response;
	HttpPost httpPost;
	HttpEntity entity;
	private HttpClient client;

	public GetUpdateInfo() {
		client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);// 设置连接超时范围
		HttpConnectionParams.setSoTimeout(httpParams, 8000);
	}

	public CheckVersion getUpdataVerJSON() {
		try {
			httpPost = new HttpPost(downPath + appVersion);
			response = client.execute(httpPost);
			entity = response.getEntity();
			StringBuffer data = new StringBuffer();
			if (entity != null) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(entity.getContent(), "GBK"), 8192);
				String line = null;
				while ((line = reader.readLine()) != null) {
					data.append(line);
					System.out.println("fufei-----数据放入成功");
				}
				reader.close();
				if (!TextUtils.isEmpty(data.toString())) {
					return JSONObject.parseObject(data.toString(),
							CheckVersion.class);
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void downloadFile() {
		HttpGet get = new HttpGet(downPath + appName);
		HttpResponse response;
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			long length = entity.getContentLength();
			Log.isLoggable("DownTag", (int) length);
			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			if (is != null) {
				File file = new File(Environment.getExternalStorageDirectory(),
						"NewAppSample.apk");
				fileOutputStream = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int ch = -1;
				do {
					ch = is.read(buf);
					if (ch <= 0)
						break;
					fileOutputStream.write(buf, 0, ch);
				} while (true);
				is.close();
				fileOutputStream.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
