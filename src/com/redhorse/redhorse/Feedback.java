package com.redhorse.redhorse;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpConnection;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.

public class Feedback extends Activity implements OnItemClickListener {

	private EditText feedback;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.feedback);
		Button button = (Button) findViewById(R.id.btn_send);
		button.setOnClickListener(SendListener);
		button = (Button) findViewById(R.id.btn_quit);
		button.setOnClickListener(QuitListener);
		feedback = (EditText) findViewById(R.id.et_feedback);
		feedback.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View arg0, boolean isFocused) {
				// TODO Auto-generated method stub

				if (isFocused == true) {
					feedback.selectAll();
				} else {
				}
			}
		});
		
	}

	private OnClickListener QuitListener = new OnClickListener() {
		public void onClick(View v) {
			Feedback.this.finish();
		}
	};

	private OnClickListener SendListener = new OnClickListener() {
		public void onClick(View v) {
			postData(feedback.getText()
					.toString(), "小红马");
		}
	};

	public void postData(String name, String type) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpRequest = new HttpPost("http://pidouhui.appspot.com/add");

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("name", name));
		nameValuePairs.add(new BasicNameValuePair("type", type));
		Log.e("feedback", name);
		Log.e("feedback", type);

		try {
			/* 添加请求参数到请求对象 */
			httpRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));
			/* 发送请求并等待响应 */
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			/* 若状态码为200 ok */
			String strResult;
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 读返回数据 */
				strResult = EntityUtils.toString(httpResponse
						.getEntity());
			} else {
				strResult = httpResponse.getStatusLine().toString();
			}
			Log.e("feedback", strResult);
//			feedback.setText(strResult);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}
}
