package ru.shcherbatykh.vacationCalculator.service.holidaysProvider;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Primary
@Service
public class XmlCalendarHolidaysProvider implements HolidaysProvider {
    @Value(value = "${application.xmlcalendar.url_pattern}")
    private String xmlcalendarUrlPattern;

    @Value("${application.xmlcalendar.date_format}")
    private String xmlcalendarDateFormat;

    private final StaticHolidaysProvider staticHolidaysProvider;
    private final RestTemplate restTemplate;

    private DateTimeFormatter dateTimeFormatter;


    public XmlCalendarHolidaysProvider(StaticHolidaysProvider jsonHolidaysProvider) {
        this.staticHolidaysProvider = jsonHolidaysProvider;
        restTemplate = new RestTemplate();
    }

    @PostConstruct
    public void init() {
        dateTimeFormatter = DateTimeFormatter.ofPattern(xmlcalendarDateFormat);
    }

    @Override
    public List<LocalDate> getHolidays(List<Integer> years) {
        List<LocalDate> holidays = new ArrayList<>();
        for (Integer year : years) {
            try {
                Document productionCalendar = getXmlDocumentOfProductionCalendarByYear(year);
                holidays.addAll(parseXmlDocumentToHolidays(productionCalendar));
            } catch (Exception e) {
                log.error("Occurred an error during getting info from " + xmlcalendarUrlPattern, e);
                holidays.addAll(staticHolidaysProvider.getHolidays(Collections.singletonList(year)));
            }
        }
        return holidays;
    }

    @SneakyThrows
    private Document getXmlDocumentOfProductionCalendarByYear(int year) {
        ResponseEntity<String> response = restTemplate.exchange(xmlcalendarUrlPattern, HttpMethod.GET, null,
                String.class, year);
        return DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(response.getBody())));
    }

    private List<LocalDate> parseXmlDocumentToHolidays(Document document) {
        List<LocalDate> holidays = new ArrayList<>();

        NodeList calendarElements = document.getElementsByTagName("calendar");
        String year = calendarElements
                .item(0)
                .getAttributes()
                .getNamedItem("year")
                .getNodeValue();

        NodeList dayElements = document.getElementsByTagName("day");
        for (int i = 0; i < dayElements.getLength(); i++) {
            Node day = dayElements.item(i);
            NamedNodeMap attributes = day.getAttributes();
            if (attributes.getNamedItem("t") != null && "1".equals(attributes.getNamedItem("t").getNodeValue())) {
                String date = attributes.getNamedItem("d").getNodeValue() + "." + year;
                holidays.add(LocalDate.parse(date, dateTimeFormatter));
            }
        }
        return holidays;
    }
}
