package com.example.campusexpensemanager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.campusexpensemanager.adapter.CategoryAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.campusexpensemanager.dataaccessobject.ExpenseCategoryDAO;
import com.example.campusexpensemanager.database.DatabaseHelper;
import com.example.campusexpensemanager.models.ExpenseCategory;

import java.util.List;

public class CategoryManagementActivity extends AppCompatActivity {
    private EditText edtCategoryName;
    private Button btnAddCategory;
    private ListView lvCategories;
    private ExpenseCategoryDAO categoryDAO;
    private DatabaseHelper dbHelper;
    private List<ExpenseCategory> categories;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtCategoryName = findViewById(R.id.edt_category_name);
        btnAddCategory = findViewById(R.id.btn_add_category);
        lvCategories = findViewById(R.id.lv_categories);

        dbHelper = new DatabaseHelper(this);
        categoryDAO = new ExpenseCategoryDAO(dbHelper);

        loadCategories();

        btnAddCategory.setOnClickListener(v -> addCategory());

        lvCategories.setOnItemClickListener((parent, view, position, id) -> {
            showEditDialog(position);
        });

        lvCategories.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteConfirmationDialog(position);
            return true;
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        loadCategories();
    }

    private void loadCategories() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            categories = categoryDAO.getAllCategoriesForAdmin();
            adapter = new CategoryAdapter(this, categories);
            lvCategories.setAdapter(adapter);
        }
    }

    private void addCategory() {
        String categoryName = edtCategoryName.getText().toString().trim();
        if (!categoryName.isEmpty()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if(currentUser != null){
                String userId = currentUser.getUid();
                ExpenseCategory newCategory = new ExpenseCategory(0, categoryName, userId);
                categoryDAO.insertExpenseCategory(newCategory);
                loadCategories();
                edtCategoryName.setText("");
                Toast.makeText(CategoryManagementActivity.this, "Category added!", Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(this, "Please enter a category name.", Toast.LENGTH_SHORT).show();
        }
    }
    private void showEditDialog(final int position) {
        final ExpenseCategory categoryToEdit = categories.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Category");

        final EditText input = new EditText(this);
        input.setText(categoryToEdit.getCategoryName());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newCategoryName = input.getText().toString().trim();
            if (!newCategoryName.isEmpty()) {
                categoryToEdit.setCategoryName(newCategoryName);
                categoryDAO.updateExpenseCategory(categoryToEdit);
                loadCategories();
                Toast.makeText(CategoryManagementActivity.this, "Category updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CategoryManagementActivity.this, "Category name cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Category");
        builder.setMessage("Are you sure you want to delete this category?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            ExpenseCategory categoryToDelete = categories.get(position);

            if (isCategoryInUse(categoryToDelete.getCategoryID())) {
                Toast.makeText(CategoryManagementActivity.this, "Cannot delete. Category is in use.", Toast.LENGTH_LONG).show();
                return;
            }

            categoryDAO.deleteExpenseCategory(categoryToDelete.getCategoryID());
            loadCategories();
            Toast.makeText(CategoryManagementActivity.this, "Category deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
    private boolean isCategoryInUse(int categoryId) {
        if (categoryDAO.isCategoryUsedInExpenses(categoryId)) {
            return true;
        }
        if (categoryDAO.isCategoryUsedInBudgets(categoryId)) {
            return true;
        }
        if (categoryDAO.isCategoryUsedInRecurringExpenses(categoryId)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}