package com.redhorse.redhorse;

import java.io.File;
import java.util.StringTokenizer;

import com.redhorse.netfox.SiteFileFetch;
import com.redhorse.netfox.SiteInfoBean;
import com.yy.ah.util.HttpRequestParser;
import com.yy.ah.util.HttpRequestParser.Request;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class redhorse extends Activity {

	private final static int ITEM_ID_GOBACK = 1;
	private final static int ITEM_ID_GOFORWARD = 2;
	private final static int ITEM_ID_GOSTOP = 3;
	private final static int ITEM_ID_GOHOME = 4;
	private final static int ITEM_ID_GODOWNLOADMANAGER = 5;
	private final static int ITEM_ID_GOQUIT = 6;

	final Context myApp = this;

	private WebView testWebView = null;
	private String homepageurl = "http://192.168.0.188/a";
	private String savetodir = "/sdcard/download";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
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
				new AlertDialog.Builder(myApp)
						.setTitle(R.string.onjsalert)
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
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
						.setTitle(R.string.wanttoquit)
						.setPositiveButton(R.string.yes,
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
		case ITEM_ID_GOFORWARD:
			if (testWebView.canGoForward()) {
				testWebView.goForward();
			}
		case ITEM_ID_GOHOME:
			testWebView.loadUrl(homepageurl);
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
					.setText((new URLUtil()).guessFileName(url,
							contentDisposition, mimeType));
			Request urlrequest = HttpRequestParser.parse(url);
			((EditText) savetoView.findViewById(R.id.dialog_savetopath_edit))
					.setText(urlrequest.getParameter("forder"));
			AlertDialog savetoDialog = new AlertDialog.Builder(myApp)
					.setIcon(R.drawable.alert_dialog_icon)
					.setTitle(R.string.dialog_saveto)
					.setView(savetoView)
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
										SiteInfoBean bean = new SiteInfoBean(
												url,
												tmpsavetodir,
												((EditText) ((AlertDialog) dialog)
														.findViewById(R.id.dialog_saveto_edit))
														.getText().toString(),
												1);
										SiteFileFetch fileFetch = new SiteFileFetch(
												bean);
										fileFetch.start();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							})
					.setNegativeButton(R.string.dialog_cancel,
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
