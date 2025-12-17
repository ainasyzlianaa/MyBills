package com.example.mybills;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.*;
import android.content.Intent;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    EditText etUnit, etRebate;
    Spinner spMonth;
    TextView tvResult;
    Button btnCalculate, btnViewBills;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUnit = findViewById(R.id.etUnit);
        etRebate = findViewById(R.id.etRebate);
        spMonth = findViewById(R.id.spMonth);
        tvResult = findViewById(R.id.tvResult);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnViewBills = findViewById(R.id.btnViewBills);

        db = new DBHelper(this);

        // Spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.months,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMonth.setAdapter(adapter);

        btnCalculate.setOnClickListener(v -> calculateBill());

        btnViewBills.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewData.class);
            startActivity(intent);
        });
    }

    private void calculateBill() {

        // Clear previous errors
        etUnit.setError(null);
        etRebate.setError(null);

        // Month validation
        if (spMonth.getSelectedItemPosition() == 0) {
            TextView errorText = (TextView) spMonth.getSelectedView();
            if (errorText != null) {
                errorText.setError("Month is required");
                errorText.setTextColor(
                        getResources().getColor(android.R.color.holo_red_dark)
                );
                errorText.setText("Please select a month");
            }
            spMonth.requestFocus();
            return;
        }

        // Unit empty
        if (etUnit.getText().toString().trim().isEmpty()) {
            etUnit.setError("Electricity unit is required");
            etUnit.requestFocus();
            return;
        }

        // Rebate empty
        if (etRebate.getText().toString().trim().isEmpty()) {
            etRebate.setError("Rebate percentage is required (0–5)");
            etRebate.requestFocus();
            return;
        }

        int unit;
        int rebatePercent;

        try {
            unit = Integer.parseInt(etUnit.getText().toString());
            rebatePercent = Integer.parseInt(etRebate.getText().toString());
        } catch (NumberFormatException e) {
            etUnit.setError("Invalid number format");
            etUnit.requestFocus();
            return;
        }

        // Unit <= 0
        if (unit <= 0) {
            etUnit.setError("Electricity unit must be greater than 0");
            etUnit.requestFocus();
            return;
        }

        // Rebate range
        if (rebatePercent < 0 || rebatePercent > 5) {
            etRebate.setError("Rebate must be between 0 and 5 only");
            etRebate.requestFocus();
            return;
        }

        // Calculation
        double totalCharge = calculateCharge(unit);
        double rebate = rebatePercent / 100.0;
        double finalCost = totalCharge - (totalCharge * rebate);

        tvResult.setText(
                "Total Charge: RM " + String.format("%.2f", totalCharge) +
                        "\nFinal Cost: RM " + String.format("%.2f", finalCost)
        );

        // Save to database
        db.insertBill(
                spMonth.getSelectedItem().toString(),
                unit,
                totalCharge,
                rebatePercent,
                finalCost
        );

        // Success message
        Snackbar snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                "✔ Bill calculated and saved successfully",
                Snackbar.LENGTH_SHORT
        );
        snackbar.setBackgroundTint(
                getResources().getColor(android.R.color.holo_green_dark)
        );
        snackbar.setTextColor(
                getResources().getColor(android.R.color.white)
        );
        snackbar.show();
    }

    // Block calculation
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
