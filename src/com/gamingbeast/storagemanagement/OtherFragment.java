package com.gamingbeast.storagemanagement;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class OtherFragment extends Fragment {

	private ListView mCusAndSou;

	private ArrayList<Customer> mCustomers = new ArrayList<Customer>();
	private ArrayList<Source> mSources = new ArrayList<Source>();

	private boolean mTag;

	public OtherFragment() {
		super();
		Cursor _c = DBHelper.getDatabase().rawQuery(
				"select * from customer where CKEY !=0  order by name", null);
		while (_c.moveToNext()) {
			mCustomers.add(new Customer(_c.getInt(_c.getColumnIndex("CKEY")),
					_c.getString(_c.getColumnIndex("name")), _c.getString(_c
							.getColumnIndex("tel")), _c.getString(_c
							.getColumnIndex("address"))));
		}
		_c = DBHelper.getDatabase().rawQuery(
				"select * from source order by name", null);
		while (_c.moveToNext()) {
			mSources.add(new Source(_c.getInt(_c.getColumnIndex("SKEY")), _c
					.getString(_c.getColumnIndex("name")), _c.getString(_c
					.getColumnIndex("tel"))));
		}
		_c.close();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View _contentView = inflater.inflate(R.layout.fragment_other,
				container, false);
		mTag = true;
		mCusAndSou = (ListView) _contentView
				.findViewById(R.id.lv_database_panel);
		final TextView _sourceData = (TextView) _contentView
				.findViewById(R.id.bt_database_source);
		final TextView _customerData = (TextView) _contentView
				.findViewById(R.id.bt_database_customer);
		mCusAndSou.setAdapter(new ContentAdapter(inflater));
		_sourceData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mTag) {
					mTag = false;
					_sourceData.setBackgroundColor(getActivity().getResources()
							.getColor(R.color.bg_selected));
					_sourceData.setTextColor(Color.WHITE);
					_customerData.setBackgroundColor(Color.WHITE);
					_customerData.setTextColor(Color.BLACK);
					mCusAndSou.invalidateViews();
				}
			}
		});
		_customerData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mTag) {
					mTag = true;
					_customerData.setBackgroundColor(getActivity()
							.getResources().getColor(R.color.bg_selected));
					_customerData.setTextColor(Color.WHITE);
					_sourceData.setBackgroundColor(Color.WHITE);
					_sourceData.setTextColor(Color.BLACK);
					mCusAndSou.invalidateViews();
				}
			}
		});
		return _contentView;
	}

	private void addNew(String name) {
		DBHelper.getDatabase().execSQL(generateQueryAddNew(name));
		if (mTag) {
			Cursor _c = DBHelper
					.getDatabase()
					.rawQuery(
							"select * from customer where CKEY = (select max(CKEY) from customer)",
							null);
			_c.moveToNext();
			mCustomers.add(new Customer(_c.getInt(_c.getColumnIndex("CKEY")),
					_c.getString(_c.getColumnIndex("name")), _c.getString(_c
							.getColumnIndex("tel")), _c.getString(_c
							.getColumnIndex("address"))));
			_c.close();
		} else {
			Cursor _c = DBHelper
					.getDatabase()
					.rawQuery(
							"select * from source where SKEY = (select max(SKEY) from source)",
							null);
			_c.moveToNext();
			mSources.add(new Source(_c.getInt(_c.getColumnIndex("SKEY")), _c
					.getString(_c.getColumnIndex("name")), _c.getString(_c
					.getColumnIndex("tel"))));
			_c.close();
		}
		mCusAndSou.invalidateViews();
	}

	private void changeTel(int KEY, String tel) {
		DBHelper.getDatabase().execSQL(generateQueryChangeTel(KEY, tel));
	}

	private void changeAdd(int CKEY, String add) {
		DBHelper.getDatabase().execSQL(generateQueryChangeAdd(CKEY, add));
	}

	private String generateQueryAddNew(String name) {
		return new StringBuffer("insert into ")
				.append(mTag ? "customer" : "source")
				.append(" (name) values ('").append(name).append("')")
				.toString();
	}

	private String generateQueryChangeTel(int KEY, String tel) {
		return new StringBuffer("update ").append(mTag ? "customer" : "source")
				.append(" set tel='").append(tel).append("' where ")
				.append(mTag ? "CKEY" : "SKEY").append(" = ").append(KEY)
				.toString();
	}

	private String generateQueryChangeAdd(int CKEY, String add) {
		return new StringBuffer("update customer set address='").append(add)
				.append("' where CKEY = ").append(CKEY).toString();
	}

	private class Customer {
		int CKEY;
		String name;
		String tel;
		String address;

		public Customer(int CKEY, String name, String tel, String address) {
			this.CKEY = CKEY;
			this.name = name;
			this.tel = tel;
			this.address = address;
		}
	}

	private class Source {
		int SKEY;
		String name;
		String tel;

		public Source(int SKEY, String name, String tel) {
			this.SKEY = SKEY;
			this.name = name;
			this.tel = tel;
		}
	}

	private class ContentAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ContentAdapter(LayoutInflater inflater) {
			mInflater = inflater;
		}

		@Override
		public int getCount() {
			return mTag ? mCustomers.size() + 1 : mSources.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (mTag) {
				if (position == mCustomers.size()) {
					convertView = mInflater.inflate(R.layout._new, parent,
							false);
					TextView _new = (TextView) convertView
							.findViewById(R.id.tv_new);
					_new.setText("新增顾客");
					_new.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							final EditText et = new EditText(getActivity());
							et.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL
									| InputType.TYPE_CLASS_TEXT);
							new AlertDialog.Builder(getActivity())
									.setTitle("新顾客姓名")
									.setView(et)
									.setPositiveButton(
											"确认",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													if (et.getText() != null
															&& !et.getText()
																	.toString()
																	.trim()
																	.equals("")) {
														addNew(et.getText()
																.toString());
														Toast.makeText(
																getActivity(),
																"新增成功",
																Toast.LENGTH_SHORT)
																.show();
													}
												}
											}).setNegativeButton("取消", null)
									.show();
						}
					});
					return convertView;
				}
				convertView = mInflater.inflate(R.layout._customer, parent,
						false);
				final Customer _temp = mCustomers.get(position);
				TextView _name = (TextView) convertView
						.findViewById(R.id.tv_customer_name);
				EditText _tel = (EditText) convertView
						.findViewById(R.id.et_customer_tel);
				EditText _address = (EditText) convertView
						.findViewById(R.id.et_customer_add);
				_name.setText(_temp.name);
				_tel.setText(_temp.tel);
				_address.setText(_temp.address);
				_tel.setOnTouchListener(new OnTouchListener() {
					@SuppressLint("ClickableViewAccessibility")
					public boolean onTouch(View v, MotionEvent event) {
						v.setFocusableInTouchMode(true);
						v.findFocus();
						return false;
					}
				});
				_tel.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (((v.getText() == null || v.getText().toString().equals("")) && _temp.tel == null)
								|| (v.getText() != null && _temp.tel != null && v
										.getText().equals(_temp.tel))) {
							v.setFocusableInTouchMode(false);
							v.clearFocus();
							return false;
						}
						changeTel(_temp.CKEY, v.getText().toString());
						_temp.tel = v.getText().toString();
						v.setText(_temp.tel);
						Toast.makeText(getActivity(), "修改成功",
								Toast.LENGTH_SHORT).show();
						v.setFocusableInTouchMode(false);
						v.clearFocus();
						return false;
					}
				});
				_address.setOnTouchListener(new OnTouchListener() {
					@SuppressLint("ClickableViewAccessibility")
					public boolean onTouch(View v, MotionEvent event) {
						v.setFocusableInTouchMode(true);
						v.findFocus();
						return false;
					}
				});
				_address.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (((v.getText() == null || v.getText().toString().equals("")) && _temp.address == null)
								|| (v.getText() != null
										&& _temp.address != null && v.getText()
										.equals(null))) {
							v.setFocusableInTouchMode(false);
							v.clearFocus();
							return false;
						}
						changeAdd(_temp.CKEY, v.getText().toString());
						_temp.address = v.getText().toString();
						v.setText(_temp.address);
						Toast.makeText(getActivity(), "修改成功",
								Toast.LENGTH_SHORT).show();
						v.setFocusableInTouchMode(false);
						v.clearFocus();
						return false;
					}
				});
				return convertView;
			} else {
				if (position == mSources.size()) {
					convertView = mInflater.inflate(R.layout._new, parent,
							false);
					TextView _new = (TextView) convertView
							.findViewById(R.id.tv_new);
					_new.setText("新增货源");
					_new.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							final EditText et = new EditText(getActivity());
							et.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL
									| InputType.TYPE_CLASS_TEXT);
							new AlertDialog.Builder(getActivity())
									.setTitle("新货源姓名")
									.setView(et)
									.setPositiveButton(
											"确认",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													if (et.getText() != null
															&& !et.getText()
																	.toString()
																	.trim()
																	.equals("")) {
														addNew(et.getText()
																.toString());
														Toast.makeText(
																getActivity(),
																"新增成功",
																Toast.LENGTH_SHORT)
																.show();
													}
												}
											}).setNegativeButton("取消", null)
									.show();
						}
					});
					return convertView;
				}
				convertView = mInflater
						.inflate(R.layout._source, parent, false);
				final Source _temp = mSources.get(position);
				TextView _name = (TextView) convertView
						.findViewById(R.id.tv_source_name);
				EditText _tel = (EditText) convertView
						.findViewById(R.id.et_source_tel);
				_name.setText(_temp.name);
				_tel.setText(_temp.tel);
				_tel.setOnTouchListener(new OnTouchListener() {
					@SuppressLint("ClickableViewAccessibility")
					public boolean onTouch(View v, MotionEvent event) {
						v.setFocusableInTouchMode(true);
						v.findFocus();
						return false;
					}
				});
				_tel.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (((v.getText() == null || v.getText().toString().equals("")) && _temp.tel == null)
								|| (v.getText() != null && _temp.tel != null && v
										.getText().equals(_temp.tel))) {
							v.setFocusableInTouchMode(false);
							v.clearFocus();
							return false;
						}
						changeTel(_temp.SKEY, v.getText().toString());
						_temp.tel = v.getText().toString();
						v.setText(_temp.tel);
						Toast.makeText(getActivity(), "修改成功",
								Toast.LENGTH_SHORT).show();
						v.setFocusableInTouchMode(false);
						v.clearFocus();
						return false;
					}
				});
				return convertView;
			}
		}
	}
}