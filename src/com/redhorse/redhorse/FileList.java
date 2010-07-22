package com.redhorse.redhorse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class FileList extends ListActivity {

	/*
		3gp	video/3gpp
		ai	application/postscript
		amr	audio/amr
		apk	application/vnd.android.package-archive
		au	audio/basic
		cab	application/x-shockwave-flash
		chm	application/mshelp
		css	text/css
		doc	application/msword
		dot	application/msword
		eps	application/postscript
		exe	application/octet-stream
		gif	image/gif
		hlp	application/mshelp
		hme	application/vnd.smartphone.thm
		htm	text/html
		html	text/html
		jad	text/vnd.sun.j2me.app-descriptor
		jar	application/java-archive
		jpe	image/jpeg
		jpeg	image/jpeg
		jpg	image/jpeg
		js	text/javascript
		m4a	audio/m4a
		mid	audio/x-midi
		midi	audio/x-midi
		mov	video/quicktime
		mp2	audio/x-mpeg
		mp3	audio/mpeg
		mp4	video/mp4
		mpe	video/mpeg
		mpeg	video/mpeg
		mpg	video/mpeg
		mtf	application/mtf
		nth	application/vnd.nok-s40theme
		ogg	application/ogg
		pdb	application/ebook
		pdf	application/pdf
		php	application/x-httpd-php
		phtml	application/x-httpd-php
		pmd	audio/pmd
		pot	application/mspowerpoint
		pps	application/mspowerpoint
		ppt	application/mspowerpoint
		ppz	application/mspowerpoint
		ps	application/postscript
		qt	video/quicktime
		rar	application/ocelet-stream
		rm	video/rm
		rmvb	video/vnd.rn-realvideo
		rng	application/vnd.nokia.ringing-tone
		rtf	application/rtf
		sdt	application/vnd.sie.thm
		shtml	text/html
		sis	application/vnd.symbian.install
		sisx	x-epoc/x-sisx-app
		snd	audio/basic
		swf	application/x-shockwave-flash
		thm	application/vnd.eri.thm
		tsk	application/vnd.ppc.thm
		txt	text/plain
		umd	application/umd
		utz	application/vnd.uiq.thm
		viv	video/vnd.vivo
		vivo	video/vnd.vivo
		wav	audio/x-wav
		xla	application/msexcel
		xls	application/msexcel
		xwd	image/x-windowdump
		zip	application/zip
	 */
	private final static int ITEM_ID_DELETE = 1;
	private final static int ITEM_ID_EDIT = 2;

	private List<String> items = null;
	private String savetodir;

	public Map mimemap=new HashMap();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
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
		
		SharedPreferences share = getPreferences(MODE_PRIVATE);
		savetodir = share.getString("savetodir", "/sdcard/download/");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.directory_list);
		fill(new File(savetodir).listFiles());

		this.getListView().setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.setHeaderTitle(R.string.menu_longprese_title);
				menu.add(0, ITEM_ID_DELETE, 0, R.string.menu_bookmarks_delete);
//				menu.add(0, ITEM_ID_EDIT, 0, R.string.menu_bookmarks_edit);
			}
		});

	}

	@Override
	protected void onListItemClick(ListView l, View v, int selectionRowID,
			long id) {
		if (selectionRowID == 0) {
			fillWithRoot();
		} else {
			File file = new File(items.get(selectionRowID));
			if (file.isDirectory())
				fill(file.listFiles());
			else {
				Intent intent = new Intent();
				intent.setAction(intent.ACTION_VIEW);
				Log.e("redhorse", "fileext:" + getExtension(file));
				intent.setDataAndType(Uri.fromFile(file),(String) mimemap.get(getExtension(file)));
				if (isIntentAvailable(this,intent))
					startActivity(intent);
				else
					Toast.makeText(this, "file open error!", Toast.LENGTH_LONG).show();
			}
		}
	}

	public static boolean isIntentAvailable(final Context context, final Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
	
	private void fillWithRoot() {
		fill(new File(savetodir).listFiles());
	}

	private void fill(File[] files) {
		items = new ArrayList<String>();
		items.add(getString(R.string.to_top));
		for (File file : files)
			items.add(file.getPath());
		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this,
				R.layout.file_row, items);
		setListAdapter(fileList);
	}

	// 长按菜单响应函数
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ITEM_ID_DELETE:
			String filepath = (String)this.getListView().getItemAtPosition(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			File file = new File(filepath);
			file.delete();
			((ArrayAdapter<String>) this.getListView().getAdapter()).remove(filepath);
			this.getListView().setAdapter(this.getListView().getAdapter());
			break;			
		case ITEM_ID_EDIT:
			break;
		}
		return super.onContextItemSelected(item);
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