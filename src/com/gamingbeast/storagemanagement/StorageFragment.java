package com.gamingbeast.storagemanagement;

import java.util.ArrayList;
import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class StorageFragment extends Fragment {

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

	public StorageFragment() {
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
		View _contentView = inflater.inflate(R.layout.fragment_storage,
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

		mTypeOptions.setAdapter(new TypeAdapter(inflater));
		mSelectAllTypes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((CheckedTextView) v).toggle();
				boolean _isChecked = ((CheckedTextView) v).isChecked();
				int _t = mTypes.size();
				for (int _i = 0; _i < _t; _i++) {
					Type _temp = mTypes.get(_i);
					_temp.checked = _isChecked;
					if (mIsTypeShown && _temp.vh != null) {
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
					closeBrandOption();
				}
				if (!mIsTypeShown) {
					mTypeOptions.invalidateViews();
				}
				mTypeOptions.setVisibility(mIsTypeShown ? View.GONE
						: View.VISIBLE);
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
				((CheckedTextView) v).toggle();
				boolean _isChecked = ((CheckedTextView) v).isChecked();
				int _t = mBrands.size();
				for (int _i = 0; _i < _t; _i++) {
					Brand _temp = mBrands.get(_i);
					_temp.checked = _isChecked;
					if (mIsBrandShown && _temp.vh != null) {
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
					closeTypeOption();
				}
				if (!mIsBrandShown) {
					mBrandOptions.invalidateViews();
				}
				mBrandOptions.setVisibility(mIsBrandShown ? View.GONE
						: View.VISIBLE);
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
					closeTypeOption();
				}
				if (mIsBrandShown) {
					closeBrandOption();
				}
				return v.performClick();
			}
		});
		// TODO
		/*
		 * mProductsPanel.setOnItemLongClickListener(new
		 * OnItemLongClickListener(){
		 * 
		 * @Override public boolean onItemLongClick(AdapterView<?> parent, View
		 * view, int position, long id) { popupShortcuts(); return false; }});
		 */
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
		mProducts.retainAll(mLockedProducts);
		mProductsPanel.invalidateViews();

		while (_c.moveToNext()) {
			if (mProducts.contains(new Product(_c.getInt(_c
					.getColumnIndex("PKEY"))))) {
				continue;
			}
			Cursor _c2 = DBHelper
					.getDatabase()
					.rawQuery(
							"select * from brand where BKEY = ?",
							new String[] { _c.getInt(_c.getColumnIndex("brand"))
									+ "" });
			_c2.moveToNext();
			Cursor _c3 = DBHelper
					.getDatabase()
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
			String _mis = null;
			if (!_c.isNull(_c.getColumnIndex("lowest_im_time"))) {
				_c3 = DBHelper.getDatabase().rawQuery(
						"select name from source where SKEY = ?",
						new String[] { _c.getInt(_c
								.getColumnIndex("lowest_im_source")) + "" });
				_c3.moveToNext();
				_mis = _c3.getString(0);
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
									+ (_c.getString(_c
											.getColumnIndex("capacity")) == null ? ""
											: (" " + _c.getString(_c
													.getColumnIndex("capacity")))))
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
							.quantityIm(
									_c.getInt(_c.getColumnIndex("quantity_im")))
							.quantityEx(
									_c.getInt(_c.getColumnIndex("quantity_ex")))
							.quantityC(
									_c.getInt(_c.getColumnIndex("quantity_c")))
							.wholesalePrice(_tp)
							.wholesaleCount(_tc)
							.lowestImTime(
									_c.getString(_c
											.getColumnIndex("lowest_im_time")))
							.lowestImSource(_mis).build());
			_c2.close();
			_c3.close();
		}
		_c.close();
		mProductsPanel.invalidateViews();
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

	private void closeBrandOption() {
		mBrandOptions.setVisibility(View.GONE);
		mShowBrands.setImageResource(R.drawable.bt_options_show);
		mIsBrandShown = false;
	}

	private void closeTypeOption() {
		mTypeOptions.setVisibility(View.GONE);
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
			convertView = mInflater.inflate(R.layout._option_item, parent,
					false);
			CheckedTextView vh = (CheckedTextView) convertView
					.findViewById(R.id.cb_option);
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
					} else if (--mSelectedBrandsCount < mBrands.size()) {
						mSelectAllBrands.setChecked(false);
					}

					showProducts();
				}
			});
			Brand _temp = mBrands.get(position);
			vh.setText(_temp.name);
			vh.setChecked(_temp.checked);
			vh.setTextColor(_temp.checked ? Color.BLUE : Color.BLACK);
			_temp.vh = vh;

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
				convertView = mInflater.inflate(R.layout._option_item, parent,
						false);
				CheckedTextView vh = (CheckedTextView) convertView.findViewById(R.id.cb_option);
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

			convertView = mInflater.inflate(R.layout._product_storage, parent,
					false);
			ImageView _photo = (ImageView) convertView
					.findViewById(R.id.iv_prod_photo);
			CheckedTextView _name = (CheckedTextView) convertView
					.findViewById(R.id.cb_prod_name);
			TextView _rPrice = (TextView) convertView
					.findViewById(R.id.tv_prod_retailprice);
			TextView _wCount1 = (TextView) convertView
					.findViewById(R.id.tv_prod_wholesaleprice_c1);
			TextView _wCount2 = (TextView) convertView
					.findViewById(R.id.tv_prod_wholesaleprice_c2);
			TextView _wCount3 = (TextView) convertView
					.findViewById(R.id.tv_prod_wholesaleprice_c3);
			TextView _wPrice1 = (TextView) convertView
					.findViewById(R.id.tv_prod_wholesaleprice_p1);
			TextView _wPrice2 = (TextView) convertView
					.findViewById(R.id.tv_prod_wholesaleprice_p2);
			TextView _wPrice3 = (TextView) convertView
					.findViewById(R.id.tv_prod_wholesaleprice_p3);
			TextView _avImPrIS = (TextView) convertView
					.findViewById(R.id.tv_prod_avimpris);
			TextView _quantity = (TextView) convertView
					.findViewById(R.id.tv_prod_qunatity);
			TextView _lowestImPr = (TextView) convertView
					.findViewById(R.id.tv_lowest_price);
			TextView _avImPr = (TextView) convertView
					.findViewById(R.id.tv_prod_avimpr);
			TextView _salesVolume = (TextView) convertView
					.findViewById(R.id.tv_prod_salesvolume);
			TextView _lowestImTime = (TextView) convertView
					.findViewById(R.id.tv_lowest_time);
			TextView _lowestImSource = (TextView) convertView
					.findViewById(R.id.tv_lowset_source);

			final Product _temp = mProducts.get(position);
			final View _detail = convertView.findViewById(R.id.ly_prod_detail);
			_name.setText(_temp.name);
			_name.setChecked(_temp.isLocked);
			_name.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((CheckedTextView) v).toggle();
					_temp.isLocked = !_temp.isLocked;
					if (_temp.isLocked) {
						mLockedProducts.add(_temp);
					} else {
						mLockedProducts.remove(_temp);
					}
				}
			});
			int _tt = getResources().getIdentifier("photo" + _temp.PKEY,
					"drawable", "com.gamingbeast.storagemanagement");
			_photo.setImageResource(_tt > 0 ? _tt
					: R.drawable.photo_prod_default);
			_rPrice.setText("￥ "
					+ (_temp.retailPrice == 0 ? " " : ("" + _temp.retailPrice)));
			_rPrice.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!_temp.showDetail) {
						_detail.setVisibility(View.VISIBLE);
						_temp.showDetail = true;
					} else {
						_detail.setVisibility(View.GONE);
						_temp.showDetail = false;
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
			_quantity.setText(Html.fromHtml(_temp.quantity
					+ "<font color=\"#FF0000\"> - " + _temp.quantityEx
					+ "</font><font color=\"#00FF00\"> + " + _temp.quantityIm
					+ "</font>"));
			_lowestImPr.setText("￥" + _temp.lowestImPrice);
			_avImPr.setText("￥" + _temp.avImPrice);
			_salesVolume.setText("" + _temp.salesVolume);
			_lowestImTime.setText(_temp.lowestImTime);
			_lowestImSource.setText(_temp.lowestImSource);
			return convertView;
		}
	}

}