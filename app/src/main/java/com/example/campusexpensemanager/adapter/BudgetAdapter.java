package com.example.campusexpensemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.campusexpensemanager.R;
import com.example.campusexpensemanager.dataaccessobject.ExpenseCategoryDAO;
import com.example.campusexpensemanager.database.DatabaseHelper;
import com.example.campusexpensemanager.models.Budget;
import com.example.campusexpensemanager.models.ExpenseCategory;

import java.util.List;

public class BudgetAdapter extends ArrayAdapter<Budget> {

    private LayoutInflater inflater;
    private DatabaseHelper dbHelper;
    private ExpenseCategoryDAO categoryDAO;

    public BudgetAdapter(Context context, List<Budget> budgets) {
        super(context, 0, budgets);
        inflater = LayoutInflater.from(context);
        dbHelper = new DatabaseHelper(context);
        categoryDAO = new ExpenseCategoryDAO(dbHelper);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_budget, parent, false);
            holder = new ViewHolder();
            holder.tvCategoryName = convertView.findViewById(R.id.tv_category_name);
            holder.tvBudgetAmount = convertView.findViewById(R.id.tv_budget_amount);
            holder.tvStartDate = convertView.findViewById(R.id.tv_start_date);
            holder.tvEndDate = convertView.findViewById(R.id.tv_end_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Budget budget = getItem(position);
        if (budget != null) {
            ExpenseCategory category = categoryDAO.getCategoryById(budget.getCategoryID());
            if (category != null) {
                holder.tvCategoryName.setText(category.getCategoryName());
            } else {
                holder.tvCategoryName.setText("Unknown Category");
            }

            holder.tvBudgetAmount.setText(String.format("%.2f", budget.getBudgetAmount()));
            holder.tvStartDate.setText(budget.getStartDate());
            holder.tvEndDate.setText(budget.getEndDate());
        }
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = new DatabaseHelper(getContext());
            categoryDAO = new ExpenseCategoryDAO(dbHelper);
        }
    }

    static class ViewHolder {
        TextView tvCategoryName;
        TextView tvBudgetAmount;
        TextView tvStartDate;
        TextView tvEndDate;
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("BudgetAdapter{");
        for (int i = 0; i < getCount(); i++) {
            Budget budget = getItem(i);
            if (budget != null) {
                result.append("[categoryID=").append(budget.getCategoryID())
                        .append(", budgetAmount=").append(budget.getBudgetAmount()).append("], ");
            }
        }
        result.append("}");
        return result.toString();
    }

}