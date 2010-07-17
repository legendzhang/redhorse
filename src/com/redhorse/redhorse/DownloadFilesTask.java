package com.redhorse.redhorse;

import com.redhorse.netfox.SiteFileFetch;
import com.redhorse.netfox.SiteInfoBean;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

class DownloadFilesTask extends AsyncTask<Object, Integer, Long> {
	protected static final int STATE_FINISH = 0;
	protected static final int STATE_ERROR = 1;
	
	private String sSiteURL;
	private String sFilePath;
	private String sFileName;
	private Context ctx = null;

	private NotificationManager mNM;

	private void notification(Context ctx, String msginfo) {
		try {
			mNM = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent intent = new Intent(ctx, bookmarkslist.class);
			CharSequence appName = ctx.getString(R.string.app_name);
			Notification notification = new Notification(R.drawable.icon,
					appName, System.currentTimeMillis());
			notification.flags = Notification.FLAG_NO_CLEAR;
			CharSequence appDescription = msginfo;
			notification.setLatestEventInfo(ctx, appName,
					appDescription, PendingIntent.getActivity(ctx,
							0, intent, PendingIntent.FLAG_CANCEL_CURRENT));
			mNM.notify(0, notification);
		} catch (Exception e) {
			mNM = null;
		}
	}

	protected Long doInBackground(Object... objects) {
		long totalSize = 0;
		// totalSize += Downloader.downloadFile(urls[i]);
		// publishProgress((int) ((i / (float) count) * 100)); //下载进度计算
		sSiteURL = (String) objects[0]; // Site's URL
		sFilePath = (String) objects[1]; // Saved File's Path
		sFileName = (String) objects[2]; // Saved File's Name
		int nSplitter = Integer.parseInt((String) objects[3]); // Count of Splited Downloading File
		ctx = (Context) objects[4];
		try {
			SiteInfoBean bean = new SiteInfoBean(sSiteURL,
					sFilePath, sFileName, nSplitter);
			SiteFileFetch fileFetch = new SiteFileFetch(handler,bean);
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

    private final Handler handler = new Handler(Looper.getMainLooper()) {
    	 
        public void handleMessage(Message msg) { // 处理Message，更新ListView
            int state = msg.getData().getInt("state");
            String msginfo = sFileName + ctx.getString(R.string.info_downloading_finished);
            switch(state){ 
                case STATE_FINISH: 
                    Toast.makeText(ctx, 
                    		msginfo, 
                            Toast.LENGTH_LONG) 
                         .show();
                    notification(ctx, msginfo);
                    break; 
                case STATE_ERROR: 
                      break; 
               default: 
 
            } 
        } 
    };
}