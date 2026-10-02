package com.travel.mytravel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.travel.mytravel.R;
import com.travel.mytravel.model.Expense;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense, int position);
        void onExpenseLongClick(Expense expense, int position);
    }

    private final List<Expense> expenseList;
    private OnExpenseClickListener listener;

    public ExpenseAdapter(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    public ExpenseAdapter(List<Expense> expenseList, OnExpenseClickListener listener) {
        this.expenseList = expenseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.tvExpenseTitle.setText(expense.getDescription());

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String sub = expense.getCategory() + " • " + expense.getExpenseDate();

        if (expense.getSplits() != null && !expense.getSplits().isEmpty()) {
            double splitAmt = expense.getSplits().get(0).getSplitAmount();
            int peopleCount = expense.getSplits().size();
            sub += " • 👥 Chia " + peopleCount + " người: " + formatter.format(splitAmt) + "đ/người";
        }

        holder.tvExpenseSub.setText(sub);

        String amountFormatted = formatter.format(expense.getAmount()) + "đ";
        holder.tvExpenseAmount.setText(amountFormatted);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onExpenseClick(expense, position);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onExpenseLongClick(expense, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return expenseList != null ? expenseList.size() : 0;
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvExpenseTitle, tvExpenseSub, tvExpenseAmount;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExpenseTitle = itemView.findViewById(R.id.tvExpenseTitle);
            tvExpenseSub = itemView.findViewById(R.id.tvExpenseSub);
            tvExpenseAmount = itemView.findViewById(R.id.tvExpenseAmount);
        }
    }
}
