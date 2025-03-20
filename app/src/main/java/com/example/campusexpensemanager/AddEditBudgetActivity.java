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
import com.example.campusexpensemanager.dataaccessobject.BudgetDAO;
import com.example.campusexpensemanager.dataaccessobject.ExpenseCategoryDAO;
import com.example.campusexpensemanager.database.DatabaseHelper;
import com.example.campusexpensemanager.models.Budget;
import com.example.campusexpensemanager.models.ExpenseCategory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEditBudgetActivity extends AppCompatActivity {

    private Spinner spnCategory;
    private EditText edtBudgetAmount, edtStartDate, edtEndDate;
    private Button btnSave, btnCancel;

    private BudgetDAO budgetDAO;
    private ExpenseCategoryDAO categoryDAO;
    private DatabaseHelper dbHelper;
    private int budgetId = -1;

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_budget);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spnCategory = findViewById(R.id.spn_category);
        edtBudgetAmount = findViewById(R.id.edt_budget_amount);
        edtStartDate = findViewById(R.id.edt_start_date);
        edtEndDate = findViewById(R.id.edt_end_date);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        dbHelper = new DatabaseHelper(this);
        budgetDAO = new BudgetDAO(dbHelper);
        categoryDAO = new ExpenseCategoryDAO(dbHelper);

        setupDatePicker(edtStartDate, startCalendar);
        setupDatePicker(edtEndDate, endCalendar);
        loadCategories();


        budgetId = getIntent().getIntExtra("budgetId", -1);
        if (budgetId != -1) {
            loadBudgetData(budgetId);
        }

        btnSave.setOnClickListener(v -> saveBudget());
        btnCancel.setOnClickListener(v -> finish());
    }


    private void setupDatePicker(EditText editText, Calendar calendar) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel(editText, calendar);
        };

        editText.setOnClickListener(v -> new DatePickerDialog(AddEditBudgetActivity.this, dateSetListener,
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

    private void loadBudgetData(int budgetId) {
        Budget budget = budgetDAO.getBudgetById(budgetId);
        if (budget != null) {
            edtBudgetAmount.setText(String.valueOf(budget.getBudgetAmount()));
            edtStartDate.setText(budget.getStartDate());
            edtEndDate.setText(budget.getEndDate());

            setSpinnerSelection(budget.getCategoryID());
        }
    }

    private void setSpinnerSelection(int categoryId) {
        for (int i = 0; i < spnCategory.getCount(); i++) {
            ExpenseCategory category = (ExpenseCategory) spnCategory.getItemAtPosition(i);
            if (category.getCategoryID() == categoryId) {
                spnCategory.setSelection(i);
                break;
            }
        }
    }

    private void saveBudget() {
        String amountStr = edtBudgetAmount.getText().toString().trim();
        String startDate = edtStartDate.getText().toString().trim();
        String endDate = edtEndDate.getText().toString().trim();
        ExpenseCategory selectedCategory = (ExpenseCategory) spnCategory.getSelectedItem();


        if (amountStr.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || selectedCategory == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double budgetAmount = Double.parseDouble(amountStr);
        int categoryId = selectedCategory.getCategoryID();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser == null){
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();
        Budget budget;
        if (budgetId == -1) {
            budget = new Budget(0, userId, categoryId, budgetAmount, startDate, endDate);
            long newId = budgetDAO.insertBudget(budget);
            if(newId > 0){
                Toast.makeText(this, "Budget added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to add budget", Toast.LENGTH_SHORT).show();
            }

        } else {
            budget = new Budget(budgetId, userId, categoryId, budgetAmount, startDate, endDate);
            int updatedRows = budgetDAO.updateBudget(budget);
            if(updatedRows > 0){
                Toast.makeText(this, "Budget updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update budget", Toast.LENGTH_SHORT).show();
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