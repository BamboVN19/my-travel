package com.travel.mytravel.controller.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.travel.mytravel.R;
import com.travel.mytravel.adapter.ExpenseAdapter;
import com.travel.mytravel.api.ApiClient;
import com.travel.mytravel.model.Expense;
import com.travel.mytravel.model.ExpenseSplit;
import com.travel.mytravel.model.ExpenseSummary;
import com.travel.mytravel.model.PageResponse;
import com.travel.mytravel.model.Trip;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetFragment extends Fragment {

    private ChipGroup cgFinancialTab;
    private LinearLayout layoutTripFinancialContainer, layoutTotalFinancialContainer;
    private Spinner spnTripSelector;

    private TextView tvTotalBudget, tvTotalSpent, tvRemaining, tvPercentSpent;
    private TextView tvCatFood, tvCatTransport, tvCatHotel, tvCatTicket;
    private ProgressBar pbExpenseProgress;
    private RecyclerView rvExpenses, rvMasterExpenses;
    private View layoutEmptyState;

    private TextView tvMasterSpent, tvMasterBudget, tvMasterRemaining;

    private ExpenseAdapter tripExpenseAdapter;
    private ExpenseAdapter masterExpenseAdapter;

    private final List<Trip> tripList = new ArrayList<>();
    private final List<Expense> currentTripExpenseList = new ArrayList<>();
    private final List<Expense> allMasterExpenseList = new ArrayList<>();

    private Trip currentSelectedTrip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        initViews(view);
        setupListeners();

        loadTripsAndExpenses();

        return view;
    }

    private void initViews(View view) {
        cgFinancialTab = view.findViewById(R.id.cgFinancialTab);
        layoutTripFinancialContainer = view.findViewById(R.id.layoutTripFinancialContainer);
        layoutTotalFinancialContainer = view.findViewById(R.id.layoutTotalFinancialContainer);
        spnTripSelector = view.findViewById(R.id.spnTripSelector);

        tvTotalBudget = view.findViewById(R.id.tvTotalBudget);
        tvTotalSpent = view.findViewById(R.id.tvTotalSpent);
        tvRemaining = view.findViewById(R.id.tvRemaining);
        tvPercentSpent = view.findViewById(R.id.tvPercentSpent);

        tvCatFood = view.findViewById(R.id.tvCatFood);
        tvCatTransport = view.findViewById(R.id.tvCatTransport);
        tvCatHotel = view.findViewById(R.id.tvCatHotel);
        tvCatTicket = view.findViewById(R.id.tvCatTicket);

        pbExpenseProgress = view.findViewById(R.id.pbExpenseProgress);
        rvExpenses = view.findViewById(R.id.rvExpenses);
        rvMasterExpenses = view.findViewById(R.id.rvMasterExpenses);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        tvMasterSpent = view.findViewById(R.id.tvMasterSpent);
        tvMasterBudget = view.findViewById(R.id.tvMasterBudget);
        tvMasterRemaining = view.findViewById(R.id.tvMasterRemaining);

        if (rvExpenses != null) {
            rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
            tripExpenseAdapter = new ExpenseAdapter(currentTripExpenseList, new ExpenseAdapter.OnExpenseClickListener() {
                @Override
                public void onExpenseClick(Expense expense, int position) {}

                @Override
                public void onExpenseLongClick(Expense expense, int position) {
                    showDeleteExpenseDialog(expense, position);
                }
            });
            rvExpenses.setAdapter(tripExpenseAdapter);
        }

        if (rvMasterExpenses != null) {
            rvMasterExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
            masterExpenseAdapter = new ExpenseAdapter(allMasterExpenseList);
            rvMasterExpenses.setAdapter(masterExpenseAdapter);
        }
    }

    private void setupListeners() {
        if (cgFinancialTab != null) {
            cgFinancialTab.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.chipTotalFinancial) {
                    if (layoutTripFinancialContainer != null) layoutTripFinancialContainer.setVisibility(View.GONE);
                    if (layoutTotalFinancialContainer != null) layoutTotalFinancialContainer.setVisibility(View.VISIBLE);
                } else {
                    if (layoutTripFinancialContainer != null) layoutTripFinancialContainer.setVisibility(View.VISIBLE);
                    if (layoutTotalFinancialContainer != null) layoutTotalFinancialContainer.setVisibility(View.GONE);
                }
            });
        }

        Button btnAddExpense = getView() != null ? getView().findViewById(R.id.btnAddExpense) : null;
        if (btnAddExpense != null) {
            btnAddExpense.setOnClickListener(v -> showAddExpenseDialog());
        }

        if (spnTripSelector != null) {
            spnTripSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position >= 0 && position < tripList.size()) {
                        currentSelectedTrip = tripList.get(position);
                        loadExpensesForSelectedTrip(currentSelectedTrip);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    private void loadTripsAndExpenses() {
        ApiClient.getService().getTrips(0, 10).enqueue(new Callback<PageResponse<Trip>>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse<Trip>> call, @NonNull Response<PageResponse<Trip>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getContent() != null && !response.body().getContent().isEmpty()) {
                    tripList.clear();
                    tripList.addAll(response.body().getContent());

                    List<String> tripTitles = new ArrayList<>();
                    for (Trip t : tripList) {
                        tripTitles.add(t.getTitle());
                    }

                    if (getContext() != null && spnTripSelector != null) {
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tripTitles);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnTripSelector.setAdapter(spinnerAdapter);
                    }

                    currentSelectedTrip = tripList.get(0);
                    loadExpensesForSelectedTrip(currentSelectedTrip);
                    calculateMasterFinancials();
                } else {
                    tripList.clear();
                    currentTripExpenseList.clear();
                    allMasterExpenseList.clear();
                    updateTripBudgetSummary(null);
                    updateMasterSummaryUI(0.0);
                    updateExpenseEmptyStateUI();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PageResponse<Trip>> call, @NonNull Throwable t) {
                tripList.clear();
                currentTripExpenseList.clear();
                allMasterExpenseList.clear();
                updateTripBudgetSummary(null);
                updateMasterSummaryUI(0.0);
                updateExpenseEmptyStateUI();
            }
        });
    }

    private void loadExpensesForSelectedTrip(Trip trip) {
        if (trip == null) {
            currentTripExpenseList.clear();
            updateTripBudgetSummary(null);
            return;
        }

        ApiClient.getService().getExpenses(trip.getId()).enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(@NonNull Call<List<Expense>> call, @NonNull Response<List<Expense>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentTripExpenseList.clear();
                    currentTripExpenseList.addAll(response.body());
                    if (tripExpenseAdapter != null) {
                        tripExpenseAdapter.notifyDataSetChanged();
                    }
                    updateTripBudgetSummary(trip);
                } else {
                    currentTripExpenseList.clear();
                    updateTripBudgetSummary(trip);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Expense>> call, @NonNull Throwable t) {
                currentTripExpenseList.clear();
                updateTripBudgetSummary(trip);
            }
        });

        ApiClient.getService().getExpenseSummary(trip.getId()).enqueue(new Callback<ExpenseSummary>() {
            @Override
            public void onResponse(@NonNull Call<ExpenseSummary> call, @NonNull Response<ExpenseSummary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ExpenseSummary summary = response.body();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExpenseSummary> call, @NonNull Throwable t) {}
        });
    }

    private void updateTripBudgetSummary(Trip trip) {
        double totalSpent = 0;
        double foodSpent = 0, transportSpent = 0, hotelSpent = 0, ticketSpent = 0;

        for (Expense e : currentTripExpenseList) {
            totalSpent += e.getAmount();
            String cat = e.getCategory() != null ? e.getCategory().toUpperCase() : "";
            if (cat.contains("FOOD") || cat.contains("ẨM THỰC")) foodSpent += e.getAmount();
            else if (cat.contains("TRANSPORT") || cat.contains("DI CHUYỂN")) transportSpent += e.getAmount();
            else if (cat.contains("ACCOMMODATION") || cat.contains("KHÁCH SẠN")) hotelSpent += e.getAmount();
            else if (cat.contains("TICKET") || cat.contains("VÉ")) ticketSpent += e.getAmount();
        }

        double estBudget = (trip != null && trip.getTotalBudget() > 0) ? trip.getTotalBudget() : 0.0;
        double remaining = estBudget > totalSpent ? estBudget - totalSpent : 0.0;
        int progress = estBudget > 0 ? (int) ((totalSpent / estBudget) * 100) : 0;

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        if (tvTotalBudget != null) tvTotalBudget.setText(formatter.format(estBudget) + "đ");
        if (tvTotalSpent != null) tvTotalSpent.setText(formatter.format(totalSpent) + "đ");
        if (tvRemaining != null) tvRemaining.setText(formatter.format(remaining) + "đ");
        if (tvPercentSpent != null) tvPercentSpent.setText(progress + "% đã tiêu");
        if (pbExpenseProgress != null) pbExpenseProgress.setProgress(Math.min(progress, 100));

        if (tvCatFood != null) tvCatFood.setText("🍜 Ăn uống: " + formatShortMoney(foodSpent));
        if (tvCatTransport != null) tvCatTransport.setText("🚗 Di chuyển: " + formatShortMoney(transportSpent));
        if (tvCatHotel != null) tvCatHotel.setText("🏨 Khách sạn: " + formatShortMoney(hotelSpent));
        if (tvCatTicket != null) tvCatTicket.setText("🎟 Vé & Tour: " + formatShortMoney(ticketSpent));

        updateExpenseEmptyStateUI();
    }

    private void updateExpenseEmptyStateUI() {
        if (currentTripExpenseList.isEmpty()) {
            if (rvExpenses != null) rvExpenses.setVisibility(View.GONE);
            if (layoutEmptyState != null) {
                layoutEmptyState.setVisibility(View.VISIBLE);
                TextView tvEmptyIcon = layoutEmptyState.findViewById(R.id.tvEmptyIcon);
                TextView tvEmptyTitle = layoutEmptyState.findViewById(R.id.tvEmptyTitle);
                TextView tvEmptySub = layoutEmptyState.findViewById(R.id.tvEmptySubtitle);
                Button btnEmptyAction = layoutEmptyState.findViewById(R.id.btnEmptyAction);

                if (tvEmptyIcon != null) tvEmptyIcon.setText("💸");
                if (tvEmptyTitle != null) tvEmptyTitle.setText("Chưa có khoản chi tiêu nào");
                if (tvEmptySub != null) tvEmptySub.setText("Theo dõi tài chính chuyến đi bằng cách thêm khoản chi đầu tiên!");
                if (btnEmptyAction != null) {
                    btnEmptyAction.setText("+ Thêm khoản chi ngay");
                    btnEmptyAction.setOnClickListener(v -> showAddExpenseDialog());
                }
            }
        } else {
            if (rvExpenses != null) rvExpenses.setVisibility(View.VISIBLE);
            if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private String formatShortMoney(double amount) {
        if (amount >= 1000000) {
            return String.format(Locale.getDefault(), "%.1fM", amount / 1000000.0);
        } else if (amount >= 1000) {
            return String.format(Locale.getDefault(), "%.0fK", amount / 1000.0);
        }
        return (int) amount + "đ";
    }

    private void calculateMasterFinancials() {
        double totalMasterBudget = 0;
        for (Trip t : tripList) {
            totalMasterBudget += t.getTotalBudget();
        }

        final double finalMasterBudget = totalMasterBudget;
        allMasterExpenseList.clear();

        if (tripList.isEmpty()) {
            updateMasterSummaryUI(0.0);
            return;
        }

        for (Trip t : tripList) {
            ApiClient.getService().getExpenses(t.getId()).enqueue(new Callback<List<Expense>>() {
                @Override
                public void onResponse(@NonNull Call<List<Expense>> call, @NonNull Response<List<Expense>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        allMasterExpenseList.addAll(response.body());
                        if (masterExpenseAdapter != null) {
                            masterExpenseAdapter.notifyDataSetChanged();
                        }
                        updateMasterSummaryUI(finalMasterBudget);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Expense>> call, @NonNull Throwable t) {
                }
            });
        }
    }

    private void updateMasterSummaryUI(double masterBudget) {
        double masterSpent = 0;
        for (Expense e : allMasterExpenseList) {
            masterSpent += e.getAmount();
        }
        double remaining = masterBudget > masterSpent ? masterBudget - masterSpent : 0.0;

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        if (tvMasterSpent != null) tvMasterSpent.setText(formatter.format(masterSpent) + "đ");
        if (tvMasterBudget != null) tvMasterBudget.setText("Tổng dự trù ngân sách: " + formatter.format(masterBudget) + "đ");
        if (tvMasterRemaining != null) tvMasterRemaining.setText("Dư quỹ: " + formatter.format(remaining) + "đ");
    }

    private void showAddExpenseDialog() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_expense, null);

        EditText edtDesc = dialogView.findViewById(R.id.edtDialogDesc);
        EditText edtAmount = dialogView.findViewById(R.id.edtDialogAmount);
        EditText edtCategory = dialogView.findViewById(R.id.edtDialogCategory);
        CheckBox cbEnableSplit = dialogView.findViewById(R.id.cbEnableSplit);
        View layoutSplitDetails = dialogView.findViewById(R.id.layoutSplitDetails);
        EditText edtNumPeople = dialogView.findViewById(R.id.edtNumPeople);
        TextView tvSplitResultPreview = dialogView.findViewById(R.id.tvSplitResultPreview);
        CheckBox cbIsSettled = dialogView.findViewById(R.id.cbIsSettled);

        Button btnCancel = dialogView.findViewById(R.id.btnDialogCancelExpense);
        Button btnSave = dialogView.findViewById(R.id.btnDialogSaveExpense);

        if (cbEnableSplit != null && layoutSplitDetails != null) {
            cbEnableSplit.setOnCheckedChangeListener((buttonView, isChecked) -> {
                layoutSplitDetails.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                updateSplitPreview(edtAmount, edtNumPeople, tvSplitResultPreview);
            });
        }

        TextWatcher updatePreviewWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSplitPreview(edtAmount, edtNumPeople, tvSplitResultPreview);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        if (edtAmount != null) edtAmount.addTextChangedListener(updatePreviewWatcher);
        if (edtNumPeople != null) edtNumPeople.addTextChangedListener(updatePreviewWatcher);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> dialog.dismiss());
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                String desc = edtDesc != null ? edtDesc.getText().toString().trim() : "";
                String amountStr = edtAmount != null ? edtAmount.getText().toString().trim() : "";
                String cat = edtCategory != null ? edtCategory.getText().toString().trim() : "FOOD";

                if (desc.isEmpty() || amountStr.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập mô tả và số tiền!", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(amountStr);
                String tripId = currentSelectedTrip != null ? currentSelectedTrip.getId() : "1";

                List<ExpenseSplit> splits = null;
                if (cbEnableSplit != null && cbEnableSplit.isChecked()) {
                    int numPeople = 2;
                    try {
                        if (edtNumPeople != null && !edtNumPeople.getText().toString().trim().isEmpty()) {
                            numPeople = Math.max(1, Integer.parseInt(edtNumPeople.getText().toString().trim()));
                        }
                    } catch (Exception ignored) {}

                    double splitAmt = amount / numPeople;
                    boolean isSettled = cbIsSettled != null && cbIsSettled.isChecked();

                    splits = new ArrayList<>();
                    for (long i = 1; i <= numPeople; i++) {
                        splits.add(new ExpenseSplit(i, "Thành viên " + i, splitAmt, isSettled));
                    }
                }

                String todayDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                Expense expense = new Expense(
                        String.valueOf(System.currentTimeMillis()),
                        tripId,
                        null,
                        "Bùi Ngọc Đại",
                        amount,
                        cat.isEmpty() ? "OTHER" : cat,
                        desc,
                        todayDate,
                        "CASH",
                        splits
                );

                final Expense finalExpense = expense;

                ApiClient.getService().addExpenseToTrip(tripId, expense).enqueue(new Callback<Expense>() {
                    @Override
                    public void onResponse(@NonNull Call<Expense> call, @NonNull Response<Expense> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            currentTripExpenseList.add(0, response.body());
                        } else {
                            currentTripExpenseList.add(0, finalExpense);
                        }
                        if (tripExpenseAdapter != null) tripExpenseAdapter.notifyItemInserted(0);
                        if (rvExpenses != null) rvExpenses.scrollToPosition(0);
                        if (currentSelectedTrip != null) updateTripBudgetSummary(currentSelectedTrip);
                        calculateMasterFinancials();
                        Toast.makeText(getContext(), "+ Thêm khoản chi " + (finalExpense.getSplits() != null ? "(Đã chia tiền nhóm) " : "") + "thành công!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Expense> call, @NonNull Throwable t) {
                        currentTripExpenseList.add(0, finalExpense);
                        if (tripExpenseAdapter != null) tripExpenseAdapter.notifyItemInserted(0);
                        if (rvExpenses != null) rvExpenses.scrollToPosition(0);
                        if (currentSelectedTrip != null) updateTripBudgetSummary(currentSelectedTrip);
                        calculateMasterFinancials();
                        Toast.makeText(getContext(), "+ Thêm khoản chi " + (finalExpense.getSplits() != null ? "(Đã chia tiền nhóm) " : "") + "thành công!", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void updateSplitPreview(EditText edtAmount, EditText edtNumPeople, TextView tvPreview) {
        if (tvPreview == null) return;
        try {
            double amt = 0;
            int num = 2;
            if (edtAmount != null && !edtAmount.getText().toString().trim().isEmpty()) {
                amt = Double.parseDouble(edtAmount.getText().toString().trim());
            }
            if (edtNumPeople != null && !edtNumPeople.getText().toString().trim().isEmpty()) {
                num = Math.max(1, Integer.parseInt(edtNumPeople.getText().toString().trim()));
            }
            double splitVal = amt / num;
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvPreview.setText("💡 Mỗi người trả: " + formatter.format(splitVal) + "đ (" + num + " người)");
        } catch (Exception ignored) {
            tvPreview.setText("💡 Mỗi người trả: 0đ");
        }
    }

    private void showDeleteExpenseDialog(Expense expense, int position) {
        if (getContext() == null || expense == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xóa Khoản Chi");
        builder.setMessage("Bạn có chắc chắn muốn xóa khoản chi \"" + expense.getDescription() + "\" không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            ApiClient.getService().deleteExpense(expense.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        if (position >= 0 && position < currentTripExpenseList.size()) {
                            currentTripExpenseList.remove(position);
                            if (tripExpenseAdapter != null) {
                                tripExpenseAdapter.notifyItemRemoved(position);
                            }
                            if (currentSelectedTrip != null) {
                                updateTripBudgetSummary(currentSelectedTrip);
                            }
                            calculateMasterFinancials();
                            Toast.makeText(getContext(), "Đã xóa khoản chi!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiClient.showError(getContext(), response, "Xóa khoản chi thất bại!");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    ApiClient.handleFailure(getContext(), t, "Lỗi kết nối khi xóa khoản chi");
                }
            });
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
