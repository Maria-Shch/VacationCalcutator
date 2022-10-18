package ru.shcherbatykh.vacationCalculator.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@Slf4j
public class CustomDoubleSerializer extends JsonSerializer<Double> {
    private static final String PROPERTIES_FILENAME = "application.properties";
    private static final String PATTERN_KEY = "application.vacation_pay.format";
    private static final String DEFAULT_PATTERN = ".##";

    private final DecimalFormat decimalFormat;

    public CustomDoubleSerializer() {
        PropertiesConfiguration config = new PropertiesConfiguration();
        String pattern;
        try {
            config.load(PROPERTIES_FILENAME);
            pattern = config.getString(PATTERN_KEY, DEFAULT_PATTERN);
        } catch (ConfigurationException e) {
            log.error("Can't read " + PATTERN_KEY + " from " + PROPERTIES_FILENAME, e);
            pattern = DEFAULT_PATTERN;
        }
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        decimalFormat = new DecimalFormat(pattern, otherSymbols);
    }

    @Override
    public void serialize(Double value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (value == null) {
            jsonGenerator.writeNull();
        } else {
            String output = decimalFormat.format(value);
            jsonGenerator.writeNumber(output);
        }
    }
}
