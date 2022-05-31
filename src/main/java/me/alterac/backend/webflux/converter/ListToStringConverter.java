package me.alterac.backend.webflux.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.ArrayList;

@Slf4j
@WritingConverter
@AllArgsConstructor
public class ListToStringConverter implements Converter<ArrayList<String>, String> {

    private final ObjectMapper objectMapper;

    @Override
    public String convert(ArrayList<String> src) {
        try {
            return objectMapper.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            log.error("Error occurred while serializing map to JSON: {}", src, e);
        }
        return "";
    }
}
