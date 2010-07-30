package com.redhorse.redhorse;

import java.io.File;

import weibo4j.Status;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

class weiboTask extends AsyncTask<Object, Integer, String> {  
	  
    // 可变长的输入参数，与AsyncTask.exucute()对应  
	private Context ctx = null;
	
    @Override  
    protected String doInBackground(Object... params) {
    	ctx = (Context) params[3];
        Weibo weibo = new Weibo((String)params[0], (String)params[1]);
        weibo4j.Status status = null;
        String result = "";
 		try {
			File file=new File((String)params[4]);
			if(file==null){
				status = weibo.updateStatus((String)params[2]);
			} else {
				status = weibo.uploadStatus((String)params[2], file);
			}
    		result = "微博发送成功！"; 
 		} catch (WeiboException e2) {
 			// TODO Auto-generated catch block
 			Log.e("weibo error", e2.toString());
    		result = "微博发送失败，\n请检查用户名密码及网络设置！"; 
 		}
        return result;  
    }  
    @Override  
    protected void onCancelled() {  
        super.onCancelled();  
    }  
    @Override  
    protected void onPostExecute(String result) {  
        // 返回HTML页面的内容  
    	Toast.makeText(ctx, 
    			result, 
                Toast.LENGTH_LONG) 
             .show();
    }  
    @Override  
    protected void onPreExecute() {  
        // 任务启动，可以在这里显示一个对话框，这里简单处理  
    }  
    @Override  
    protected void onProgressUpdate(Integer... values) {  
        // 更新进度  
    }  
    
}
