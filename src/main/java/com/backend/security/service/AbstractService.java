package com.backend.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.backend.security.service.user.ResponseService;
import com.backend.security.util.MessageType;
import com.google.gson.Gson;

public abstract class AbstractService {

    @Autowired
    ResponseService responseService;

    @Autowired
    protected Gson gson;

    public ResponseEntity<?> response(MessageType type, HttpStatus status, Object... objects) {
        return responseService.response(type, status, objects);
    }
}
