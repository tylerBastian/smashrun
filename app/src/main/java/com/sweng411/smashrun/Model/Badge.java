package com.sweng411.smashrun.Model;

public class Badge {

    public int id;
    public String name;
    public String badgeSet;
    public String image;
    public String imageSmall;
    public String requirement;
    public String dateEarnedUTC;
    public int badgeOrder;

    public Badge(int id, String name, String badgeSet, String image, String imageSmall, String requirement, String dateEarnedUTC, int badgeOrder) {
        this.id = id;
        this.name = name;
        this.badgeSet = badgeSet;
        this.image = image;
        this.imageSmall = imageSmall;
        this.requirement = requirement;
        this.dateEarnedUTC = dateEarnedUTC;
        this.badgeOrder = badgeOrder;
    }

    public Badge() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBadgeSet() {
        return badgeSet;
    }

    public void setBadgeSet(String badgeSet) {
        this.badgeSet = badgeSet;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageSmall() {
        return imageSmall;
    }

    public void setImageSmall(String imageSmall) {
        this.imageSmall = imageSmall;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getDateEarnedUTC() {
        return dateEarnedUTC;
    }

    public void setDateEarnedUTC(String dateEarnedUTC) {
        this.dateEarnedUTC = dateEarnedUTC;
    }

    public int getBadgeOrder() {
        return badgeOrder;
    }

    public void setBadgeOrder(int badgeOrder) {
        this.badgeOrder = badgeOrder;
    }


}
