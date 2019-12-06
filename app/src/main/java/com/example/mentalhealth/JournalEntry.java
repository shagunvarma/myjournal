package com.example.mentalhealth;

import android.os.Parcel;
import android.os.Parcelable;

public class JournalEntry implements Parcelable {
    private String date;
    private float stars;
    private String firstPos;
    private String secondPos;
    private String thirdPos;
    private String extraInfo;

    public JournalEntry(String givenDate, float givenStars, String givenFirstPos, String givenSecondPos,
                        String givenThirdPos, String givenExtra) {
        date = givenDate;
        stars = givenStars;
        firstPos = givenFirstPos;
        secondPos = givenSecondPos;
        thirdPos = givenThirdPos;
        extraInfo = givenExtra;
    }

    protected JournalEntry(Parcel in) {
        date = in.readString();
        stars = in.readFloat();
        firstPos = in.readString();
        secondPos = in.readString();
        thirdPos = in.readString();
        extraInfo = in.readString();
    }

    public static final Creator<JournalEntry> CREATOR = new Creator<JournalEntry>() {
        @Override
        public JournalEntry createFromParcel(Parcel in) {
            return new JournalEntry(in);
        }
        @Override
        public JournalEntry[] newArray(int size) {
            return new JournalEntry[size];
        }
    };

    public String getDate() {
        return date;
    }

    public float getStars() {
        return stars;
    }

    public String getFirstPos() {
        return  firstPos;
    }

    public String getSecondPos() {
        return  secondPos;
    }

    public String getThirdPos() {
        return  thirdPos;
    }

    public String getExtraInfo() {
        return  extraInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeFloat(stars);
        parcel.writeString(firstPos);
        parcel.writeString(secondPos);
        parcel.writeString(thirdPos);
        parcel.writeString(extraInfo);
    }

    public String getWritable() {
        String sendoff = "\nStartJournalEntry/" + ("\n" + date) + ("\n" + stars) + ("\n" + firstPos) + ("\n" + secondPos);
        sendoff = sendoff + ("\n" + thirdPos) + "\nStartExtraInfo/" + ("\n" + extraInfo)+ "\n/EndExtraInfo" + "\n/EndJournalEntry";
        return  sendoff;
    }

    public String[] getPdfArray() {
        String[] sendoff = new String[6];
        sendoff[0] = date;
        sendoff[1] = Float.toString(stars);
        sendoff[2] = firstPos;
        sendoff[3] = secondPos;
        sendoff[4] = thirdPos;
        sendoff[5] = extraInfo;
        return  sendoff;
    }
}
