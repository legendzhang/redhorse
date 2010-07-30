package com.redhorse.redhorse;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

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
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CacheManager;
import android.webkit.CacheManager.CacheResult;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class redhorse extends Activity {

	private dbConfigKeyValueHelper dbConfigKeyValue = null;
	private dbBookmarksAdapter dbBookmarks = null;
	private Cursor dbConfigKeyValueCursor;
	private ProgressBar circleProgressBar;
	private ImageView iv;
	private String picurl;

	private final static int ITEM_ID_GOBACK = 1;
	private final static int ITEM_ID_GOFORWARD = 2;
	private final static int ITEM_ID_GOSTOP = 3;
	private final static int ITEM_ID_GOHOME = 4;
	private final static int ITEM_ID_GODOWNLOADMANAGER = 5;
	private final static int ITEM_ID_GOQUIT = 6;
	private final static int ITEM_ID_BOOKMARKS = 7;
	private final static int ITEM_ID_ADDBOOKMARK = 8;
	private final static int ITEM_ID_NEWURL = 9;
	private final static int ITEM_ID_REFRESH = 10;
	private final static int ITEM_ID_SETTING = 11;
	private final static int ITEM_ID_ABOUT = 12;
	private final static int ITEM_ID_DOWNLOADFILES = 13;
	private final static int ITEM_ID_SHARE = 14;

	private static final int BOOKMARKS_REQUEST = 0;

	private final static String STRING_HOMEPAGEURL = "http://redhorse4you.appspot.com/";
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

	@Override  
    protected void onNewIntent(Intent i) {  
        // TODO Auto-generated method stub  
        super.onNewIntent(i);  
		String url = "";
		try {
			url = i.getData().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}		          
		if (url.equals(""))
			url = homepageurl;
		testWebView.loadUrl(url);
    }
	
	@Override
	public void onRestart() {
		super.onRestart();
		Log.e("onRestart", "onRestart");
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.e("onStart", "onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.e("onResume", "onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.e("onPause", "onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.e("onStop", "onStop");
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("onCreate", "onCreate");
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);		
		setContentView(R.layout.main);

		// notification();

		// dbConfigKeyValue = new dbConfigKeyValueHelper(this);
		// dbConfigKeyValue.insert("savetodir", savetodir);
		// dbConfigKeyValueCursor = dbConfigKeyValue.select("savetodir");
		// dbConfigKeyValueCursor.moveToFirst();
		// Log.e("debug",
		// "config savetodir is " + dbConfigKeyValueCursor.getString(2));
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
			// this.savetodir = STRING_SAVETODIR;
			java.io.File sdcardDir = android.os.Environment
					.getExternalStorageDirectory();
			this.savetodir = sdcardDir.getAbsolutePath() + "/download";
			Editor editor = share.edit();// 取得编辑器
			editor.putString("savetodir", this.savetodir);
			editor.commit();// 提交刷新数据
		}

		dbBookmarks = new dbBookmarksAdapter(this);
		dbBookmarks.open();
		// bookmarkid =
		// dbBookmarks.insertTitle("","redhorse主页",this.homepageurl);

		testWebView = (WebView) this.findViewById(R.id.WebView01);
		testWebView.getSettings().setSaveFormData(true);
		testWebView.getSettings().setSavePassword(true);
		testWebView.getSettings().setSupportZoom(true);
		testWebView.getSettings().setBuiltInZoomControls(true);
		testWebView.getSettings().setJavaScriptEnabled(true);

		Intent i = getIntent();
		String url = "";
		try {
			url = i.getData().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (url.equals(""))
			url = homepageurl;
		testWebView.loadUrl(url);

		testWebView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (!testWebView.hasFocus()) {
						testWebView.requestFocus();
					}
					break;
				case MotionEvent.ACTION_UP:
					Looper looper = Looper.myLooper();// 取得当前线程里的looper
					MyHandler mHandler = new MyHandler(looper);// 构造一个handler使之可与looper通信
					// buton等组件可以由mHandler将消息传给looper后,再放入messageQueue中,同时mHandler也可以接受来自looper消息
					mHandler.removeMessages(0);
					String msgStr = "主线程不同组件通信:消息来自button";
					Message m = mHandler.obtainMessage(1, 1, 1, msgStr);// 构造要传递的消息
					testWebView.requestImageRef(m);
//					mHandler.sendMessage(m);// 发送消息:系统会自动调用handleMessage方法来处理消息
											// testWebView.requestImageRef(msg);
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
		urlText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View arg0, boolean isFocused) {
				// TODO Auto-generated method stub

				if (isFocused == true) {
					urlText.selectAll();
				} else {
				}
			}
		});

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
				circleProgressBar.setVisibility(View.INVISIBLE);
