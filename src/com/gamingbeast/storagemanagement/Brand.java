package com.gamingbeast.storagemanagement;

import android.widget.CheckedTextView;

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
