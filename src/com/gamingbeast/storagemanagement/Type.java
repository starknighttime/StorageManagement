package com.gamingbeast.storagemanagement;

import android.widget.CheckedTextView;


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
