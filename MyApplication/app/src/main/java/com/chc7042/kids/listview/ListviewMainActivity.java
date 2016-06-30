package com.chc7042.kids.listview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.chc7042.kids.R;

import java.util.List;

public class ListviewMainActivity extends Activity implements OnItemClickListener {
	private static final String rssFeed = "https://www.dropbox.com/s/t4o5wo6gdcnhgj8/imagelistview.xml?dl=1";

	List<Item> arrayOfList;
	ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_main);

		listView = (ListView) findViewById(R.id.listview);
		listView.setOnItemClickListener(this);

		if (Utils.isNetworkAvailable(ListviewMainActivity.this)) {
			new MyTask().execute(rssFeed);
		} else {
			showToast("No Network Connection!!!");
		}

	}

	// My AsyncTask start...

	class MyTask extends AsyncTask<String, Void, Void> {

		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(ListviewMainActivity.this);
			pDialog.setMessage("Loading...");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(String... params) {
			arrayOfList = new NamesParser().getData(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (null != pDialog && pDialog.isShowing()) {
				pDialog.dismiss();
			}

			if (null == arrayOfList || arrayOfList.size() == 0) {
				showToast("No data found from web!!!");
				ListviewMainActivity.this.finish();
			} else {

				// check data...
				/*
				 * for (int i = 0; i < arrayOfList.size(); i++) { Item item =
				 * arrayOfList.get(i); System.out.println(item.getId());
				 * System.out.println(item.getTitle());
				 * System.out.println(item.getDesc());
				 * System.out.println(item.getPubdate());
				 * System.out.println(item.getLink()); }
				 */

				setAdapterToListview();

			}

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Item item = arrayOfList.get(position);
		Intent intent = new Intent(ListviewMainActivity.this, DetailActivity.class);
		intent.putExtra("url", item.getLink());
		intent.putExtra("title", item.getTitle());
		intent.putExtra("desc", item.getDesc());
		startActivity(intent);
	}

	public void setAdapterToListview() {
		NewsRowAdapter objAdapter = new NewsRowAdapter(ListviewMainActivity.this,
				R.layout.listview_row, arrayOfList);
		listView.setAdapter(objAdapter);
	}

	public void showToast(String msg) {

	}
}
