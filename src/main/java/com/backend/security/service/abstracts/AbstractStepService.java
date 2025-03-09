package com.backend.security.service.abstracts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.security.model.operation.Operation;
import com.backend.security.model.step.Step;
import com.backend.security.repository.operation.OperationRepository;
import com.backend.security.repository.step.StepRepository;
import com.backend.security.service.httpRequest.HttpService;
import lombok.extern.slf4j.Slf4j;
import com.backend.security.util.Constants;
import com.google.gson.Gson;


@Service
@Slf4j
public abstract class AbstractStepService {
    @Autowired
    protected StepRepository stepRepository;
    @Autowired
    protected OperationRepository operationRepository;
    @Autowired
    private HttpService httpService;
    protected CloseableHttpResponse responseMap;
    protected String requestBody;
    protected ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> headerMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>>();
    protected ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> extraOperationHeaderMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>>();
    protected ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> operationMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>>();
    protected ConcurrentHashMap<String, String> resultMap = new ConcurrentHashMap<String, String>();
    protected Constants constants;
    protected Step step;

    @Autowired
    public Gson gson;






        public void getNextOperationSteps(int operationId) {

        Step step = stepRepository.getStepToExecute(operationId);
        if (step != null) {
            log.debug("step start executing  step {}  operation {} step name  {} ", step.getStepId(), operationId,
                    step.getName());
            executeOperationSteps(step.getStepId(), operationId);
        }

    }

    public void executeOperationSteps(int stepId, int operationId) {

        executeSteps(stepId, operationId);
        getNextOperationSteps(operationId);
    }
    protected abstract void onInitiate(int stepId, int operationId) throws Exception;
    protected abstract void onCompleteStep(int stepId, int operationId, Hashtable<CloseableHttpResponse, String> response);


    public Boolean executeSteps(int stepId, int operationId) {

        Hashtable<CloseableHttpResponse, String> response = null;
        Step step = stepRepository.findByStepAndOpertaionId(stepId, operationId);

        try {
            if (step.getCallType() != null) {
                
                switch (step.getCallType()) {
                    case Constants.POST:
                        response = httpService.sendPOST(step.getOperation().getId(), step.getStepId(),
                                step.getCallDataType(), step.getUrl(), requestBody, headerMap.get(step.getOperation().getId()),
                                extraOperationHeaderMap.get(operationId));
                        break;
                    // case Constants.GET:
                    //     response = httpService.sendGET(step.getOperation().getId(), step.getStepId(),
                    //             step.getCallDataType(), step.getUrl(), headerMap.get(step.getOperation().getId()));
                    //     break;
                    // case Constants.PUT:
                    //     response = httpService.sendPUT(step.getOperation().getId(), step.getStepId(),
                    //             step.getCallDataType(), step.getUrl(), requestBody, headerMap.get(step.getOperation().getId()),
                    //             extraOperationHeaderMap.get(operationId));
                    //     break;
                    // case Constants.PATCH:
                    //     response = httpService.sendPATCH(step.getOperation().getId(), step.getStepId(),
                    //             step.getCallDataType(), step.getUrl(), requestBody,
                    //             headerMap.get(step.getOperation().getId()));
                    //     break;
                    // case Constants.DELETE:
                    //     response = httpService.sendDELETE(step.getOperation().getId(), step.getStepId(),
                    //             step.getCallDataType(), step.getUrl(), requestBody,
                    //             headerMap.get(step.getOperation().getId()));
                    //     break;
                    // case Constants.POSTAUTH:
                    //     response = httpService.sendPOST(step.getUrl(), null, jsonObject.get("username").toString(),
                    //             jsonObject.get("password").toString());
                    //     break;
                    default:
                        updateStep(step, Constants.FAIL);
                        updateOperation("The requested HTTP method is not supported for this endpoint.", operationId,
                                Constants.FAIL);
                }
            }
            onCompleteStep(stepId, operationId, response);           
        } catch (IOException e) {
            log.debug(e.getMessage());
            log.error("An error occurred: {}", e.getMessage(), e);;
            e.printStackTrace();
            updateStep(step, Constants.FAIL);
            updateOperation(e.getMessage(),operationId, Constants.FAIL);
            step.setRollbackUrl(e.getMessage());
            step.setResult(e.getMessage());
        } catch (Exception e) {
            log.debug(e.getMessage());
            e.printStackTrace();
            log.error("An error occurred: {}", e.getMessage(), e);;
            updateStep(step, Constants.FAIL);
            updateOperation( e.getMessage(),operationId, Constants.FAIL);
            step.setRollbackUrl(e.getMessage());
            step.setResult(e.getMessage());

        }

        log.debug("**************");
        log.debug("step end  step {}  operation {} step name  {} step status  {}", step.getStepId(), operationId,
                step.getName(), step.getStatus());
        log.debug("**********************************");

        return true;

    }

    public void updateStep(Step step, int status) {
        step.setStatus(status);
        stepRepository.save(step);

    }

    public void updateOperation(String result, int operationId, int status) {
        stepRepository.setAllStepToFail(operationId);
        java.util.Optional<Operation> operation = operationRepository.findById(operationId);
        operation.get().setResult(result);
        operation.get().setStatus(status);
        operationRepository.save(operation.get());

    }

    
}
