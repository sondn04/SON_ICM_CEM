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

import com.example.campusexpensemanager.adapter.BudgetAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.campusexpensemanager.dataaccessobject.BudgetDAO;
import com.example.campusexpensemanager.database.DatabaseHelper;
import com.example.campusexpensemanager.models.Budget;

import java.util.List;

public class BudgetListActivity extends AppCompatActivity {
    private ListView lvBudgets;
    private Button btnAddBudget;
    private BudgetDAO budgetDAO;
    private DatabaseHelper dbHelper;
    private List<Budget> budgets;
    private BudgetAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_budget_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lvBudgets = findViewById(R.id.lv_budgets);
        dbHelper = new DatabaseHelper(this);
        budgetDAO = new BudgetDAO(dbHelper);
        btnAddBudget = findViewById(R.id.btn_add_budget);

        btnAddBudget.setOnClickListener(v -> {
            Intent intent = new Intent(BudgetListActivity.this, AddEditBudgetActivity.class);
            startActivity(intent);
        });

        loadBudgets();

        lvBudgets.setOnItemClickListener((parent, view, position, id) -> {
            Budget selectedBudget = budgets.get(position);
            Intent intent = new Intent(BudgetListActivity.this, AddEditBudgetActivity.class);
            intent.putExtra("budgetId", selectedBudget.getBudgetID());
            startActivity(intent);
        });

        lvBudgets.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteConfirmationDialog(position);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBudgets();
    }
    private void loadBudgets(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            String userId = currentUser.getUid();
            budgets = budgetDAO.getAllBudgets(userId);
            adapter = new BudgetAdapter(this, budgets);
            lvBudgets.setAdapter(adapter);
        }
    }
    private void showDeleteConfirmationDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Budget");
        builder.setMessage("Are you sure you want to delete this budget?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            Budget budgetToDelete = budgets.get(position);
            budgetDAO.deleteBudget(budgetToDelete.getBudgetID());
            loadBudgets();
            Toast.makeText(BudgetListActivity.this, "Budget deleted", Toast.LENGTH_SHORT).show();
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