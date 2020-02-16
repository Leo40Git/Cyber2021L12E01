package com.leo.cyber2021l12e01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.leo.cyber2021l12e01.db.GradeEntry;
import com.leo.cyber2021l12e01.db.HelperDB;
import com.leo.cyber2021l12e01.db.StudentEntry;

import java.util.ArrayList;

/**
 * Displays a screen to add students and grades to the database.<br>
 * Created by Leo40Git on 16/02/2020.
 * @author Leo40Git
 */
public class InputActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

	private Spinner spnTableSelect;
	private ArrayAdapter<String> spnTableSelectAdp;

	private LinearLayout layStudentInput;
	private EditText etStudentName;
	private EditText etStudentAddress;
	private EditText etStudentPhoneHome;
	private EditText etStudentPhoneMobile;
	private EditText etStudentMomName;
	private EditText etStudentMomPhone;
	private EditText etStudentDadName;
	private EditText etStudentDadPhone;
	private Button btnStudentInsert;

	private LinearLayout layGradeInput;
	private Spinner spnGradeStudent;
	private ArrayAdapter<NameIDPair> spnGradeStudentAdp;
	private RadioGroup rgGradeQuarter;
	private EditText etGradeMaths;
	private EditText etGradeEnglish;
	private EditText etGradeCyber;
	private EditText etGradeHistory;
	private Button btnGradeInsert;

	private HelperDB hlp;
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input);

		hlp = new HelperDB(this);

		spnTableSelect = findViewById(R.id.spnTableSelect);

		layStudentInput = findViewById(R.id.layStudentInput);
		etStudentName = findViewById(R.id.etStudentName);
		etStudentAddress = findViewById(R.id.etStudentAddress);
		etStudentPhoneHome = findViewById(R.id.etStudentPhoneHome);
		etStudentPhoneMobile = findViewById(R.id.etStudentPhoneMobile);
		etStudentMomName = findViewById(R.id.etStudentMomName);
		etStudentMomPhone = findViewById(R.id.etStudentMomPhone);
		etStudentDadName = findViewById(R.id.etStudentDadName);
		etStudentDadPhone = findViewById(R.id.etStudentDadPhone);
		btnStudentInsert = findViewById(R.id.btnStudentInsert);

		layGradeInput = findViewById(R.id.layGradeInput);
		spnGradeStudent = findViewById(R.id.spnGradeStudent);
		rgGradeQuarter = findViewById(R.id.rgGradeQuarter);
		etGradeMaths = findViewById(R.id.etGradeMaths);
		etGradeEnglish = findViewById(R.id.etGradeEnglish);
		etGradeCyber = findViewById(R.id.etGradeCyber);
		etGradeHistory = findViewById(R.id.etGradeHistory);
		btnGradeInsert = findViewById(R.id.btnGradeInsert);

		spnTableSelect.setOnItemSelectedListener(this);
		spnTableSelectAdp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, HelperDB.TABLE_NAMES);
		spnTableSelect.setAdapter(spnTableSelectAdp);

		btnStudentInsert.setOnClickListener(this);
		btnGradeInsert.setOnClickListener(this);
	}

	/**
	 * Called when an item in {@link #spnTableSelect} is selected.<br>
	 * Changes the displayed input form to the one for the selected table.
	 * @param adapterView {@link #spnTableSelect}
	 * @param view selected item's {@link View}
	 * @param pos selected item's position
	 * @param id selected item's ID
	 */
	@Override
	public void onItemSelected(final AdapterView<?> adapterView, final View view, final int pos, final long id) {
		if (spnTableSelect.equals(adapterView)) {
			String tbl = spnTableSelectAdp.getItem(pos);
			if (StudentEntry.TABLE_NAME.equals(tbl)) {
				layGradeInput.setVisibility(View.GONE);
				layStudentInput.setVisibility(View.VISIBLE);
			} else if (GradeEntry.TABLE_NAME.equals(tbl)) {
				ArrayAdapter<NameIDPair> adp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
				setViewEnabledRecursive(layGradeInput, populateStudentsAdapter(hlp, adp));
				spnGradeStudentAdp = adp;
				spnGradeStudent.setAdapter(adp);
				layStudentInput.setVisibility(View.GONE);
				layGradeInput.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * Gets the contents of an {@link EditText}, clearing it in the process. If it is already empty, displays a {@link Toast} about how it should not be empty.
	 * @param ctx {@link Context} of field
	 * @param et {@link EditText} to act on
	 * @return contents of the {@link EditText}, or <code>null</code> if it was empty.
	 */
	public static String getEditTextContents(final Context ctx, final EditText et) {
		String c = et.getText().toString().trim();
		if (c.isEmpty()) {
			Toast.makeText(ctx, et.getHint() + " cannot be empty!", Toast.LENGTH_LONG).show();
			return null;
		}
		et.setText("");
		return c;
	}

	/**
	 * Parses the contents of an {@link EditText} as an <code>int</code>, clearing it in the process. If it is already empty or doesn't contain a number, displays a {@link Toast} about the issue.
	 * @param ctx {@link Context} of field
	 * @param et {@link EditText} to act on
	 * @return the parsed value of the field, or an empty {@link OptionalInt} if the field was empty or didn't contain a number.
	 */
	@NonNull
	public static OptionalInt parseEditText(final Context ctx, final EditText et) {
		String c = et.getText().toString().trim();
		if (c.isEmpty()) {
			Toast.makeText(ctx, et.getHint() + " cannot be empty!", Toast.LENGTH_LONG).show();
			return OptionalInt.empty();
		}
		int x;
		try {
			x = Integer.parseInt(c);
		} catch (NumberFormatException e) {
			Toast.makeText(ctx, et.getHint() + " does not contain a number!", Toast.LENGTH_LONG).show();
			return OptionalInt.empty();
		}
		et.setText("");
		return OptionalInt.of(x);
	}

	/**
	 * Called when {@link #btnStudentInsert} or {@link #btnGradeInsert} are clicked on.<br>
	 * Inserts a new student or grade entry into the database, based on which button was clicked.
	 * @param view {@link #btnStudentInsert} or {@link #btnGradeInsert}
	 */
	@Override
	public void onClick(final View view) {
		if (btnStudentInsert.equals(view)) {
			String name = getEditTextContents(this, etStudentName);
			if (name == null)
				return;
			String address = getEditTextContents(this, etStudentAddress);
			if (address == null)
				return;
			String phoneHome = getEditTextContents(this, etStudentPhoneHome);
			if (phoneHome == null)
				return;
			String phoneMobile = getEditTextContents(this, etStudentPhoneMobile);
			if (phoneMobile == null)
				return;
			String momName = getEditTextContents(this, etStudentMomName);
			if (momName == null)
				return;
			String momPhone = getEditTextContents(this, etStudentMomPhone);
			if (momPhone == null)
				return;
			String dadName = getEditTextContents(this, etStudentDadName);
			if (dadName == null)
				return;
			String dadPhone = getEditTextContents(this, etStudentDadPhone);
			if (dadPhone == null)
				return;
			ContentValues cv = new ContentValues();
			cv.put(StudentEntry.NAME, name);
			cv.put(StudentEntry.ADDRESS, address);
			cv.put(StudentEntry.PHONE_HOME, phoneHome);
			cv.put(StudentEntry.PHONE_MOBILE, phoneMobile);
			cv.put(StudentEntry.MOM_NAME, momName);
			cv.put(StudentEntry.MOM_PHONE, momPhone);
			cv.put(StudentEntry.DAD_NAME, dadName);
			cv.put(StudentEntry.DAD_PHONE, dadPhone);
			db = hlp.getWritableDatabase();
			db.insert(StudentEntry.TABLE_NAME, null, cv);
			db.close();
			spnTableSelect.requestFocus();
			Toast.makeText(this, "Student inserted successfully!", Toast.LENGTH_LONG).show();
		} else if (btnGradeInsert.equals(view)) {
			NameIDPair selStudent = spnGradeStudentAdp.getItem(spnGradeStudent.getSelectedItemPosition());
			if (selStudent == null) {
				Toast.makeText(this, "Please select a student!", Toast.LENGTH_LONG).show();
				return;
			}
			int studentID = selStudent.getId();
			int quarter;
			switch (rgGradeQuarter.getCheckedRadioButtonId()) {
			case R.id.rbGradeQuarter1:
				quarter = 0;
				break;
			case R.id.rbGradeQuarter2:
				quarter = 1;
				break;
			case R.id.rbGradeQuarter3:
				quarter = 2;
				break;
			case R.id.rbGradeQuarter4:
				quarter = 3;
				break;
			default:
				Toast.makeText(this, "Please select a quarter!", Toast.LENGTH_LONG).show();
				return;
			}
			OptionalInt gradeMathsO = parseEditText(this, etGradeMaths);
			if (!gradeMathsO.isPresent())
				return;
			int gradeMaths = gradeMathsO.getAsInt();
			if (gradeMaths < 0 || gradeMaths > 100) {
				Toast.makeText(this, etGradeMaths.getHint() + " must be between 0 and 100!", Toast.LENGTH_LONG).show();
			}
			OptionalInt gradeEnglishO = parseEditText(this, etGradeEnglish);
			if (!gradeEnglishO.isPresent())
				return;
			int gradeEnglish = gradeEnglishO.getAsInt();
			if (gradeEnglish < 0 || gradeEnglish > 100) {
				Toast.makeText(this, etGradeEnglish.getHint() + " must be between 0 and 100!", Toast.LENGTH_LONG).show();
			}
			OptionalInt gradeCyberO = parseEditText(this, etGradeCyber);
			if (!gradeCyberO.isPresent())
				return;
			int gradeCyber = gradeCyberO.getAsInt();
			if (gradeCyber < 0 || gradeCyber > 100) {
				Toast.makeText(this, etGradeCyber.getHint() + " must be between 0 and 100!", Toast.LENGTH_LONG).show();
			}
			OptionalInt gradeHistoryO = parseEditText(this, etGradeHistory);
			if (!gradeHistoryO.isPresent())
				return;
			int gradeHistory = gradeHistoryO.getAsInt();
			if (gradeHistory < 0 || gradeHistory > 100) {
				Toast.makeText(this, etGradeHistory.getHint() + " must be between 0 and 100!", Toast.LENGTH_LONG).show();
			}
			ContentValues cv = new ContentValues();
			cv.put(GradeEntry.STUDENT_ID, studentID);
			cv.put(GradeEntry.QUARTER, quarter);
			cv.put(GradeEntry.GRADE_MATHS, gradeMaths);
			cv.put(GradeEntry.GRADE_ENGLISH, gradeEnglish);
			cv.put(GradeEntry.GRADE_CYBER, gradeCyber);
			cv.put(GradeEntry.GRADE_HISTORY, gradeHistory);
			db = hlp.getWritableDatabase();
			db.insert(GradeEntry.TABLE_NAME, null, cv);
			db.close();
			spnTableSelect.requestFocus();
			Toast.makeText(this, "Grade inserted successfully!", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Gets all students in the database.
	 * @param hlp {@link HelperDB} to use to read from the database
	 * @return list of students
	 */
	public static ArrayList<NameIDPair> getStudents(final HelperDB hlp) {
		final ArrayList<NameIDPair> list = new ArrayList<>();
		final SQLiteDatabase db = hlp.getReadableDatabase();
		final Cursor crsr = db.query(StudentEntry.TABLE_NAME, new String[] { StudentEntry._ID, StudentEntry.NAME }, null, null, null, null, null);
		final int colId = crsr.getColumnIndex(StudentEntry._ID);
		final int colName = crsr.getColumnIndex(StudentEntry.NAME);
		crsr.moveToFirst();
		while (!crsr.isAfterLast()) {
			int id = crsr.getInt(colId);
			String name = crsr.getString(colName);
			list.add(new NameIDPair(name, id));
			crsr.moveToNext();
		}
		crsr.close();
		db.close();
		return list;
	}

	/**
	 * Populates an {@link ArrayAdapter} with all the students that are currently in the database.
	 * @param hlp {@link HelperDB} to use to read from the database
	 * @param adp {@link ArrayAdapter} to populate
	 * @return <code>false</code> if there are no students in the database, <code>true</code> otherwise.
	 */
	public static boolean populateStudentsAdapter(final HelperDB hlp, final ArrayAdapter<NameIDPair> adp) {
		adp.clear();
		final ArrayList<NameIDPair> students = getStudents(hlp);
		if (students.isEmpty()) {
			adp.add(new NameIDPair("(no students)", -1));
			return false;
		} else {
			adp.addAll(students);
			return true;
		}
	}

	/**
	 * Recursively sets all views in a {@link ViewGroup}'s enabled state to the specified state.
	 * @param vg {@link ViewGroup} to set enabled state in
	 * @param state new enabled state
	 */
	public static void setViewEnabledRecursive(final ViewGroup vg, final boolean state) {
		vg.setEnabled(state);
		final int c = vg.getChildCount();
		for (int i = 0; i < c; i++) {
			View v = vg.getChildAt(i);
			if (v instanceof ViewGroup)
				setViewEnabledRecursive((ViewGroup) v, state);
			else
				v.setEnabled(state);
		}
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
		case R.id.menuView:
			gi = new Intent(this, ViewActivity.class);
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
