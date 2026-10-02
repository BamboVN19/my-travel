package com.travel.mytravel.controller.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.travel.mytravel.R;
import com.travel.mytravel.adapter.ItineraryAdapter;
import com.travel.mytravel.adapter.LocationAdapter;
import com.travel.mytravel.api.ApiClient;
import com.travel.mytravel.controller.SearchLocationActivity;
import android.text.Editable;
import android.text.TextWatcher;
import com.travel.mytravel.model.ItineraryItem;
import com.travel.mytravel.model.LocationResponse;
import com.travel.mytravel.model.Trip;
import com.travel.mytravel.model.TripRequest;
import com.travel.mytravel.repository.GlobalDataCache;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import okhttp3.ResponseBody;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanFragment extends Fragment implements ItineraryAdapter.OnItineraryClickListener {

    private AutoCompleteTextView edtDestination;
    private EditText edtDates, edtBudget;
    private ImageView btnOpenMap;
    private Button btnCreatePlan;

    private String selectedStartDate = "18-10-2026";
    private String selectedEndDate = "22-10-2026";

    private BottomSheetDialog itineraryBottomSheetDialog;
    private ItineraryAdapter popupItineraryAdapter;
    private final List<ItineraryItem> allPopupItineraries = new ArrayList<>();
    private final List<ItineraryItem> displayedPopupItineraries = new ArrayList<>();
    private Trip currentCreatedTrip;
    private int popupSelectedDay = 1;
    private ChipGroup cgPopupDaySelector;

    private ActivityResultLauncher<Intent> locationPickerLauncher;
    private ActivityResultLauncher<Intent> itineraryLocationSearchLauncher;
    private EditText targetItineraryLocationEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String selectedLoc = result.getData().getStringExtra("selected_location");
                        if (selectedLoc != null && edtDestination != null) {
                            edtDestination.setText(selectedLoc);
                            Toast.makeText(getContext(), "📍 Đã chọn địa điểm: " + selectedLoc, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        itineraryLocationSearchLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String selectedLoc = result.getData().getStringExtra("selected_location");
                        if (selectedLoc != null && targetItineraryLocationEditText != null) {
                            targetItineraryLocationEditText.setText(selectedLoc);
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        initViews(view);
        setupAutoCompleteSuggestions();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        edtDestination = view.findViewById(R.id.edtDestination);
        edtDates = view.findViewById(R.id.edtDates);
        edtBudget = view.findViewById(R.id.edtBudget);
        btnOpenMap = view.findViewById(R.id.btnOpenMap);
        btnCreatePlan = view.findViewById(R.id.btnCreatePlan);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupAutoCompleteSuggestions() {
        if (getContext() == null || edtDestination == null) return;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_dropdown_item_1line) {
            @NonNull
            @Override
            public Filter getFilter() {
                return new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults filterResults = new FilterResults();
                        List<String> suggestions = new ArrayList<>();
                        String query = constraint != null ? constraint.toString().trim() : "";
                        try {
                            Response<List<LocationResponse>> response = ApiClient.getService()
                                    .searchLocations(query)
                                    .execute();
                            if (response.isSuccessful() && response.body() != null) {
                                for (LocationResponse loc : response.body()) {
                                    String displayText = loc.getDisplayText();
                                    if (displayText != null && !displayText.isEmpty()) {
                                        suggestions.add(displayText);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        filterResults.values = suggestions;
                        filterResults.count = suggestions.size();
                        return filterResults;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        clear();
                        if (results != null && results.count > 0 && results.values != null) {
                            @SuppressWarnings("unchecked")
                            List<String> list = (List<String>) results.values;
                            addAll(list);
                            notifyDataSetChanged();
                        } else {
                            notifyDataSetInvalidated();
                        }
                    }

                    @Override
                    public CharSequence convertResultToString(Object resultValue) {
                        return resultValue != null ? resultValue.toString() : "";
                    }
                };
            }
        };

        edtDestination.setAdapter(adapter);

        edtDestination.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                edtDestination.showDropDown();
            }
            return false;
        });
    }

    private void setupListeners() {
        if (edtDestination != null) {
            edtDestination.setFocusable(false);
            edtDestination.setClickable(true);
            edtDestination.setOnClickListener(v -> showLocationSearchDialog());
        }

        if (edtDates != null) {
            edtDates.setOnClickListener(v -> showDateRangePicker());
        }

        if (btnOpenMap != null) {
            btnOpenMap.setOnClickListener(v -> showLocationSearchDialog());
        }

        if (btnCreatePlan != null) {
            btnCreatePlan.setOnClickListener(v -> performCreatePlanAndShowPopup());
        }
    }

    private void showLocationSearchDialog() {
        if (getContext() == null) return;

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.activity_search_location, null);
        dialog.setContentView(dialogView);

        ImageView btnBack = dialogView.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> dialog.dismiss());
        }

        EditText edtSearchLocation = dialogView.findViewById(R.id.edtSearchLocation);
        RecyclerView rvSearchHistory = dialogView.findViewById(R.id.rvSearchHistory);
        View layoutEmptyState = dialogView.findViewById(R.id.layoutEmptyState);
        TextView btnDoSearch = dialogView.findViewById(R.id.btnDoSearch);
        TextView btnClearHistory = dialogView.findViewById(R.id.btnClearHistory);

        List<LocationAdapter.LocationItem> dialogLocations = new ArrayList<>();
        dialogLocations.add(new LocationAdapter.LocationItem("Đà Nẵng", "Thành phố Đà Nẵng, Việt Nam"));
        dialogLocations.add(new LocationAdapter.LocationItem("Hà Nội", "Thủ đô Hà Nội, Việt Nam"));
        dialogLocations.add(new LocationAdapter.LocationItem("Thành phố Hồ Chí Minh", "TP. Hồ Chí Minh, Việt Nam"));
        dialogLocations.add(new LocationAdapter.LocationItem("Đà Lạt, Lâm Đồng", "Thành phố Đà Lạt, Lâm Đồng, Việt Nam"));
        dialogLocations.add(new LocationAdapter.LocationItem("Vịnh Hạ Long", "Thành phố Hạ Long, Quảng Ninh, Việt Nam"));
        dialogLocations.add(new LocationAdapter.LocationItem("Phố Cổ Hội An", "Hội An, Quảng Nam, Việt Nam"));
        dialogLocations.add(new LocationAdapter.LocationItem("Phú Quốc, Kiên Giang", "Thành phố Phú Quốc, Kiên Giang, Việt Nam"));
        dialogLocations.add(new LocationAdapter.LocationItem("Sa Pa, Lào Cai", "Thị xã Sa Pa, Lào Cai, Việt Nam"));
        dialogLocations.add(new LocationAdapter.LocationItem("Nha Trang, Khánh Hòa", "Thành phố Nha Trang, Khánh Hòa, Việt Nam"));

        List<LocationAdapter.LocationItem> filteredDialogList = new ArrayList<>(dialogLocations);

        LocationAdapter dialogAdapter = new LocationAdapter(filteredDialogList, item -> {
            if (edtDestination != null) {
                edtDestination.setText(item.getName());
            }
            dialog.dismiss();
        });

        if (rvSearchHistory != null) {
            rvSearchHistory.setLayoutManager(new LinearLayoutManager(getContext()));
            rvSearchHistory.setAdapter(dialogAdapter);
        }

        if (edtSearchLocation != null) {
            edtSearchLocation.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String query = s.toString().trim().toLowerCase();
                    filteredDialogList.clear();
                    if (query.isEmpty()) {
                        filteredDialogList.addAll(dialogLocations);
                    } else {
                        for (LocationAdapter.LocationItem loc : dialogLocations) {
                            if (loc.getName().toLowerCase().contains(query) || loc.getAddress().toLowerCase().contains(query)) {
                                filteredDialogList.add(loc);
                            }
                        }
                    }
                    if (dialogAdapter != null) {
                        dialogAdapter.notifyDataSetChanged();
                    }
                    if (layoutEmptyState != null) {
                        layoutEmptyState.setVisibility(filteredDialogList.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        if (btnDoSearch != null) {
            btnDoSearch.setOnClickListener(v -> {
                if (edtSearchLocation != null) {
                    String query = edtSearchLocation.getText().toString().trim();
                    if (!query.isEmpty()) {
                        if (edtDestination != null) {
                            edtDestination.setText(query);
                        }
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Vui lòng nhập vị trí!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (btnClearHistory != null) {
            btnClearHistory.setOnClickListener(v -> {
                filteredDialogList.clear();
                if (dialogAdapter != null) dialogAdapter.notifyDataSetChanged();
                if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.VISIBLE);
            });
        }

        dialog.show();

        dialog.show();
    }

    private void showDateRangePicker() {
        MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Chọn Ngày Khởi Hành & Kết Thúc")
                .setSelection(new Pair<>(MaterialDatePicker.todayInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds() + 4 * 86400000L))
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection != null && selection.first != null && selection.second != null) {
                SimpleDateFormat sdfDisp = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat sdfApi = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                Date start = new Date(selection.first);
                Date end = new Date(selection.second);

                selectedStartDate = sdfApi.format(start);
                selectedEndDate = sdfApi.format(end);

                String displayDates = sdfDisp.format(start) + " ➔ " + sdfDisp.format(end);
                edtDates.setText(displayDates);
            }
        });

        datePicker.show(getParentFragmentManager(), "MATERIAL_DATE_RANGE_PICKER");
    }

    private void performCreatePlanAndShowPopup() {
        String destInput = edtDestination.getText().toString().trim();
        String budgetInput = edtBudget.getText().toString().trim();

        String destination = destInput.isEmpty() ? "Đà Lạt, Lâm Đồng" : destInput;

        double budget = 12000000.0;
        try {
            if (!budgetInput.isEmpty()) {
                budget = Double.parseDouble(budgetInput.replaceAll("[^0-9]", ""));
            }
        } catch (Exception ignored) {
        }

        TripRequest req = new TripRequest(
                "Chuyến đi " + destination,
                destination,
                selectedStartDate,
                selectedEndDate,
                budget,
                "PLANNED"
        );

        if (getContext() != null) {
            Toast.makeText(getContext(), "Đang tạo gợi ý lịch trình cho " + destination + "...", Toast.LENGTH_SHORT).show();
        }

        ApiClient.getService().createTrip(req).enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(@NonNull Call<Trip> call, @NonNull Response<Trip> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentCreatedTrip = response.body();
                    GlobalDataCache.getInstance().invalidateTripsCache();
                    Toast.makeText(getContext(), "🎉 Tạo chuyến đi thành công!", Toast.LENGTH_SHORT).show();
                    showSuggestedItineraryPopup(currentCreatedTrip);
                } else {
                    ApiClient.showError(getContext(), response, "Không thể khởi tạo chuyến đi trên hệ thống");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Trip> call, @NonNull Throwable t) {
                ApiClient.handleFailure(getContext(), t, "Lỗi kết nối mạng khi tạo chuyến đi");
            }
        });
    }

    private void showSuggestedItineraryPopup(Trip trip) {
        if (getContext() == null) return;

        itineraryBottomSheetDialog = new BottomSheetDialog(getContext());
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_suggested_itinerary, null);
        itineraryBottomSheetDialog.setContentView(sheetView);

        TextView tvPopupDest = sheetView.findViewById(R.id.tvPopupDest);
        TextView tvPopupDates = sheetView.findViewById(R.id.tvPopupDates);
        TextView tvPopupBudget = sheetView.findViewById(R.id.tvPopupBudget);
        TextView btnPopupAddItinerary = sheetView.findViewById(R.id.btnPopupAddItinerary);
        RecyclerView rvPopupItineraries = sheetView.findViewById(R.id.rvPopupItineraries);
        Button btnPopupSave = sheetView.findViewById(R.id.btnPopupSave);
        Button btnPopupClose = sheetView.findViewById(R.id.btnPopupClose);
        cgPopupDaySelector = sheetView.findViewById(R.id.cgPopupDaySelector);

        if (tvPopupDest != null) tvPopupDest.setText(trip.getDestination());
        if (tvPopupDates != null) tvPopupDates.setText(trip.getStartDate() + " ➔ " + trip.getEndDate());
        if (tvPopupBudget != null) {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvPopupBudget.setText(formatter.format(trip.getTotalBudget()) + "đ");
        }

        if (rvPopupItineraries != null) {
            rvPopupItineraries.setLayoutManager(new LinearLayoutManager(getContext()));
            popupItineraryAdapter = new ItineraryAdapter(displayedPopupItineraries, this);
            rvPopupItineraries.setAdapter(popupItineraryAdapter);
        }

        if (btnPopupAddItinerary != null) {
            btnPopupAddItinerary.setOnClickListener(v -> showAddNewItineraryDialogPopup(trip.getId()));
        }

        if (btnPopupSave != null) {
            btnPopupSave.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Đã lưu chuyến đi vào danh sách thành công!", Toast.LENGTH_LONG).show();
                itineraryBottomSheetDialog.dismiss();
            });
        }

        if (btnPopupClose != null) {
            btnPopupClose.setOnClickListener(v -> itineraryBottomSheetDialog.dismiss());
        }

        loadPopupItineraries(trip.getId());

        itineraryBottomSheetDialog.show();
    }

    private int calculateDurationDays(String startDateStr, String endDateStr) {
        if (startDateStr == null || endDateStr == null) return 1;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date start = sdf.parse(startDateStr);
            Date end = sdf.parse(endDateStr);
            if (start != null && end != null) {
                long diffMs = end.getTime() - start.getTime();
                long days = (diffMs / (1000 * 60 * 60 * 24)) + 1;
                return (int) Math.max(1, days);
            }
        } catch (Exception ignored) {}
        return 1;
    }

    private void setupDynamicPopupDayChips() {
        if (getContext() == null || cgPopupDaySelector == null) return;
        cgPopupDaySelector.removeAllViews();

        int maxDurationDays = 1;
        if (currentCreatedTrip != null && currentCreatedTrip.getStartDate() != null && currentCreatedTrip.getEndDate() != null) {
            maxDurationDays = calculateDurationDays(currentCreatedTrip.getStartDate(), currentCreatedTrip.getEndDate());
        }

        int maxItineraryDay = 1;
        for (ItineraryItem item : allPopupItineraries) {
            if (item.getDayNumber() != null && item.getDayNumber() > maxItineraryDay) {
                maxItineraryDay = item.getDayNumber();
            }
        }

        int totalDaysToShow = Math.max(maxDurationDays, maxItineraryDay);

        for (int i = 1; i <= totalDaysToShow; i++) {
            final int dayNum = i;
            Chip chip = new Chip(getContext());
            chip.setText("Ngày " + dayNum);
            chip.setCheckable(true);
            chip.setClickable(true);
            if (dayNum == popupSelectedDay) {
                chip.setChecked(true);
            }
            chip.setOnClickListener(v -> filterPopupItinerariesByDay(dayNum));
            cgPopupDaySelector.addView(chip);
        }

        if (totalDaysToShow < maxDurationDays) {
            final int nextDay = totalDaysToShow + 1;
            Chip addChip = new Chip(getContext());
            addChip.setText("+ Ngày");
            addChip.setClickable(true);
            addChip.setOnClickListener(v -> addNewDayPopup(nextDay));
            cgPopupDaySelector.addView(addChip);
        }
    }

    private void addNewDayPopup(int newDayNum) {
        int maxDurationDays = 1;
        if (currentCreatedTrip != null && currentCreatedTrip.getStartDate() != null && currentCreatedTrip.getEndDate() != null) {
            maxDurationDays = calculateDurationDays(currentCreatedTrip.getStartDate(), currentCreatedTrip.getEndDate());
        }

        if (newDayNum > maxDurationDays) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "⚠️ Đã đạt số ngày tối đa (" + maxDurationDays + " ngày)! Hãy chỉnh sửa thời gian chuyến đi để thêm ngày.", Toast.LENGTH_LONG).show();
            }
            return;
        }

        popupSelectedDay = newDayNum;
        String tripId = currentCreatedTrip != null ? currentCreatedTrip.getId() : "1";

        ItineraryItem newDayItem = new ItineraryItem(
                String.valueOf(System.currentTimeMillis()),
                tripId,
                newDayNum,
                "08:30:00",
                "Hoạt động sáng Ngày " + newDayNum,
                "Điểm đến Ngày " + newDayNum,
                0.0, 0.0, "p_day",
                "Tự do tham quan"
        );
        allPopupItineraries.add(newDayItem);

        setupDynamicPopupDayChips();
        filterPopupItinerariesByDay(newDayNum);
        if (getContext() != null) {
            Toast.makeText(getContext(), "+ Đã khởi tạo lịch trình cho Ngày " + newDayNum, Toast.LENGTH_SHORT).show();
        }
    }

    private void filterPopupItinerariesByDay(int dayNum) {
        popupSelectedDay = dayNum;
        displayedPopupItineraries.clear();
        for (ItineraryItem item : allPopupItineraries) {
            if (item.getDayNumber() != null && item.getDayNumber() == dayNum) {
                displayedPopupItineraries.add(item);
            }
        }
        if (displayedPopupItineraries.isEmpty()) {
            String tripId = currentCreatedTrip != null ? currentCreatedTrip.getId() : "1";
            ItineraryItem placeholder = new ItineraryItem(
                    String.valueOf(System.currentTimeMillis()),
                    tripId,
                    dayNum,
                    "08:30:00",
                    "Hoạt động sáng Ngày " + dayNum,
                    "Điểm đến Ngày " + dayNum,
                    0.0, 0.0, "p_day",
                    "Tự do tham quan"
            );
            allPopupItineraries.add(placeholder);
            displayedPopupItineraries.add(placeholder);
        }
        if (popupItineraryAdapter != null) {
            popupItineraryAdapter.notifyDataSetChanged();
        }
    }

    private void loadPopupItineraries(String tripId) {
        ApiClient.getService().getItineraries(tripId).enqueue(new Callback<List<ItineraryItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<ItineraryItem>> call, @NonNull Response<List<ItineraryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allPopupItineraries.clear();
                    allPopupItineraries.addAll(response.body());
                    setupDynamicPopupDayChips();
                    filterPopupItinerariesByDay(popupSelectedDay);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ItineraryItem>> call, @NonNull Throwable t) {
            }
        });
    }

    @Override
    public void onItemClick(ItineraryItem item, int position) {
        showEditItineraryDialogPopup(item, position);
    }

    private void showTimePickerDialog(Context context, EditText edtTime) {
        if (context == null || edtTime == null) return;
        int hour = 8;
        int minute = 0;
        String currentText = edtTime.getText().toString().trim();
        if (!currentText.isEmpty()) {
            try {
                String[] parts = currentText.split(":");
                if (parts.length >= 2) {
                    hour = Integer.parseInt(parts[0]);
                    minute = Integer.parseInt(parts[1]);
                }
            } catch (Exception ignored) {}
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                (view, hourOfDay, selectedMinute) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d:00", hourOfDay, selectedMinute);
                    edtTime.setText(formattedTime);
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }

    private String normalizeTimeFormat(String time) {
        if (time == null || time.trim().isEmpty()) {
            return "09:00:00";
        }
        String trimmed = time.trim();
        try {
            String[] parts = trimmed.split(":");
            if (parts.length == 2) {
                int hour = Integer.parseInt(parts[0]);
                int min = Integer.parseInt(parts[1]);
                return String.format(Locale.getDefault(), "%02d:%02d:00", hour, min);
            } else if (parts.length >= 3) {
                int hour = Integer.parseInt(parts[0]);
                int min = Integer.parseInt(parts[1]);
                int sec = Integer.parseInt(parts[2]);
                return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, min, sec);
            }
        } catch (Exception ignored) {}
        return trimmed;
    }

    private String parseErrorMessage(Response<?> response) {
        if (response == null || response.errorBody() == null) {
            return "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau!";
        }
        try (ResponseBody errorBody = response.errorBody()) {
            String errorJson = errorBody.string();
            JSONObject jsonObject = new JSONObject(errorJson);
            if (jsonObject.has("message")) {
                return jsonObject.getString("message");
            }
        } catch (Exception ignored) {}
        return "Đã xảy ra lỗi hệ thống (mã " + response.code() + "). Vui lòng thử lại sau!";
    }

    private void handleAddPopupItineraryError(Response<?> response, ItineraryItem newItem) {
        if (getContext() == null) return;
        String errorMsg = parseErrorMessage(response);
        Toast.makeText(getContext(), "⚠️ " + errorMsg, Toast.LENGTH_LONG).show();
        if (newItem.getId() == null) {
            newItem.setId(String.valueOf(System.currentTimeMillis()));
        }
        allPopupItineraries.add(newItem);
        setupDynamicPopupDayChips();
        filterPopupItinerariesByDay(popupSelectedDay);
    }

    private void handleAddPopupItineraryFallbackLocally(ItineraryItem newItem) {
        if (getContext() == null) return;
        Toast.makeText(getContext(), "⚠️ Không thể kết nối máy chủ. Đã lưu lịch trình tạm thời!", Toast.LENGTH_SHORT).show();
        if (newItem.getId() == null) {
            newItem.setId(String.valueOf(System.currentTimeMillis()));
        }
        allPopupItineraries.add(newItem);
        setupDynamicPopupDayChips();
        filterPopupItinerariesByDay(popupSelectedDay);
    }

    private void showEditItineraryDialogPopup(ItineraryItem item, int position) {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_itinerary, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogItineraryTitle);
        EditText edtTime = dialogView.findViewById(R.id.edtDialogTime);
        EditText edtTitle = dialogView.findViewById(R.id.edtDialogActivityTitle);
        EditText edtLocation = dialogView.findViewById(R.id.edtDialogLocation);
        EditText edtNote = dialogView.findViewById(R.id.edtDialogNote);
        Button btnDelete = dialogView.findViewById(R.id.btnDialogDelete);
        Button btnSave = dialogView.findViewById(R.id.btnDialogSave);

        if (tvTitle != null) tvTitle.setText("✎ Chỉnh Sửa Mốc Lịch Trình (Ngày " + item.getDayNumber() + ")");
        if (edtTime != null) {
            edtTime.setText(item.getActivityTime());
            edtTime.setFocusable(false);
            edtTime.setClickable(true);
            edtTime.setOnClickListener(v -> showTimePickerDialog(getContext(), edtTime));
        }
        if (edtTitle != null) edtTitle.setText(item.getActivityName());
        if (edtLocation != null) {
            edtLocation.setText(item.getLocationName());
            edtLocation.setFocusable(false);
            edtLocation.setClickable(true);
            edtLocation.setOnClickListener(v -> {
                targetItineraryLocationEditText = edtLocation;
                Intent intent = new Intent(getContext(), SearchLocationActivity.class);
                itineraryLocationSearchLauncher.launch(intent);
            });
        }
        if (edtNote != null) edtNote.setText(item.getNote());

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                item.setActivityTime(normalizeTimeFormat(edtTime.getText().toString().trim()));
                item.setActivityName(edtTitle.getText().toString().trim());
                item.setLocationName(edtLocation.getText().toString().trim());
                item.setNote(edtNote.getText().toString().trim());

                if (item.getId() != null) {
                    ApiClient.getService().updateItineraryItem(item.getId(), item).enqueue(new Callback<ItineraryItem>() {
                        @Override
                        public void onResponse(@NonNull Call<ItineraryItem> call, @NonNull Response<ItineraryItem> response) {
                            if (popupItineraryAdapter != null) {
                                popupItineraryAdapter.notifyItemChanged(position);
                            }
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Đã cập nhật mốc lịch trình!", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMsg = parseErrorMessage(response);
                                Toast.makeText(getContext(), "⚠️ " + errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ItineraryItem> call, @NonNull Throwable t) {
                            if (popupItineraryAdapter != null) {
                                popupItineraryAdapter.notifyItemChanged(position);
                            }
                            Toast.makeText(getContext(), "Lỗi cập nhật lịch trình! Đã lưu tạm thời.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    if (popupItineraryAdapter != null) {
                        popupItineraryAdapter.notifyItemChanged(position);
                    }
                    Toast.makeText(getContext(), "Đã cập nhật mốc lịch trình!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            });
        }

        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                if (position >= 0 && position < displayedPopupItineraries.size()) {
                    ItineraryItem removed = displayedPopupItineraries.remove(position);
                    allPopupItineraries.remove(removed);
                    if (popupItineraryAdapter != null) {
                        popupItineraryAdapter.notifyItemRemoved(position);
                    }
                    if (removed.getId() != null) {
                        ApiClient.getService().deleteItineraryItem(removed.getId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                Toast.makeText(getContext(), "Đã xóa mốc lịch trình!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {}
                        });
                    } else {
                        Toast.makeText(getContext(), "Đã xóa mốc lịch trình!", Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void showAddNewItineraryDialogPopup(String tripId) {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_itinerary, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogItineraryTitle);
        EditText edtTime = dialogView.findViewById(R.id.edtDialogTime);
        EditText edtTitle = dialogView.findViewById(R.id.edtDialogActivityTitle);
        EditText edtLocation = dialogView.findViewById(R.id.edtDialogLocation);
        EditText edtNote = dialogView.findViewById(R.id.edtDialogNote);
        Button btnDelete = dialogView.findViewById(R.id.btnDialogDelete);
        Button btnSave = dialogView.findViewById(R.id.btnDialogSave);

        if (tvTitle != null) tvTitle.setText("+ Thêm Mốc Lịch Trình (Ngày " + popupSelectedDay + ")");
        if (btnDelete != null) btnDelete.setVisibility(View.GONE);
        if (btnSave != null) btnSave.setText("Thêm mốc mới");

        if (edtTime != null) {
            edtTime.setFocusable(false);
            edtTime.setClickable(true);
            edtTime.setOnClickListener(v -> showTimePickerDialog(getContext(), edtTime));
        }

        if (edtLocation != null) {
            edtLocation.setFocusable(false);
            edtLocation.setClickable(true);
            edtLocation.setOnClickListener(v -> {
                targetItineraryLocationEditText = edtLocation;
                Intent intent = new Intent(getContext(), SearchLocationActivity.class);
                itineraryLocationSearchLauncher.launch(intent);
            });
        }

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                String time = edtTime.getText().toString().trim();
                String title = edtTitle.getText().toString().trim();
                String loc = edtLocation.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                String formattedTime = normalizeTimeFormat(time);

                ItineraryItem newItem = new ItineraryItem(
                        null,
                        tripId,
                        popupSelectedDay,
                        formattedTime,
                        title.isEmpty() ? "Hoạt động tự chọn" : title,
                        loc,
                        0.0, 0.0, "p_new",
                        note
                );

                ApiClient.getService().addItineraryItem(newItem).enqueue(new Callback<ItineraryItem>() {
                    @Override
                    public void onResponse(@NonNull Call<ItineraryItem> call, @NonNull Response<ItineraryItem> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            allPopupItineraries.add(response.body());
                            setupDynamicPopupDayChips();
                            filterPopupItinerariesByDay(popupSelectedDay);
                            Toast.makeText(getContext(), "+ Thêm mốc lịch trình Ngày " + popupSelectedDay + " thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            ApiClient.getService().addItineraryItemToTrip(tripId, newItem).enqueue(new Callback<ItineraryItem>() {
                                @Override
                                public void onResponse(@NonNull Call<ItineraryItem> call2, @NonNull Response<ItineraryItem> response2) {
                                    if (response2.isSuccessful() && response2.body() != null) {
                                        allPopupItineraries.add(response2.body());
                                        setupDynamicPopupDayChips();
                                        filterPopupItinerariesByDay(popupSelectedDay);
                                        Toast.makeText(getContext(), "+ Thêm mốc lịch trình Ngày " + popupSelectedDay + " thành công!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        handleAddPopupItineraryError(response, newItem);
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<ItineraryItem> call2, @NonNull Throwable t2) {
                                    handleAddPopupItineraryError(response, newItem);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ItineraryItem> call, @NonNull Throwable t) {
                        ApiClient.getService().addItineraryItemToTrip(tripId, newItem).enqueue(new Callback<ItineraryItem>() {
                            @Override
                            public void onResponse(@NonNull Call<ItineraryItem> call2, @NonNull Response<ItineraryItem> response2) {
                                if (response2.isSuccessful() && response2.body() != null) {
                                    allPopupItineraries.add(response2.body());
                                    setupDynamicPopupDayChips();
                                    filterPopupItinerariesByDay(popupSelectedDay);
                                    Toast.makeText(getContext(), "+ Thêm mốc lịch trình Ngày " + popupSelectedDay + " thành công!", Toast.LENGTH_SHORT).show();
                                } else {
                                    handleAddPopupItineraryFallbackLocally(newItem);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ItineraryItem> call2, @NonNull Throwable t2) {
                                handleAddPopupItineraryFallbackLocally(newItem);
                            }
                        });
                    }
                });

                dialog.dismiss();
            });
        }

        dialog.show();
    }
}
