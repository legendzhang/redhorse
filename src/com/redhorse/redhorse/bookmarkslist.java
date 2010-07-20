package com.redhorse.redhorse;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.AdapterView.OnItemClickListener;

public class bookmarkslist extends Activity {
    
	private final static int ITEM_ID_DELETE = 1;
	private final static int ITEM_ID_EDIT = 2;

	private static final int BOOKMARKS_REQUEST = 0; 

	private dbBookmarksAdapter dbBookmarks = null;
	private ListView list = null;
	private Long ItemID;
	private SimpleAdapter listItemAdapter;
	private ArrayList<HashMap<String, Object>> listItem;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmarks);
		// 绑定Layout里面的ListView
		list = (ListView) findViewById(R.id.ListView01);

		dbBookmarks = new dbBookmarksAdapter(this);
		dbBookmarks.open();

		setTitle(R.string.text_longprese);

		// 生成动态数组，加入数据
		listItem = new ArrayList<HashMap<String, Object>>();
        Cursor c = dbBookmarks.getAllTitles();
        if (c.moveToFirst())
        {
         do{
            int idColumn = c.getColumnIndex(dbBookmarks.KEY_ROWID);
            int titleColumn = c.getColumnIndex(dbBookmarks.KEY_TITLE);
            int urlColumn = c.getColumnIndex(dbBookmarks.KEY_URL);
            int typeColumn = c.getColumnIndex(dbBookmarks.KEY_TYPE);
 			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.file_open);// 图像资源的ID
			map.put("ItemTitle", c.getString(titleColumn));
			map.put("ItemText", c.getString(urlColumn));
			map.put("ItemID", c.getString(idColumn));
			listItem.add(map);
         } while (c.moveToNext());
        }
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new SimpleAdapter(this, listItem,// 数据源
				R.layout.bookmarksrow,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "ItemImage", "ItemTitle", "ItemText" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.ItemImage, R.id.ItemTitle, R.id.ItemText });

		// 添加并且显示
		list.setAdapter(listItemAdapter);

		// 添加点击
		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
//				setTitle("点击第" + ((HashMap)arg0.getItemAtPosition(arg2)).get("ItemID").toString() + "个项目");
				Intent i = getIntent();  
		        Bundle b = new Bundle();  
		        b.putString("URL", (listItem.get(arg2)).get("ItemText").toString());  
		        i.putExtras(b);  
				bookmarkslist.this.setResult(RESULT_OK, i);  
				bookmarkslist.this.finish();  			}
		});

		// 添加长按点击
		list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.setHeaderTitle(R.string.menu_longprese_title);
				menu.add(0, ITEM_ID_DELETE, 0, R.string.menu_bookmarks_delete);
				menu.add(0, ITEM_ID_EDIT, 0, R.string.menu_bookmarks_edit);
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
			dbBookmarks.deleteTitle(id);
			listItem.remove(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			list.setAdapter(listItemAdapter);
			break;			
		case ITEM_ID_EDIT:
			break;
		}
		return super.onContextItemSelected(item);
	}
}
