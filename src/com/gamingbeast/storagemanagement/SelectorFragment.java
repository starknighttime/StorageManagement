package com.gamingbeast.storagemanagement;

import java.util.ArrayList;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class SelectorFragment extends Fragment {

	private GridView mTypeOptions;
	private GridView mBrandOptions;
	private ListView mProductsPanel;
	private CheckedTextView mSelectAllTypes;
	private CheckedTextView mSelectAllBrands;
	private ImageButton mShowTypes;
	private ImageButton mShowBrands;

	private ArrayList<Type> mTypes = new ArrayList<Type>();
	private ArrayList<Brand> mBrands = new ArrayList<Brand>();
	private ArrayList<Product> mProducts = new ArrayList<Product>();
	private ArrayList<Product> mLockedProducts = new ArrayList<Product>();

	private boolean mIsTypeShown = false;
	private boolean mIsBrandShown = false;

	private int mSelectedTypesCount = 0;
	private int mSelectedBrandsCount = 0;

	public SelectorFragment() {
		super();
		Cursor _c = DBHelper.getDatabase().rawQuery("select * from type orders order by name",
				null);
		while (_c.moveToNext()) {
			mTypes.add(new Type(_c.getInt(_c.getColumnIndex("TKEY")), _c
					.getString(_c.getColumnIndex("name"))));
		}
		_c.close();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View _conentView = inflater.inflate(R.layout.fragment_selector,
				container, false);

		mSelectAllTypes = (CheckedTextView) _conentView
				.findViewById(R.id.cb_selectall_types);
		mSelectAllBrands = (CheckedTextView) _conentView
				.findViewById(R.id.cb_selectall_brands);
		mTypeOptions = (GridView) _conentView
				.findViewById(R.id.gd_options_type);
		mBrandOptions = (GridView) _conentView
				.findViewById(R.id.gd_options_brand);
		mShowTypes = (ImageButton) _conentView
				.findViewById(R.id.bt_showall_type);
		mShowBrands = (ImageButton) _conentView
				.findViewById(R.id.bt_showall_brand);
		mProductsPanel = (ListView) _conentView.findViewById(R.id.lv_products);

		mTypeOptions.setAdapter(new TypeAdpter(inflater));
		mSelectAllTypes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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

				if (mIsBrandShown) {
					mBrandOptions.setVisibility(View.GONE);
					mBrandOptions.setAdapter(null);
					mShowBrands.setImageResource(R.drawable.bt_options_show);
					mIsBrandShown = false;
				}

				if (!mIsTypeShown) {
					mTypeOptions.setAdapter(new TypeAdpter(getActivity()
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
		mBrandOptions.setAdapter(new BrandAdpter(inflater));
		mSelectAllBrands.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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
				if (mIsTypeShown) {
					mTypeOptions.setVisibility(View.GONE);
					mTypeOptions.setAdapter(null);
					mShowTypes.setImageResource(R.drawable.bt_options_show);
					mIsTypeShown = false;
				}
				if (!mIsBrandShown) {
					mBrandOptions.setAdapter(new BrandAdpter(getActivity()
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

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mIsTypeShown) {
					mTypeOptions.setVisibility(View.GONE);
					mTypeOptions.setAdapter(null);
					mShowTypes.setImageResource(R.drawable.bt_options_show);
					mIsTypeShown = false;
				}
				if (mIsBrandShown) {
					mBrandOptions.setVisibility(View.GONE);
					mBrandOptions.setAdapter(null);
					mShowBrands.setImageResource(R.drawable.bt_options_show);
					mIsBrandShown = false;
				}
				return false;
			}
		});
		//TODO
		/*mProductsPanel.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				popupShortcuts();
				return false;
			}});*/
		return _conentView;
	}

	private boolean updateBrands() {
		if (mIsBrandShown) {
			mBrandOptions.setVisibility(View.GONE);
			mBrandOptions.setAdapter(null);
			mShowBrands.setImageResource(R.drawable.bt_options_show);
			mIsBrandShown = false;
		}
		if (mSelectedTypesCount <= 0) {
			mSelectAllBrands.setChecked(false);
			mSelectAllBrands.setEnabled(false);
			mShowBrands.setClickable(false);
			mShowBrands.setImageResource(R.drawable.bt_options_disabled);
			mSelectedBrandsCount = 0;
			return false;
		}
		Cursor _c = DBHelper.getDatabase().rawQuery(generateQueryBrands(), null);
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
		Cursor _c = DBHelper.getDatabase().rawQuery(generateQueryProducts(), null);
		mProducts.retainAll(mLockedProducts);
		mProductsPanel.invalidateViews();

		while (_c.moveToNext()) {
			Cursor _c2 = DBHelper.getDatabase()
					.rawQuery(
							"select * from brand where BKEY = ?",
							new String[] { _c.getInt(_c.getColumnIndex("brand"))
									+ "" });
			_c2.moveToNext();
			Cursor _c3 = DBHelper.getDatabase()
					.rawQuery(
							"select * from wholesale_price where product = ? order by count asc",
							new String[] { _c.getInt(_c.getColumnIndex("PKEY"))
									+ "" });
			int _i = 0;
			float[] _tp = new float[3];
			int[] _tc = new int[3];
			while (_c3.moveToNext()) {
				_tp[_i] = _c3.getFloat(_c3.getColumnIndex("price"));
				_tc[_i] = _c3.getInt(_c3.getColumnIndex("count"));
				_i++;
			}
			while (_i < 3) {
				_tp[_i] = 0;
				_tc[_i] = 0;
				_i++;
			}

			mProducts
					.add(new Product.Builder(
							_c.getInt(_c.getColumnIndex("PKEY")),
							_c2.getString(_c2.getColumnIndex("ename"))
									+ " "
									+ (_c2.getString(_c2.getColumnIndex("name")) == null ? ""
											: _c2.getString(_c2
													.getColumnIndex("name"))
													+ " ")
									+ _c.getString(_c.getColumnIndex("name"))
									+ (_c.getString(
											_c.getColumnIndex("capacity"))
											.equals("(null)") ? ""
											: _c.getString(_c
													.getColumnIndex("capacity"))))
							.retailPrice(
									_c.getFloat(_c
											.getColumnIndex("retail_price")))
							.lowestImPrice(
									_c.getFloat(_c
											.getColumnIndex("lowest_im_price")))
							.avImPrice(
									_c.getFloat(_c
											.getColumnIndex("av_im_price")))
							.avImPriceInStock(
									_c.getFloat(_c
											.getColumnIndex("av_im_price_in_stock")))
							.avShipmentCostInStock(
									_c.getFloat(_c
											.getColumnIndex("av_shipment_cost_in_stock")))
							.quantity(_c.getInt(_c.getColumnIndex("quantity")))
							.salesVolume(
									_c.getInt(_c
											.getColumnIndex("total_imported"))
											- _c.getInt(_c
													.getColumnIndex("quantity")))
							.wholesalePrice(_tp).wholesaleCount(_tc).build());
		}
		_c.close();
		this.mProductsPanel.invalidate();
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
	//TODO
	public void popupShortcuts(){
		
	}

	public class BrandAdpter extends BaseAdapter {
		private LayoutInflater mInflater;

		public BrandAdpter(LayoutInflater inflater) {
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

	public class Brand {
		int BKEY;
		String name;
		CheckedTextView vh;
		boolean checked = true;

		public Brand(int BKEY, String name) {
			this.BKEY = BKEY;
			this.name = name;
		}
	}

	public class TypeAdpter extends BaseAdapter {
		private LayoutInflater mInflater;

		public TypeAdpter(LayoutInflater inflater) {
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

	public class Type {
		int TKEY;
		String name;
		CheckedTextView vh;
		boolean checked = false;

		public Type(int TKEY, String name) {
			this.TKEY = TKEY;
			this.name = name;
		}
	}

	public class ProductAdapter extends BaseAdapter {
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

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			convertView = mInflater.inflate(R.layout._product_item, parent,
					false);
			// ImageView _photo =
			// (ImageView)convertView.findViewById(R.id.iv_prodphoto);
			CheckedTextView _name = (CheckedTextView) convertView
					.findViewById(R.id.cb_prodname);
			TextView _rPrice = (TextView) convertView
					.findViewById(R.id.tv_retailprice);
			TextView _wCount1 = (TextView) convertView
					.findViewById(R.id.tv_wholesaleprice_c1);
			TextView _wCount2 = (TextView) convertView
					.findViewById(R.id.tv_wholesaleprice_c2);
			TextView _wCount3 = (TextView) convertView
					.findViewById(R.id.tv_wholesaleprice_c3);
			TextView _wPrice1 = (TextView) convertView
					.findViewById(R.id.tv_wholesaleprice_p1);
			TextView _wPrice2 = (TextView) convertView
					.findViewById(R.id.tv_wholesaleprice_p2);
			TextView _wPrice3 = (TextView) convertView
					.findViewById(R.id.tv_wholesaleprice_p3);
			TextView _avImPrIS = (TextView) convertView
					.findViewById(R.id.tv_avimpris);
			TextView _quantity = (TextView) convertView
					.findViewById(R.id.tv_qunatity);
			TextView _lowestImPr = (TextView) convertView
					.findViewById(R.id.tv_lowestimpr);
			TextView _avImPr = (TextView) convertView
					.findViewById(R.id.tv_avimpr);
			TextView _salesVolume = (TextView) convertView
					.findViewById(R.id.tv_salesvolume);

			Product _temp = mProducts.get(position);
			_temp.detail = convertView.findViewById(R.id.ly_product_detail);
			_name.setText(_temp.name);
			_name.setChecked(_temp.isLocked);
			_name.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((CheckedTextView) v).toggle();
					mProducts.get(position).isLocked = !mProducts.get(position).isLocked;
					if (mProducts.get(position).isLocked) {
						mLockedProducts.add(mProducts.get(position));
					} else {
						mLockedProducts.remove(mProducts.get(position));
					}
				}
			});
			_rPrice.setText("￥ "
					+ (_temp.retailPrice == 0 ? " " : ("" + _temp.retailPrice)));
			_rPrice.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!mProducts.get(position).showDetail) {
						mProducts.get(position).detail
								.setVisibility(View.VISIBLE);
						mProducts.get(position).showDetail = true;
					} else {
						mProducts.get(position).detail.setVisibility(View.GONE);
						mProducts.get(position).showDetail = false;
					}
				}
			});
			int _i = 0;
			String[] _wholesaleCounts = new String[] { "", "", "" };
			String[] _wholesalePrices = new String[] { "", "", "" };
			while (_i < 3) {
				if (_temp.wholesalePrice[_i] == 0) {
					break;
				}
				StringBuffer _x = new StringBuffer();
				StringBuffer _y = new StringBuffer();
				if (_i == 0) {
					if (_temp.wholesaleCount[_i] == 1) {
						_x.append("1");
					} else {
						_x.append("1-" + _temp.wholesaleCount[_i]);
					}
					_y.append("￥").append(_temp.wholesalePrice[_i]);
					_wholesaleCounts[_i] = _x.toString().trim();
					_wholesalePrices[_i] = _y.toString().trim();
					_i++;
					continue;
				}
				if (_i == 2 || _temp.wholesalePrice[_i + 1] == 0) {
					_x.append(_temp.wholesaleCount[_i] + "-");
					_y.append("￥").append(_temp.wholesalePrice[_i]);
					_wholesaleCounts[_i] = _x.toString().trim();
					_wholesalePrices[_i] = _y.toString().trim();
					break;
				}
				_x.append((_temp.wholesaleCount[_i - 1] + 1) + "-"
						+ _temp.wholesaleCount[_i]);
				_y.append("￥").append(_temp.wholesalePrice[_i]);
				_wholesaleCounts[_i] = _x.toString().trim();
				_wholesalePrices[_i] = _y.toString().trim();
				_i++;
			}
			_wCount1.setText(_wholesaleCounts[0]);
			_wCount2.setText(_wholesaleCounts[1]);
			_wCount3.setText(_wholesaleCounts[2]);
			_wPrice1.setText(_wholesalePrices[0]);
			_wPrice2.setText(_wholesalePrices[1]);
			_wPrice3.setText(_wholesalePrices[2]);
			_avImPrIS.setText("￥"
					+ (_temp.avImPriceInStock + _temp.avShipmentCostInStock)
					+ "(￥" + _temp.avShipmentCostInStock + ")");
			_quantity.setText(Html.fromHtml(_temp.quantity+"<font color=\"#FF0000\">-0</font><font color=\"#00FF00\">+0</font>"));
			_lowestImPr.setText("￥"+_temp.lowestImPrice);
			_avImPr.setText("￥"+_temp.avImPrice);
			_salesVolume.setText(""+_temp.salesVolume);
			return convertView;
		}
	}
}