package com.example.mentalhealth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.bottomappbar.BottomAppBar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {

    private String fileStorageName = "userJournalStorage";
    private List<JournalEntry> journals = new ArrayList<>();
    private ProgressDialog DisplayLoading;
    private Map<String, ArrayList<JournalEntry>> DateToJournalList = new HashMap<>();
    private int newJournalScreen = 22;
    private  List<View> views = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        BottomAppBar menu = findViewById(R.id.bottomAppBar);
        setSupportActionBar(menu);
        Calendar calendar = Calendar.getInstance();
        //https://github.com/Applandeo/Material-Calendar-View
        //http://applandeo.com/blog/material-calendar-view-customized-calendar-widget-android/
        //https://medium.com/@Patel_Prashant_/android-custom-calendar-with-events-fa48dfca8257
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
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.JournalNavButton) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("NeedFinger", false);
            startActivity(intent);
            finish();
        }
        if (id == R.id.PanicNavButton) {
            Intent intent = new Intent(this, PanicAttack.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.CalendarNavButton) {
            readJournalFileIntoArray();
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleDayClick(Calendar given) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        format.setTimeZone(given.getTimeZone());
        String date = format.format(given.getTime());
        if (DateToJournalList.containsKey(date) == true) {
            ArrayList<JournalEntry> holding =  DateToJournalList.get(date);
            createCardView(holding);
        }
    }

    private void createCardView(ArrayList<JournalEntry> given) {
        LinearLayout parent = findViewById(R.id.JournalEntriesDisplayCal);
        clearViewsNotCal();
        for (int i = given.size() - 1; i >= 0; i--) {
            final JournalEntry entry = given.get(i);
            final int index = journals.indexOf(entry);;
            View chunk = getLayoutInflater().inflate(R.layout.chunk_mini_journal_entry, parent, false);
            TextView date = chunk.findViewById(R.id.DateField);
            date.setText(entry.getDate());
            RatingBar rate = chunk.findViewById(R.id.OldRating);
            rate.setRating(entry.getStars());
            TextView first = chunk.findViewById(R.id.FirstPos);
            first.setText(entry.getFirstPos());
            TextView second = chunk.findViewById(R.id.SecondPos);
            second.setText(entry.getSecondPos());
            TextView third = chunk.findViewById(R.id.ThirdPos);
            third.setText(entry.getThirdPos());
            Button view = chunk.findViewById(R.id.ViewButton);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CalendarActivity.this, NewJournalEntry.class);
                    intent.putExtra("JournalEntry", entry);
                    intent.putExtra("index", index);
                    intent.putExtra("From", "Calendar");
                    startActivityForResult(intent, newJournalScreen);
                }
            });
            views.add(chunk);
            parent.addView(chunk);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == newJournalScreen && resultCode == RESULT_OK) {
            String type = data.getStringExtra("TypeOfResponse");
            if (type != null) {
                if (type.equals("add")) {
                    JournalEntry entry = data.getParcelableExtra("JournalEntry");
                    if (entry != null) {
                        journals.add(entry);
                    }
                } else if (type.equals("edit")) {
                    int index = data.getIntExtra("index", -1);
                    if (index != -1) {
                        JournalEntry entry = data.getParcelableExtra("JournalEntry");
                        if (entry != null) {
                            journals.set(index, entry);
                            // re-write file
                            try {
                                FileOutputStream file = openFileOutput(fileStorageName, Context.MODE_PRIVATE);
                                for (int i = 0; i < journals.size(); i++) {
                                    JournalEntry journal = journals.get(i);
                                    file.write(journal.getWritable().getBytes());
                                }
                                file.close();
                            } catch(Exception e){

                            }

                        }
                    }
                } else if (type.equals("delete")) {
                    int index = data.getIntExtra("index", -1);
                    if (index != -1) {
                        journals.remove(index);
                        try {
                            FileOutputStream file = openFileOutput(fileStorageName, Context.MODE_PRIVATE);
                            for (int i = 0; i < journals.size(); i++) {
                                JournalEntry journal = journals.get(i);
                                file.write(journal.getWritable().getBytes());
                            }
                            file.close();
                        } catch(Exception e){

                        }
                    }
                }
                reloadMap();
                clearViewsNotCal();
                createCalendarEvents(false);
            }
        }
    }

    private void clearViewsNotCal() {
        LinearLayout parent = findViewById(R.id.JournalEntriesDisplayCal);
        for (int i = 0; i < views.size(); i++) {
            parent.removeView(views.get(i));
        }
        views.clear();
    }

    private  void reloadMap() {
        DateToJournalList.clear();
        for (int i = 0; i < journals.size(); i++) {
            JournalEntry newJournal = journals.get(i);
            String date = newJournal.getDate();
            if (DateToJournalList.containsKey(date) == false) {
                ArrayList<JournalEntry> addto = new ArrayList<>();
                addto.add(newJournal);
                DateToJournalList.put(date, addto);
            } else {
                ArrayList<JournalEntry> addto = DateToJournalList.get(date);
                addto.add(newJournal);
                DateToJournalList.put(date, addto);
            }
        }
    }

    private void readJournalFileIntoArray() {
        ReadJournalFile asyncTask = new ReadJournalFile();
        asyncTask.execute();
    }

    private void createCalendarEvents(boolean hide) {
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
        if (hide) {
            DisplayLoading.hide();
            DisplayLoading.dismiss();
        }
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
            createCalendarEvents(true);
            DisplayLoading.hide();
            DisplayLoading.dismiss();
        }
    }
}
