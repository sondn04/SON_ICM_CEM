package com.example.campusexpensemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.campusexpensemanager.R;
import com.example.campusexpensemanager.models.ExpenseCategory;

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<ExpenseCategory> {

    private LayoutInflater inflater;

    public CategoryAdapter(Context context, List<ExpenseCategory> categories) {
        super(context, 0, categories);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_category, parent, false);
            holder = new ViewHolder();
            holder.tvCategoryName = convertView.findViewById(R.id.tv_category_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ExpenseCategory category = getItem(position);
        if (category != null) {
            holder.tvCategoryName.setText(category.getCategoryName());
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvCategoryName;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("CategoryAdapter{");
        for (int i = 0; i < getCount(); i++) {
            ExpenseCategory category = getItem(i);
            if (category != null) {
                result.append("[categoryName=").append(category.getCategoryName()).append("], ");
            }
        }
        result.append("}");
        return result.toString();
    }

}