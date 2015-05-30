package com.gamingbeast.storagemanagement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

public class DBHelper {

	private static SQLiteDatabase mDB;

	public static void copyDataBase(int where, Activity main) {

		String _dbPath = null;
		if (where == 1) { // sdcard
			_dbPath = Environment.getExternalStorageDirectory()
					+ File.separator + "data" + File.separator
					+ Constants.DB_NAME;
		} else { // local
			_dbPath = Constants.PACKAGE_NAME + Constants.DB_NAME;
		}

		if (where == 2) {
			new File(Constants.PACKAGE_NAME).mkdirs();
		}

		if (where == 1
				&& !Environment.MEDIA_MOUNTED.equals(Environment
						.getExternalStorageState())) {
			return; // 未挂载外部存储，拷贝到内部不用判断
		}

		File dbFile = new File(_dbPath);
		if (dbFile.exists()) {
			dbFile.delete();
		}
		try {
			dbFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		try {
			InputStream is = main.getResources().getAssets()
					.open(Constants.DB_NAME);
			OutputStream os = new FileOutputStream(_dbPath);

			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}

			os.flush();
			os.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Toast.makeText(main, "拷贝成功", Toast.LENGTH_LONG).show();
	}
	
	public static void intiDatabase(Activity main){
			String dbPath = Environment.getExternalStorageDirectory()
					+ File.separator + "data" + File.separator
					+ Constants.DB_NAME;

			File dbFile = new File(dbPath);
			if (!dbFile.exists()) {
				Toast.makeText(main, "请先点击拷贝到SDCard", Toast.LENGTH_LONG).show();
				return;
			}
			main.openOrCreateDatabase(dbPath,
					SQLiteDatabase.CREATE_IF_NECESSARY, null);
			mDB = SQLiteDatabase.openDatabase(dbPath, null,
					SQLiteDatabase.OPEN_READWRITE);	
	}
	
	public static SQLiteDatabase getDatabase() {	
		return mDB;
	}

}