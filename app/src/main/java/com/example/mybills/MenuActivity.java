package com.example.mybills;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

public class MenuActivity extends AppCompatActivity {

    ListView listView;

    String[] title = {
            "Calculate My Bills",
            "View My Bills",
            "About Page"
    };

    String[] subtitle = {
            "Calculate monthly electricity charges",
            "View or delete bills",
            "Application information"
    };

    Integer[] image = {
            R.drawable.calculate,
            R.drawable.bills,
            R.drawable.about
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        listView = findViewById(R.id.listViewMenu);

        MenuAdapter adapter = new MenuAdapter(this, title, subtitle, image);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent;
            if (position == 0)
                intent = new Intent(this, MainActivity.class);
            else if (position == 1)
                intent = new Intent(this, ViewData.class);
            else
                intent = new Intent(this, AboutActivity.class);

            startActivity(intent);
        });
    }
}
