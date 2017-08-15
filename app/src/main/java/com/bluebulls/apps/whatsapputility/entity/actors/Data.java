package com.bluebulls.apps.whatsapputility.entity.actors;


/**
 * Created by dell on 7/22/2017.
 */

public class Data {
    String event,description;
    int date,year;
    int hour,minute;

    String title,month;
    Option optionData1,optionData2,optionData3,optionData4,optionData5,optionData6;
    Integer number;
    boolean answered = false;
    private Poll poll;

    public Data(String event, String description, int date, String month, int year) {
        this.event = event;
        this.description = description;
        this.date = date;
        this.month = month;
        this.year = year;
    }

    public Data(String event, String description, int date, String month, int year, int hour, int minute) {
        this.event = event;
        this.description = description;
        this.date = date;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public Data() {
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Data(int date, String month, int year) {
        this.date = date;
        this.month = month;
        this.year = year;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Data(String event, String description) {
        this.event = event;
        this.description = description;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Data(String title, Option optionData1, Option optionData2, Option optionData3, Option optionData4, Option optionData5, Option optionData6, Integer number,boolean answered,Poll poll) {
        this.title = title;
        this.optionData1 = optionData1;
        this.optionData2 = optionData2;
        this.optionData3 = optionData3;
        this.optionData4 = optionData4;
        this.optionData5 = optionData5;
        this.optionData6 = optionData6;
        this.poll = poll;
        this.number = number;
        this.answered = answered;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Option getOptionData1() {
        return optionData1;
    }

    public void setOptionData1(Option optionData1) {
        this.optionData1 = optionData1;
    }

    public Option getOptionData2() {
        return optionData2;
    }

    public void setOptionData2(Option optionData2) {
        this.optionData2 = optionData2;
    }

    public Option getOptionData3() {
        return optionData3;
    }

    public void setOptionData3(Option optionData3) {
        this.optionData3 = optionData3;
    }

    public Option getOptionData4() {
        return optionData4;
    }

    public void setOptionData4(Option optionData4) {
        this.optionData4 = optionData4;
    }

    public Option getOptionData5() {
        return optionData5;
    }

    public void setOptionData5(Option optionData5) {
        this.optionData5 = optionData5;
    }

    public Option getOptionData6() {
        return optionData6;
    }

    public void setOptionData6(Option optionData6) {
        this.optionData6 = optionData6;
    }
}
