package com.backend.security.service.abstracts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.backend.security.model.operation.Operation;
import com.backend.security.repository.operation.OperationRepository;
import com.backend.security.service.aws.AwsOperationService;
import com.backend.security.util.Constants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public abstract class AbstractOperationService {

	public AbstractStepService getOperationToExecute(String name, int providerId, int operationId) {
		return null;

	}

	@Lazy
	@Autowired
	private AwsOperationService awsOperationService;

	@Autowired
	private OperationRepository operationRepository;

	public AbstractOperationService getOperation(int providerId) {

		switch (providerId) {
			case Constants.AWS:
				return awsOperationService;
			default:
				return null;
		}

	}

	protected void setOperationToFail(int operationId) {
		@SuppressWarnings("deprecation")
		Operation operation = operationRepository.getById(operationId);
		operation.setStatus(Constants.FAIL);
		operation.setResult("operation not found issue");
		operationRepository.save((operation));
	}


}
