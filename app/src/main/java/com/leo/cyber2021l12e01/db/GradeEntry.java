package com.leo.cyber2021l12e01.db;

import android.provider.BaseColumns;

/**
 * Defines fields for the "Grades" table.<br>
 * Created by Leo40Git on 16/02/2020.
 * @author Leo40Git
 */
public class GradeEntry implements BaseColumns {
	/**
	 * Name of the "Grades" table.
	 */
	public static final String TABLE_NAME = "Grades";
	/**
	 * The ID of the student, INTEGER.
	 */
	public static final String STUDENT_ID = "StudentID";
	/**
	 * The quarter these grades belong to, INTEGER.
	 */
	public static final String QUARTER = "Quarter";
	/**
	 * The subject this grade is in, INTEGER.
	 */
	public static final String SUBJECT = "Subject";
	/**
	 * The grade value, INTEGER.
	 */
	public static final String VALUE = "Value";
}
