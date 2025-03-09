package com.backend.security.service.operation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.security.model.operation.Operation;
import com.backend.security.model.operation.OperationConfig;
import com.backend.security.model.step.Step;
import com.backend.security.model.step.StepConfig;
import com.backend.security.repository.operation.OperationConfigRepository;
import com.backend.security.repository.operation.OperationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class OperationCreation {

    private final OperationConfigRepository operationConfigRepository;
    private final OperationRepository operationRepository;

    public Operation generateOperation(Operation operation, String userId) {
        OperationConfig operationsConfig = operationConfigRepository.getByName(operation.getName());
        if (operationsConfig == null) {
            Operation op = new Operation();
            op.setStatus(-1);
            op.setName("no operation found");
            return op;
        }
        List<Step> arrayStep = new ArrayList<Step>();

        List<OperationConfig> beforeOperationsList = operationConfigRepository
                .findByIds(operationsConfig.getBeforeStart());
        try {
            makeSteps(arrayStep, operationsConfig, operation, beforeOperationsList);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        makeOperation(operation, arrayStep, userId, operationsConfig);

        operationRepository.save(operation);

        return operation;

    }

    public void makeSteps(List<Step> arrayStep, OperationConfig operationsConfig, Operation operation,
            List<OperationConfig> beforeOperationsList) throws Exception {

        for (OperationConfig beforeoperationConfig : beforeOperationsList) {
            for (StepConfig conStep : beforeoperationConfig.getStep()) {
                if (conStep.getStatus() == 1) {

                    Step step = new Step();
                    step.setStepId(conStep.getStepId());
                    step.setName(conStep.getName());
                    step.setPriority(conStep.getPriority());
                    step.setCallType(conStep.getCallType());
                    step.setCallDataType(conStep.getCallDataType());
                    step.setDependOn(conStep.getDependOn());
                    step.setProviderId(conStep.getProviderId());
                    step.setOperation(operation);
                    // step.setDefaultParameters(conStep.getDefaultParameters());
                    step.setStepParameters(conStep.getRequestParameters());
                    step.setInputParameters(operation.getParameters());
                    step.setRequestParameters(conStep.getRequestParameters());
                    step.setUrl(conStep.getUrl());
                    log.info("Setting step URL as: " + step.getUrl());

                    arrayStep.add(step);
                }
            }

        }

        int intStepCount = arrayStep.size();
        List<StepConfig> steps = operationsConfig.getStep();
        for (StepConfig conStep : steps) {
            if (conStep.getStatus() == 1) {
                Step step = new Step();
                step.setStepId(conStep.getStepId() + 1);

                step.setName(conStep.getName());
                step.setPriority(conStep.getPriority());
                if (conStep.getCallType() != null) {
                    step.setCallType(conStep.getCallType());
                }
                step.setOptional(conStep.getOptional());
                if (conStep.getCallDataType() != null) {
                    step.setCallDataType(conStep.getCallDataType());
                }
                step.setDependOn(conStep.getDependOn() + intStepCount);
                step.setDefaultParameters(conStep.getDefaultParameters());
                step.setProviderId(conStep.getProviderId());
                step.setAnsibleType(conStep.getAnsibleType());
                step.setInputParameters(operation.getParameters());
                step.setOperation(operation);
                step.setRequestParameters(conStep.getRequestParameters());
                if (conStep.getUrl() != null) {
                    step.setUrl(conStep.getUrl());
                }

                arrayStep.add(step);
            }

        }

    }

    public void makeOperation(Operation operation, List<Step> arrayStep, String userId,
            OperationConfig operationsConfig) {
        operation.setSteps(arrayStep);
        operation.setUserName(userId);
        operation.setUserId(userId);
        operation.setContractId(operation.getContractId());
        operation.setStatus(0);
        operation.setAutoExecution(operationsConfig.getAutoExecution());
        operation.setChildOperationId(operationsConfig.getChildOperationId());
        operation.setMultiOpStatus(operationsConfig.getMultiOpStatus());
        operation.setApprovalSteps(operationsConfig.getApprovalSteps());
        operation.setParentOperationId(operationsConfig.getParentOperationId());

        operation.setProductId(operation.getProductId());
        operation.setIsPooling(operationsConfig.getIsPooling());
        operation.setPreserveData(operationsConfig.getPreserveData());
        operation.setIsRollback(operationsConfig.getIsRollback());

        operation.setLongOperation(operationsConfig.getLongOperation());
        operation.setRollbackOperationName(operationsConfig.getRollbackOperationName());
        // operation.setOperationRollbackGroupId(operationsConfig.getOperationRollbackGroupId());
        if (operation.getType() == null)
            operation.setType(operationsConfig.getType());
        if (operation.getProviderId() == 0)
            operation.setProviderId(200);
    }

}
