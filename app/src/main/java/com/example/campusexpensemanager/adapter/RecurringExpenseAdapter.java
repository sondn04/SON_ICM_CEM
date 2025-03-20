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
import com.example.campusexpensemanager.models.ExpenseCategory;
import com.example.campusexpensemanager.models.RecurringExpense;

import java.util.List;

public class RecurringExpenseAdapter extends ArrayAdapter<RecurringExpense> {

    private LayoutInflater inflater;
    private DatabaseHelper dbHelper;
    private ExpenseCategoryDAO categoryDAO;

    public RecurringExpenseAdapter(Context context, List<RecurringExpense> recurringExpenses) {
        super(context, 0, recurringExpenses);
        inflater = LayoutInflater.from(context);
        dbHelper = new DatabaseHelper(context);
        categoryDAO = new ExpenseCategoryDAO(dbHelper);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_recurring_expense, parent, false);
            holder = new ViewHolder();
            holder.tvDescription = convertView.findViewById(R.id.tv_description);
            holder.tvAmount = convertView.findViewById(R.id.tv_amount);
            holder.tvCategoryName = convertView.findViewById(R.id.tv_category_name);
            holder.tvStartDate = convertView.findViewById(R.id.tv_start_date);
            holder.tvEndDate = convertView.findViewById(R.id.tv_end_date);
            holder.tvFrequency = convertView.findViewById(R.id.tv_frequency);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RecurringExpense recurringExpense = getItem(position);
        if (recurringExpense != null) {
            holder.tvDescription.setText(recurringExpense.getDescription());
            holder.tvAmount.setText(String.format("%.2f", recurringExpense.getAmount()));

            ExpenseCategory category = categoryDAO.getCategoryById(recurringExpense.getCategoryID());
            if (category != null) {
                holder.tvCategoryName.setText(category.getCategoryName());
            }else{
                holder.tvCategoryName.setText("Unknown Category");
            }

            holder.tvStartDate.setText(recurringExpense.getStartDate());
            holder.tvEndDate.setText(recurringExpense.getEndDate());
            holder.tvFrequency.setText(recurringExpense.getRecurrenceFrequency());
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
        TextView tvCategoryName;
        TextView tvStartDate;
        TextView tvEndDate;
        TextView tvFrequency;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("RecurringExpenseAdapter{");
        for (int i = 0; i < getCount(); i++) {
            RecurringExpense expense = getItem(i);
            if (expense != null) {
                result.append("[description=").append(expense.getDescription())
                        .append(", amount=").append(expense.getAmount()).append("], ");
            }
        }
        result.append("}");
        return result.toString();
    }

}