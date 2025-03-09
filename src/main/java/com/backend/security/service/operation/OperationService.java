package com.backend.security.service.operation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.backend.security.model.operation.Operation;
import com.backend.security.service.AbstractService;
import com.backend.security.util.MessageType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OperationService extends AbstractService {

    private final OperationCreation operationCreation;

    public ResponseEntity<?> start(Operation operation, String userId) {
        try {
            operation = operationCreation.generateOperation(operation, userId);
            if(operation.getStatus() < 0){
                return new ResponseEntity<>("operation not found something went wrong", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(operation.getId(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

}
