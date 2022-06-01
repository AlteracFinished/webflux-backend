package me.alterac.backend.webflux.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@ReadingConverter
@AllArgsConstructor
public class StringToListConverter implements Converter<String, List<String>> {

    private final ObjectMapper objectMapper;

    @Override
    public List<String> convert(String src) {
        try {
            return objectMapper.readValue(src, new TypeReference<ArrayList<String>>() {});
        } catch (IOException e) {
            log.error("Problem while parsing JSON: {}", src, e);
        }
        return Collections.emptyList();
    }

}
