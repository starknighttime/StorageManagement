package com.gamingbeast.storagemanagement;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class BriefFragment extends Fragment {
	private ListView mMessagesPanel;
	private ArrayList<Message> mMessages = new ArrayList<Message>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initMessages();
		View _contentView = inflater.inflate(R.layout.fragment_brief,
				container, false);
		mMessagesPanel = (ListView) _contentView.findViewById(R.id.lv_messages);
		mMessagesPanel.setAdapter(new MessageAdapter(inflater));
		mMessagesPanel.invalidateViews();
		return _contentView;
	}

	private void initMessages() {
		mMessages.clear();
		Cursor _c = DBHelper
				.getDatabase()
				.rawQuery(
						"select * from product where quantity_c<0 order by brand",
						null);
		while (_c.moveToNext()) {
			Cursor _c2 = DBHelper
					.getDatabase()
					.rawQuery(
							"select * from brand where BKEY = ?",
							new String[] { _c.getInt(_c.getColumnIndex("brand"))
									+ "" });
			_c2.moveToNext();
			String _title = _c2.getString(_c2.getColumnIndex("ename"))
					+ " "
					+ (_c2.getString(_c2.getColumnIndex("name")) == null ? ""
							: _c2.getString(_c2.getColumnIndex("name")) + " ")
					+ _c.getString(_c.getColumnIndex("name"))
					+ (_c.getString(_c.getColumnIndex("capacity")) == null ? ""
							: (" " + _c
									.getString(_c.getColumnIndex("capacity"))));
			mMessages.add(new Message(_title,
					Constants.MESSAGE_TYPE_INSUFFICIENT_STORAGE));
		}
		_c = DBHelper
				.getDatabase()
				.rawQuery(
						"select * from import_order where receive_quantity < sum_quantity or pay_time is null",
						null);
		while (_c.moveToNext()) {
			Cursor _c2 = DBHelper.getDatabase()
					.rawQuery(
							"select name from source where SKEY = ?",
							new String[] { _c.getInt(_c
									.getColumnIndex("source")) + "" });
			_c2.moveToNext();
			String _title = "进货订单: "
					+ _c.getString(_c.getColumnIndex("order_time")).substring(
							0, 10) + " " + _c2.getString(0);
			StringBuffer _t = new StringBuffer();
			if (_c.isNull(_c.getColumnIndex("pay_time"))) {
				_t.append(Constants.MESSAGE_TYPE_NOT_PAID).append(" ");
			}
			if (_c.getInt(_c.getColumnIndex("receive_quantity")) == 0) {
				_t.append(Constants.MESSAGE_TYPE_NOT_RECEIVED);
			} else if (_c.getInt(_c.getColumnIndex("receive_quantity")) < _c
					.getInt(_c.getColumnIndex("sum_quantity"))) {
				_t.append(Constants.MESSAGE_TYPE_NOT_RECEIVED_ALL);
			}
			mMessages.add(new Message(_title, _t.toString().trim()));
		}
		_c = DBHelper
				.getDatabase()
				.rawQuery(
						"select * from export_order where post_quantity < sum_quantity or sum_paid < sum_price",
						null);
		while (_c.moveToNext()) {
			Cursor _c2 = DBHelper.getDatabase().rawQuery(
					"select name from customer where CKEY = ?",
					new String[] { _c.getInt(_c.getColumnIndex("customer"))
							+ "" });
			_c2.moveToNext();
			String _title = "出货订单: "
					+ _c.getString(_c.getColumnIndex("order_time")).substring(
							0, 10) + " " + _c2.getString(0);
			StringBuffer _t = new StringBuffer();
			if (_c.getFloat(_c.getColumnIndex("sum_paid")) == 0) {
				_t.append(Constants.MESSAGE_TYPE_NOT_CHECK).append(" ");
			} else if (_c.getFloat(_c.getColumnIndex("sum_paid")) < _c
					.getFloat(_c.getColumnIndex("sum_price"))) {
				_t.append(Constants.MESSAGE_TYPE_NOT_CHECK_ALL).append(" ");
			}
			if (_c.getInt(_c.getColumnIndex("post_quantity")) == 0) {
				_t.append(Constants.MESSAGE_TYPE_NOT_POST);
			} else if (_c.getInt(_c.getColumnIndex("post_quantity")) < _c
					.getInt(_c.getColumnIndex("sum_quantity"))) {
				_t.append(Constants.MESSAGE_TYPE_NOT_POST_ALL);
			}
			mMessages.add(new Message(_title, _t.toString().trim()));
		}
		_c.close();
		if (mMessages.size() == 0) {
			mMessages.add(new Message("没有新的消息", null));
		}
	}

	public class Message {
		String title;
		String content;

		public Message(String title, String content) {
			this.title = title;
			this.content = content;
		}
	}

	private class MessageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MessageAdapter(LayoutInflater inflater) {
			mInflater = inflater;
		}

		@Override
		public int getCount() {
			return mMessages.size();
		}

		@Override
		public Object getItem(int position) {
			return mMessages.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			convertView = mInflater.inflate(R.layout._message, parent, false);
			TextView _title = (TextView) convertView
					.findViewById(R.id.tv_message_title);
			TextView _content = (TextView) convertView
					.findViewById(R.id.tv_message_content);
			Message _temp = mMessages.get(position);
			_title.setText(_temp.title);
			if (_temp.content == null) {
				_title.setTextColor(Color.LTGRAY);
			} else {
				_content.setText(_temp.content);
			}
			return convertView;
		}
	}
}