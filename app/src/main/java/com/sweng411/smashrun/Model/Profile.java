package com.sweng411.smashrun.Model;

public class Profile {
    public String fName;
    public String lName;
    public String dateLastRunUTC;
    public String dateJoinedUTC;

    public Profile(String fName, String lName, String dateLastRunUTC, String dateJoinedUTC){
        this.fName = fName;
        this.lName = lName;
        this.dateLastRunUTC = dateLastRunUTC;
        this.dateJoinedUTC = dateJoinedUTC;
    }
    public Profile(){
    }

}
