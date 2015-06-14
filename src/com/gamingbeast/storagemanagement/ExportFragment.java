package com.gamingbeast.storagemanagement;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class ExportFragment extends Fragment {

	private GridView mTypeOptions;
	private GridView mBrandOptions;
	private ListView mProductsPanel;
	private ListView mOrderPanel;
	private LinearLayout mBottomPanel;
	private CheckedTextView mSelectAllTypes;
	private CheckedTextView mSelectAllBrands;
	private ImageButton mShowTypes;
	private ImageButton mShowBrands;
	private Spinner mCustomer;
	private TextView mCartState;
	private TextView mCartClean;
	private TextView mOrderConfirm;

	private ArrayList<Type> mTypes = new ArrayList<Type>();
	private ArrayList<Brand> mBrands = new ArrayList<Brand>();
	private ArrayList<ExportFragment.Product> mProducts = new ArrayList<ExportFragment.Product>();
	private ArrayList<ExportFragment.Product> mSelectedProducts = new ArrayList<ExportFragment.Product>();

	private boolean mIsTypeShown = false;
	private boolean mIsBrandShown = false;
	private boolean mFocusOnSelector = true;

	private int mSelectedTypesCount = 0;
	private int mSelectedBrandsCount = 0;

	public ExportFragment() {
		super();
		Cursor _c = DBHelper.getDatabase().rawQuery(
				"select * from type order by name", null);
		while (_c.moveToNext()) {
			mTypes.add(new Type(_c.getInt(_c.getColumnIndex("TKEY")), _c
					.getString(_c.getColumnIndex("name"))));
		}
		_c.close();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View _contentView = inflater.inflate(R.layout.fragment_export,
				container, false);

		mSelectAllTypes = (CheckedTextView) _contentView
				.findViewById(R.id.cb_selectall_types);
		mSelectAllBrands = (CheckedTextView) _contentView
				.findViewById(R.id.cb_selectall_brands);
		mTypeOptions = (GridView) _contentView
				.findViewById(R.id.gv_options_type);
		mBrandOptions = (GridView) _contentView
				.findViewById(R.id.gv_options_brand);
		mShowTypes = (ImageButton) _contentView
				.findViewById(R.id.bt_showall_type);
		mShowBrands = (ImageButton) _contentView
				.findViewById(R.id.bt_showall_brand);
		mProductsPanel = (ListView) _contentView.findViewById(R.id.lv_products);
		mOrderPanel = (ListView) _contentView.findViewById(R.id.lv_order);
		mBottomPanel = (LinearLayout) _contentView
				.findViewById(R.id.ly_order_actions);
		mCustomer = (Spinner) _contentView
				.findViewById(R.id.sp_select_customer);
		mCartState = (TextView) _contentView.findViewById(R.id.bt_cart_state);
		mCartClean = (TextView) _contentView.findViewById(R.id.bt_cart_clean);
		mOrderConfirm = (TextView) _contentView
				.findViewById(R.id.bt_order_confirm);

		mTypeOptions.setAdapter(new TypeAdapter(inflater));
		mSelectAllTypes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mFocusOnSelector) {
					switchFocus(true);
				}
				boolean _isChecked = !((CheckedTextView) v).isChecked();
				((CheckedTextView) v).toggle();
				int _t = mTypes.size();
				for (int _i = 0; _i < _t; _i++) {
					Type _temp = mTypes.get(_i);
					_temp.checked = _isChecked;
					if (_temp.vh != null) {
						_temp.vh.setChecked(_isChecked);
						_temp.vh.setTextColor(_isChecked ? Color.BLUE
								: Color.BLACK);
					}
				}
				mSelectedTypesCount = _isChecked ? _t : 0;
				if (updateBrands()) {
					showProducts();
				}

			}
		});
		mShowTypes.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (!mFocusOnSelector) {
					switchFocus(true);
				}
				if (mIsBrandShown) {
					closeBrandOption();
				}

				if (!mIsTypeShown) {
					mTypeOptions.setAdapter(new TypeAdapter(getActivity()
							.getLayoutInflater()));
				}
				mTypeOptions.setVisibility(mIsTypeShown ? View.GONE
						: View.VISIBLE);
				if (mIsTypeShown) {
					mTypeOptions.setAdapter(null);
				}
				mTypeOptions.invalidateViews();
				mShowTypes
						.setImageResource(mIsTypeShown ? R.drawable.bt_options_show
								: R.drawable.bt_options_hide);
				mIsTypeShown = !mIsTypeShown;
			}
		});
		mBrandOptions.setAdapter(new BrandAdapter(inflater));
		mSelectAllBrands.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mFocusOnSelector) {
					switchFocus(true);
				}
				boolean _isChecked = !((CheckedTextView) v).isChecked();
				((CheckedTextView) v).toggle();
				int _t = mBrands.size();
				for (int _i = 0; _i < _t; _i++) {
					Brand _temp = mBrands.get(_i);
					_temp.checked = _isChecked;
					if (_temp.vh != null) {
						_temp.vh.setChecked(_isChecked);
						_temp.vh.setTextColor(_isChecked ? Color.BLUE
								: Color.BLACK);
					}
				}
				mSelectedBrandsCount = _isChecked ? _t : 0;
				showProducts();
			}
		});
		mShowBrands.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (!mFocusOnSelector) {
					switchFocus(true);
				}
				if (mIsTypeShown) {
					closeTypeOption();
				}
				if (!mIsBrandShown) {
					mBrandOptions.setAdapter(new BrandAdapter(getActivity()
							.getLayoutInflater()));
				}
				mBrandOptions.setVisibility(mIsBrandShown ? View.GONE
						: View.VISIBLE);
				if (mIsBrandShown) {
					mBrandOptions.setAdapter(null);
				}
				mBrandOptions.invalidateViews();
				mShowBrands
						.setImageResource(mIsBrandShown ? R.drawable.bt_options_show
								: R.drawable.bt_options_hide);
				mIsBrandShown = !mIsBrandShown;
			}
		});
		mShowBrands.setClickable(false);
		mProductsPanel.setAdapter(new ProductAdapter(inflater));
		mProductsPanel.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mIsTypeShown) {
					closeTypeOption();
				}
				if (mIsBrandShown) {
					closeBrandOption();
				}
				return false;
			}
		});
		mOrderPanel.setAdapter(new OrderAdapter(inflater));

		ArrayAdapter<Customer> _mCustomers = new ArrayAdapter<Customer>(
				this.getActivity(), android.R.layout.simple_spinner_item);
		Cursor _c = DBHelper.getDatabase().rawQuery(
				"select * from customer order by name", null);
		while (_c.moveToNext()) {
			_mCustomers.add(new Customer(_c.getInt(_c.getColumnIndex("CKEY")),
					_c.getString(_c.getColumnIndex("name"))));
		}
		_c.close();
		_mCustomers
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCustomer.setAdapter(_mCustomers);
		mCartState.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switchFocus(false);
				if (mIsBrandShown) {
					closeBrandOption();
				}
				if (mIsTypeShown) {
					closeTypeOption();
				}
			}
		});
		mCartClean.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSelectedProducts.size() > 0) {
					new AlertDialog.Builder(getActivity())
							.setTitle("清空订单")
							.setMessage("确定吗？")
							.setPositiveButton("是",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											mSelectedProducts.clear();
											mOrderPanel.invalidateViews();
										}
									}).setNegativeButton("否", null).show();
				}
			}

		});
		mOrderConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int _j = mSelectedProducts.size();
				boolean _tag = false;
				for (int _i = 0; _i < _j; _i++) {
					if (mSelectedProducts.get(_i).amount <= 0) {
						continue;
					}
					_tag = true;
					break;
				}
				if (_tag) {
					new AlertDialog.Builder(getActivity())
							.setTitle("确认订单")
							.setMessage(
									"向 "
											+ ((Customer) mCustomer
													.getSelectedItem()).name
											+ " 出货？")
							.setPositiveButton("是",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											exportProducts();
										}
									}).setNegativeButton("否", null).show();
				} else {
					Toast.makeText(getActivity(), "请检查订单", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		return _contentView;
	}

	private boolean updateBrands() {
		if (mIsBrandShown) {
			closeBrandOption();
		}
		if (mSelectedTypesCount <= 0) {
			mSelectAllBrands.setChecked(false);
			mSelectAllBrands.setEnabled(false);
			mShowBrands.setClickable(false);
			mShowBrands.setImageResource(R.drawable.bt_options_disabled);
			mSelectedBrandsCount = 0;
			return false;
		}
		Cursor _c = DBHelper.getDatabase()
				.rawQuery(generateQueryBrands(), null);
		mBrands.clear();
		while (_c.moveToNext()) {
			StringBuffer _name = new StringBuffer(_c.getString(_c
					.getColumnIndex("ename")));
			_name.append("\n").append(
					_c.getString(_c.getColumnIndex("name")) == null ? " " : _c
							.getString(_c.getColumnIndex("name")));
			mBrands.add(new Brand(_c.getInt(_c.getColumnIndex("BKEY")), _name
					.toString()));
		}
		_c.close();
		mBrandOptions.invalidateViews();
		mSelectAllBrands.setChecked(true);
		mSelectAllBrands.setEnabled(true);
		mShowBrands.setClickable(true);
		mShowBrands.setImageResource(R.drawable.bt_options_show);
		mSelectedBrandsCount = mBrands.size();
		return true;
	}

	private void showProducts() {
		if (mSelectedBrandsCount <= 0) {
			return;
		}
		Cursor _c = DBHelper.getDatabase().rawQuery(generateQueryProducts(),
				null);
		mProducts.clear();

		while (_c.moveToNext()) {
			Cursor _c2 = DBHelper
					.getDatabase()
					.rawQuery(
							"select * from brand where BKEY = ?",
							new String[] { _c.getInt(_c.getColumnIndex("brand"))
									+ "" });
			_c2.moveToNext();
			ExportFragment.Product temp = new ExportFragment.Product(
					_c.getInt(_c.getColumnIndex("PKEY")));
			temp.name = _c2.getString(_c2.getColumnIndex("ename"))
					+ " "
					+ (_c2.getString(_c2.getColumnIndex("name")) == null ? ""
							: _c2.getString(_c2.getColumnIndex("name")) + " ")
					+ _c.getString(_c.getColumnIndex("name"));
			mProducts.add(temp);
			if (mSelectedProducts.contains(temp)) {
				temp.isSelected = true;
			}
			_c2.close();
		}
		_c.close();
		mProductsPanel.invalidateViews();
	}

	private void exportProducts() {
		SQLiteDatabase _DB = DBHelper.getDatabase();
		_DB.execSQL(generateQueryOrder());
		Cursor _c = DBHelper.getDatabase().rawQuery(
				"select max(OKEY) from export_order", null);
		_c.moveToNext();
		int _OKEY = _c.getInt(_c.getColumnIndex("max(OKEY)"));
		_c.close();
		int _j = mSelectedProducts.size();
		for (int _i = 0; _i < _j; _i++) {
			if (mSelectedProducts.get(_i).amount <= 0) {
				continue;
			}
			_DB.execSQL(generateQueryOrderDetail(_OKEY,
					mSelectedProducts.get(_i)));
			if (((Customer) mCustomer.getSelectedItem()).CKEY == 0) {
				_DB.execSQL("update export_detail set post_time = date('now'),shipment_cost = 0 ,express_no = '自用' where EKEY = (select max(EKEY) from export_detail)");
			}
		}
		if (((Customer) mCustomer.getSelectedItem()).CKEY == 0) {
			_DB.execSQL("update export_order set paid_time =datetime('now'),sum_paid = sum_price,sum_profit =0 where OKEY = (select max(OKEY) from export_order)");
		}
		Toast.makeText(getActivity(), "出货成功", Toast.LENGTH_SHORT).show();
		mSelectedProducts.clear();
		mProducts.clear();
		mProductsPanel.invalidateViews();
		mOrderPanel.invalidateViews();
	}

	private String generateQueryBrands() {
		StringBuffer _sql = new StringBuffer(
				"select * from brand where BKEY in (select distinct(brand) from product where type in (");
		int _t = mTypes.size();
		for (int _i = 0; _i < _t; _i++) {
			Type _temp = mTypes.get(_i);
			if (_temp.checked) {
				_sql.append(_temp.TKEY);
				_sql.append(",");
			}
		}
		_sql.deleteCharAt(_sql.length() - 1);
		_sql.append(")) order by ename");
		return _sql.toString();
	}

	private String generateQueryProducts() {
		StringBuffer _sql = new StringBuffer(
				"select * from product where type in (");
		int _t = mTypes.size();
		for (int _i = 0; _i < _t; _i++) {
			Type _temp = mTypes.get(_i);
			if (_temp.checked) {
				_sql.append(_temp.TKEY);
				_sql.append(",");
			}
		}
		_sql.deleteCharAt(_sql.length() - 1).append(")");
		if (mSelectedBrandsCount < mBrands.size()) {
			_sql.append(" and Brand in (");
			_t = mBrands.size();
			for (int _i = 0; _i < _t; _i++) {
				Brand _temp = mBrands.get(_i);
				if (_temp.checked) {
					_sql.append(_temp.BKEY);
					_sql.append(",");
				}
			}
			_sql.deleteCharAt(_sql.length() - 1);
			_sql.append(")");
		}
		_sql.append(" order by brand");
		return _sql.toString();
	}

	private String generateQueryOrder() {
		return new StringBuffer("insert into export_order (customer) values (")
				.append(((Customer) mCustomer.getSelectedItem()).CKEY)
				.append(")").toString();
	}

	private String generateQueryOrderDetail(int OKEY, ExportFragment.Product p) {
		return new StringBuffer(
				"insert into export_detail (order_k,product,quantity,price) values (")
				.append(OKEY).append(",").append(p.PKEY).append(",")
				.append(p.amount).append(",").append(p.exPrice).append(")")
				.toString();
	}

	private void switchFocus(boolean target) {
		mFocusOnSelector = target;
		mProductsPanel.setVisibility(target ? View.VISIBLE : View.GONE);
		mCartState.setVisibility(target ? View.VISIBLE : View.GONE);
		mBottomPanel.setVisibility(!target ? View.VISIBLE : View.GONE);
		mOrderPanel.setVisibility(!target ? View.VISIBLE : View.GONE);
		if (!mFocusOnSelector) {
			mOrderPanel.invalidateViews();
		}
	}

	private void closeBrandOption() {
		mBrandOptions.setVisibility(View.GONE);
		mBrandOptions.setAdapter(null);
		mShowBrands.setImageResource(R.drawable.bt_options_show);
		mIsBrandShown = false;
	}

	private void closeTypeOption() {
		mTypeOptions.setVisibility(View.GONE);
		mTypeOptions.setAdapter(null);
		mShowTypes.setImageResource(R.drawable.bt_options_show);
		mIsTypeShown = false;
	}

	private class BrandAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public BrandAdapter(LayoutInflater inflater) {
			mInflater = inflater;
		}

		@Override
		public int getCount() {
			return mBrands.size();
		}

		@Override
		public Object getItem(int position) {
			return mBrands.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				CheckedTextView vh = null;
				convertView = mInflater.inflate(R.layout._option_item, parent,
						false);
				vh = (CheckedTextView) convertView.findViewById(R.id.cb_option);
				vh.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						((CheckedTextView) v).toggle();
						mBrands.get(position).checked = ((CheckedTextView) v)
								.isChecked();
						((CheckedTextView) v).setTextColor(((CheckedTextView) v)
								.isChecked() ? Color.BLUE : Color.BLACK);
						if (((CheckedTextView) v).isChecked()) {
							if (++mSelectedBrandsCount == mBrands.size()) {
								mSelectAllBrands.setChecked(true);
							}
						} else {
							if (--mSelectedBrandsCount < mBrands.size()) {
								mSelectAllBrands.setChecked(false);
							}
						}
						showProducts();
					}
				});
				Brand _temp = mBrands.get(position);
				vh.setText(_temp.name);
				vh.setChecked(_temp.checked);
				vh.setTextColor(_temp.checked ? Color.BLUE : Color.BLACK);
				_temp.vh = vh;
			}
			return convertView;
		}
	}

	private class TypeAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public TypeAdapter(LayoutInflater inflater) {
			mInflater = inflater;
		}

		@Override
		public int getCount() {
			return mTypes.size();
		}

		@Override
		public Object getItem(int position) {
			return mTypes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				CheckedTextView vh = null;
				convertView = mInflater.inflate(R.layout._option_item, parent,
						false);
				vh = (CheckedTextView) convertView.findViewById(R.id.cb_option);
				vh.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						((CheckedTextView) v).toggle();
						mTypes.get(position).checked = ((CheckedTextView) v)
								.isChecked();
						((CheckedTextView) v).setTextColor(((CheckedTextView) v)
								.isChecked() ? Color.BLUE : Color.BLACK);
						if (((CheckedTextView) v).isChecked()) {
							if (++mSelectedTypesCount == mTypes.size()) {
								mSelectAllTypes.setChecked(true);
							}
						} else {
							if (--mSelectedTypesCount < mTypes.size()) {
								mSelectAllTypes.setChecked(false);
							}
						}
						if (updateBrands()) {
							showProducts();
						}
					}
				});
				Type _temp = mTypes.get(position);
				vh.setText(_temp.name);
				vh.setChecked(_temp.checked);
				vh.setTextColor(_temp.checked ? Color.BLUE : Color.BLACK);
				vh.setTextSize(18);
				_temp.vh = vh;
			}
			return convertView;
		}
	}

	private class ProductAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ProductAdapter(LayoutInflater inflater) {
			mInflater = inflater;
		}

		@Override
		public int getCount() {
			return mProducts.size();
		}

		@Override
		public Object getItem(int position) {
			return mProducts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			convertView = mInflater.inflate(R.layout._product_imexport, parent,
					false);
			TextView _name = (TextView) convertView
					.findViewById(R.id.tv_prod_name);
			final ImageButton _add = (ImageButton) convertView
					.findViewById(R.id.bt_cart_add);
			ImageView _photo = (ImageView) convertView
					.findViewById(R.id.iv_prod_photo);
			final ExportFragment.Product _temp = mProducts.get(position);
			_name.setText(_temp.name);
			int _tt = getResources().getIdentifier("photo" + _temp.PKEY,
					"drawable", "com.gamingbeast.storagemanagement");
			_photo.setImageResource(_tt > 0 ? _tt
					: R.drawable.photo_prod_default);
			if (_temp.isSelected) {
				_add.setImageResource(R.drawable.bt_cart_added);
				_add.setClickable(false);
			} else {
				_add.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						_temp.isSelected = true;
						_add.setImageResource(R.drawable.bt_cart_added);
						_add.setClickable(false);
						mSelectedProducts.add(_temp);
					}
				});
			}
			return convertView;
		}
	}

	private class OrderAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public OrderAdapter(LayoutInflater inflater) {
			mInflater = inflater;
		}

		@Override
		public int getCount() {
			return mSelectedProducts.size();
		}

		@Override
		public Object getItem(int position) {
			return mSelectedProducts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			convertView = mInflater.inflate(R.layout._product_order, parent,
					false);
			TextView _name = (TextView) convertView
					.findViewById(R.id.tv_prod_name);
			final ImageButton _minus = (ImageButton) convertView
					.findViewById(R.id.bt_action_minus);
			final EditText _amount = (EditText) convertView
					.findViewById(R.id.et_prod_amount);
			ImageButton _plus = (ImageButton) convertView
					.findViewById(R.id.bt_action_plus);
			final EditText _price = (EditText) convertView
					.findViewById(R.id.et_prod_price);
			ImageButton remove = (ImageButton) convertView
					.findViewById(R.id.bt_cart_remove);
			ImageView _photo = (ImageView) convertView
					.findViewById(R.id.iv_prod_photo);

			final ExportFragment.Product _temp = mSelectedProducts
					.get(position);
			_name.setText(_temp.name);
			_amount.setText("" + _temp.amount);
			int _tt = getResources().getIdentifier("photo" + _temp.PKEY,
					"drawable", "com.gamingbeast.storagemanagement");
			_photo.setImageResource(_tt > 0 ? _tt
					: R.drawable.photo_prod_default);
			if (_temp.amount <= 0) {
				_amount.setText(null);
				_minus.setEnabled(false);
			}
			_amount.setOnTouchListener(new OnTouchListener() {
				@SuppressLint("ClickableViewAccessibility")
				public boolean onTouch(View v, MotionEvent event) {
					v.setFocusableInTouchMode(true);
					v.findFocus();
					return false;
				}
			});
			_amount.setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (v.getText() == null || v.getText().toString().equals("")) {
						v.setFocusableInTouchMode(false);
						v.clearFocus();
						return false;
					}
					int _t = Integer.parseInt(v.getText().toString());
					_temp.amount = _t;
					if (_t == 0) {
						_minus.setEnabled(false);
						v.setText(null);
					} else {
						_minus.setEnabled(true);
					}
					v.setFocusableInTouchMode(false);
					v.clearFocus();
					return false;
				}
			});
			if (_temp.exPrice <= 0) {
				_price.setText(null);
			} else {
				_price.setText("￥" + _temp.exPrice);
			}
			_price.setOnTouchListener(new OnTouchListener() {
				@SuppressLint("ClickableViewAccessibility")
				public boolean onTouch(View v, MotionEvent event) {
					v.setFocusableInTouchMode(true);
					v.findFocus();
					return false;
				}
			});
			_price.setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (v.getText() == null || v.getText().toString().equals("")) {
						v.setFocusableInTouchMode(false);
						v.clearFocus();
						return false;
					}
					float _t = Float.parseFloat(v.getText().toString()
							.replace("￥", ""));
					if (_t > 0) {
						_temp.exPrice = _t;
						v.setText("￥" + _t);
						v.setFocusableInTouchMode(false);
						v.clearFocus();
						return false;
					}
					_temp.exPrice = 0;
					v.setText(null);
					v.setFocusableInTouchMode(false);
					v.clearFocus();
					return false;
				}
			});

			_minus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (_temp.amount - 1 == 0) {
						v.setEnabled(false);
					}
					_temp.amount--;
					_amount.setText("" + _temp.amount);
					mOrderPanel.invalidateViews();
				}
			});
			_plus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (_temp.amount == 0) {
						_minus.setEnabled(true);
					}
					_temp.amount++;
					_amount.setText("" + _temp.amount);
					mOrderPanel.invalidateViews();
				}
			});
			remove.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new AlertDialog.Builder(getActivity())
							.setTitle("移除产品")
							.setMessage("确定吗？")
							.setPositiveButton("是",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											mSelectedProducts.remove(position);
											mOrderPanel.invalidateViews();
										}
									}).setNegativeButton("否", null).show();
				}
			});
			return convertView;
		}
	}

	public class Product {
		boolean isSelected = false;
		int amount = 0;
		int PKEY;
		String name;
		float exPrice = 0;

		public Product(int PKEY) {
			this.PKEY = PKEY;
		}

		@Override
		public boolean equals(Object another) {
			if (another instanceof Product) {
				if (PKEY == ((Product) another).PKEY) {
					return true;
				} else {
					return false;
				}
			}
			return super.equals(another);
		}
	}

	public class Customer {
		int CKEY;
		String name;

		public Customer(int CKEY, String name) {
			this.CKEY = CKEY;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}