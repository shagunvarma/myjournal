package com.example.mentalhealth;

public class JournalEntry {
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
}