//				iv.setVisibility(View.VISIBLE);
			}
		});

		testWebView.setDownloadListener(new WebDownloadListener());

		testWebView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Looper looper = Looper.myLooper();// 取得当前线程里的looper
				MyHandler mHandler = new MyHandler(looper);// 构造一个handler使之可与looper通信
				// buton等组件可以由mHandler将消息传给looper后,再放入messageQueue中,同时mHandler也可以接受来自looper消息
				mHandler.removeMessages(0);
				String msgStr = "";
				Message m = mHandler.obtainMessage(1, 1, 1, msgStr);// 构造要传递的消息
				testWebView.requestImageRef(m);
				// mHandler.sendMessage(m);//
				// 发送消息:系统会自动调用handleMessage方法来处理消息
				// testWebView.requestImageRef(msg);

				AlertDialog opDialog = new AlertDialog.Builder(redhorse.this)
                .setTitle(R.string.select_dialog)
                .setItems(R.array.select_dialog_shareimage, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        /* User clicked so do some stuff */
                        String[] items = getResources().getStringArray(R.array.select_dialog_items);
//                        new AlertDialog.Builder(downloadlist.this)
//                                .setMessage("You selected: " + which + " , " + items[which])
//                                .show();
                		switch (which) {
                		case 0:
                			Log.e("debug", Integer.toString(which));
                			Intent itShare = new Intent();
                			itShare.setClass(redhorse.this, weibo.class);
                			Bundle mBundle = new Bundle();
                			mBundle.putString("title", testWebView.getTitle() + "");
                			mBundle.putString("url", testWebView.getUrl());
                			CacheResult cs = CacheManager.getCacheFile(picurl, new HashMap());
                			File cachedir = CacheManager.getCacheFileBaseDir();
                			String filename = cs.getLocalPath();
                			mBundle.putString("uploadfile", cachedir.getPath()+"/"+filename);
                			itShare.putExtras(mBundle);
                			startActivity(itShare);
                			break;
                		}
                    }
                })
                .create();
				opDialog.show();
				return false;
			}
		});

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
		circleProgressBar = (ProgressBar)findViewById(R.id.circleProgressBar);
		iv = (ImageView)findViewById(R.id.searchIcon);
//		circleProgressBar.setVisibility(View.VISIBLE);
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

