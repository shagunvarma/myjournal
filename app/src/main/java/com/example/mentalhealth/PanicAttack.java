package com.example.mentalhealth;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomappbar.BottomAppBar;

public class PanicAttack extends AppCompatActivity {
    private int mCounter = 0;
    Button counterButton;
    TextView descriptionText;
    TextView counterNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_attack);
        BottomAppBar menu = findViewById(R.id.bottomAppBar);
        setSupportActionBar(menu);
        counterButton = findViewById(R.id.counter);
        counterNum = findViewById(R.id.number);
        descriptionText = findViewById(R.id.description);

        counterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCounter++;
                counterNum.setText(Integer.toString(mCounter));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.JournalNavButton) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("NeedFinger", false);
            startActivity(intent);
            finish();
        }
        if (id == R.id.CalendarNavButton) {
            Intent intent = new Intent(this, CalendarActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
