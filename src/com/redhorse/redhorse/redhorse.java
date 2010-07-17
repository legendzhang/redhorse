package com.redhorse.redhorse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.yy.ah.util.HttpRequestParser;
import com.yy.ah.util.HttpRequestParser.Request;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class redhorse extends Activity {

	private dbConfigKeyValueHelper dbConfigKeyValue = null;
	private dbBookmarksAdapter dbBookmarks = null;
	private Cursor dbConfigKeyValueCursor;

	private final static int ITEM_ID_GOBACK = 1;
	private final static int ITEM_ID_GOFORWARD = 2;
	private final static int ITEM_ID_GOSTOP = 3;
	private final static int ITEM_ID_GOHOME = 4;
	private final static int ITEM_ID_GODOWNLOADMANAGER = 5;
	private final static int ITEM_ID_GOQUIT = 6;
	private final static int ITEM_ID_BOOKMARKS = 7;
	private final static int ITEM_ID_ADDBOOKMARK = 8;
	private final static int ITEM_ID_REFRESH = 9;

	private final static String STRING_HOMEPAGEURL = "http://10.1.1.74/a";
	private final static String STRING_SAVETODIR = "/sdcard/download";

	final Context myApp = this;

	private WebView testWebView = null;
	private String homepageurl;
	private String savetodir;
	private long bookmarkid;

	private NotificationManager mNM;

	private void notification() {
		try {
			mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Intent intent = new Intent(this, bookmarkslist.class);
			CharSequence appName = getString(R.string.app_name);
			Notification notification = new Notification(R.drawable.icon,
					appName, System.currentTimeMillis());
			notification.flags = Notification.FLAG_NO_CLEAR;
			CharSequence appDescription = "";
			notification.setLatestEventInfo(redhorse.this, appName,
					appDescription, PendingIntent.getActivity(getBaseContext(),
							0, intent, PendingIntent.FLAG_CANCEL_CURRENT));
			mNM.notify(0, notification);
		} catch (Exception e) {
			mNM = null;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

//		notification();

		dbConfigKeyValue = new dbConfigKeyValueHelper(this);
		dbConfigKeyValue.insert("savetodir", savetodir);
		dbConfigKeyValueCursor = dbConfigKeyValue.select("savetodir");
		dbConfigKeyValueCursor.moveToFirst();
		Log.e("debug", "config savetodir is "
				+ dbConfigKeyValueCursor.getString(2));
		SharedPreferences share = this.getPreferences(MODE_PRIVATE);
		this.homepageurl = share.getString("homepageurl", "");
		if (this.homepageurl == "") {
			this.homepageurl = STRING_HOMEPAGEURL;
			Editor editor = share.edit();// 取得编辑器
			editor.putString("homepageurl", this.homepageurl);
			editor.commit();// 提交刷新数据
		}
		this.savetodir = share.getString("savetodir", "");
		if (this.savetodir == "") {
			this.savetodir = STRING_SAVETODIR;
			Editor editor = share.edit();// 取得编辑器
			editor.putString("savetodir", this.savetodir);
			editor.commit();// 提交刷新数据
		}

		dbBookmarks = new dbBookmarksAdapter(this);
		dbBookmarks.open();
		// bookmarkid =
		// dbBookmarks.insertTitle("","redhorse主页",this.homepageurl);

		testWebView = (WebView) this.findViewById(R.id.WebView01);
		testWebView.getSettings().setJavaScriptEnabled(true);
		testWebView.loadUrl(homepageurl);

		testWebView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (!testWebView.hasFocus()) {
						testWebView.requestFocus();
					}
					break;
				default:
					break;
				}

				// 点击关闭软键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						((EditText) findViewById(R.id.urlText))
								.getWindowToken(), 0);
				return false;
			}
		});

		// Button btn_loadUrl = (Button) this.findViewById(R.id.loadUrl);
		final EditText urlText = (EditText) this.findViewById(R.id.urlText);

		urlText.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub

				// 如果按的是回车键则加载url
				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					loadUrl(urlText);
				}
				return false;
			}
		});

		// btn_loadUrl.setOnClickListener(new View.OnClickListener() {

		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// loadUrl(urlText);
		// }
		// });

		// 设置webview为一个单独的client, 这样可以使加载url不调用系统的browser
		testWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// Log.i("",
				// ".......EXPID_LOCAL.. shouldOverrideUrlLoading......url=="+url);
				view.loadUrl(url);
				return true;
			}

			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				Log.e("url", "finish url is " + url);
				((EditText) findViewById(R.id.urlText)).setText(url);
				testWebView.requestFocus();
			}
		});

		testWebView.setDownloadListener(new WebDownloadListener());

		/* WebChromeClient must be set BEFORE calling loadUrl! */
		testWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final android.webkit.JsResult result) {
				new AlertDialog.Builder(myApp).setTitle(R.string.onjsalert)
						.setMessage(message).setPositiveButton(
								android.R.string.ok,
								new AlertDialog.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								}).setCancelable(false).create().show();

				return true;
			};
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (testWebView == null) {
			return true;
		}
		// 按下BACK键回到历史页面中
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (testWebView.canGoBack()) {
				testWebView.goBack();
				return true;
			} else {
				// 退出询问
				AlertDialog exitDialog = new AlertDialog.Builder(this)
						.setTitle(R.string.wanttoquit).setPositiveButton(
								R.string.yes,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										finish();
									}
								}).setNegativeButton(R.string.no, null)
						.create();
				exitDialog.show();
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	// 加载url同时关闭软键盘
	private void loadUrl(EditText urlText) {
		String url = "";
		url = urlText.getText().toString();
		if (!url.toLowerCase().startsWith("http")) {
			url = "http://" + url;
		}
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(urlText.getWindowToken(), 0);

		testWebView.loadUrl(url);
	}

	// 创建菜单
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(1, ITEM_ID_GOBACK, 1, R.string.back).setIcon(
				R.drawable.controlbar_backward_enable);
		menu.add(1, ITEM_ID_GOFORWARD, 2, R.string.go).setIcon(
				R.drawable.controlbar_forward_enable);
		menu.add(1, ITEM_ID_GOSTOP, 3, R.string.stop).setIcon(
				R.drawable.controlbar_stop);
		menu.add(1, ITEM_ID_GOHOME, 4, R.string.home).setIcon(
				R.drawable.controlbar_homepage);
		menu.add(1, ITEM_ID_GODOWNLOADMANAGER, 5, R.string.downloadmanager)
				.setIcon(R.drawable.menu_downmanager);
		menu.add(1, ITEM_ID_GOQUIT, 6, R.string.quit).setIcon(
				R.drawable.menu_quit);
		menu.add(1, ITEM_ID_ADDBOOKMARK, 7, R.string.addbookmark).setIcon(
				R.drawable.menu_quit);
		menu.add(1, ITEM_ID_BOOKMARKS, 8, R.string.bookmarks).setIcon(
				R.drawable.menu_quit);
		menu.add(1, ITEM_ID_REFRESH, 9, R.string.refresh).setIcon(
				R.drawable.menu_refresh);
		return true;
	}

	// 给菜单加事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case ITEM_ID_GOBACK:
			if (testWebView.canGoBack()) {
				testWebView.goBack();
			}
			break;
		case ITEM_ID_GOFORWARD:
			if (testWebView.canGoForward()) {
				testWebView.goForward();
			}
			break;
		case ITEM_ID_GOSTOP:
			testWebView.stopLoading();
			break;
		case ITEM_ID_GOHOME:
			testWebView.loadUrl(homepageurl);
			break;
		case ITEM_ID_GOQUIT:
			finish();
		case ITEM_ID_ADDBOOKMARK:
			bookmarkid = dbBookmarks.insertTitle("", testWebView.getTitle(),
					testWebView.getUrl());
			Toast.makeText(this, 
            		R.string.info_addbookmark, 
                    Toast.LENGTH_LONG) 
                 .show();
			break;
		case ITEM_ID_BOOKMARKS:
			Intent it = new Intent();
			it.setClass(this, bookmarkslist.class);
			startActivityForResult(it, RESULT_OK);
			break;
		case ITEM_ID_REFRESH:
			testWebView.reload();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	class WebDownloadListener implements DownloadListener {
		public void onDownloadStart(final String url, String userAgent,
				String contentDisposition, String mimeType, long contentLength) {
			Log.e("Download", "url is " + url);
			Log.e("Download", "userAgent is " + userAgent);
			Log.e("Download", "contentDisposition is " + contentDisposition);
			Log.e("Download", "mimetype is " + mimeType);
			Log.e("Download", "contentLength is " + contentLength);
			LayoutInflater factory = LayoutInflater.from(myApp);
			final View savetoView = factory.inflate(
					R.layout.dialog_save_download_to, null);
			((EditText) savetoView.findViewById(R.id.dialog_saveto_edit))
					.setText(URLUtil.guessFileName(url, contentDisposition,
							mimeType));
			Request urlrequest = HttpRequestParser.parse(url);
			((EditText) savetoView.findViewById(R.id.dialog_savetopath_edit))
					.setText(urlrequest.getParameter("forder"));
			AlertDialog savetoDialog = new AlertDialog.Builder(myApp).setIcon(
					R.drawable.alert_dialog_icon).setTitle(
					R.string.dialog_saveto).setView(savetoView)
					.setPositiveButton(R.string.dialog_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked OK so do some stuff */
									try {
										String forder = ((EditText) ((AlertDialog) dialog)
												.findViewById(R.id.dialog_savetopath_edit))
												.getText().toString();
										String tmpsavetodir = savetodir;
										if (forder != "") {
											tmpsavetodir = savetodir + "/"
													+ forder;
										}
										StringTokenizer st = new StringTokenizer(
												tmpsavetodir, "/");
										String path1 = st.nextToken() + "/";
										String path2 = path1;
										while (st.hasMoreTokens()) {
											path1 = st.nextToken() + "/";
											path2 += path1;
											File inbox = new File(path2);
											if (!inbox.exists())
												inbox.mkdir();
										}
										Log.e("forder", "forder is " + forder);
										Log.e("forder", "tmpsavetodir is "
												+ tmpsavetodir);
										new DownloadFilesTask()
												.execute(
														url,
														tmpsavetodir,
														((EditText) ((AlertDialog) dialog)
																.findViewById(R.id.dialog_saveto_edit))
																.getText()
																.toString(),
														String.valueOf(1),
														myApp
																.getApplicationContext());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}).setNegativeButton(R.string.dialog_cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked cancel so do some stuff */
								}
							}).create();
			savetoDialog.show();
		}
	}
}
