package com.example.campusexpensemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.campusexpensemanager.adapter.ExpenseAdapter;
import com.example.campusexpensemanager.dataaccessobject.ExpenseCategoryDAO;
import com.example.campusexpensemanager.models.ExpenseCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.campusexpensemanager.dataaccessobject.ExpenseDAO;
import com.example.campusexpensemanager.database.DatabaseHelper;
import com.example.campusexpensemanager.models.Expense;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExpenseListActivity extends AppCompatActivity {

    private ListView lvExpenses;
    private TextView tvTotalExpenses;
    private ExpenseDAO expenseDAO;
    private ExpenseCategoryDAO categoryDAO;
    private DatabaseHelper dbHelper;
    private List<Expense> expenses;
    private ExpenseAdapter adapter;
    private Button btnAddExpense;
    private Spinner spnCategoryFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expense_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lvExpenses = findViewById(R.id.lv_expenses);
        tvTotalExpenses = findViewById(R.id.tv_total_expenses);
        btnAddExpense = findViewById(R.id.btn_add_expense);
        spnCategoryFilter = findViewById(R.id.spn_category_filter);
        dbHelper = new DatabaseHelper(this);
        expenseDAO = new ExpenseDAO(dbHelper);
        categoryDAO = new ExpenseCategoryDAO(dbHelper);

        loadExpenses();
        setupCategoryFilter();

        lvExpenses.setOnItemClickListener((parent, view, position, id) -> {
            Expense selectedExpense = expenses.get(position);
            Intent intent = new Intent(ExpenseListActivity.this, AddEditExpenseActivity.class);
            intent.putExtra("expenseId", selectedExpense.getExpenseID());
            startActivity(intent);
        });

        lvExpenses.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteConfirmationDialog(position);
            return true;
        });

        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseListActivity.this, AddEditExpenseActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
        loadCategoriesForFilter();
    }

    private void loadExpenses() {
        loadExpenses(null);
    }
    private void loadExpenses(Integer categoryId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            expenses = (categoryId == null) ? expenseDAO.getAllExpenses(userId) : expenseDAO.getExpensesByCategory(userId, categoryId);
            adapter = new ExpenseAdapter(this, expenses);
            lvExpenses.setAdapter(adapter);

            double total = 0;
            for (Expense expense : expenses) {
                total += expense.getAmount();
            }
            tvTotalExpenses.setText(String.format(Locale.US, "Total Expenses: $%.2f", total));
        }
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Expense");
        builder.setMessage("Are you sure you want to delete this expense?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            Expense expenseToDelete = expenses.get(position);
            expenseDAO.deleteExpense(expenseToDelete.getExpenseID());
            loadExpenses();
            Toast.makeText(ExpenseListActivity.this, "Expense deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    private void setupCategoryFilter() {
        loadCategoriesForFilter();

        spnCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ExpenseCategory selectedCategory = (ExpenseCategory) parent.getItemAtPosition(position);
                if (selectedCategory.getCategoryID() == -1) {
                    loadExpenses(null);
                } else {
                    loadExpenses(selectedCategory.getCategoryID());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void loadCategoriesForFilter() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            List<ExpenseCategory> categories = categoryDAO.getAllCategories(userId);

            categories.add(0, new ExpenseCategory(-1, "All Categories", userId));


            ArrayAdapter<ExpenseCategory> filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
            filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnCategoryFilter.setAdapter(filterAdapter);


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}