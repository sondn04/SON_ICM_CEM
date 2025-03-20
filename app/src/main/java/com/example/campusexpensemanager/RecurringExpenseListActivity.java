package com.example.campusexpensemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.campusexpensemanager.adapter.RecurringExpenseAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.campusexpensemanager.dataaccessobject.RecurringExpenseDAO;
import com.example.campusexpensemanager.database.DatabaseHelper;
import com.example.campusexpensemanager.models.RecurringExpense;

import java.util.List;

public class RecurringExpenseListActivity extends AppCompatActivity {

    private ListView lvRecurringExpenses;
    private Button btnAddExpense;
    private RecurringExpenseDAO recurringExpenseDAO;
    private DatabaseHelper dbHelper;
    private List<RecurringExpense> recurringExpenses;
    private RecurringExpenseAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recurring_expense_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lvRecurringExpenses = findViewById(R.id.lv_recurring_expenses);
        dbHelper = new DatabaseHelper(this);
        recurringExpenseDAO = new RecurringExpenseDAO(dbHelper);
        btnAddExpense = findViewById(R.id.btn_add_expense);

        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(RecurringExpenseListActivity.this, AddEditRecurringExpenseActivity.class);
            startActivity(intent);
        });

        loadRecurringExpenses();

        lvRecurringExpenses.setOnItemClickListener((parent, view, position, id) -> {
            RecurringExpense selectedRecurringExpense = recurringExpenses.get(position);
            Intent intent = new Intent(RecurringExpenseListActivity.this, AddEditRecurringExpenseActivity.class);
            intent.putExtra("recurringExpenseId", selectedRecurringExpense.getRecurringExpenseID());
            startActivity(intent);
        });
        lvRecurringExpenses.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteConfirmationDialog(position);
            return true;
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        loadRecurringExpenses();
    }

    private void loadRecurringExpenses(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            String userId = currentUser.getUid();
            recurringExpenses = recurringExpenseDAO.getAllRecurringExpenses(userId);
            adapter = new RecurringExpenseAdapter(this, recurringExpenses);
            lvRecurringExpenses.setAdapter(adapter);
        }
    }
    private void showDeleteConfirmationDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Recurring Expense");
        builder.setMessage("Are you sure you want to delete this recurring expense");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            RecurringExpense recurringExpenseToDelete = recurringExpenses.get(position);
            recurringExpenseDAO.deleteRecurringExpense(recurringExpenseToDelete.getRecurringExpenseID());
            loadRecurringExpenses();
            Toast.makeText(RecurringExpenseListActivity.this, "Recurring Expense deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        dbHelper.close();
    }
}