package com.example.mentalhealth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ActionMenuView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.bottomappbar.BottomAppBar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {

    private String fileStorageName = "userJournalStorage";
    private List<JournalEntry> journals = new ArrayList<>();
    private ProgressDialog DisplayLoading;
    private Map<String, ArrayList<JournalEntry>> DateToJournalList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        BottomAppBar menu = findViewById(R.id.bottomAppBar);
        setSupportActionBar(menu);
        Calendar calendar = Calendar.getInstance();
        CalendarView editCalendar = findViewById(R.id.calendarView);
        try {
            editCalendar.setDate(calendar);
        } catch (Exception e) {

        }
        editCalendar.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                handleDayClick(clickedDayCalendar);
            }
        });
        readJournalFileIntoArray();
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
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleDayClick(Calendar given) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        format.setTimeZone(given.getTimeZone());
        String date = format.format(given.getTime());
        if (DateToJournalList.containsKey(date) == true) {
            ArrayList<JournalEntry> holding =  DateToJournalList.get(date);
            System.out.println(holding.size());
        }
    }

    private void readJournalFileIntoArray() {
        ReadJournalFile asyncTask = new ReadJournalFile();
        asyncTask.execute();
    }

    private void createCalendarEvents() {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        List<EventDay> events = new ArrayList<>();
        for (int i = 0; i < journals.size(); i++) {
            JournalEntry entry = journals.get(i);
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(format.parse(entry.getDate()));
                events.add(new EventDay(cal, R.drawable.calendar_dots));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setEvents(events);
    }

    //https://www.tutorialspoint.com/android-asynctask-example-and-explanation
    private class ReadJournalFile extends AsyncTask<String, String, List<JournalEntry>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DisplayLoading = new ProgressDialog(CalendarActivity.this);
            DisplayLoading.setMessage("Please wait while your calendar loads.");
            DisplayLoading.setIndeterminate(false);
            DisplayLoading.setCancelable(false);
            DisplayLoading.show();
        }
        @Override
        protected List<JournalEntry> doInBackground(String[] strings) {
            journals.clear();
            //https://www.dev2qa.com/android-read-write-internal-storage-file-example/
            try {
                FileInputStream file = openFileInput(fileStorageName);
                InputStreamReader inputStreamReader = new InputStreamReader(file);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                String date = "";
                float stars = (float) 0.0;
                String firstPos = "";
                String secondPos = "";
                String thirdPos = "";
                String extraInfo = "";
                boolean firstLine = true;
                int count = -1;
                while((line=bufferedReader.readLine()) != null) {
                    if (line.equals("StartJournalEntry/")) {
                        date = "";
                        stars = (float) 0.0;
                        firstPos = "";
                        secondPos = "";
                        thirdPos = "";
                        extraInfo = "";
                        count = 0;
                        firstLine = true;
                    } else if (count == 0) {
                        date = line;
                        count++;
                    } else if (count == 1) {
                        stars = Float.parseFloat(line);
                        count++;
                    } else if (count == 2) {
                        firstPos = line;
                        count++;
                    } else if (count == 3) {
                        secondPos = line;
                        count++;
                    } else if (count == 4) {
                        thirdPos = line;
                        count++;
                    } else if (line.equals("StartExtraInfo/")) {
                        while ((line=bufferedReader.readLine()) != null && !line.equals("/EndExtraInfo")) {
                            if (firstLine) {
                                extraInfo = extraInfo + line;
                                firstLine = false;
                            } else {
                                extraInfo = extraInfo + "\n" + line;
                            }
                        }
                    } else if (line.equals("/EndJournalEntry")) {
                        JournalEntry newJournal = new JournalEntry(date, stars, firstPos, secondPos, thirdPos, extraInfo);
                        if (DateToJournalList.containsKey(date) == false) {
                            ArrayList<JournalEntry> addto =  new ArrayList<>();
                            addto.add(newJournal);
                            DateToJournalList.put(date, addto);
                        } else {
                            ArrayList<JournalEntry> addto =  DateToJournalList.get(date);
                            addto.add(newJournal);
                            DateToJournalList.put(date, addto);
                        }
                        journals.add(newJournal);
                        count = -1;
                    }
                }
                file.close();
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            return journals;
        }
        @Override
        protected void onPostExecute(List<JournalEntry> readJournals) {
            super.onPostExecute(readJournals);
            createCalendarEvents();
            DisplayLoading.hide();
            DisplayLoading.dismiss();
        }
    }
}
