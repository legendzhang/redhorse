package com.redhorse.redhorse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FileList extends ListActivity {

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
	}

    @Override
	protected void onListItemClick(ListView l, View v, int selectionRowID, long id) {
		if (selectionRowID == 0) {
			fillWithRoot();
		} else {
			File file = new File(items.get(selectionRowID));
			if (file.isDirectory())
				fill(file.listFiles());
		}
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
}