/*
 **SiteInfoBean.java
 */
package com.redhorse.netfox;

import android.content.Context;

public class SiteInfoBean {
	private String sSiteURL; // Site's URL
	private String sFilePath; // Saved File's Path
	private String sFileName; // Saved File's Name
	private int nSplitter; // Count of Splited Downloading File
	private Context ctx;

	public SiteInfoBean() {
		// default value of nSplitter is 5
		this("", "", "", 5, null);
	}

	public SiteInfoBean(String sURL, String sPath, String sName, int nSpiltter, Context ctx) {
		sSiteURL = sURL;
		sFilePath = sPath;
		sFileName = sName;
		this.nSplitter = nSpiltter;
		this.ctx = ctx; 
	}

	public String getSSiteURL() {
		return sSiteURL;
	}

	public void setSSiteURL(String value) {
		sSiteURL = value;
	}

	public String getSFilePath() {
		return sFilePath;
	}

	public void setSFilePath(String value) {
		sFilePath = value;
	}

	public String getSFileName() {
		return sFileName;
	}

	public void setSFileName(String value) {
		sFileName = value;
	}

	public int getNSplitter() {
		return nSplitter;
	}

	public void setNSplitter(int nCount) {
		nSplitter = nCount;
	}

	public Context getCtx() {
		return ctx;
	}

	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}

}