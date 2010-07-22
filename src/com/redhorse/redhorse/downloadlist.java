package com.redhorse.redhorse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class downloadlist extends Activity {
    
	private final static int ITEM_ID_DELETE = 1;
	private final static int ITEM_ID_OPEN = 2;

	private static final int Download_REQUEST = 0; 

	private dbDownloadAdapter dbDownload = null;
	private ListView list = null;
	private Long ItemID;
	private SimpleAdapter listItemAdapter;
	private ArrayList<HashMap<String, Object>> listItem;

	public Map mimemap=new HashMap();
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//定义mime类型
		mimemap.put("3gp", "video/3gpp");
		mimemap.put("ai", "application/postscript");
		mimemap.put("amr", "audio/amr");
		mimemap.put("apk", "application/vnd.android.package-archive");
		mimemap.put("au", "audio/basic");
		mimemap.put("cab", "application/x-shockwave-flash");
		mimemap.put("chm", "application/mshelp");
		mimemap.put("css", "text/css");
		mimemap.put("doc", "application/msword");
		mimemap.put("dot", "application/msword");
		mimemap.put("eps", "application/postscript");
		mimemap.put("exe", "application/octet-stream");
		mimemap.put("gif", "image/gif");
		mimemap.put("hlp", "application/mshelp");
		mimemap.put("hme", "application/vnd.smartphone.thm");
		mimemap.put("htm", "text/html");
		mimemap.put("html", "text/html");
		mimemap.put("jad", "text/vnd.sun.j2me.app-descriptor");
		mimemap.put("jar", "application/java-archive");
		mimemap.put("jpe", "image/jpeg");
		mimemap.put("jpeg", "image/jpeg");
		mimemap.put("jpg", "image/jpeg");
		mimemap.put("js", "text/javascript");
		mimemap.put("m4a", "audio/m4a");
		mimemap.put("mid", "audio/x-midi");
		mimemap.put("midi", "audio/x-midi");
		mimemap.put("mov", "video/quicktime");
		mimemap.put("mp2", "audio/x-mpeg");
		mimemap.put("mp3", "audio/mpeg");
		mimemap.put("mp4", "video/mp4");
		mimemap.put("mpe", "video/mpeg");
		mimemap.put("mpeg", "video/mpeg");
		mimemap.put("mpg", "video/mpeg");
		mimemap.put("mtf", "application/mtf");
		mimemap.put("nth", "application/vnd.nok-s40theme");
		mimemap.put("ogg", "application/ogg");
		mimemap.put("pdb", "application/ebook");
		mimemap.put("pdf", "application/pdf");
		mimemap.put("php", "application/x-httpd-php");
		mimemap.put("phtml", "application/x-httpd-php");
		mimemap.put("pmd", "audio/pmd");
		mimemap.put("pot", "application/mspowerpoint");
		mimemap.put("pps", "application/mspowerpoint");
		mimemap.put("ppt", "application/mspowerpoint");
		mimemap.put("ppz", "application/mspowerpoint");
		mimemap.put("ps", "application/postscript");
		mimemap.put("qt", "video/quicktime");
		mimemap.put("rar", "application/ocelet-stream");
		mimemap.put("rm", "video/rm");
		mimemap.put("rmvb", "video/vnd.rn-realvideo");
		mimemap.put("rng", "application/vnd.nokia.ringing-tone");
		mimemap.put("rtf", "application/rtf");
		mimemap.put("sdt", "application/vnd.sie.thm");
		mimemap.put("shtml", "text/html");
		mimemap.put("sis", "application/vnd.symbian.install");
		mimemap.put("sisx", "x-epoc/x-sisx-app");
		mimemap.put("snd", "audio/basic");
		mimemap.put("swf", "application/x-shockwave-flash");
		mimemap.put("thm", "application/vnd.eri.thm");
		mimemap.put("tsk", "application/vnd.ppc.thm");
		mimemap.put("txt", "text/plain");
		mimemap.put("umd", "application/umd");
		mimemap.put("utz", "application/vnd.uiq.thm");
		mimemap.put("viv", "video/vnd.vivo");
		mimemap.put("vivo", "video/vnd.vivo");
		mimemap.put("wav", "audio/x-wav");
		mimemap.put("xla", "application/msexcel");
		mimemap.put("xls", "application/msexcel");
		mimemap.put("xwd", "image/x-windowdump");
		mimemap.put("zip", "application/zip");
		
		setContentView(R.layout.download);
		// 绑定Layout里面的ListView
		list = (ListView) findViewById(R.id.ListView01);

		dbDownload = new dbDownloadAdapter(this);
		dbDownload.open();
		//dbDownload.insertTitle("xxx", "redhorse", "xxx", "/sdcard/redhorse.apk", "f");

		setTitle(R.string.text_longprese);

		// 生成动态数组，加入数据
		listItem = new ArrayList<HashMap<String, Object>>();
        Cursor c = dbDownload.getAllTitles();
        if (c.moveToFirst())
        {
         do{
            int idColumn = c.getColumnIndex(dbDownload.KEY_ROWID);
            int titleColumn = c.getColumnIndex(dbDownload.KEY_TITLE);
            int urlColumn = c.getColumnIndex(dbDownload.KEY_URL);
            int typeColumn = c.getColumnIndex(dbDownload.KEY_TYPE);
            int fileColumn = c.getColumnIndex(dbDownload.KEY_FILE);
            int statusColumn = c.getColumnIndex(dbDownload.KEY_STATUS);
 			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.file_open);// 图像资源的ID
			map.put("ItemTitle", c.getString(titleColumn));
			map.put("ItemText", c.getString(fileColumn));
			map.put("ItemID", c.getString(idColumn));
			listItem.add(map);
         } while (c.moveToNext());
        }
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new SimpleAdapter(this, listItem,// 数据源
				R.layout.downloadrow,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "ItemImage", "ItemTitle", "ItemText" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.ItemImage, R.id.ItemTitle, R.id.ItemText });

		// 添加并且显示
		list.setAdapter(listItemAdapter);

		// 添加长按点击
		list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.setHeaderTitle(R.string.menu_longprese_title);
				menu.add(0, ITEM_ID_DELETE, 0, R.string.menu_bookmarks_delete);
				menu.add(0, ITEM_ID_OPEN, 0, R.string.menu_bookmarks_open);
			}
		});
	}

	// 长按菜单响应函数
	@Override
	public boolean onContextItemSelected(MenuItem item) {
//		setTitle("点击了长按菜单里面的第" + item.getItemId() + "个项目");
		switch (item.getItemId()) {
		case ITEM_ID_DELETE:
			String id=((HashMap)list.getItemAtPosition(((AdapterContextMenuInfo) item.getMenuInfo()).position)).get("ItemID").toString();
			Log.e("debug", id);
			dbDownload.deleteTitle(id);
			listItem.remove(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			list.setAdapter(listItemAdapter);
			break;			
		case ITEM_ID_OPEN:
			String filepath = (String)((HashMap)list.getItemAtPosition(((AdapterContextMenuInfo) item.getMenuInfo()).position)).get("ItemText").toString();
			Log.e("redhorse", "filepath:" + filepath);
			File file = new File(filepath);
			Intent intent = new Intent();
			intent.setAction(intent.ACTION_VIEW);
			Log.e("redhorse", "fileext:" + getExtension(file));
			intent.setDataAndType(Uri.fromFile(file),(String) mimemap.get(getExtension(file)));
			if (isIntentAvailable(this,intent))
				startActivity(intent);
			else
				Toast.makeText(this, "file open error!", Toast.LENGTH_LONG).show();
			break;
		}
		return super.onContextItemSelected(item);
	}

	public static boolean isIntentAvailable(final Context context, final Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
	
    /** 
     * Return the extension portion of the file's name . 
     * 
     * @see #getExtension 
     */ 
    public static String getExtension(File f) { 
        return (f != null) ? getExtension(f.getName()) : ""; 
    } 

    public static String getExtension(String filename) { 
        return getExtension(filename, ""); 
    } 

    public static String getExtension(String filename, String defExt) { 
        if ((filename != null) && (filename.length() > 0)) { 
            int i = filename.lastIndexOf('.'); 

            if ((i >-1) && (i < (filename.length() - 1))) { 
                return filename.substring(i + 1); 
            } 
        } 
        return defExt; 
    } 

    public static String trimExtension(String filename) { 
        if ((filename != null) && (filename.length() > 0)) { 
            int i = filename.lastIndexOf('.'); 
            if ((i >-1) && (i < (filename.length()))) { 
                return filename.substring(0, i); 
            } 
        } 
        return filename; 
    } 
    
}
