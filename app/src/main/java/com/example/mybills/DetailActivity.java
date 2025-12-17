package com.example.mybills;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;

import com.google.android.material.snackbar.Snackbar;

public class DetailActivity extends AppCompatActivity {

    TextView tvMonth, tvUnit, tvRebate, tvResult;
    Button btnDelete;
    DBHelper db;
    int billId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvMonth = findViewById(R.id.tvMonth);
        tvUnit = findViewById(R.id.tvUnit);
        tvRebate = findViewById(R.id.tvRebate);
        tvResult = findViewById(R.id.tvResult);
        btnDelete = findViewById(R.id.btnDelete);

        db = new DBHelper(this);
        billId = getIntent().getIntExtra("ID", -1);

        loadBillDetails();

        btnDelete.setOnClickListener(v -> {

            // 1️⃣ Perform delete
            int result = db.deleteBill(billId);

            // 2️⃣ Check result BEFORE showing message
            if (result > 0) {
                // ✅ Delete successful
                Snackbar snackbar = Snackbar.make(
                        findViewById(android.R.id.content),
                        "✔ Bill deleted successfully",
                        Snackbar.LENGTH_SHORT
                );

                snackbar.setBackgroundTint(
                        getResources().getColor(android.R.color.holo_green_dark)
                );
                snackbar.setTextColor(
                        getResources().getColor(android.R.color.white)
                );
                snackbar.show();

                // 3️⃣ Delay exit so user sees success
                new Handler().postDelayed(() -> {
                    setResult(RESULT_OK);
                    finish();
                }, 1200);

            } else {
                // ❌ Delete failed (edge case, but professional)
                Snackbar snackbar = Snackbar.make(
                        findViewById(android.R.id.content),
                        "❌ Failed to delete bill",
                        Snackbar.LENGTH_SHORT
                );

                snackbar.setBackgroundTint(
                        getResources().getColor(android.R.color.holo_red_dark)
                );
                snackbar.setTextColor(
                        getResources().getColor(android.R.color.white)
                );
                snackbar.show();
            }
        });
    }

    private void loadBillDetails() {
        Cursor c = db.getBillById(billId);

        if (c != null && c.moveToFirst()) {

            tvMonth.setText("Month: " + c.getString(1));
            tvUnit.setText(String.valueOf(c.getInt(2)));
            tvRebate.setText(String.valueOf((int) c.getDouble(4)));

            tvResult.setText(
                    "Total Charge: RM " + String.format("%.2f", c.getDouble(3)) +
                            "\nFinal Cost: RM " + String.format("%.2f", c.getDouble(5))
            );

            c.close();
        }
    }
}
