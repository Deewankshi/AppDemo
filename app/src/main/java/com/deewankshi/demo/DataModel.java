package com.deewankshi.demo;

/**
 * Created by Dell on 12/11/2017.
 */

public class DataModel {
    public String title;
    public String description;
    public Integer id;
    public String timeframe;

    // Empty constructor
    public DataModel(){

    }

    // constructor
    public DataModel(int id, String name, String description, String timeframe){
        this.id = id;
        this.title = name;
        this.description = description;
        this.timeframe = timeframe;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }
}
