package com.gamingbeast.storagemanagement;

import android.annotation.SuppressLint;

public class Constants {
	public static final int ACTION_TAB_MAIN = 0;
	public static final int ACTION_TAB_IMPORT = 1;
	public static final int ACTION_TAB_EXPORT = 2;
	public static final int ACTION_TAB_RECEIVE = 3;
	public static final int ACTION_TAB_POST = 4;
	public static final int SELECTOR_MODE_STORAGE = 0;
	public static final int SELECTOR_MODE_IMPORT = 1;
	public static final int SELECTOR_MODE_EXPORT = 2;
	@SuppressLint("SdCardPath")
	public static final String PACKAGE_NAME = "/data/data/com.gamingbeast.storagemanagement/databases/";
	public static final String DB_NAME = "storage_management.db";
	public static final String MESSAGE_TYPE_INSUFFICIENT_STORAGE = "库存不足";
	public static final String MESSAGE_TYPE_NOT_PAID = "未付款";
	public static final String MESSAGE_TYPE_NOT_RECEIVED = "未到货";
	public static final String MESSAGE_TYPE_NOT_RECEIVED_ALL = "未全部到货";
	public static final String MESSAGE_TYPE_NOT_CHECK = "未收款";
	public static final String MESSAGE_TYPE_NOT_CHECK_ALL = "未收全款";
	public static final String MESSAGE_TYPE_NOT_POST = "未发货";
	public static final String MESSAGE_TYPE_NOT_POST_ALL = "未全部发货";
}