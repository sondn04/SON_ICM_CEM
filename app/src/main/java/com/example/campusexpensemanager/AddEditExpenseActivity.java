package com.example.campusexpensemanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
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
import com.example.campusexpensemanager.R;
import com.example.campusexpensemanager.dataaccessobject.ExpenseCategoryDAO;
import com.example.campusexpensemanager.models.ExpenseCategory;
import com.example.campusexpensemanager.database.DatabaseHelper;
import com.example.campusexpensemanager.dataaccessobject.ExpenseDAO;
import com.example.campusexpensemanager.models.Expense;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEditExpenseActivity extends AppCompatActivity {

    private EditText edtDescription, edtAmount, edtDate;
    private Spinner spnCategory;
    private Button btnSave, btnCancel;
    private ExpenseDAO expenseDAO;
    private ExpenseCategoryDAO categoryDAO;
    private DatabaseHelper dbHelper;
    private int expenseId = -1;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_expense);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        expenseDAO = new ExpenseDAO(dbHelper);
        categoryDAO = new ExpenseCategoryDAO(dbHelper);

        edtDescription = findViewById(R.id.edt_description);
        edtAmount = findViewById(R.id.edt_amount);
        edtDate = findViewById(R.id.edt_date);
        spnCategory = findViewById(R.id.spn_category);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        };

        edtDate.setOnClickListener(v -> new DatePickerDialog(AddEditExpenseActivity.this, dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());

        loadCategories();

        expenseId = getIntent().getIntExtra("expenseId", -1);
        if (expenseId != -1) {
            loadExpenseData(expenseId);
        }

        btnSave.setOnClickListener(v -> saveExpense());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void updateDateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edtDate.setText(sdf.format(calendar.getTime()));
    }

    private void loadCategories() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        List<ExpenseCategory> categories = categoryDAO.getAllCategories(userId);
        ArrayAdapter<ExpenseCategory> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(adapter);

        // IMPORTANT: Override toString() in ExpenseCategory
        // Add this to ExpenseCategory.java:
        // @Override
        // public String toString() {
        //     return categoryName;
        // }
    }


    private void loadExpenseData(int expenseId) {
        Expense expense = expenseDAO.getExpenseById(expenseId);
        if (expense != null) {
            edtDescription.setText(expense.getDescription());
            edtAmount.setText(String.valueOf(expense.getAmount()));
            edtDate.setText(expense.getDate());

            setSpinnerSelection(expense.getCategoryID());
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
    private void saveExpense() {
        String description = edtDescription.getText().toString().trim();
        String amountStr = edtAmount.getText().toString().trim();
        String date = edtDate.getText().toString().trim();
        ExpenseCategory selectedCategory = (ExpenseCategory) spnCategory.getSelectedItem();

        if (description.isEmpty() || amountStr.isEmpty() || date.isEmpty() || selectedCategory == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        int categoryId = selectedCategory.getCategoryID();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : null;
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Expense expense;
        if (expenseId == -1) {
            expense = new Expense(0, userId, categoryId, description, amount, date);
            long newId = expenseDAO.insertExpense(expense);
            if(newId > 0){
                Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to add expense", Toast.LENGTH_SHORT).show();
            }

        } else {
            expense = new Expense(expenseId, userId, categoryId, description, amount, date);
            int updatedRows = expenseDAO.updateExpense(expense);

            if(updatedRows > 0){
                Toast.makeText(this, "Expense updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update expense", Toast.LENGTH_SHORT).show();
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