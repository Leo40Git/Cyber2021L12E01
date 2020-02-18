package com.leo.cyber2021l12e01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.core.text.TextUtilsCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.leo.androidutils.NameIDPair;
import com.leo.cyber2021l12e01.db.GradeEntry;
import com.leo.cyber2021l12e01.db.HelperDB;
import com.leo.cyber2021l12e01.db.StudentEntry;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Displays the information currently stored in the database.<br>
 * Created by Leo40Git on 16/02/2020.
 * @author Leo40Git
 */
public class ViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

	public static final String[] ORDINAL_NUMBERS = { "1st", "2nd", "3rd", "4th" };

	private Spinner spnTableSelect;
	private ArrayAdapter<String> spnTableSelectAdp;

	private ListView lvDisplay;
	private ArrayAdapter<NameIDPair> lvDisplayAdp;

	private TextView tvEmpty;

	private HelperDB hlp;
	private SQLiteDatabase db;
	private Cursor crsr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);

		hlp = new HelperDB(this);

		spnTableSelect = findViewById(R.id.spnTableSelect);
		lvDisplay = findViewById(R.id.lvDisplay);
		tvEmpty = findViewById(R.id.tvEmpty);

		lvDisplay.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvDisplay.setOnItemClickListener(this);

		spnTableSelect.setOnItemSelectedListener(this);
		spnTableSelectAdp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, HelperDB.TABLE_NAMES);
		spnTableSelect.setAdapter(spnTableSelectAdp);
	}

	/**
	 * Called when an item in {@link #spnTableSelect} is selected.<br>
	 * Displays the information in the selected table.
	 * @param adapterView {@link #spnTableSelect}
	 * @param view selected item's {@link View}
	 * @param pos selected item's position
	 * @param id selected item's ID
	 */
	@Override
	public void onItemSelected(final AdapterView<?> adapterView, final View view, final int pos, final long id) {
		if (spnTableSelect.equals(adapterView)) {
			String tbl = spnTableSelectAdp.getItem(pos);
			if (StudentEntry.TABLE_NAME.equals(tbl))
				displayStudents();
			else if (GradeEntry.TABLE_NAME.equals(tbl))
				displayGrades();
		}
	}

	/**
	 * Called when an item in {@link #lvDisplay} is clicked on.<br>
	 * Shows more information about the selected item.
	 * @param adapterView {@link #lvDisplay}
	 * @param view selected item's {@link View}
	 * @param pos selected item's position
	 * @param id selected item's ID
	 */
	@Override
	public void onItemClick(final AdapterView<?> adapterView, final View view, final int pos, final long id) {
		if (lvDisplay.equals(adapterView)) {
			String tbl = spnTableSelectAdp.getItem(spnTableSelect.getSelectedItemPosition());
			NameIDPair pair = lvDisplayAdp.getItem(pos);
			if (pair == null)
				return;
			if (StudentEntry.TABLE_NAME.equals(tbl))
				displayStudentDetails(pair);
			else if (GradeEntry.TABLE_NAME.equals(tbl))
				displayGradeDetails(pair);
		}
	}

	/**
	 * Display list of students.
	 */
	private void displayStudents() {
		final ArrayList<NameIDPair> students = InputActivity.getStudents(hlp);
		if (students.isEmpty()) {
			lvDisplay.setVisibility(View.GONE);
			tvEmpty.setVisibility(View.VISIBLE);
		} else {
			lvDisplayAdp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, students);
			lvDisplay.setAdapter(lvDisplayAdp);
			tvEmpty.setVisibility(View.GONE);
			lvDisplay.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Gets all students in the database.
	 * @param hlp {@link HelperDB} to use to read from the database
	 * @return array of students
	 */
	public static SparseArray<String> getStudentArray(final HelperDB hlp) {
		final SparseArray<String> studentNames = new SparseArray<>();
		SQLiteDatabase db = hlp.getReadableDatabase();
		Cursor crsr = db.query(StudentEntry.TABLE_NAME, new String[] { StudentEntry._ID, StudentEntry.NAME }, null, null, null, null, null);
		final int colId = crsr.getColumnIndex(StudentEntry._ID);
		final int colName = crsr.getColumnIndex(StudentEntry.NAME);
		crsr.moveToFirst();
		while (!crsr.isAfterLast()) {
			int id = crsr.getInt(colId);
			String name = crsr.getString(colName);
			studentNames.put(id, name);
			crsr.moveToNext();
		}
		crsr.close();
		db.close();
		return studentNames;
	}

	/**
	 * Display list of grades.
	 */
	private void displayGrades() {
		final SparseArray<String> studentNames = getStudentArray(hlp);

		final ArrayList<NameIDPair> grades = new ArrayList<>();
		db = hlp.getReadableDatabase();
		crsr = db.query(GradeEntry.TABLE_NAME, new String[] { GradeEntry._ID, GradeEntry.STUDENT_ID, GradeEntry.QUARTER }, null, null, null, null, null);
		final int colId = crsr.getColumnIndex(GradeEntry._ID);
		final int colStudentId = crsr.getColumnIndex(GradeEntry.STUDENT_ID);
		final int colQuarter = crsr.getColumnIndex(GradeEntry.QUARTER);
		crsr.moveToFirst();
		while (!crsr.isAfterLast()) {
			int id = crsr.getInt(colId);
			int studentId = crsr.getInt(colStudentId);
			int quarter = crsr.getInt(colQuarter);
			grades.add(new NameIDPair(String.format(
					"%s, %s Quarter",
					studentNames.get(studentId, "(unknown student)"),
					ORDINAL_NUMBERS[quarter]),
					id));
			crsr.moveToNext();
		}
		crsr.close();

		db.close();

		if (grades.isEmpty()) {
			lvDisplay.setVisibility(View.GONE);
			tvEmpty.setVisibility(View.VISIBLE);
		} else {
			lvDisplayAdp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, grades);
			lvDisplay.setAdapter(lvDisplayAdp);
			tvEmpty.setVisibility(View.GONE);
			lvDisplay.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Displays a yes/no {@link AlertDialog}. If "yes" is selected, runs the specified {@link Runnable}.
	 * @param ctx {@link Context} of dialog
	 * @param prompt prompt to display in dialog
	 * @param r {@link Runnable} to run if "yes" is selected
	 */
	public static void confirmActionDialog(final Context ctx, final CharSequence prompt, final Runnable r) {
		AlertDialog.Builder adb = new AlertDialog.Builder(ctx);
		adb.setTitle("Confirm action");
		adb.setMessage(prompt);
		adb.setNeutralButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		adb.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				r.run();
				dialog.dismiss();
			}
		});
		adb.show();
	}

	/**
	 * Shows an {@link AlertDialog} containing details about the specified student.
	 * @param student student to show details about
	 */
	private void displayStudentDetails(final NameIDPair student) {
		db = hlp.getReadableDatabase();
		crsr = db.query(StudentEntry.TABLE_NAME, null, StudentEntry._ID + "=?", new String[] { Integer.toString(student.getId()) }, null, null, null);
		crsr.moveToFirst();
		final String name = crsr.getString(crsr.getColumnIndex(StudentEntry.NAME));
		final String address = crsr.getString(crsr.getColumnIndex(StudentEntry.ADDRESS));
		final String phoneHome = crsr.getString(crsr.getColumnIndex(StudentEntry.PHONE_HOME));
		final String phoneMobile = crsr.getString(crsr.getColumnIndex(StudentEntry.PHONE_MOBILE));
		final String momName = crsr.getString(crsr.getColumnIndex(StudentEntry.MOM_NAME));
		final String momPhone = crsr.getString(crsr.getColumnIndex(StudentEntry.MOM_PHONE));
		final String dadName = crsr.getString(crsr.getColumnIndex(StudentEntry.DAD_NAME));
		final String dadPhone = crsr.getString(crsr.getColumnIndex(StudentEntry.DAD_PHONE));
		crsr.close();
		db.close();

		String source = String.format(Locale.getDefault(),
				"<b>Name:</b> %s<br>"
				+ "<b>Address:</b> %s<br>"
				+ "<b>Phone:</b> %s (home) / %s (mobile)<br>"
				+ "<b>Mother:</b> %s, %s<br>"
				+ "<b>Father:</b> %s, %s",
				TextUtilsCompat.htmlEncode(name),
				TextUtilsCompat.htmlEncode(address),
				TextUtilsCompat.htmlEncode(phoneHome),
				TextUtilsCompat.htmlEncode(phoneMobile),
				TextUtilsCompat.htmlEncode(momName),
				TextUtilsCompat.htmlEncode(momPhone),
				TextUtilsCompat.htmlEncode(dadName),
				TextUtilsCompat.htmlEncode(dadPhone)
		);

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Student details");
		adb.setMessage(HtmlCompat.fromHtml(source, HtmlCompat.FROM_HTML_MODE_COMPACT));
		adb.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		adb.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				confirmActionDialog(ViewActivity.this, "Are you sure you want to remove this student?", new Runnable() {
					@Override
					public void run() {
						db = hlp.getWritableDatabase();
						db.delete(StudentEntry.TABLE_NAME, StudentEntry._ID + "=?", new String[] { Integer.toString(student.getId()) });
						displayStudents();
					}
				});
				dialog.dismiss();
			}
		});
		adb.show();
	}

	/**
	 * Shows an {@link AlertDialog} containing details about the specified grade.
	 * @param grade grade to show details about
	 */
	private void displayGradeDetails(final NameIDPair grade) {
		db = hlp.getReadableDatabase();
		crsr = db.query(GradeEntry.TABLE_NAME, null, GradeEntry._ID + "=?", new String[] { Integer.toString(grade.getId()) }, null, null, null);
		crsr.moveToFirst();
		final int studentID = crsr.getInt(crsr.getColumnIndex(GradeEntry.STUDENT_ID));
		final int quarter = crsr.getInt(crsr.getColumnIndex(GradeEntry.QUARTER));
		final int subject = crsr.getInt(crsr.getColumnIndex(GradeEntry.SUBJECT));
		final int value = crsr.getInt(crsr.getColumnIndex(GradeEntry.VALUE));
		crsr.close();
		crsr = db.query(StudentEntry.TABLE_NAME, new String[] { StudentEntry.NAME }, StudentEntry._ID + "=?", new String[] { Integer.toString(studentID) }, null, null, null);
		crsr.moveToFirst();
		final String studentName;
		if (crsr.isAfterLast())
			studentName = "(unknown)";
		else
			studentName = crsr.getString(crsr.getColumnIndex(StudentEntry.NAME));
		crsr.close();
		db.close();

		String source = String.format(Locale.getDefault(),
				"<b>Student:</b> %s<br>"
				+ "<b>Quarter:</b> %s<br>"
				+ "<b>Subject:</b> %s<br>"
				+ "<b>Value:</b> %d",
				TextUtilsCompat.htmlEncode(studentName),
				ORDINAL_NUMBERS[quarter],
				InputActivity.SUBJECTS[subject],
				value
		);

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Grade details");
		adb.setMessage(HtmlCompat.fromHtml(source, HtmlCompat.FROM_HTML_MODE_COMPACT));
		adb.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		adb.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				confirmActionDialog(ViewActivity.this, "Are you sure you want to remove this grade?", new Runnable() {
					@Override
					public void run() {
						db = hlp.getWritableDatabase();
						db.delete(GradeEntry.TABLE_NAME, GradeEntry._ID + "=?", new String[] { Integer.toString(grade.getId()) });
						displayGrades();
					}
				});
				dialog.dismiss();
			}
		});
		adb.show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {

	}

	/**
	 * Creates the activity's options menu.<br>
	 * Inflates the "main" menu.
	 * @param menu the activity's menu
	 * @return <code>true</code>
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Called when an option in the activity's options menu is selected.<br>
	 * {@linkplain #startActivity(Intent) Starts the selected activity.}
	 * @param item selected menu's option.
	 * @return <code>true</code>
	 */
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		Intent gi = null;
		switch (item.getItemId()) {
		case R.id.menuInput:
			gi = new Intent(this, InputActivity.class);
			break;
		case R.id.menuSortFilter:
			// gi = new Intent(this, SortFilterActivity.class);
			break;
		case R.id.menuCredits:
			gi = new Intent(this, CreditsActivity.class);
			break;
		default:
			break;
		}
		if (gi != null)
			startActivity(gi);
		return true;
	}

}
