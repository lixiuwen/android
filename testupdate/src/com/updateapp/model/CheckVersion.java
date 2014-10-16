package com.updateapp.model;

import java.io.Serializable;

public class CheckVersion implements Serializable {
	private static final long serialVersionUID = -5954214045807756960L;
	private String downPath;
	private String appName;
	private int appVersion;

	public String getDownPath() {
		return downPath;
	}

	public void setDownPath(String downPath) {
		this.downPath = downPath;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public int getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(int appVersion) {
		this.appVersion = appVersion;
	}

}
