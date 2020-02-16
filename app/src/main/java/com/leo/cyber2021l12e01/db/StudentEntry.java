package com.leo.cyber2021l12e01.db;

import android.provider.BaseColumns;

/**
 * Defines fields for the "Students" table.<br>
 * Created by Leo40Git on 16/02/2020.
 * @author Leo40Git
 */
public class StudentEntry implements BaseColumns {
	/**
	 * Name of the "Students" table.
	 */
	public static final String TABLE_NAME = "Students";
	/**
	 * The name of the student, TEXT.
	 */
	public static final String NAME = "Name";
	/**
	 * The address of the student, TEXT.
	 */
	public static final String ADDRESS = "Address";
	/**
	 * The phone number of the student's home, TEXT.
	 */
	public static final String PHONE_HOME = "PhoneHome";
	/**
	 * The student's mobile phone number, TEXT.
	 */
	public static final String PHONE_MOBILE = "PhoneMobile";
	/**
	 * The name of the student's mother, TEXT.
	 */
	public static final String MOM_NAME = "MomName";
	/**
	 * The student's mother's phone number, TEXT.
	 */
	public static final String MOM_PHONE = "MomPhone";
	/**
	 * The name of the student's father, TEXT.
	 */
	public static final String DAD_NAME = "DadName";
	/**
	 * The student's father's phone number, TEXT.
	 */
	public static final String DAD_PHONE = "DadPhone";
}
