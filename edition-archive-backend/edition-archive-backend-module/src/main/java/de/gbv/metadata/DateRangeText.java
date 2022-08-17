package de.gbv.metadata;

import java.util.Date;

public class DateRangeText {

    public DateRangeText(Date from, Date to, String text) {
        this.from = from;
        this.to = to;
        this.text = text;
    }

    public DateRangeText() {
    }

    Date from;
    Date to;
    String text;

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
