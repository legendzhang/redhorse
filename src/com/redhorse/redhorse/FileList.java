package com.redhorse.redhorse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	private final static int ITEM_ID_DELETE = 1;
	private final static int ITEM_ID_EDIT = 2;

	private List<String> items = null;
	private String savetodir;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
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
				intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
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
}