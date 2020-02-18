package com.leo.cyber2021l12e01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.leo.androidutils.NameIDPair;
import com.leo.cyber2021l12e01.db.GradeEntry;
import com.leo.cyber2021l12e01.db.HelperDB;

import java.util.Locale;

/**
 * Displays information from the database, sorted and filtered to the user's specifications.<br>
 * Created by Leo40Git on 18/02/2020.
 * @author Leo40Git
 */
public class SortFilterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

	/**
	 * Represents a filter that filters through the database's contents.<br>
	 * Created by Leo40Git on 18/02/2020.
	 * @author Leo40Git
	 */
	public static abstract class DBFilter {
		/**
		 * Result of {@link #prepare(HelperDB, ArrayAdapter)}.
		 */
		public enum PrepareResult {
			/**
			 * Filter is ready to be {@linkplain #execute(HelperDB, ArrayAdapter, NameIDPair, boolean) executed}.
			 */
			READY,
			/**
			 * Filter has no parameter, but is ready to be {@linkplain #execute(HelperDB, ArrayAdapter, NameIDPair, boolean) executed}.
			 */
			NO_PARAMETER,
			/**
			 * Filter cannot currently be executed.
			 */
			CANNOT_EXECUTE
		}

		/**
		 * Populates the given {@link ArrayAdapter} with possible values for the parameter of this filter.
		 * @param hlp {@link HelperDB} to use to read from the database
		 * @param adp {@link ArrayAdapter} to populate
		 * @return {@link PrepareResult}.
		 */
		public abstract PrepareResult prepare(final HelperDB hlp, final ArrayAdapter<NameIDPair> adp);

		/**
		 * Executes the filter and retrieves its results.
		 * @param hlp {@link HelperDB} to use to read from the database
		 * @param adp {@link ArrayAdapter} to populate with results
		 * @param param filter parameter
		 * @param desc <code>true</code> to sort descending, <code>false</code> to sort ascending
		 */
		public abstract void execute(final HelperDB hlp, final ArrayAdapter<String> adp, final NameIDPair param, final boolean desc);

		/**
		 * Returns the filter's name.
		 * @return filter name
		 */
		@Override
		@NonNull
		public abstract String toString();
	}

	public static final DBFilter[] DB_FILTERS = {
			new DBFilter() {
				@Override
				public PrepareResult prepare(HelperDB hlp, ArrayAdapter<NameIDPair> adp) {
					if (InputActivity.populateStudentsAdapter(hlp, adp))
						return PrepareResult.READY;
					else {
						NameIDPair pair = adp.getItem(0);
						if (pair != null)
							pair.setName("(no students in database, cannot execute filter)");
						return PrepareResult.CANNOT_EXECUTE;
					}
				}

				@Override
				public void execute(HelperDB hlp, ArrayAdapter<String> adp, NameIDPair param, boolean desc) {
					if (param.getId() < 0) {

						return;
					}
					SQLiteDatabase db = hlp.getReadableDatabase();
					Cursor crsr = db.query(GradeEntry.TABLE_NAME, null, GradeEntry.STUDENT_ID + "=?", new String[] { Integer.toString(param.getId())}, GradeEntry.QUARTER, null, GradeEntry.VALUE + (desc ? " DESC" : ""));
					final int colQuarter = crsr.getColumnIndex(GradeEntry.QUARTER);
					final int colSubject = crsr.getColumnIndex(GradeEntry.SUBJECT);
					final int colValue = crsr.getColumnIndex(GradeEntry.VALUE);
					crsr.moveToFirst();
					while (!crsr.isAfterLast()) {
						int quarter = crsr.getInt(colQuarter);
						int subject = crsr.getInt(colSubject);
						int value = crsr.getInt(colValue);
						adp.add(String.format(Locale.getDefault(),
								"%s Quarter, %s: %d",
								ViewActivity.ORDINAL_NUMBERS[quarter],
								InputActivity.SUBJECTS[subject],
								value));
						crsr.moveToNext();
					}
					crsr.close();
					db.close();
				}

				@NonNull
				@Override
				public String toString() {
					return "All grades of student";
				}
			}, new DBFilter() {
				@Override
				public PrepareResult prepare(HelperDB hlp, ArrayAdapter<NameIDPair> adp) {
					NameIDPair[] subjects = new NameIDPair[InputActivity.SUBJECTS.length];
					for (int i = 0; i < subjects.length; i++)
						subjects[i] = new NameIDPair(InputActivity.SUBJECTS[i], i);
					adp.addAll(subjects);
					return PrepareResult.READY;
				}

				@Override
				public void execute(HelperDB hlp, ArrayAdapter<String> adp, NameIDPair param, boolean desc) {
					SparseArray<String> studentNames = ViewActivity.getStudentArray(hlp);
					SQLiteDatabase db = hlp.getReadableDatabase();
					Cursor crsr = db.query(GradeEntry.TABLE_NAME, null, GradeEntry.SUBJECT + "=?", new String[] { Integer.toString(param.getId())}, GradeEntry.QUARTER, null, GradeEntry.VALUE + (desc ? " DESC" : ""));
					final int colStudentId = crsr.getColumnIndex(GradeEntry.STUDENT_ID);
					final int colQuarter = crsr.getColumnIndex(GradeEntry.QUARTER);
					final int colValue = crsr.getColumnIndex(GradeEntry.VALUE);
					crsr.moveToFirst();
					while (!crsr.isAfterLast()) {
						int studentId = crsr.getInt(colStudentId);
						int quarter = crsr.getInt(colQuarter);
						int value = crsr.getInt(colValue);
						adp.add(String.format(Locale.getDefault(),
								"%s, %s Quarter: %d",
								studentNames.get(studentId, "(unknown student)"),
								ViewActivity.ORDINAL_NUMBERS[quarter],
								value));
						crsr.moveToNext();
					}
					crsr.close();
					db.close();
				}

				@NonNull
				@Override
				public String toString() {
					return "All grades in subject";
				}
			}, new DBFilter() {
				@Override
				public PrepareResult prepare(HelperDB hlp, ArrayAdapter<NameIDPair> adp) {
					NameIDPair[] subjects = new NameIDPair[InputActivity.SUBJECTS.length];
					for (int i = 0; i < subjects.length; i++)
						subjects[i] = new NameIDPair(InputActivity.SUBJECTS[i], i);
					adp.addAll(subjects);
					return PrepareResult.READY;
				}

				@Override
				public void execute(HelperDB hlp, ArrayAdapter<String> adp, NameIDPair param, boolean desc) {
					SparseArray<String> studentNames = ViewActivity.getStudentArray(hlp);
					SQLiteDatabase db = hlp.getReadableDatabase();
					Cursor crsr = db.query(GradeEntry.TABLE_NAME, null, GradeEntry.SUBJECT + "=?", new String[] { Integer.toString(param.getId())}, GradeEntry.QUARTER, null, GradeEntry.VALUE + (desc ? " DESC" : ""));
					final int colStudentId = crsr.getColumnIndex(GradeEntry.STUDENT_ID);
					final int colQuarter = crsr.getColumnIndex(GradeEntry.QUARTER);
					final int colValue = crsr.getColumnIndex(GradeEntry.VALUE);
					crsr.moveToFirst();
					while (!crsr.isAfterLast()) {
						int studentId = crsr.getInt(colStudentId);
						int quarter = crsr.getInt(colQuarter);
						int value = crsr.getInt(colValue);
						if (value >= 60) {
							adp.add(String.format(Locale.getDefault(),
									"%s, %s Quarter: %d",
									studentNames.get(studentId, "(unknown student)"),
									ViewActivity.ORDINAL_NUMBERS[quarter],
									value));
						}
						crsr.moveToNext();
					}
					crsr.close();
					db.close();
				}

				@NonNull
				@Override
				public String toString() {
					return "All students with passing grade";
				}
			}
	};

	private Spinner spnFilterSelect;
	private ArrayAdapter<DBFilter> spnFilterSelectAdp;

	private Spinner spnFilterParam;
	private ArrayAdapter<NameIDPair> spnFilterParamAdp;

	private RadioGroup rgSort;

	private Button btnFilterExec;

	private ListView lvDisplay;
	private ArrayAdapter<String> lvDisplayAdp;

	private TextView tvNoResults;

	private HelperDB hlp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sort_filter);

		hlp = new HelperDB(this);

		spnFilterSelect = findViewById(R.id.spnFilterSelect);
		spnFilterParam = findViewById(R.id.spnFilterParam);
		rgSort = findViewById(R.id.rgSort);
		btnFilterExec = findViewById(R.id.btnFilterExec);
		lvDisplay = findViewById(R.id.lvDisplay);
		tvNoResults = findViewById(R.id.tvNoResults);

		spnFilterSelect.setOnItemSelectedListener(this);
		spnFilterSelectAdp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, DB_FILTERS);
		spnFilterSelect.setAdapter(spnFilterSelectAdp);

		btnFilterExec.setOnClickListener(this);

		lvDisplay.setChoiceMode(ListView.CHOICE_MODE_NONE);
	}

	/**
	 * Called when an item in {@link #spnFilterSelect} is selected.<br>
	 * Populates {@link #spnFilterParam} with {@linkplain DBFilter#prepare(HelperDB, ArrayAdapter) the selected filter's parameter values.}
	 * If the filter has no parameter, disables {@link #spnFilterParam}.
	 * @param adapterView {@link #spnFilterSelect}
	 * @param view selected item's {@link View}
	 * @param pos selected item's position
	 * @param id selected item's ID
	 */
	@Override
	public void onItemSelected(final AdapterView<?> adapterView, final View view, final int pos, final long id) {
		DBFilter filter = spnFilterSelectAdp.getItem(pos);
		if (filter == null)
			return;
		spnFilterParamAdp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
		switch (filter.prepare(hlp, spnFilterParamAdp)) {
		case READY:
			spnFilterParam.setEnabled(true);
			btnFilterExec.setEnabled(true);
			break;
		case NO_PARAMETER:
			spnFilterParamAdp.add(new NameIDPair("(no parameter)", -1));
			spnFilterParam.setEnabled(false);
			btnFilterExec.setEnabled(true);
			break;
		case CANNOT_EXECUTE:
			spnFilterParam.setEnabled(false);
			btnFilterExec.setEnabled(false);
			break;
		}
		spnFilterParam.setAdapter(spnFilterParamAdp);
	}

	/**
	 * Called when {@link #btnFilterExec} is clicked on.<br>
	 * Executes the selected filter.
	 * @param view {@link #btnFilterExec}
	 */
	@Override
	public void onClick(View view) {
		DBFilter filter = spnFilterSelectAdp.getItem(spnFilterSelect.getSelectedItemPosition());
		if (filter == null)
			return;
		NameIDPair param = spnFilterParamAdp.getItem(spnFilterParam.getSelectedItemPosition());
		if (param == null)
			return;
		boolean desc = rgSort.getCheckedRadioButtonId() == R.id.rbSortDescending;
		lvDisplayAdp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
		filter.execute(hlp, lvDisplayAdp, param, desc);
		if (lvDisplayAdp.getCount() < 1) {
			lvDisplay.setVisibility(View.GONE);
			tvNoResults.setVisibility(View.VISIBLE);
		} else {
			tvNoResults.setVisibility(View.GONE);
			lvDisplay.setVisibility(View.VISIBLE);
			lvDisplay.setAdapter(lvDisplayAdp);
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
		case R.id.menuInput:
			gi = new Intent(this, InputActivity.class);
			break;
		case R.id.menuView:
			gi = new Intent(this, ViewActivity.class);
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
