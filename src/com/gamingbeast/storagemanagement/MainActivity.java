package com.gamingbeast.storagemanagement;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

public class MainActivity extends FragmentActivity implements
		OnPageChangeListener, OnClickListener {

	protected ViewPager mViewContent;
	private List<Fragment> mTransContents = new ArrayList<Fragment>();
	private List<Fragment> mContents = new ArrayList<Fragment>();
	private MyFragmentPagerAdapter mContentAdapter;
	private boolean mFragmentUpdateFlag = false;
	private List<CustomTextView> mTabIndicator = new ArrayList<CustomTextView>();
	private int mSelectedActionTab = Constants.ACTION_TAB_MAIN;
	private int mSelectedTab = R.id.bt_brief;

	// duration
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		mViewContent = (ViewPager) findViewById(R.id.vp_container);
		init();
		mViewContent.setAdapter(mContentAdapter);
		mViewContent.setOnPageChangeListener(this);

	}

	@Override
	public void onBackPressed() {
		onClick(findViewById(R.id.bt_action));
		return;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	// initialize
	private void init() {
		initContentFragments();
		initAdapters();
		initTabIndicator();
	}

	private void initContentFragments() {
		mContents.add(new BriefFragment());
		mContents.add(new ActionFragment());
		mContents.add(new StorageFragment());
		mContents.add(new OtherFragment());
		mTransContents.add(mContents.get(1));
		mTransContents.add(new ImportFragment());
		mTransContents.add(new ExportFragment());
		mTransContents.add(new ReceiveFragment());
		mTransContents.add(new PostFragment());
	}

	private void initAdapters() {
		mContentAdapter = new MyFragmentPagerAdapter(
				getSupportFragmentManager());
	}

	private void initTabIndicator() {
		CustomTextView _bt1 = (CustomTextView) findViewById(R.id.bt_brief);
		CustomTextView _bt2 = (CustomTextView) findViewById(R.id.bt_action);
		CustomTextView _bt3 = (CustomTextView) findViewById(R.id.bt_storage);
		CustomTextView _bt4 = (CustomTextView) findViewById(R.id.bt_other);
		mTabIndicator.add(_bt1);
		mTabIndicator.add(_bt2);
		mTabIndicator.add(_bt3);
		mTabIndicator.add(_bt4);
		_bt1.setIconAlpha(1.0f);
	}

	// implement methods
	@Override
	public void onClick(View v) {
		int _id = v.getId();
		if (_id == mSelectedTab) {
			if (_id != R.id.bt_action) {
				return;
			} else if (mSelectedActionTab == Constants.ACTION_TAB_MAIN) {
				return;
			}
		}
		resetOtherTabs();
		switch (_id) {
		case R.id.bt_brief:
			mTabIndicator.get(0).setIconAlpha(1.0f);
			mSelectedTab = _id;
			mViewContent.setCurrentItem(0, false);
			break;
		case R.id.bt_action:
			mTabIndicator.get(1).setIconAlpha(1.0f);
			mSelectedTab = _id;
			mSelectedActionTab = Constants.ACTION_TAB_MAIN;
			if (mContents.get(1) != mTransContents.get(0)) {
				mContents.set(1, mTransContents.get(0));
				mViewContent.getAdapter().notifyDataSetChanged();
			}
			mViewContent.setCurrentItem(1, false);
			mFragmentUpdateFlag = false;
			break;
		case R.id.bt_storage:
			mTabIndicator.get(2).setIconAlpha(1.0f);
			mSelectedTab = _id;
			mViewContent.setCurrentItem(2, false);
			break;
		case R.id.bt_other:
			mTabIndicator.get(3).setIconAlpha(1.0f);
			mSelectedTab = _id;
			mViewContent.setCurrentItem(3, false);
			break;
		case R.id.bt_import:
			mTabIndicator.get(1).setIconAlpha(1.0f);
			mSelectedTab = R.id.bt_action;
			mSelectedActionTab = Constants.ACTION_TAB_IMPORT;
			mContents.set(1, mTransContents.get(1));
			mFragmentUpdateFlag = true;
			mViewContent.getAdapter().notifyDataSetChanged();
			break;
		case R.id.bt_export:
			mTabIndicator.get(1).setIconAlpha(1.0f);
			mSelectedTab = R.id.bt_action;
			mSelectedActionTab = Constants.ACTION_TAB_EXPORT;
			mContents.set(1, mTransContents.get(2));
			mFragmentUpdateFlag = true;
			mViewContent.getAdapter().notifyDataSetChanged();
			break;
		case R.id.bt_receive:
			mTabIndicator.get(1).setIconAlpha(1.0f);
			mSelectedTab = R.id.bt_action;
			mSelectedActionTab = Constants.ACTION_TAB_RECEIVE;
			mContents.set(1, mTransContents.get(3));
			mFragmentUpdateFlag = true;
			mViewContent.getAdapter().notifyDataSetChanged();
			break;
		case R.id.bt_post:
			mTabIndicator.get(1).setIconAlpha(1.0f);
			mSelectedTab = R.id.bt_action;
			mSelectedActionTab = Constants.ACTION_TAB_POST;
			mContents.set(1, mTransContents.get(4));
			mFragmentUpdateFlag = true;
			mViewContent.getAdapter().notifyDataSetChanged();
			break;
		}
	}

	private void resetOtherTabs() {
		for (int i = 0; i < mTabIndicator.size(); i++) {
			mTabIndicator.get(i).setIconAlpha(0);
		}
	}

	// override
	@Override
	public void onPageScrollStateChanged(int position) {
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		if (positionOffset > 0) {
			CustomTextView _left = mTabIndicator.get(position);
			CustomTextView _right = mTabIndicator.get(position + 1);
			_left.setIconAlpha(1 - positionOffset);
			_right.setIconAlpha(positionOffset);
		}

	}

	@Override
	public void onPageSelected(int position) {
	}

	class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		FragmentManager mFragmentManager;

		MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
			mFragmentManager = fm;
		}
		
		@Override
		public Fragment getItem(int position) {
			return mContents.get(position);
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Fragment _fragment = (Fragment) super.instantiateItem(container,
					position);
			if (mFragmentUpdateFlag && position == 1) {
				String _Tag = _fragment.getTag();
				FragmentTransaction _ft = mFragmentManager.beginTransaction();
				Log.e("sada1",_fragment.toString());
				_ft.remove(_fragment);
				_fragment = mContents.get(1);
				Log.e("sada2",_fragment.toString());
				_ft.add(container.getId(), _fragment, _Tag).attach(_fragment)
						.commit();

				Log.e("sada3",_fragment.toString());
			}
			return _fragment;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

	}
}
