package com.gamingbeast.storagemanagement;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView.OnEditorActionListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ReceiveFragment extends Fragment {
	private GridView mSourceOptions;
	private ExpandableListView mOrderPanel;
	private CheckedTextView mSelectAllSources;
	private ImageButton mShowSources;
	private ImageButton mNextPage;
	private ImageButton mPrevPage;
	private EditText mShipmentCost;
	private TextView mCurrPage;
	private TextView mRecevieConfirm;

	private int mSelectedSourcesCount = 0;
	private int mPage = 1;
	private int mPageLimit = 1;
	private int mSelectedO = -1;
	private float mShipment = 0;

	private ArrayList<Source> mSources = new ArrayList<Source>();
	private ArrayList<Order> mOrders = new ArrayList<Order>();
	private ArrayList<List<OrderDetail>> mOrderDetails = new ArrayList<List<OrderDetail>>();

	private boolean mIsSourceShown = false;

	public ReceiveFragment() {
		super();
		Cursor _c = DBHelper.getDatabase().rawQuery(
				"select * from source order by name", null);
		while (_c.moveToNext()) {
			mSources.add(new Source(_c.getInt(_c.getColumnIndex("SKEY")), _c
					.getString(_c.getColumnIndex("name"))));
		}
		_c.close();
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View _contentView = inflater.inflate(R.layout.fragment_receive,
				container, false);

		mSourceOptions = (GridView) _contentView
				.findViewById(R.id.gv_options_source);
		mOrderPanel = (ExpandableListView) _contentView
				.findViewById(R.id.el_orders);
		mSelectAllSources = (CheckedTextView) _contentView
				.findViewById(R.id.cb_selectall_sources);
		mShowSources = (ImageButton) _contentView
				.findViewById(R.id.bt_showall_sources);
		mNextPage = (ImageButton) _contentView.findViewById(R.id.bt_page_next);
		mPrevPage = (ImageButton) _contentView.findViewById(R.id.bt_page_prev);
		mShipmentCost = (EditText) _contentView
				.findViewById(R.id.et_shipmentcost);
		mCurrPage = (TextView) _contentView.findViewById(R.id.tv_page_curr);
		mRecevieConfirm = (TextView) _contentView
				.findViewById(R.id.bt_receive_confirm);

		mSourceOptions.setAdapter(new SourceAdapter(inflater));
		mSelectAllSources.setChecked(true);
		mSelectAllSources.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean _isChecked = !((CheckedTextView) v).isChecked();
				((CheckedTextView) v).toggle();
				int _t = mSources.size();
				for (int _i = 0; _i < _t; _i++) {
					Source _temp = mSources.get(_i);
					_temp.checked = _isChecked;
					if (_temp.vh != null) {
						_temp.vh.setChecked(_isChecked);
						_temp.vh.setTextColor(_isChecked ? Color.BLUE
								: Color.BLACK);
					}
				}
				mSelectedSourcesCount = _isChecked ? _t : 0;
				mPage = 1;
				mSelectedO = -1;
				showOrders();
			}
		});
		mShowSources.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (mIsSourceShown) {
					mSourceOptions.invalidateViews();
				}
				mSourceOptions.setVisibility(mIsSourceShown ? View.GONE
						: View.VISIBLE);
				mShowSources
						.setImageResource(mIsSourceShown ? R.drawable.bt_options_show
								: R.drawable.bt_options_hide);
				mIsSourceShown = !mIsSourceShown;
			}
		});
		mPrevPage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPage--;
				mCurrPage.setText(mPage + "");
				mNextPage.setEnabled(true);
				if (mPage <= 1) {
					v.setEnabled(false);
				}
				showOrders();
			}
		});
		mPrevPage.setEnabled(false);
		mNextPage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPage++;
				mCurrPage.setText(mPage + "");
				mPrevPage.setEnabled(true);
				if (mPage >= mPageLimit) {
					v.setEnabled(false);
				}
				showOrders();
			}
		});
		mNextPage.setEnabled(false);
		mCurrPage.setText(mPage + "");
		mRecevieConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmReceive();
			}
		});
		mOrderPanel.setGroupIndicator(getResources().getDrawable(
				R.drawable.bg_divider_order));
		mOrderPanel.setChildDivider(getResources().getDrawable(
				R.drawable.bg_divider_orderdetail));
		mOrderPanel.setAdapter(new OrderAdapter(inflater));
		mOrderPanel.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mSourceOptions.setVisibility(View.GONE);
				return false;
			}
		});
		mSelectedSourcesCount = mSources.size();
		mShipmentCost.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) {
				v.setFocusableInTouchMode(true);
				v.findFocus();
				return false;
			}
		});
		mShipmentCost.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (v.getText() == null ||v.getText().toString().equals("")) {
					mShipment = 0;
					v.setFocusableInTouchMode(false);
					v.clearFocus();
					return false;
				}
				float _t = Float.parseFloat(v.getText().toString()
						.replace("￥", ""));
				mShipment = _t;
				v.setText("￥" + _t);
				v.setFocusableInTouchMode(false);
				v.clearFocus();
				return false;
			}
		});
		showOrders();
		return _contentView;
	}

	private void showOrders() {
		if (mSelectedSourcesCount <= 0) {
			return;
		}
		mOrders.clear();
		mOrderDetails.clear();
		String _limit = "";
		if (mPage == 1) {
			Cursor _cc = DBHelper.getDatabase().rawQuery(
					generateQueryOrdersCount(), null);
			_cc.moveToNext();
			if (_cc.getInt(0) > 10) {
				mPageLimit = _cc.getInt(0) / 10 + 1;
				mNextPage.setEnabled(true);
				_limit = "limit 10";
			}
			_cc.close();
		} else {
			_limit = "limit " + mPage * 10 + " offset " + (mPage - 1) * 10;
		}
		Cursor _c = DBHelper.getDatabase().rawQuery(
				generateQueryOrders(_limit), null);
		while (_c.moveToNext()) {
			Order _temp = new Order(_c.getInt(_c.getColumnIndex("OKEY")),
					_c.getString(_c.getColumnIndex("order_time")), _c.getInt(_c
							.getColumnIndex("source")), _c.getFloat(_c
							.getColumnIndex("sum_price")), _c.getString(_c
							.getColumnIndex("pay_time")), _c.getInt(_c
							.getColumnIndex("sum_quantity")), _c.getInt(_c
							.getColumnIndex("receive_quantity")));
			mOrders.add(_temp);
			Cursor _c2 = DBHelper.getDatabase().rawQuery(
					"select name from source where SKEY = " + _temp.source,
					null);
			_c2.moveToNext();
			_temp.sourceName = _c2.getString(0);
			_c2.close();
		}

		int _j = mOrders.size();
		for (int _i = 0; _i < _j; _i++) {
			_c = DBHelper.getDatabase().rawQuery(
					generateQueryOrderDetails(mOrders.get(_i).OKEY), null);
			List<OrderDetail> _t = new ArrayList<OrderDetail>();
			while (_c.moveToNext()) {
				OrderDetail _temp = new OrderDetail(_c.getInt(_c
						.getColumnIndex("IKEY")), _c.getInt(_c
						.getColumnIndex("order_k")), _c.getInt(_c
						.getColumnIndex("product")), _c.getInt(_c
						.getColumnIndex("quantity")), _c.getString(_c
						.getColumnIndex("receive_time")), _c.getFloat(_c
						.getColumnIndex("price")));
				_t.add(_temp);
				Cursor _c2 = DBHelper.getDatabase().rawQuery(
						"select * from product where PKEY = " + _temp.product,
						null);
				_c2.moveToNext();
				Cursor _c3 = DBHelper
						.getDatabase()
						.rawQuery(
								"select * from brand where BKEY = "
										+ _c2.getInt(_c2
												.getColumnIndex("brand")), null);
				_c3.moveToNext();
				_temp.productName = _c3.getString(_c3.getColumnIndex("ename"))
						+ " "
						+ (_c3.getString(_c3.getColumnIndex("name")) == null ? ""
								: _c3.getString(_c3.getColumnIndex("name"))
										+ " ")
						+ _c2.getString(_c2.getColumnIndex("name"));
				_c2.close();
				_c3.close();
			}
			mOrderDetails.add(_t);
		}
		_c.close();
		mOrderPanel.invalidateViews();
	}

	private void payOrder(Order o) {
		DBHelper.getDatabase().execSQL(generateQueryPayOrder(o.OKEY));
		o.paid = true;
	}

	private void cancelImport(int IKEY) {
		DBHelper.getDatabase().execSQL(generateQueryDeleteOrderDetail(IKEY));
		mPage = 1;
		mSelectedO = -1;
		showOrders();
	}

	private void confirmReceive() {
		if (mSelectedO == -1 || mShipment == 0) {
			return;
		}
		List<OrderDetail> _temp = mOrderDetails.get(mSelectedO);
		StringBuffer _s = new StringBuffer();
		int _j = _temp.size();
		int _count = 0;
		for (int _i = 0; _i < _j; _i++) {
			if (!_temp.get(_i).performR) {
				continue;
			}
			OrderDetail _t = _temp.get(_i);
			_s.append(_t.productName + " X " + _t.quantityR + "\n");
			_count += _t.quantityR;
		}
		final int _fc = _count;
		new AlertDialog.Builder(getActivity()).setTitle("邮费 ￥" + mShipment)
				.setMessage(_s.toString())
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						List<OrderDetail> _temp = mOrderDetails.get(mSelectedO);
						int _j = _temp.size();
						for (int _i = 0; _i < _j; _i++) {
							if (!_temp.get(_i).performR) {
								continue;
							}
							OrderDetail _t = _temp.get(_i);
							if (_t.quantityR < _t.quantity) {
								DBHelper.getDatabase().execSQL(
										generateQueryReceive(_t.IKEY,
												" ,quantity = " + _t.quantityR,
												mShipment / _fc));
								DBHelper.getDatabase().execSQL(
										generateQueryOrderDetail(_t,
												_t.quantity - _t.quantityR));
								DBHelper.getDatabase().execSQL(
										"update product set quantity_im = quantity_im-"
												+ (_t.quantity - _t.quantityR)
												+ " where product.PKEY = "
												+ _t.product);
								DBHelper.getDatabase()
										.execSQL(
												"update import_order set sum_price = sum_price - "
														+ (_t.quantity - _t.quantityR)
														* _t.price
														+ " , sum_quantity = sum_quantity - "
														+ (_t.quantity - _t.quantityR)
														+ " where import_order.OKEY = "
														+ _t.orderK);
							} else {
								DBHelper.getDatabase().execSQL(
										generateQueryReceive(_t.IKEY, "",
												mShipment / _fc));
							}
						}
						mShipment = 0;
						mShipmentCost.setText(null);
						mPage = 1;
						mSelectedO = -1;
						showOrders();
						mOrderPanel.invalidateViews();
						Toast.makeText(getActivity(), "收货成功",
								Toast.LENGTH_SHORT).show();
					}
				}).setNegativeButton("否", null).show();
	}

	private String generateQueryPayOrder(int OKEY) {
		return new StringBuffer(
				"update import_order set pay_time = datetime('now') where OKEY = ")
				.append(OKEY).toString();
	}

	private String generateQueryOrdersCount() {
		StringBuffer _sql = new StringBuffer(
				"select count(OKEY) from import_order where source in (");
		int _t = mSources.size();
		for (int _i = 0; _i < _t; _i++) {
			Source _temp = mSources.get(_i);
			if (_temp.checked) {
				_sql.append(_temp.SKEY);
				_sql.append(",");
			}
		}
		_sql.deleteCharAt(_sql.length() - 1).append(")");
		return _sql.toString();
	}

	private String generateQueryOrders(String limit) {
		StringBuffer _sql = new StringBuffer(
				"select * from import_order where source in (");
		int _t = mSources.size();
		for (int _i = 0; _i < _t; _i++) {
			Source _temp = mSources.get(_i);
			if (_temp.checked) {
				_sql.append(_temp.SKEY);
				_sql.append(",");
			}
		}
		_sql.deleteCharAt(_sql.length() - 1).append(") order by OKEY ")
				.append(limit);
		return _sql.toString().trim();
	}

	private String generateQueryOrderDetails(int OKEY) {
		return new StringBuffer("select * from import_detail where order_k = ")
				.append(OKEY).append(" order by receive_time").toString();
	}

	private String generateQueryReceive(int IKEY, String opt, float shipmentC) {
		return new StringBuffer(
				"update import_detail set receive_time = date('now'), shipment_cost = "
						+ shipmentC + opt + " where IKEY = ").append(IKEY)
				.toString();
	}

	private String generateQueryOrderDetail(OrderDetail od, int quantity) {
		return new StringBuffer(
				"insert into import_detail (order_k,product,quantity,price) values (")
				.append(od.orderK).append(",").append(od.product).append(",")
				.append(quantity).append(",").append(od.price).append(")")
				.toString();
	}

	private String generateQueryDeleteOrderDetail(int IKEY) {
		return new StringBuffer("delete from import_detail where IKEY = ")
				.append(IKEY).toString();
	}

	public class Source {
		int SKEY;
		String name;
		boolean checked = true;
		CheckedTextView vh;

		public Source(int SKEY, String name) {
			this.SKEY = SKEY;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public class SourceAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public SourceAdapter(LayoutInflater inflater) {
			mInflater = inflater;
		}

		@Override
		public int getCount() {
			return mSources.size();
		}

		@Override
		public Object getItem(int position) {
			return mSources.get(position);
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
						mSources.get(position).checked = ((CheckedTextView) v)
								.isChecked();
						((CheckedTextView) v).setTextColor(((CheckedTextView) v)
								.isChecked() ? Color.BLUE : Color.BLACK);
						if (((CheckedTextView) v).isChecked()) {
							if (++mSelectedSourcesCount == mSources.size()) {
								mSelectAllSources.setChecked(true);
							}
						} else {
							if (--mSelectedSourcesCount < mSources.size()) {
								mSelectAllSources.setChecked(false);
							}
						}
						mPage = 1;
						mSelectedO = -1;
						showOrders();
					}
				});
				Source _temp = mSources.get(position);
				vh.setText(_temp.name);
				vh.setChecked(_temp.checked);
				vh.setTextColor(_temp.checked ? Color.BLUE : Color.BLACK);
				_temp.vh = vh;
			}
			return convertView;
		}
	}

	public class Order {
		boolean paid = false;
		int OKEY;
		String orderTime;
		int source;
		String sourceName;
		float sumPrice;
		String payTime;
		int sumQuantity;
		int receiveQuantity;

		public Order(int OKEY, String orderTime, int source, float sumPrice,
				String payTime, int sumQuantity, int receiveQuantity) {
			this.OKEY = OKEY;
			this.orderTime = orderTime;
			this.source = source;
			this.sumPrice = sumPrice;
			this.sumQuantity = sumQuantity;
			this.receiveQuantity = receiveQuantity;
			if (payTime != null) {
				this.payTime = payTime.substring(0, 16);
				paid = true;
			}
		}
	}

	public class OrderDetail {
		boolean received = false;
		boolean performR = false;
		int IKEY;
		int orderK;
		int product;
		int quantity;
		int quantityR;
		float price;
		float shipmentPrice;
		String receiveTime;
		String productName;

		public OrderDetail(int IKEY, int orderK, int product, int quantity,
				String receiveTime, float price) {
			this.IKEY = IKEY;
			this.orderK = orderK;
			this.product = product;
			this.quantity = quantity;
			this.receiveTime = receiveTime;
			this.price = price;
			quantityR = 0;
			if (receiveTime != null) {
				received = true;
			}
		}
	}

	public class OrderAdapter extends BaseExpandableListAdapter {
		private LayoutInflater mInflater;

		public OrderAdapter(LayoutInflater inflater) {
			mInflater = inflater;
		}

		@Override
		public int getGroupCount() {
			return mOrders.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mOrderDetails.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mOrders.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mOrderDetails.get(groupPosition).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return mOrders.get(groupPosition).OKEY;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return mOrderDetails.get(groupPosition).get(childPosition).IKEY;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout._order_import, parent,
					false);
			final Order _temp = mOrders.get(groupPosition);
			TextView _imOrder = (TextView) convertView
					.findViewById(R.id.tv_im_order);
			TextView _imState = (TextView) convertView
					.findViewById(R.id.tv_im_state);
			final ToggleButton _imPaid = (ToggleButton) convertView
					.findViewById(R.id.st_im_pay);
			final TextView _imPayTime = (TextView) convertView
					.findViewById(R.id.tv_im_paytime);
			_imOrder.setText(" " + _temp.orderTime + " " + _temp.sourceName);
			_imState.setText(" " + _temp.receiveQuantity + "/"
					+ _temp.sumQuantity);
			if (_temp.paid) {
				_imPaid.setVisibility(View.GONE);
				_imPayTime.setText("  付款时间 " + _temp.payTime);
				_imPayTime.setVisibility(View.VISIBLE);
			} else {
				_imOrder.setTextColor(Color.BLUE);
				_imPaid.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						buttonView.setChecked(false);
						new AlertDialog.Builder(getActivity())
								.setTitle("付款")
								.setMessage("确认支付￥" + _temp.sumPrice)
								.setPositiveButton("是",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												payOrder(_temp);
												_imPaid.setVisibility(View.GONE);
												_imPayTime.setText("已付款");
												_imPayTime
														.setVisibility(View.VISIBLE);
												Toast.makeText(getActivity(),
														"付款成功",
														Toast.LENGTH_SHORT)
														.show();
											}
										}).setNegativeButton("否", null).show();
					}
				});
				_imPaid.setFocusable(false);
			}
			return convertView;
		}

		@Override
		public View getChildView(final int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout._orderdetail_imexport,
					parent, false);
			TextView _name = (TextView) convertView
					.findViewById(R.id.tv_prod_name);
			LinearLayout _actionArea = (LinearLayout) convertView
					.findViewById(R.id.ly_action_area);
			TextView _receiveTime = (TextView) convertView
					.findViewById(R.id.tv_action_date);
			ImageView _photo = (ImageView) convertView
					.findViewById(R.id.iv_prod_photo);

			final OrderDetail _temp = mOrderDetails.get(groupPosition).get(
					childPosition);
			_name.setText(_temp.productName);
			int _tt = getResources().getIdentifier("photo" + _temp.product,
					"drawable", "com.gamingbeast.storagemanagement");
			_photo.setImageResource(_tt > 0 ? _tt
					: R.drawable.photo_prod_default);
			if (_temp.received) {
				_actionArea.setVisibility(View.GONE);
				_receiveTime.setVisibility(View.VISIBLE);
				_receiveTime.setText("数量 " + _temp.quantity + "  收货时间 "
						+ _temp.receiveTime);
			} else {
				final ImageButton _minus = (ImageButton) convertView
						.findViewById(R.id.bt_action_minus);
				final EditText _quantityR = (EditText) convertView
						.findViewById(R.id.et_quantity_action);
				TextView _quantityS = (TextView) convertView
						.findViewById(R.id.tv_quantity_sum);
				final ImageButton _plus = (ImageButton) convertView
						.findViewById(R.id.bt_action_plus);
				ImageButton remove = (ImageButton) convertView
						.findViewById(R.id.bt_cart_remove);
				if (_temp.quantityR != 0) {
					_quantityR.setText(_temp.quantityR + "");
				}

				_quantityS.setText("/ " + _temp.quantity);
				_minus.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						_temp.quantityR--;
						if (_temp.quantityR == 0) {
							_minus.setEnabled(true);
							_temp.performR = false;
						}
						_plus.setEnabled(true);
						_quantityR.setText("" + _temp.quantityR);
					}
				});
				_minus.setEnabled(false);
				_quantityR.setOnTouchListener(new OnTouchListener() {
					@SuppressLint("ClickableViewAccessibility")
					public boolean onTouch(View v, MotionEvent event) {
						v.setFocusableInTouchMode(true);
						v.findFocus();
						return false;
					}
				});
				_quantityR
						.setOnEditorActionListener(new OnEditorActionListener() {
							@Override
							public boolean onEditorAction(TextView v,
									int actionId, KeyEvent event) {
								if (v.getText() == null || v.getText().toString().equals("")) {
									_minus.setEnabled(false);
									_plus.setEnabled(true);
									_temp.performR = false;
									_temp.quantityR = 0;
									v.setFocusableInTouchMode(false);
									v.clearFocus();
									return false;
								}
								int _t = Integer.parseInt(v.getText()
										.toString());
								if (_t == 0) {
									_minus.setEnabled(false);
									_plus.setEnabled(true);
									_temp.performR = false;
								} else if (_t >= _temp.quantity) {
									v.setText("" + _temp.quantity);
									_t = _temp.quantity;
									_minus.setEnabled(true);
									_plus.setEnabled(false);
								} else {
									_minus.setEnabled(true);
									_plus.setEnabled(true);
								}
								_temp.performR = true;
								mSelectedO = groupPosition;
								_temp.quantityR = _t;
								v.setFocusableInTouchMode(false);
								v.clearFocus();
								return false;
							}
						});
				_plus.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						_temp.quantityR++;
						if (_temp.quantityR == _temp.quantity) {
							_plus.setEnabled(false);
						}
						_temp.performR = true;
						mSelectedO = groupPosition;
						_minus.setEnabled(true);
						_quantityR.setText("" + _temp.quantityR);
					}
				});

				_quantityS.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						_temp.quantityR = _temp.quantity;
						_plus.setEnabled(false);
						_minus.setEnabled(true);
						_temp.performR = true;
						mSelectedO = groupPosition;
						_quantityR.setText("" + _temp.quantityR);
						mOrderPanel.invalidateViews();
					}
				});
				remove.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						new AlertDialog.Builder(getActivity())
								.setTitle("取消进货")
								.setMessage("一旦取消不能恢复 确定吗？")
								.setPositiveButton("是",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												cancelImport(_temp.IKEY);
											}
										}).setNegativeButton("否", null).show();
					}
				});
			}
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
	}
}