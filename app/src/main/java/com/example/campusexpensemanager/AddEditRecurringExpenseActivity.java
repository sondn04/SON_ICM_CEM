package com.example.campusexpensemanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.campusexpensemanager.dataaccessobject.ExpenseCategoryDAO;
import com.example.campusexpensemanager.dataaccessobject.RecurringExpenseDAO;
import com.example.campusexpensemanager.database.DatabaseHelper;
import com.example.campusexpensemanager.models.ExpenseCategory;
import com.example.campusexpensemanager.models.RecurringExpense;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEditRecurringExpenseActivity extends AppCompatActivity {

    private EditText edtDescription, edtAmount, edtStartDate, edtEndDate;
    private Spinner spnCategory, spnFrequency;
    private Button btnSave, btnCancel;

    private RecurringExpenseDAO recurringExpenseDAO;
    private ExpenseCategoryDAO categoryDAO;
    private DatabaseHelper dbHelper;
    private int recurringExpenseId = -1;

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_recurring_expense);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtDescription = findViewById(R.id.edt_description);
        edtAmount = findViewById(R.id.edt_amount);
        edtStartDate = findViewById(R.id.edt_start_date);
        edtEndDate = findViewById(R.id.edt_end_date);
        spnCategory = findViewById(R.id.spn_category);
        spnFrequency = findViewById(R.id.spn_frequency);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);


        dbHelper = new DatabaseHelper(this);
        recurringExpenseDAO = new RecurringExpenseDAO(dbHelper);
        categoryDAO = new ExpenseCategoryDAO(dbHelper);

        setupDatePicker(edtStartDate, startCalendar);
        setupDatePicker(edtEndDate, endCalendar);
        loadCategories();
        setupFrequencySpinner();

        recurringExpenseId = getIntent().getIntExtra("recurringExpenseId", -1);
        if (recurringExpenseId != -1) {
            loadRecurringExpenseData(recurringExpenseId);
        }

        btnSave.setOnClickListener(v -> saveRecurringExpense());
        btnCancel.setOnClickListener(v -> finish());

    }

    private void setupDatePicker(EditText editText, Calendar calendar) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel(editText, calendar);
        };

        editText.setOnClickListener(v -> new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void updateDateLabel(EditText editText, Calendar calendar) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(calendar.getTime()));
    }

    private void loadCategories() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            String userId = currentUser.getUid();
            List<ExpenseCategory> categories = categoryDAO.getAllCategories(userId);
            ArrayAdapter<ExpenseCategory> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnCategory.setAdapter(adapter);
        }
    }

    private void setupFrequencySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.recurrence_frequencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFrequency.setAdapter(adapter);
    }
    private void loadRecurringExpenseData(int recurringExpenseId) {
        RecurringExpense recurringExpense = recurringExpenseDAO.getRecurringExpenseById(recurringExpenseId);
        if (recurringExpense != null) {
            edtDescription.setText(recurringExpense.getDescription());
            edtAmount.setText(String.valueOf(recurringExpense.getAmount()));
            edtStartDate.setText(recurringExpense.getStartDate());
            edtEndDate.setText(recurringExpense.getEndDate());

            setCategorySpinnerSelection(recurringExpense.getCategoryID());
            setFrequencySpinnerSelection(recurringExpense.getRecurrenceFrequency());
        }
    }

    private void setCategorySpinnerSelection(int categoryId) {
        for (int i = 0; i < spnCategory.getCount(); i++) {
            ExpenseCategory category = (ExpenseCategory) spnCategory.getItemAtPosition(i);
            if (category.getCategoryID() == categoryId) {
                spnCategory.setSelection(i);
                break;
            }
        }
    }
    private void setFrequencySpinnerSelection(String frequency) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spnFrequency.getAdapter();
        int position = adapter.getPosition(frequency);
        if (position != -1) {
            spnFrequency.setSelection(position);
        }
    }

    private void saveRecurringExpense() {
        String description = edtDescription.getText().toString().trim();
        String amountStr = edtAmount.getText().toString().trim();
        String startDate = edtStartDate.getText().toString().trim();
        String endDate = edtEndDate.getText().toString().trim();
        ExpenseCategory selectedCategory = (ExpenseCategory) spnCategory.getSelectedItem();
        String frequency = spnFrequency.getSelectedItem().toString();

        if (description.isEmpty() || amountStr.isEmpty() || startDate.isEmpty() || endDate.isEmpty()
                || selectedCategory == null || frequency.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        double amount = Double.parseDouble(amountStr);
        int categoryId = selectedCategory.getCategoryID();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();

        RecurringExpense recurringExpense;

        if(recurringExpenseId == -1){
            recurringExpense = new RecurringExpense(0, userId, categoryId, description, amount, startDate, endDate, frequency);
            long newId = recurringExpenseDAO.insertRecurringExpense(recurringExpense);
            if(newId > 0){
                Toast.makeText(this, "Recurring Expense added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to add recurring expense", Toast.LENGTH_SHORT).show();
            }

        } else {
            recurringExpense = new RecurringExpense(recurringExpenseId, userId, categoryId, description, amount, startDate, endDate, frequency);
            int updateRows = recurringExpenseDAO.updateRecurringExpense(recurringExpense);
            if(updateRows > 0){
                Toast.makeText(this, "Recurring Expense updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update recurring expense", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}