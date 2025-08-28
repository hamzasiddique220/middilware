package com.backend.security.service.user;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.backend.security.util.MessageType;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ResponseService {

    @Autowired
    protected Gson gson;

    public ResponseEntity<?> response(MessageType type, HttpStatus status, Object... objects) {
        Map<String, Object> resultMap = new HashMap<>();

        if (type == MessageType.SUCCESS) {
            resultMap.put("success", true);
        }

        Iterator<Object> iterator = Arrays.asList(objects).iterator();
        while (iterator.hasNext()) {
            String key = String.valueOf(iterator.next());
            Object value = iterator.hasNext() ? iterator.next() : null;
            value = (value == null) ? key : value;
            if (isPrimitiveOrString(value)) {
                resultMap.put(key, String.valueOf(value));
            } else {
                resultMap.put(key, gson.toJson(value));
            }
        }
        

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    private boolean isPrimitiveOrString(Object value) {
        return value != null && (value.getClass().isPrimitive() || value instanceof String);
    }

}
