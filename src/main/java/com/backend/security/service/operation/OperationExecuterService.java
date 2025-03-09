package com.backend.security.service.operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.backend.security.repository.operation.OperationRepository;
import com.backend.security.service.abstracts.AbstractOperationService;
import com.backend.security.service.abstracts.AbstractStepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OperationExecuterService extends AbstractOperationService{
    
    private final OperationRepository operationRepository;

    public void getImportantOperationByPriorityForExecution() {
        log.debug("Task executed by thread:  execute normal operation" + Thread.currentThread().getName());
        operationRepository.getImportantOperationToExecute().stream().forEach(operation->{
            executeOperation(operation.getId(), operation.getName(), operation.getProviderId());
        });


    }


    private void executeOperation(int operationId, String name, int providerId) {

		AbstractStepService service = getOperation(providerId).getOperationToExecute(name, providerId, operationId);
		if (service == null) {
			setOperationToFail(operationId);
		} else {
			service.getNextOperationSteps(operationId);
		}
	}




}
