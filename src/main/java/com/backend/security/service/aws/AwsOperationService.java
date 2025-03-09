package com.backend.security.service.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.security.model.operation.OperationConfig;
import com.backend.security.repository.operation.OperationConfigRepository;
import com.backend.security.service.abstracts.AbstractOperationService;
import com.backend.security.service.abstracts.AbstractStepService;
import com.backend.security.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwsOperationService extends AbstractOperationService {


	private final OperationConfigRepository operationConfigRepository;
	private final AWSComputeService aWSComputeService;

	@Override
	public AbstractStepService getOperationToExecute(String name, int providerId, int operationId) {
		OperationConfig operationConfig = operationConfigRepository.getByName(name);
		log.debug("aws operation");
		switch (operationConfig.getType()) {
			case Constants.INSTANCE:
				return aWSComputeService;
			default:
				return null;
		}
	}

}
