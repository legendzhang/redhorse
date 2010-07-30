package com.redhorse.redhorse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class bookmarkslist extends Activity {
    
	private final static int ITEM_ID_OPEN = 0;
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

			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
					final long arg3) {
				// TODO Auto-generated method stub
				AlertDialog opDialog = new AlertDialog.Builder(bookmarkslist.this)
                .setTitle(R.string.select_dialog)
                .setItems(R.array.select_dialog_items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        /* User clicked so do some stuff */
                        String[] items = getResources().getStringArray(R.array.select_dialog_items);
//                        new AlertDialog.Builder(downloadlist.this)
//                                .setMessage("You selected: " + which + " , " + items[which])
//                                .show();
                		switch (which) {
                		case ITEM_ID_DELETE:
                			String id = ((HashMap) list.getItemAtPosition((int) arg3)).get("ItemID").toString();
                			Log.e("debug", id);
                			dbBookmarks.deleteTitle(id);
                			listItem.remove((int) arg3);
                			list.setAdapter(listItemAdapter);
                			break;
                		case ITEM_ID_OPEN:
//            				setTitle("点击第" + ((HashMap)arg0.getItemAtPosition(arg2)).get("ItemID").toString() + "个项目");
            				Intent i = getIntent();  
            		        Bundle b = new Bundle();  
            		        b.putString("URL", (listItem.get(arg2)).get("ItemText").toString());  
            		        i.putExtras(b);  
            				bookmarkslist.this.setResult(RESULT_OK, i);  
            				bookmarkslist.this.finish();
                			break;
                		}
                    }
                })
                .create();
				opDialog.show();
			}
		});
	}

	// 长按菜单响应函数
	@Override
	public boolean onContextItemSelected(MenuItem item) {
//		setTitle("点击了长按菜单里面的第" + item.getItemId() + "个项目");
		switch (item.getItemId()) {
		case ITEM_ID_DELETE:
		case ITEM_ID_EDIT:
			break;
		}
		return super.onContextItemSelected(item);
	}
}
