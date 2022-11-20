package com.sweng411.smashrun;

public class Runs {

    private final String date;
    private final String distance;
    private final String duration;
    private final String pace;
    private final String calories;


    public Runs(String date, String distance, String pace, String speed, String duration) {
        this.date = date;
        this.distance = distance;
        this.duration = duration;
        this.pace = pace;
        this.calories = speed;
    }

    public String getDate() {
        return date;
    }

    public String getDistance() {
        return distance;
    }

    public String getPace() {
        return pace;
    }

    public String getCalories() {
        return calories;
    }

    public String getDuration() {
        return duration;
    }

}