//		iv.setVisibility(View.INVISIBLE);
		circleProgressBar.setVisibility(View.VISIBLE);
		
		testWebView.loadUrl(url);

	}

	// 创建菜单
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(1, ITEM_ID_SHARE, 0, R.string.share).setIcon(
				R.drawable.menu_sharepage);
		menu.add(1, ITEM_ID_GOBACK, 0, R.string.back).setIcon(
				R.drawable.controlbar_backward_enable);
		menu.add(1, ITEM_ID_GOFORWARD, 0, R.string.go).setIcon(
				R.drawable.controlbar_forward_enable);
		menu.add(1, ITEM_ID_GOSTOP, 0, R.string.stop).setIcon(
				R.drawable.controlbar_stop);
		menu.add(1, ITEM_ID_GOHOME, 0, R.string.home).setIcon(
				R.drawable.controlbar_homepage);
		menu.add(1, ITEM_ID_GODOWNLOADMANAGER, 0, R.string.downloadmanager)
				.setIcon(R.drawable.menu_downmanager);
		menu.add(1, ITEM_ID_DOWNLOADFILES, 0, R.string.downloadfile).setIcon(
				R.drawable.menu_redownload);
		menu.add(1, ITEM_ID_NEWURL, 0, R.string.newurl).setIcon(
				R.drawable.menu_newurl);
		menu.add(1, ITEM_ID_REFRESH, 0, R.string.refresh).setIcon(
				R.drawable.menu_refresh);
		menu.add(1, ITEM_ID_ADDBOOKMARK, 0, R.string.addbookmark).setIcon(
				R.drawable.menu_add_to_bookmark);
		menu.add(1, ITEM_ID_BOOKMARKS, 0, R.string.bookmarks).setIcon(
				R.drawable.menu_bookmark);
		// menu.add(1, ITEM_ID_SETTING, 0, R.string.setting).setIcon(
		// R.drawable.menu_syssettings);
		menu.add(1, ITEM_ID_ABOUT, 0, R.string.about).setIcon(
				R.drawable.menu_help);
		menu.add(1, ITEM_ID_GOQUIT, 0, R.string.quit).setIcon(
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
			break;
		case ITEM_ID_ADDBOOKMARK:
			bookmarkid = dbBookmarks.insertTitle("", testWebView.getTitle()
					+ "", testWebView.getUrl());
			Toast.makeText(this, R.string.info_addbookmark, Toast.LENGTH_LONG)
					.show();
			break;
		case ITEM_ID_BOOKMARKS:
			Intent it = new Intent();
			it.setClass(redhorse.this, bookmarkslist.class);
			startActivityForResult(it, BOOKMARKS_REQUEST);
			break;
		case ITEM_ID_REFRESH:
			testWebView.reload();
			break;
		case ITEM_ID_NEWURL:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("打开新页面");
			alert.setMessage("网址");

			// Set an EditText view to get user input
			final EditText input = new EditText(this);
			alert.setView(input);
			alert.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							loadUrl(input);
						}
					});

			alert.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});

			alert.show();
			break;
		case ITEM_ID_GODOWNLOADMANAGER:
			Intent itdownloadmgr = new Intent();
			itdownloadmgr.setClass(redhorse.this, downloadlist.class);
			startActivity(itdownloadmgr);
			break;
		case ITEM_ID_SETTING:
			Intent setting = new Intent();
			setting.setClass(redhorse.this, AppGrid.class);
			startActivity(setting);
			break;
		case ITEM_ID_ABOUT:
			// Toast.makeText(this, R.string.info_about, Toast.LENGTH_LONG)
			// .show();
			Intent itfeedback = new Intent();
			itfeedback.setClass(redhorse.this, Feedback.class);
			startActivity(itfeedback);
			break;
		case ITEM_ID_DOWNLOADFILES:
			// Log.e("redhorse", "start ansrozip");
			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			List<ResolveInfo> mAllApps = getPackageManager()
					.queryIntentActivities(mainIntent, 0);
			boolean found = false;
			Iterator it1 = mAllApps.iterator();
			while (it1.hasNext()) {
				ResolveInfo info = (ResolveInfo) it1.next();
				if (("com.agilesoftresource")
						.equals(info.activityInfo.packageName)) {
					found = true;
					break;
				}
			}
			Intent intent = new Intent();
			PackageManager packageManager = this.getPackageManager();
			if (found)
				try {
					intent = packageManager
							.getLaunchIntentForPackage("com.agilesoftresource");
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else
				intent.setClass(redhorse.this, FileList.class);
			startActivity(intent);
			break;
		case ITEM_ID_SHARE:
			Intent itShare = new Intent();
			itShare.setClass(redhorse.this, weibo.class);
			Bundle mBundle = new Bundle();
			mBundle.putString("title", testWebView.getTitle() + "");
			mBundle.putString("url", testWebView.getUrl());
			itShare.putExtras(mBundle);
			startActivity(itShare);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			Bundle b = data.getExtras();
			String url = b.getString("URL");
			testWebView.loadUrl(url);
			Log.e("bookmarkmenu", "url is " + url);
		}
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
										new DownloadFilesTask()
												.execute(
														url,
														tmpsavetodir,
														((EditText) ((AlertDialog) dialog)
																.findViewById(R.id.dialog_saveto_edit))
																.getText()
																.toString(),
														String.valueOf(1),
														myApp.getApplicationContext());
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

	private class MyHandler extends Handler {
		public MyHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {// 处理消息
			picurl = msg.getData().getString("url");
			Log.e("Message", "image url is " + picurl);
		}
	}
}
