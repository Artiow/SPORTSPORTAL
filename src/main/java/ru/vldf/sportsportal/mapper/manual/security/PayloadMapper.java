package ru.vldf.sportsportal.mapper.manual.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ru.vldf.sportsportal.service.security.keykeeper.Payload;

import java.util.Map;

@Component
public class PayloadMapper {

    private static final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final TypeReference<Map<String, Object>> mapTypeReference = new TypeReference<Map<String, Object>>() {
    };


    public Map<String, Object> toMap(Payload payload) {
        return mapper.convertValue(payload, mapTypeReference);
    }

    public Payload toPayload(Map<String, Object> map) {
        return mapper.convertValue(map, Payload.class);
    }
}