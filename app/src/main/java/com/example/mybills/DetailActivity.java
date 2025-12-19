package com.example.mybills;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import android.os.Handler;
import android.view.View;
import android.graphics.Color;

import com.google.android.material.snackbar.Snackbar;

public class DetailActivity extends AppCompatActivity {

    TextView tvMonth, tvUnit, tvRebate, tvResult;
    Button btnEdit, btnDelete;
    DBHelper db;

    int billId;
    int currentUnit, currentRebate;
    String month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvMonth = findViewById(R.id.tvMonth);
        tvUnit = findViewById(R.id.tvUnit);
        tvRebate = findViewById(R.id.tvRebate);
        tvResult = findViewById(R.id.tvResult);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        db = new DBHelper(this);
        billId = getIntent().getIntExtra("ID", -1);

        loadBillDetails();

        btnEdit.setOnClickListener(v -> showEditDialog());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void loadBillDetails() {
        Cursor c = db.getBillById(billId);

        if (c != null && c.moveToFirst()) {

            month = c.getString(1);
            currentUnit = c.getInt(2);
            double total = c.getDouble(3);
            currentRebate = (int) c.getDouble(4);
            double finalCost = c.getDouble(5);

            tvMonth.setText("Month: " + month);
            tvUnit.setText(String.valueOf(currentUnit));
            tvRebate.setText(String.valueOf(currentRebate));
            tvResult.setText(
                    "Total Charge: RM " + String.format("%.2f", total) +
                            "\nFinal Cost: RM " + String.format("%.2f", finalCost)
            );

            c.close();
        }
    }

    // Edit bill
    private void showEditDialog() {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        TextView tvUnitLabel = new TextView(this);
        tvUnitLabel.setText("Electricity Unit (kWh)");

        EditText etUnit = new EditText(this);
        etUnit.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etUnit.setText(String.valueOf(currentUnit));

        TextView tvRebateLabel = new TextView(this);
        tvRebateLabel.setText("Rebate (%)");

        RadioGroup rgRebate = new RadioGroup(this);
        rgRebate.setOrientation(RadioGroup.HORIZONTAL);

        for (int i = 0; i <= 5; i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(String.valueOf(i));
            rb.setId(View.generateViewId());

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
            params.setMargins(0, 0, 16, 0);
            rb.setLayoutParams(params);

            if (i == currentRebate) rb.setChecked(true);
            rgRebate.addView(rb);
        }

        layout.addView(tvUnitLabel);
        layout.addView(etUnit);
        layout.addView(tvRebateLabel);
        layout.addView(rgRebate);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Bill")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            btnSave.setOnClickListener(v -> {

                etUnit.setError(null);

                if (etUnit.getText().toString().trim().isEmpty()) {
                    etUnit.setError("Electricity unit is required");
                    etUnit.requestFocus();
                    return;
                }

                int newUnit;
                try {
                    newUnit = Integer.parseInt(etUnit.getText().toString());
                } catch (NumberFormatException e) {
                    etUnit.setError("Invalid number");
                    etUnit.requestFocus();
                    return;
                }

                if (newUnit < 1 || newUnit > 1000) {
                    etUnit.setError("Electricity unit must be between 1 and 1000 kWh");
                    etUnit.requestFocus();
                    return;
                }

                if (rgRebate.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(this, "Please select rebate value", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedRb =
                        rgRebate.findViewById(rgRebate.getCheckedRadioButtonId());

                int newRebate = Integer.parseInt(selectedRb.getText().toString());
                double newTotal = calculateCharge(newUnit);
                double newFinal = newTotal - (newTotal * newRebate / 100.0);

                int result = db.updateBill(
                        billId,
                        newUnit,
                        newRebate,
                        newTotal,
                        newFinal
                );

                if (result > 0) {
                    Snackbar snackbar = Snackbar.make(
                            findViewById(android.R.id.content),
                            "✔ Bill updated successfully",
                            Snackbar.LENGTH_SHORT
                    );
                    snackbar.setBackgroundTint(getResources().getColor(R.color.green_save));
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();

                    loadBillDetails();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    // Delete bill
    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Bill")
                .setMessage("Are you sure you want to delete this bill?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (d, w) -> deleteBill())
                .show();
    }

    private void deleteBill() {
        int result = db.deleteBill(billId);

        if (result > 0) {

            Snackbar snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "✔ Bill deleted successfully",
                    Snackbar.LENGTH_SHORT
            );
            snackbar.setBackgroundTint(
                    getResources().getColor(R.color.green_save)
            );
            snackbar.setTextColor(Color.WHITE);
            snackbar.show();

            // Close detail page after delete
            new Handler().postDelayed(() -> {
                setResult(RESULT_OK);
                finish();
            }, 1000);
        }
    }


    private double calculateCharge(int unit) {
        if (unit <= 200)
            return unit * 0.218;
        else if (unit <= 300)
            return 200 * 0.218 + (unit - 200) * 0.334;
        else if (unit <= 600)
            return 200 * 0.218 + 100 * 0.334 + (unit - 300) * 0.516;
        else
            return 200 * 0.218 + 100 * 0.334 + 300 * 0.516 + (unit - 600) * 0.546;
    }
}
