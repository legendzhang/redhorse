/*
 **SiteFileFetch.java
 */
package com.redhorse.netfox;

import java.io.*;
import java.net.*;

import com.redhorse.redhorse.dbDownloadAdapter;
import com.redhorse.redhorse.redhorse;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SiteFileFetch extends Thread {
	private static final int STATE_FINISH = 0;
	
	SiteInfoBean siteInfoBean = null; // 文件信息Bean
	long[] nStartPos; // 开始位置
	long[] nEndPos; // 结束位置
	FileSplitterFetch[] fileSplitterFetch; // 子线程对象
	long nFileLength; // 文件长度
	boolean bFirst = true; // 是否第一次取文件
	boolean bStop = false; // 停止标志
	File tmpFile; // 文件下载的临时信息
	DataOutputStream output; // 输出到文件的输出流
	private Handler handler = null;

	public SiteFileFetch(Handler handlertmp, SiteInfoBean bean) throws IOException {
		handler = handlertmp;
		siteInfoBean = bean;
		// tmpFile = File.createTempFile ("zhong","1111",new
		// File(bean.getSFilePath()));
		tmpFile = new File(bean.getSFilePath() + File.separator
				+ bean.getSFileName() + ".info" + ".redhorse.rhs");
		if (tmpFile.exists()) {
			bFirst = false;
			read_nPos();
		} else {
			nStartPos = new long[bean.getNSplitter()];
			nEndPos = new long[bean.getNSplitter()];
		}
	}

	public void run() {
		// 获得文件长度
		// 分割文件
		// 实例FileSplitterFetch
		// 启动FileSplitterFetch线程
		// 等待子线程返回
		try {
			if (bFirst) {
				nFileLength = getFileSize();
				if (nFileLength == -1) {
					System.err.println("File Length is not known!");
				} else if (nFileLength == -2) {
					System.err.println("File is not access!");
				} else {
					for (int i = 0; i < nStartPos.length; i++) {
						nStartPos[i] = (long) (i * (nFileLength / nStartPos.length));
					}
					for (int i = 0; i < nEndPos.length - 1; i++) {
						nEndPos[i] = nStartPos[i + 1];
					}
					nEndPos[nEndPos.length - 1] = nFileLength;
				}
			}
			String url =siteInfoBean.getSSiteURL();
			String fullfilename = siteInfoBean.getSFilePath()+ File.separator + siteInfoBean.getSFileName();
			dbDownloadAdapter dbDownload = new dbDownloadAdapter(siteInfoBean.getCtx());
			dbDownload.open();
			long rowid = dbDownload.insertTitle("", fullfilename, url, fullfilename, "d");
			
			// 启动子线程
			fileSplitterFetch = new FileSplitterFetch[nStartPos.length];
			for (int i = 0; i < nStartPos.length; i++) {
				fileSplitterFetch[i] = new FileSplitterFetch(
						siteInfoBean.getSSiteURL(), siteInfoBean.getSFilePath()
								+ File.separator + siteInfoBean.getSFileName() + ".redhorse.rhs",
						nStartPos[i], nEndPos[i], i);
				Utility.log("Thread " + i + " , nStartPos = " + nStartPos[i]
						+ ", nEndPos = " + nEndPos[i]);
				fileSplitterFetch[i].start();
			}
			// fileSplitterFetch[nPos.length-1] = new
			// FileSplitterFetch(siteInfoBean.getSSiteURL(),siteInfoBean.getSFilePath()
			// + File.separator +
			// siteInfoBean.getSFileName(),nPos[nPos.length-1],nFileLength,nPos.length-1);
			// Utility.log("Thread " + (nPos.length-1) + " , nStartPos = " +
			// nPos[nPos.length-1] + ",nEndPos = " + nFileLength);
			// fileSplitterFetch[nPos.length-1].start();
			// 等待子线程结束
			// int count = 0;
			// 是否结束while循环
			boolean breakWhile = false;
			while (!bStop) {
				write_nPos();
				Utility.sleep(500);
				breakWhile = true;
				for (int i = 0; i < nStartPos.length; i++) {
					if (!fileSplitterFetch[i].bDownOver) {
						breakWhile = false;
						break;
					}
				}
				if (breakWhile)
					break;
				// count++;
				// if(count>4)
				// siteStop();
			}
			System.err.println("Downloading finished!");
			tmpFile.delete();
			File okFile = new File(siteInfoBean.getSFilePath() + File.separator + siteInfoBean.getSFileName() + ".redhorse.rhs");
			Log.e("redhorse", "okfilename " + siteInfoBean.getSFilePath() + File.separator + siteInfoBean.getSFileName() + ".redhorse.rhs");
			okFile.renameTo(new File(siteInfoBean.getSFilePath() + File.separator + siteInfoBean.getSFileName()));
			Log.e("redhorse", "okfilename " + siteInfoBean.getSFilePath() + File.separator + siteInfoBean.getSFileName());
			dbDownload.updateTitle(rowid, "", fullfilename, url, fullfilename, "f");

			Message msg = handler.obtainMessage(); 
            Bundle b = new Bundle(); 
            b.putInt("state", STATE_FINISH); 
            msg.setData(b); 
            handler.sendMessage(msg); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获得文件长度
	public long getFileSize() {
		int nFileLength = -1;
		try {
			URL url = new URL(siteInfoBean.getSSiteURL());
			HttpURLConnection httpConnection = (HttpURLConnection) url
					.openConnection();
			httpConnection.setRequestProperty("User-Agent", "NetFox");
			int responseCode = httpConnection.getResponseCode();
			if (responseCode >= 400) {
				processErrorCode(responseCode);
				return -2; // -2 represent access is error
			}
			nFileLength = httpConnection.getContentLength();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utility.log(nFileLength);
		System.err.println(nFileLength);
		return nFileLength;
	}

	// 保存下载信息（文件指针位置）
	private void write_nPos() {
		try {
			output = new DataOutputStream(new FileOutputStream(tmpFile));
			output.writeInt(nStartPos.length);
			for (int i = 0; i < nStartPos.length; i++) {
				// output.writeLong(nPos[i]);
				output.writeLong(fileSplitterFetch[i].nStartPos);
				output.writeLong(fileSplitterFetch[i].nEndPos);
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 读取保存的下载信息（文件指针位置）
	private void read_nPos() {
		try {
			DataInputStream input = new DataInputStream(new FileInputStream(
					tmpFile));
			int nCount = input.readInt();
			nStartPos = new long[nCount];
			nEndPos = new long[nCount];
			for (int i = 0; i < nStartPos.length; i++) {
				nStartPos[i] = input.readLong();
				nEndPos[i] = input.readLong();
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processErrorCode(int nErrorCode) {
		System.err.println("Error Code : " + nErrorCode);
	}

	// 停止文件下载
	public void siteStop() {
		bStop = true;
		for (int i = 0; i < nStartPos.length; i++)
			fileSplitterFetch[i].splitterStop();
	}
}