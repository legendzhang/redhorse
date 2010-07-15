package com.redhorse.redhorse;

import com.redhorse.netfox.SiteFileFetch;
import com.redhorse.netfox.SiteInfoBean;

import android.os.AsyncTask;

class DownloadFilesTask extends AsyncTask<Object, Integer, Long> {
	protected Long doInBackground(Object... objects) {
		long totalSize = 0;
		// totalSize += Downloader.downloadFile(urls[i]);
		// publishProgress((int) ((i / (float) count) * 100)); //下载进度计算
		String sSiteURL = (String) objects[0]; // Site's URL
		String sFilePath = (String) objects[1]; // Saved File's Path
		String sFileName = (String) objects[2]; // Saved File's Name
		int nSplitter = Integer.parseInt((String) objects[3]); // Count of Splited Downloading File
		try {
			SiteInfoBean bean = new SiteInfoBean(sSiteURL,
					sFilePath, sFileName, nSplitter);
			SiteFileFetch fileFetch = new SiteFileFetch(bean);
			fileFetch.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalSize;
	}

	protected void onProgressUpdate(Integer... progress) {
		// setProgressPercent(progress[0]); //更新进度显示
	}

	protected void onPostExecute(Long result) {
		// showDialog("Android123下载测试 " + result + " bytes");
	}
}