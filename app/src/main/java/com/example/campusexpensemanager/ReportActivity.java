package com.example.campusexpensemanager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.campusexpensemanager.dataaccessobject.ExpenseCategoryDAO;
import com.example.campusexpensemanager.dataaccessobject.ExpenseDAO;
import com.example.campusexpensemanager.database.DatabaseHelper;
import com.example.campusexpensemanager.models.Expense;
import com.example.campusexpensemanager.models.ExpenseCategory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    private TextView tvTotalExpenses, tvAverageDaily, tvAverageWeekly, tvAverageMonthly, tvTrend;
    private Spinner spnTimePeriod;
    private ExpenseDAO expenseDAO;

    private ExpenseCategoryDAO categoryDAO;
    private DatabaseHelper dbHelper;
    private TextView tvCategoryBreakdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTotalExpenses = findViewById(R.id.tv_total_expenses);
        tvAverageDaily = findViewById(R.id.tv_average_daily);
        tvAverageWeekly = findViewById(R.id.tv_average_weekly);
        tvAverageMonthly = findViewById(R.id.tv_average_monthly);
        tvTrend = findViewById(R.id.tv_trend);
        spnTimePeriod = findViewById(R.id.spn_time_period);
        tvCategoryBreakdown = findViewById(R.id.tv_category_breakdown);


        dbHelper = new DatabaseHelper(this);
        expenseDAO = new ExpenseDAO(dbHelper);
        categoryDAO = new ExpenseCategoryDAO(dbHelper);

        setupTimePeriodSpinner();

        spnTimePeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                generateReport();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupTimePeriodSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.report_time_periods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTimePeriod.setAdapter(adapter);
    }

    private void generateReport() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String selectedPeriod = spnTimePeriod.getSelectedItem().toString();

            Calendar startCal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();

            switch (selectedPeriod) {
                case "Weekly":
                    startCal.set(Calendar.DAY_OF_WEEK, startCal.getFirstDayOfWeek());
                    break;
                case "Monthly":
                    startCal.set(Calendar.DAY_OF_MONTH, 1);
                    break;
                case "Quarterly":
                    startCal.set(Calendar.DAY_OF_MONTH, 1);
                    startCal.set(Calendar.MONTH, startCal.get(Calendar.MONTH) / 3 * 3); // First month of quarter
                    endCal.setTime(startCal.getTime());
                    endCal.add(Calendar.MONTH, 2);
                    endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));

                    break;
                case "Yearly":
                    startCal.set(Calendar.DAY_OF_YEAR, 1);
                    break;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String startDate = sdf.format(startCal.getTime());
            String endDate = sdf.format(endCal.getTime());

            List<Expense> expenses = expenseDAO.getExpensesByDateRange(userId, startDate, endDate);

            double totalExpenses = 0;
            for (Expense expense : expenses) {
                totalExpenses += expense.getAmount();
            }
            tvTotalExpenses.setText(String.format(Locale.US, "Total Expenses: $%.2f", totalExpenses));

            long diffInMillies = Math.abs(endCal.getTimeInMillis() - startCal.getTimeInMillis());
            long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);
            if (diffInDays == 0) {
                diffInDays = 1;
            }
            double averageDaily = totalExpenses / (diffInDays + 1);
            tvAverageDaily.setText(String.format(Locale.US, "Average Daily: $%.2f", averageDaily));

            if(selectedPeriod.equals("Weekly")){
                tvAverageWeekly.setText(String.format(Locale.US, "Average Weekly: $%.2f", totalExpenses));
            }
            else{
                double averageWeekly = totalExpenses / ((diffInDays + 1)/7.0);
                tvAverageWeekly.setText(String.format(Locale.US, "Average Weekly: $%.2f", averageWeekly));
            }

            if(selectedPeriod.equals("Monthly")){
                tvAverageMonthly.setText(String.format(Locale.US, "Average Monthly: $%.2f", totalExpenses));
            }
            else{
                double averageMonthly = totalExpenses / ((diffInDays + 1)/30.0); // Approximate
                tvAverageMonthly.setText(String.format(Locale.US, "Average Monthly: $%.2f", averageMonthly));
            }


            Calendar prevStartCal = (Calendar) startCal.clone();
            prevStartCal.add(selectedPeriod.equals("Weekly") ? Calendar.WEEK_OF_YEAR :
                    selectedPeriod.equals("Monthly") ? Calendar.MONTH :
                            selectedPeriod.equals("Quarterly") ? Calendar.MONTH :
                                    Calendar.YEAR, -1);
            if(selectedPeriod.equals("Quarterly")){
                prevStartCal.add(Calendar.MONTH, -2);
            }
            Calendar prevEndCal = (Calendar) endCal.clone();
            if(!selectedPeriod.equals("Yearly")){
                prevEndCal.setTime(startCal.getTime());
                prevEndCal.add(Calendar.DATE, -1);
            } else {
                prevEndCal.add(Calendar.YEAR, -1);
                prevEndCal.set(Calendar.MONTH, 11);
                prevEndCal.set(Calendar.DAY_OF_MONTH, 31);
            }


            String prevStartDate = sdf.format(prevStartCal.getTime());
            String prevEndDate = sdf.format(prevEndCal.getTime());
            List<Expense> prevExpenses = expenseDAO.getExpensesByDateRange(userId, prevStartDate, prevEndDate);
            double prevTotal = 0;
            for (Expense expense : prevExpenses) {
                prevTotal += expense.getAmount();
            }

            if (totalExpenses > prevTotal) {
                tvTrend.setText("Spending Trend: Up");
                tvTrend.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else if (totalExpenses < prevTotal) {
                tvTrend.setText("Spending Trend: Down");
                tvTrend.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                tvTrend.setText("Spending Trend: Stable");
                tvTrend.setTextColor(getResources().getColor(android.R.color.black));
            }
            Map<Integer, Double> categoryTotals = new HashMap<>();
            for (Expense expense : expenses) {
                int categoryId = expense.getCategoryID();
                categoryTotals.put(categoryId, categoryTotals.getOrDefault(categoryId, 0.0) + expense.getAmount());
            }

            StringBuilder categoryBreakdownText = new StringBuilder();
            categoryBreakdownText.append("Category Breakdown:\n");
            for (Map.Entry<Integer, Double> entry : categoryTotals.entrySet()) {
                int categoryId = entry.getKey();
                double amount = entry.getValue();
                ExpenseCategory category = categoryDAO.getCategoryById(categoryId);
                String categoryName = (category != null) ? category.getCategoryName() : "Unknown Category";
                categoryBreakdownText.append(String.format(Locale.US, "%s: $%.2f\n", categoryName, amount));
            }
            tvCategoryBreakdown.setText(categoryBreakdownText.toString());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}