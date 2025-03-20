package com.example.campusexpensemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.campusexpensemanager.R;
import com.example.campusexpensemanager.models.Expense;
import com.example.campusexpensemanager.dataaccessobject.ExpenseCategoryDAO;
import com.example.campusexpensemanager.database.DatabaseHelper;
import com.example.campusexpensemanager.models.ExpenseCategory;

import java.util.List;

public class ExpenseAdapter extends ArrayAdapter<Expense> {

    private LayoutInflater inflater;
    private DatabaseHelper dbHelper;
    private ExpenseCategoryDAO categoryDAO;

    public ExpenseAdapter(Context context, List<Expense> expenses) {
        super(context, 0, expenses);
        inflater = LayoutInflater.from(context);
        dbHelper = new DatabaseHelper(context);
        categoryDAO = new ExpenseCategoryDAO(dbHelper);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_expense, parent, false);
            holder = new ViewHolder();
            holder.tvDescription = convertView.findViewById(R.id.tv_description);
            holder.tvAmount = convertView.findViewById(R.id.tv_amount);
            holder.tvDate = convertView.findViewById(R.id.tv_date);
            holder.tvCategory = convertView.findViewById(R.id.tv_category);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Expense expense = getItem(position);
        if (expense != null) {
            holder.tvDescription.setText(expense.getDescription());
            holder.tvAmount.setText(String.format("%.2f", expense.getAmount()));
            holder.tvDate.setText(expense.getDate());

            ExpenseCategory category = categoryDAO.getCategoryById(expense.getCategoryID());
            if (category != null) {
                holder.tvCategory.setText(category.getCategoryName());
            } else {
                holder.tvCategory.setText("Unknown Category");
            }
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
        TextView tvDescription;
        TextView tvAmount;
        TextView tvDate;
        TextView tvCategory;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("ExpenseAdapter{");
        for (int i = 0; i < getCount(); i++) {
            Expense expense = getItem(i);
            if (expense != null) {
                result.append("[description=").append(expense.getDescription())
                        .append(", amount=").append(expense.getAmount())
                        .append(", date=").append(expense.getDate()).append("], ");
            }
        }
        result.append("}");
        return result.toString();
    }
}