package org.jsapar.text.format;

import org.jsapar.model.CellType;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DurationFormat implements Format<Duration> {
    private final DateTimeFormatter formatter;

    public DurationFormat(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public CellType cellType() {
        return CellType.DURATION;
    }

    @Override
    public Duration parse(String csValue) throws ParseException {
        LocalTime time = LocalTime.parse(csValue, formatter);
        return Duration.ofNanos(time.toNanoOfDay());
    }

    @Override
    public String format(Object value) throws IllegalArgumentException {
        LocalTime time = LocalTime.ofNanoOfDay(((Duration) value).toNanos());
        return time.format(formatter);
    }
}
