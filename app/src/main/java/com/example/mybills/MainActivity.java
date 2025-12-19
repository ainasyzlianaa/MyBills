package com.example.mybills;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    EditText etUnit;
    Spinner spMonth;
    RadioGroup rgRebate;
    Button btnCalculate, btnViewBills;
    LinearLayout layoutResult;
    TextView tvTotalCharge, tvFinalCost;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUnit = findViewById(R.id.etUnit);
        spMonth = findViewById(R.id.spMonth);
        rgRebate = findViewById(R.id.rgRebate);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnViewBills = findViewById(R.id.btnViewBills);
        layoutResult = findViewById(R.id.layoutResult);
        tvTotalCharge = findViewById(R.id.tvTotalCharge);
        tvFinalCost = findViewById(R.id.tvFinalCost);

        db = new DBHelper(this);

        // Spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.months,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spMonth.setAdapter(adapter);

        btnCalculate.setOnClickListener(v -> calculateBill());

        btnViewBills.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ViewData.class))
        );
    }

    private void calculateBill() {

        etUnit.setError(null);

        // Month validation
        if (spMonth.getSelectedItemPosition() == 0) {
            TextView errorText = (TextView) spMonth.getSelectedView();
            if (errorText != null) {
                errorText.setError("");
                errorText.setTextColor(Color.RED);
                errorText.setText("Please select a month");
            }
            return;
        }

        // Unit empty
        if (etUnit.getText().toString().trim().isEmpty()) {
            etUnit.setError("Electricity unit is required");
            return;
        }

        int unit;
        try {
            unit = Integer.parseInt(etUnit.getText().toString());
        } catch (NumberFormatException e) {
            etUnit.setError("Invalid number");
            return;
        }

        // Unit range
        if (unit < 1 || unit > 1000) {
            etUnit.setError("Electricity unit must be between 1 and 1000 kWh");
            return;
        }

        // Rebate selected
        if (rgRebate.getCheckedRadioButtonId() == -1) {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    "Please select rebate value",
                    Snackbar.LENGTH_SHORT
            ).show();
            return;
        }

        RadioButton rb =
                findViewById(rgRebate.getCheckedRadioButtonId());
        int rebatePercent = Integer.parseInt(rb.getText().toString());

        // Calculation
        double totalCharge = calculateCharge(unit);
        double finalCost =
                totalCharge - (totalCharge * (rebatePercent / 100.0));

        // Show result
        layoutResult.setVisibility(View.VISIBLE);
        tvTotalCharge.setText(
                "Total Charge: RM " + String.format("%.2f", totalCharge)
        );
        tvFinalCost.setText(
                "Final Cost: RM " + String.format("%.2f", finalCost)
        );

        // Save to DB
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
                getResources().getColor(R.color.green_save)
        );
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
    }

    // Calculation
    private double calculateCharge(int unit) {
        if (unit <= 200)
            return unit * 0.218;
        else if (unit <= 300)
            return 200 * 0.218 + (unit - 200) * 0.334;
        else if (unit <= 600)
            return 200 * 0.218 + 100 * 0.334 + (unit - 300) * 0.516;
        else
            return 200 * 0.218 + 100 * 0.334
                    + 300 * 0.516 + (unit - 600) * 0.546;
    }
}