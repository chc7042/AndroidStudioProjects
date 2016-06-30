/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chc7042.kids.slidingtabs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chc7042.kids.R;
import com.chc7042.kids.common.logger.Log;
import com.chc7042.kids.common.view.SlidingTabLayout;
import com.chc7042.kids.listview.DetailActivity;
import com.chc7042.kids.listview.Item;
import com.chc7042.kids.listview.NamesParser;
import com.chc7042.kids.listview.NewsRowAdapter;
import com.chc7042.kids.listview.Utils;

import java.util.List;

public class SlidingTabsFragment extends Fragment {
    static final String TAG = "SlidingTabsBasicFragment";

    //private Context mContext = null;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    private List<Item> arrayOfList;
    private ListView listView = null;

    private static final String rssFeed = "https://www.dropbox.com/s/t4o5wo6gdcnhgj8/imagelistview.xml?dl=1";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "SlidingTabsFragment onCreateView");
        return inflater.inflate(R.layout.listview_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(TAG, "SlidingTabsFragment onViewCreated");
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Kids " + (position + 1);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.i(TAG, "instantiateItem");
            View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item, container, false);

            //TextView title = (TextView) view.findViewById(R.id.item_title);
            //title.setText(String.valueOf(position + 1));

            Log.i(TAG, "instantiateItem() [position: " + position + "]");

            listView = (ListView) view.findViewById(R.id.contentlistview);
            if (listView == null) {
                Log.i(TAG, "listview is null");
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    Log.i(TAG, "onItemClick");
                    Item item = arrayOfList.get(position);
                    Intent intent = new Intent(((SlidingMainActivity)getActivity()), DetailActivity.class);
                    intent.putExtra("url", item.getLink());
                    intent.putExtra("title", item.getTitle());
                    intent.putExtra("desc", item.getDesc());
                    startActivity(intent);                }
            });


            if (Utils.isNetworkAvailable(((SlidingMainActivity)getActivity()))) {
                new MyTask().execute(rssFeed);
            } else {
                showToast("No Network Connection!!!");
            }

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            Log.i(TAG, "destroyItem() [position: " + position + "]");
        }
    }

    // My AsyncTask start...
    class MyTask extends AsyncTask<String, Void, Void> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "MyTask onPreExecute");

            pDialog = new ProgressDialog(((SlidingMainActivity)getActivity()));
            pDialog.setMessage("Loading data...");
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
            Log.i(TAG, "MyTask onPostExecute");

            if (null != pDialog && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (null == arrayOfList || arrayOfList.size() == 0) {
                showToast("No data found from web!!!");
                ((SlidingMainActivity)getActivity()).finish();
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

    public void setAdapterToListview() {
        Log.i(TAG, "setAdapterToListview");
        NewsRowAdapter objAdapter = new NewsRowAdapter(((SlidingMainActivity)getActivity()),
                R.layout.listview_row, arrayOfList);

        if (listView != null) {
            listView.setAdapter(objAdapter);
        } else {
            Log.i(TAG, "listview is null");
        }
    }

    public void showToast(String msg) {

    }
}
