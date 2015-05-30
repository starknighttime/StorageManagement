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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends FragmentActivity implements
		OnPageChangeListener {
	public static int selectorTag = Constants.SELECTOR_MODE_STORAGE;

	private ViewPager mViewContent;
	private MyFragmentPagerAdapter mContentAdapter;
	private List<Fragment> mTransContents = new ArrayList<Fragment>();
	private List<Fragment> mContents = new ArrayList<Fragment>();
	private List<CustomTextView> mTabIndicator = new ArrayList<CustomTextView>();
	private Fragment mSelector;
	
	private boolean mFragmentUpdateFlag = false;
	private int mSelectedActionTab = Constants.ACTION_TAB_MAIN;
	private int mSelectedTab = R.id.tab_brief;

	// duration
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//TODO
		//DBHelper.copyDataBase(1,this);
		DBHelper.intiDatabase(this);
		mViewContent = (ViewPager) findViewById(R.id.vp_container);
		init();
		mViewContent.setAdapter(mContentAdapter);
		mViewContent.setOnPageChangeListener(this);
		// mDB.execSQL("");
	}

	
	@Override
	public void onBackPressed() {
		onTabSwitched(findViewById(R.id.tab_action));
		return;
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		DBHelper.getDatabase().close();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	// actionbar

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		if ((mSelectedTab == R.id.tab_action && mSelectedActionTab == Constants.ACTION_TAB_MAIN)
				|| mSelectedTab == R.id.tab_other) {
			getMenuInflater().inflate(R.menu.without_search, menu);
		} else {
			getMenuInflater().inflate(R.menu.with_search, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// TODO
		if (item.getTitle().toString()
				.equals(getResources().getString(R.string.bt_notification))) {
			mTabIndicator.get(2).setIconAlpha(1.0f);
			mSelectedTab = R.id.tab_storage;
			mViewContent.setCurrentItem(2, false);
			getActionBar().setTitle(
					getResources().getString(R.string.tab_storage));
		}
		return super.onOptionsItemSelected(item);
	}

	// initialize
	private void init() {
		initContentFragments();
		initAdapters();
		initTabIndicator();
	}

	private void initContentFragments() {
		mSelector= new SelectorFragment();
		mContents.add(new BriefFragment());
		mContents.add(new ActionFragment());
		mContents.add(mSelector);
		mContents.add(new OtherFragment());
		mTransContents.add(mContents.get(1));
		mTransContents.add(mSelector);
		mTransContents.add(mSelector);
		mTransContents.add(new ReceiveFragment());
		mTransContents.add(new PostFragment());
	}

	private void initAdapters() {
		mContentAdapter = new MyFragmentPagerAdapter(
				getSupportFragmentManager());
	}

	private void initTabIndicator() {
		CustomTextView _bt1 = (CustomTextView) findViewById(R.id.tab_brief);
		CustomTextView _bt2 = (CustomTextView) findViewById(R.id.tab_action);
		CustomTextView _bt3 = (CustomTextView) findViewById(R.id.tab_storage);
		CustomTextView _bt4 = (CustomTextView) findViewById(R.id.tab_other);
		mTabIndicator.add(_bt1);
		mTabIndicator.add(_bt2);
		mTabIndicator.add(_bt3);
		mTabIndicator.add(_bt4);
		_bt1.setIconAlpha(1.0f);
	}

	// implement methods
	public void onTabSwitched(View v) {
		int _id = v.getId();
		if (_id == mSelectedTab) {
			if (_id != R.id.tab_action) {
				return;
			} else if (mSelectedActionTab == Constants.ACTION_TAB_MAIN) {
				return;
			}
		}
		resetOtherTabs();
		switch (_id) {
		case R.id.tab_brief:
			mTabIndicator.get(0).setIconAlpha(1.0f);
			mSelectedTab = _id;
			mViewContent.setCurrentItem(0, false);
			break;
		case R.id.tab_action:
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
		case R.id.tab_storage:
			mTabIndicator.get(2).setIconAlpha(1.0f);
			mSelectedTab = _id;
			mViewContent.setCurrentItem(2, false);
			break;
		case R.id.tab_other:
			mTabIndicator.get(3).setIconAlpha(1.0f);
			mSelectedTab = _id;
			mViewContent.setCurrentItem(3, false);
			break;
		case R.id.bt_import:
			mTabIndicator.get(1).setIconAlpha(1.0f);
			mSelectedTab = R.id.tab_action;
			mSelectedActionTab = Constants.ACTION_TAB_IMPORT;
			mContents.set(1, mTransContents.get(1));
			mFragmentUpdateFlag = true;
			mViewContent.getAdapter().notifyDataSetChanged();
			break;
		case R.id.bt_export:
			mTabIndicator.get(1).setIconAlpha(1.0f);
			mSelectedTab = R.id.tab_action;
			mSelectedActionTab = Constants.ACTION_TAB_EXPORT;
			mContents.set(1, mTransContents.get(2));
			mFragmentUpdateFlag = true;
			mViewContent.getAdapter().notifyDataSetChanged();
			break;
		case R.id.bt_receive:
			mTabIndicator.get(1).setIconAlpha(1.0f);
			mSelectedTab = R.id.tab_action;
			mSelectedActionTab = Constants.ACTION_TAB_RECEIVE;
			mContents.set(1, mTransContents.get(3));
			mFragmentUpdateFlag = true;
			mViewContent.getAdapter().notifyDataSetChanged();
			break;
		case R.id.bt_post:
			mTabIndicator.get(1).setIconAlpha(1.0f);
			mSelectedTab = R.id.tab_action;
			mSelectedActionTab = Constants.ACTION_TAB_POST;
			mContents.set(1, mTransContents.get(4));
			mFragmentUpdateFlag = true;
			mViewContent.getAdapter().notifyDataSetChanged();
			break;
		}
		invalidateOptionsMenu();
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
				_ft.remove(_fragment);
				_fragment = mContents.get(1);
				_ft.add(container.getId(), _fragment, _Tag).attach(_fragment)
						.commit();
			}
			return _fragment;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

	}
}
