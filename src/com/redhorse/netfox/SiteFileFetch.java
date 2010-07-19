/*
 **SiteFileFetch.java
 */
package com.redhorse.netfox;

import java.io.*;
import java.net.*;

import com.redhorse.redhorse.R;
import com.redhorse.redhorse.redhorse;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class SiteFileFetch extends Thread {
	private static final int STATE_FINISH = 0;
	
	SiteInfoBean siteInfoBean = null; // �ļ���ϢBean
	long[] nStartPos; // ��ʼλ��
	long[] nEndPos; // ����λ��
	FileSplitterFetch[] fileSplitterFetch; // ���̶߳���
	long nFileLength; // �ļ�����
	boolean bFirst = true; // �Ƿ��һ��ȡ�ļ�
	boolean bStop = false; // ֹͣ��־
	File tmpFile; // �ļ����ص���ʱ��Ϣ
	DataOutputStream output; // ������ļ��������
	private Handler handler = null;

	public SiteFileFetch(Handler handlertmp, SiteInfoBean bean) throws IOException {
		handler = handlertmp;
		siteInfoBean = bean;
		// tmpFile = File.createTempFile ("zhong","1111",new
		// File(bean.getSFilePath()));
		tmpFile = new File(bean.getSFilePath() + File.separator
				+ bean.getSFileName() + ".info");
		if (tmpFile.exists()) {
			bFirst = false;
			read_nPos();
		} else {
			nStartPos = new long[bean.getNSplitter()];
			nEndPos = new long[bean.getNSplitter()];
		}
	}

	public void run() {
		// ����ļ�����
		// �ָ��ļ�
		// ʵ��FileSplitterFetch
		// ����FileSplitterFetch�߳�
		// �ȴ����̷߳���
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
			// �������߳�
			fileSplitterFetch = new FileSplitterFetch[nStartPos.length];
			for (int i = 0; i < nStartPos.length; i++) {
				fileSplitterFetch[i] = new FileSplitterFetch(
						siteInfoBean.getSSiteURL(), siteInfoBean.getSFilePath()
								+ File.separator + siteInfoBean.getSFileName(),
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
			// �ȴ����߳̽���
			// int count = 0;
			// �Ƿ����whileѭ��
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
			Message msg = handler.obtainMessage(); 
            Bundle b = new Bundle(); 
            b.putInt("state", STATE_FINISH); 
            msg.setData(b); 
            handler.sendMessage(msg); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ����ļ�����
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

	// ����������Ϣ���ļ�ָ��λ�ã�
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

	// ��ȡ�����������Ϣ���ļ�ָ��λ�ã�
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

	// ֹͣ�ļ�����
	public void siteStop() {
		bStop = true;
		for (int i = 0; i < nStartPos.length; i++)
			fileSplitterFetch[i].splitterStop();
	}
}