package ru.shcherbatykh.vacationCalculator.service.holidaysProvider;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class StaticHolidaysProvider implements HolidaysProvider {
    @Value(value = "${application.static_holidays.json_path}")
    private String pathToHolidaysJson;

    @Value(value = "${application.static_holidays.date_format}")
    private String staticHolidaysDateFormat;

    private DateTimeFormatter dateTimeFormatter;

    @PostConstruct
    public void init() {
        dateTimeFormatter = DateTimeFormatter.ofPattern(staticHolidaysDateFormat);
    }

    @Override
    public List<LocalDate> getHolidays(List<Integer> years) {
        List<LocalDate> holidays = new ArrayList<>();
        for (Integer year : years) {
            holidays.addAll(parseJsonToHolidays(year));
        }
        return holidays;
    }

    private List<LocalDate> parseJsonToHolidays(int year) {
        List<LocalDate> holidays = new ArrayList<>();
        JSONObject jsonObject = readJsonFromFile();
        JSONArray arr = (JSONArray) jsonObject.get("holidays");
        for (Object o : arr) {
            String date = o + "." + year;
            holidays.add(LocalDate.parse(date, dateTimeFormatter));
        }
        return holidays;
    }

    private JSONObject readJsonFromFile() {
        FileReader reader;
        try {
            reader = new FileReader(pathToHolidaysJson);
        } catch (FileNotFoundException e) {
            log.error("The file " + pathToHolidaysJson + " was not found", e);
            throw new IllegalStateException("File with static holidays was not found");
        }

        JSONParser jsonParser = new JSONParser();
        try {
            return (JSONObject) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
            log.error("Occurred error when parse JSON " + pathToHolidaysJson, e);
            throw new IllegalStateException("File with static holidays cannot be parsed");
        }
    }
}
