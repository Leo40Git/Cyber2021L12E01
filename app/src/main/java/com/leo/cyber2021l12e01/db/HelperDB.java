package com.leo.cyber2021l12e01.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Helper class for managing our SQLite database.<br>
 * Created by Leo40Git on 16/02/2020.
 */
public class HelperDB extends SQLiteOpenHelper {
	/**
	 * Array of table names. Used for displaying a table selection spinner.
	 */
	public static final String[] TABLE_NAMES = { StudentEntry.TABLE_NAME, GradeEntry.TABLE_NAME };

	private static final String DB_NAME = "teachapp.db";
	private static final int DB_VERSION = 1000;

	private String sqlCreate, sqlDelete;

	public HelperDB(@Nullable Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		sqlCreate = "CREATE TABLE " + StudentEntry.TABLE_NAME
			+ " (" + StudentEntry._ID + " INTEGER PRIMARY KEY,"
			+ " " + StudentEntry.NAME + " TEXT,"
			+ " " + StudentEntry.ADDRESS + " TEXT,"
			+ " " + StudentEntry.PHONE_HOME + " TEXT,"
			+ " " + StudentEntry.PHONE_MOBILE + " TEXT,"
			+ " " + StudentEntry.MOM_NAME + " TEXT,"
			+ " " + StudentEntry.MOM_PHONE + " TEXT,"
			+ " " + StudentEntry.DAD_NAME + " TEXT,"
			+ " " + StudentEntry.DAD_PHONE + " TEXT"
			+ ")";
		db.execSQL(sqlCreate);
		sqlCreate = "CREATE TABLE " + GradeEntry.TABLE_NAME
				+ " (" + GradeEntry._ID + " INTEGER PRIMARY KEY,"
				+ " " + GradeEntry.STUDENT_ID + " INTEGER,"
				+ " " + GradeEntry.QUARTER + " INTEGER,"
				+ " " + GradeEntry.GRADE_MATHS + " INTEGER,"
				+ " " + GradeEntry.GRADE_ENGLISH + " INTEGER,"
				+ " " + GradeEntry.GRADE_CYBER + " INTEGER,"
				+ " " + GradeEntry.GRADE_HISTORY + " INTEGER"
				+ ")";
		db.execSQL(sqlCreate);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		sqlDelete = "DROP TABLE IF EXISTS " + StudentEntry.TABLE_NAME;
		db.execSQL(sqlDelete);
		sqlDelete = "DROP TABLE IF EXISTS " + GradeEntry.TABLE_NAME;
		db.execSQL(sqlDelete);
	}
}
