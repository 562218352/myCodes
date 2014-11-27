package com.zh.swipelistviewtest;

import java.util.ArrayList;

import com.zh.swipelistviewtest.SwipeDismissListView.OnDismissCallback;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ArrayAdapter arrayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		SwipeDismissListView swipeDismissListView = (SwipeDismissListView)findViewById(R.id.swipelist);
		swipeDismissListView.setOnDismissCallback(new OnDismissCallback() {
			@Override
			public void onDismiss(int mDownPosition) {
				arrayAdapter.remove(arrayAdapter.getItem(mDownPosition));
			}
		});
		ArrayList<String> data = new ArrayList<String>();
		for(int i = 0 ;i < 20; i++){
			data.add("滑动删除"+i);
		}
		
		arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, data);
		swipeDismissListView.setAdapter(arrayAdapter);
		
		swipeDismissListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(MainActivity.this, "点击", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
