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
import android.text.InputType;
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

public class PostFragment extends Fragment {
	private GridView mCustomerOptions;
	private ExpandableListView mOrderPanel;
	private CheckedTextView mSelectAllCustomers;
	private ImageButton mShowCustomers;
	private ImageButton mNextPage;
	private ImageButton mPrevPage;
	private EditText mShipmentCost;
	private TextView mCurrPage;
	private TextView mRecevieConfirm;

	private int mSelectedCustomersCount = 0;
	private int mPage = 1;
	private int mPageLimit = 1;
	private int mSelectedO = -1;
	private float mShipment = 0;

	private ArrayList<Customer> mCustomers = new ArrayList<Customer>();
	private ArrayList<Order> mOrders = new ArrayList<Order>();
	private ArrayList<List<OrderDetail>> mOrderDetails = new ArrayList<List<OrderDetail>>();

	private boolean mIsCustomerShown = false;

	public PostFragment() {
		super();
		Cursor _c = DBHelper.getDatabase().rawQuery(
				"select * from customer order by name", null);
		while (_c.moveToNext()) {
			mCustomers.add(new Customer(_c.getInt(_c.getColumnIndex("CKEY")),
					_c.getString(_c.getColumnIndex("name"))));
		}
		_c.close();
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View _contentView = inflater.inflate(R.layout.fragment_post, container,
				false);

		mCustomerOptions = (GridView) _contentView
				.findViewById(R.id.gv_options_customer);
		mOrderPanel = (ExpandableListView) _contentView
				.findViewById(R.id.el_orders);
		mSelectAllCustomers = (CheckedTextView) _contentView
				.findViewById(R.id.cb_selectall_customers);
		mShowCustomers = (ImageButton) _contentView
				.findViewById(R.id.bt_showall_customers);
		mNextPage = (ImageButton) _contentView.findViewById(R.id.bt_page_next);
		mPrevPage = (ImageButton) _contentView.findViewById(R.id.bt_page_prev);
		mShipmentCost = (EditText) _contentView
				.findViewById(R.id.et_shipmentcost);
		mCurrPage = (TextView) _contentView.findViewById(R.id.tv_page_curr);
		mRecevieConfirm = (TextView) _contentView
				.findViewById(R.id.bt_post_confirm);

		mCustomerOptions.setAdapter(new CustomerAdapter(inflater));
		mSelectAllCustomers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean _isChecked = !((CheckedTextView) v).isChecked();
				((CheckedTextView) v).toggle();
				int _t = mCustomers.size();
				for (int _i = 0; _i < _t; _i++) {
					Customer _temp = mCustomers.get(_i);
					_temp.checked = _isChecked;
					if (mIsCustomerShown && _temp.vh != null) {
						_temp.vh.setChecked(_isChecked);
						_temp.vh.setTextColor(_isChecked ? Color.BLUE
								: Color.BLACK);
					}
				}
				mSelectedCustomersCount = _isChecked ? _t : 0;
				mPage = 1;
				mSelectedO = -1;
				showOrders();
			}
		});
		mShowCustomers.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (!mIsCustomerShown) {
					mCustomerOptions.invalidateViews();
				}
				mCustomerOptions.setVisibility(mIsCustomerShown ? View.GONE
						: View.VISIBLE);
				mShowCustomers
						.setImageResource(mIsCustomerShown ? R.drawable.bt_options_show
								: R.drawable.bt_options_hide);
				mIsCustomerShown = !mIsCustomerShown;
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
				confirmPost();
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
				mCustomerOptions.setVisibility(View.GONE);
				return false;
			}
		});
		mSelectedCustomersCount = mCustomers.size() - 1;
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
				if (v.getText() == null || v.getText().toString().equals("")) {
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
		if (mSelectedCustomersCount <= 0) {
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
							.getColumnIndex("customer")), _c.getFloat(_c
							.getColumnIndex("sum_price")), _c.getFloat(_c
							.getColumnIndex("sum_paid")), _c.getString(_c
							.getColumnIndex("paid_time")), _c.getInt(_c
							.getColumnIndex("sum_quantity")), _c.getInt(_c
							.getColumnIndex("post_quantity")), _c.getFloat(_c
							.getColumnIndex("sum_profit")));
			mOrders.add(_temp);
			Cursor _c2 = DBHelper.getDatabase().rawQuery(
					"select name from customer where CKEY = " + _temp.customer,
					null);
			_c2.moveToNext();
			_temp.customerName = _c2.getString(0);
			_c2.close();
		}

		int _j = mOrders.size();
		for (int _i = 0; _i < _j; _i++) {
			_c = DBHelper.getDatabase().rawQuery(
					generateQueryOrderDetails(mOrders.get(_i).OKEY), null);
			List<OrderDetail> _t = new ArrayList<OrderDetail>();
			while (_c.moveToNext()) {
				OrderDetail _temp = new OrderDetail(_c.getInt(_c
						.getColumnIndex("EKEY")), _c.getInt(_c
						.getColumnIndex("order_k")), _c.getInt(_c
						.getColumnIndex("product")), _c.getInt(_c
						.getColumnIndex("quantity")), _c.getString(_c
						.getColumnIndex("post_time")), _c.getFloat(_c
						.getColumnIndex("price")), _c.getFloat(_c
						.getColumnIndex("profit")), _c.getString(_c
						.getColumnIndex("express_no")));
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

	private boolean getPaid(Order o) {
		DBHelper.getDatabase()
				.execSQL(generateQueryPayOrder(o.OKEY, o.sumPaid));
		if (o.sumPaid == o.sumPrice) {
			o.paid = true;
		}
		showOrders();
		return o.paid;
	}

	private void cancelImport(int EKEY) {
		DBHelper.getDatabase().execSQL(generateQueryDeleteOrderDetail(EKEY));
		mPage = 1;
		mSelectedO = -1;
		showOrders();
	}

	private void confirmPost() {
		if (mSelectedO == -1) {
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
		final EditText et = new EditText(getActivity());
		et.setInputType(InputType.TYPE_CLASS_NUMBER);
		et.setHint("输入快递单号");

		new AlertDialog.Builder(getActivity()).setTitle("邮费 ￥" + mShipment)
				.setMessage(_s.toString()).setView(et)
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						List<OrderDetail> _temp = mOrderDetails.get(mSelectedO);
						int _j = _temp.size();
						for (int _i = 0; _i < _j; _i++) {
							if (!_temp.get(_i).performR) {
								continue;
							}
							if (et.getText().equals("")) {
								et.setText("送货");
							}
							String _opt = "'" + et.getText().toString() + "'";
							OrderDetail _t = _temp.get(_i);
							if (_t.quantityR < _t.quantity) {
								DBHelper.getDatabase()
										.execSQL(
												generateQueryPost(_t.EKEY, _opt
														+ " ,quantity = "
														+ _t.quantityR,
														mShipment / _fc));
								DBHelper.getDatabase().execSQL(
										generateQueryOrderDetail(_t,
												_t.quantity - _t.quantityR));
								DBHelper.getDatabase().execSQL(
										"update product set quantity_ex = quantity_ex-"
												+ (_t.quantity - _t.quantityR)
												+ " where product.PKEY = "
												+ _t.product);
								DBHelper.getDatabase()
										.execSQL(
												"update export_order set sum_price = sum_price - "
														+ (_t.quantity - _t.quantityR)
														* _t.price
														+ " , sum_quantity = sum_quantity - "
														+ (_t.quantity - _t.quantityR)
														+ " where export_order.OKEY = "
														+ _t.orderK);
							} else {
								DBHelper.getDatabase().execSQL(
										generateQueryPost(_t.EKEY, _opt,
												mShipment / _fc));
							}
						}
						mShipment = 0;
						mShipmentCost.setText(null);
						mPage = 1;
						mSelectedO = -1;
						showOrders();
						mOrderPanel.invalidateViews();
						Toast.makeText(getActivity(), "发货成功",
								Toast.LENGTH_SHORT).show();
					}
				}).setNegativeButton("否", null).show();
	}

	private String generateQueryPayOrder(int OKEY, float sumPaid) {
		return new StringBuffer(
				"update export_order set paid_time = datetime('now')")
				.append(" ,sum_paid = ").append(sumPaid)
				.append(" where OKEY = ").append(OKEY).toString();
	}

	private String generateQueryOrdersCount() {
		StringBuffer _sql = new StringBuffer(
				"select count(OKEY) from export_order where customer in (");
		int _t = mCustomers.size();
		for (int _i = 0; _i < _t; _i++) {
			Customer _temp = mCustomers.get(_i);
			if (_temp.checked) {
				_sql.append(_temp.CKEY);
				_sql.append(",");
			}
		}
		_sql.deleteCharAt(_sql.length() - 1).append(")");
		return _sql.toString();
	}

	private String generateQueryOrders(String limit) {
		StringBuffer _sql = new StringBuffer(
				"select * from export_order where customer in (");
		int _t = mCustomers.size();
		for (int _i = 0; _i < _t; _i++) {
			Customer _temp = mCustomers.get(_i);
			if (_temp.checked) {
				_sql.append(_temp.CKEY);
				_sql.append(",");
			}
		}
		_sql.deleteCharAt(_sql.length() - 1).append(") order by OKEY ")
				.append(limit);
		return _sql.toString().trim();
	}

	private String generateQueryOrderDetails(int OKEY) {
		return new StringBuffer("select * from export_detail where order_k = ")
				.append(OKEY).append(" order by post_time").toString();
	}

	private String generateQueryPost(int EKEY, String opt, float shipmentC) {
		return new StringBuffer(
				"update export_detail set post_time = date('now'), shipment_cost = "
						+ shipmentC + ",express_no = " + opt + " where EKEY = ")
				.append(EKEY).toString();
	}

	private String generateQueryOrderDetail(OrderDetail od, int quantity) {
		return new StringBuffer(
				"insert into export_detail (order_k,product,quantity,price) values (")
				.append(od.orderK).append(",").append(od.product).append(",")
				.append(quantity).append(",").append(od.price).append(")")
				.toString();
	}

	private String generateQueryDeleteOrderDetail(int EKEY) {
		return new StringBuffer("delete from export_detail where EKEY = ")
				.append(EKEY).toString();
	}

	public class Customer {
		int CKEY;
		String name;
		boolean checked = true;
		CheckedTextView vh;

		public Customer(int CKEY, String name) {
			this.CKEY = CKEY;
			this.name = name;
			if (CKEY == 0) {
				checked = false;
			}
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public class CustomerAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public CustomerAdapter(LayoutInflater inflater) {
			mInflater = inflater;
		}

		@Override
		public int getCount() {
			return mCustomers.size();
		}

		@Override
		public Object getItem(int position) {
			return mCustomers.get(position);
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
						mCustomers.get(position).checked = ((CheckedTextView) v)
								.isChecked();
						((CheckedTextView) v).setTextColor(((CheckedTextView) v)
								.isChecked() ? Color.BLUE : Color.BLACK);
						if (((CheckedTextView) v).isChecked()) {
							if (++mSelectedCustomersCount == mCustomers.size()) {
								mSelectAllCustomers.setChecked(true);
							}
						} else {
							if (--mSelectedCustomersCount < mCustomers.size()) {
								mSelectAllCustomers.setChecked(false);
							}
						}
						mPage = 1;
						mSelectedO = -1;
						showOrders();
					}
				});
				Customer _temp = mCustomers.get(position);
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
		int customer;
		String customerName;
		float sumPrice;
		float sumPaid;
		float sumProfit;
		String payTime;
		int sumQuantity;
		int postQuantity;

		public Order(int OKEY, String orderTime, int customer, float sumPrice,
				float sumPaid, String payTime, int sumQuantity,
				int postQuantity, float sumProfit) {
			this.OKEY = OKEY;
			this.orderTime = orderTime;
			this.customer = customer;
			this.sumPrice = sumPrice;
			this.sumPaid = sumPaid;
			this.sumQuantity = sumQuantity;
			this.postQuantity = postQuantity;
			this.sumProfit = sumProfit;
			if (payTime != null) {
				paid = true;
				this.payTime = payTime.substring(0, 16);
			}
		}
	}

	public class OrderDetail {
		boolean posted = false;
		boolean performR = false;
		int EKEY;
		int orderK;
		int product;
		int quantity;
		int quantityR;
		float price;
		float shipmentPrice;
		float profit;
		String postTime;
		String productName;
		String expressNo;

		public OrderDetail(int EKEY, int orderK, int product, int quantity,
				String postTime, float price, float profit, String expressNo) {
			this.EKEY = EKEY;
			this.orderK = orderK;
			this.product = product;
			this.quantity = quantity;
			this.postTime = postTime;
			this.price = price;
			this.profit = profit;
			quantityR = 0;
			if (postTime != null) {
				posted = true;
				this.expressNo = expressNo;
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
			return mOrderDetails.get(groupPosition).get(childPosition).EKEY;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout._order_export, parent,
					false);
			final Order _temp = mOrders.get(groupPosition);
			TextView _exOrder = (TextView) convertView
					.findViewById(R.id.tv_ex_order);
			TextView _exState = (TextView) convertView
					.findViewById(R.id.tv_ex_state);
			final ToggleButton _exPaid = (ToggleButton) convertView
					.findViewById(R.id.st_ex_pay);
			final TextView _exPayTime = (TextView) convertView
					.findViewById(R.id.tv_ex_paytime);
			TextView _exSumPaid = (TextView) convertView
					.findViewById(R.id.tv_ex_sum_paid);
			TextView _exSumPrice = (TextView) convertView
					.findViewById(R.id.tv_ex_sum_price);
			TextView _exSumProf = (TextView) convertView
					.findViewById(R.id.tv_ex_sum_profit);
			_exOrder.setText(" " + _temp.orderTime + " " + _temp.customerName);
			_exState.setText(" " + _temp.postQuantity + "/" + _temp.sumQuantity);
			_exSumPaid.setText("已收款 ￥ " + _temp.sumPaid);
			_exSumPrice.setText("总货款 ￥ " + _temp.sumPrice);
			_exSumProf.setText("利润 ￥ " + _temp.sumProfit);
			if (_temp.paid) {
				_exPaid.setVisibility(View.GONE);
				_exPayTime.setText("  付款时间 " + _temp.payTime);
				_exPayTime.setVisibility(View.VISIBLE);
			} else {
				_exOrder.setTextColor(Color.BLUE);
				_exPaid.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						buttonView.setChecked(false);
						final EditText et = new EditText(getActivity());
						et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL
								| InputType.TYPE_CLASS_NUMBER);
						et.setText("￥ " + _temp.sumPrice);
						et.setOnEditorActionListener(new OnEditorActionListener() {
							@Override
							public boolean onEditorAction(TextView v,
									int actionId, KeyEvent event) {
								if (v.getText() == null
										|| v.getText().toString().equals("")) {
									v.setText("￥ " + _temp.sumPrice);
									return false;
								}
								float _t = Float.parseFloat(et.getText()
										.toString().replace("￥", ""));
								if (_t <= 0 || _t > _temp.sumPrice) {
									v.setText("￥ " + _temp.sumPrice);
								}
								return false;
							}
						});
						new AlertDialog.Builder(getActivity())
								.setTitle(
										"订单余款 ￥"
												+ (_temp.sumPrice - _temp.sumPaid))
								.setView(et)
								.setPositiveButton("收款",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												float _t = Float.parseFloat(et
														.getText().toString()
														.replace("￥", ""));
												_temp.sumPaid = _t;
												if (getPaid(_temp)) {
													_exPaid.setVisibility(View.GONE);
													_exPayTime.setText("已收款");
													_exPayTime
															.setVisibility(View.VISIBLE);
												}
												Toast.makeText(getActivity(),
														"收款成功",
														Toast.LENGTH_SHORT)
														.show();
											}
										}).setNegativeButton("取消", null).show();
					}
				});
				_exPaid.setFocusable(false);
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
			TextView _postTime = (TextView) convertView
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
			if (_temp.posted) {
				_actionArea.setVisibility(View.GONE);
				_postTime.setVisibility(View.VISIBLE);
				_postTime.setText("数量 " + _temp.quantity + "  收货时间 "
						+ _temp.postTime + " " + _temp.expressNo);
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
								if (v.getText() == null
										|| v.getText().toString().equals("")) {
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
												cancelImport(_temp.EKEY);
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