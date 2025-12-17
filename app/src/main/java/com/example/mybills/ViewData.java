package com.example.mybills;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

public class ViewData extends AppCompatActivity {

    ListView listView;
    DBHelper db;
    ArrayList<Integer> idList;
    ArrayList<String> monthList, amountList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        listView = findViewById(R.id.listView);
        db = new DBHelper(this);

        loadData();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(ViewData.this, DetailActivity.class);
            i.putExtra("ID", idList.get(position));
            startActivityForResult(i, 1);
        });
    }

    private void loadData() {
        idList = new ArrayList<>();
        monthList = new ArrayList<>();
        amountList = new ArrayList<>();

        Cursor c = db.getAllBills();
        while (c.moveToNext()) {
            idList.add(c.getInt(0));
            monthList.add(c.getString(1));
            amountList.add("RM " + String.format("%.2f", c.getDouble(5)));
        }

        BillAdapter adapter = new BillAdapter();
        listView.setAdapter(adapter);
    }

    class BillAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return monthList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(ViewData.this)
                        .inflate(R.layout.item_bill, parent, false);
            }

            TextView tvMonth = convertView.findViewById(R.id.tvMonth);
            TextView tvAmount = convertView.findViewById(R.id.tvAmount);

            tvMonth.setText(monthList.get(position));
            tvAmount.setText(amountList.get(position));

            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadData();
        }
    }
}
