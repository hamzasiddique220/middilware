package com.backend.security.service.aws;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.stereotype.Service;
import com.backend.security.model.step.Step;
import com.backend.security.service.abstracts.AbstractStepService;
import com.backend.security.util.Constants;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class AWSComputeService extends AbstractStepService {



	@Override
	@Transactional
	public void onCompleteStep(int stepId, int operationId, Hashtable<CloseableHttpResponse, String> response) {
		step = stepRepository.findByStepAndOpertaionId(stepId, operationId);

		responseMap = (CloseableHttpResponse) response.keySet().toArray()[0];
		String entity = (String) response.values().toArray()[0];
		StatusLine statusLine = responseMap.getStatusLine();

		// set response
		setResponse(stepId, operationId, entity);
		step.setResult(resultMap.get(operationId + "_" + stepId));

		if (statusLine.getStatusCode() >= 200 && statusLine.getStatusCode() <= 300) {
			switch (step.getName()) {
				case Constants.AWS_CREATE_VM_STEP:
					getInstanceIdFromResponse(operationId, stepId);/*  */
					break;
				case Constants.AWS_DETAIL_VM_STEP:
					checkStatus(step, operationId, entity);
					break;
				default:

			}
			updateStep(step, Constants.COMPLETED);
		} else if (statusLine.getStatusCode() >= 400) {
			updateStep(step, Constants.FAIL);
			updateOperation(resultMap.get(operationId + "_" + stepId), operationId, Constants.FAIL);
		}

	}

    @Override
	@Transactional
	public void onInitiate(int stepId, int operationId) throws Exception {
		step = stepRepository.findByStepAndOpertaionId(stepId, operationId);
		switch (step.getName()) {
			case Constants.AWS_DETAIL_VM_STEP:
				detailsVm(step);
				break;
			default:
				updateIdStepURL(step, step.getInputParameters());
				if (step.getOperation().getOtherParameters() != null
						&& !step.getOperation().getOtherParameters().isBlank()) {
					updateIdStepURL(step, step.getOperation().getOtherParameters());
				}

				if (step.getCallType().contains("post") || step.getCallType().contains("put")) {
					setRequestBodyFetch(step);

				}

		}

	}

    	private void updateIdStepURL(Step step, String parameters) {
		String url = step.getUrl();
		// JsonObject serverJson = gson.fromJson(parameters, JsonObject.class);
		// url = UtilFunction.updateVariables(url, serverJson);
		step.setUrl(url);
		stepRepository.save(step);
	}

        private void setRequestBodyFetch(Step step) {
            JsonObject stepInputParameterJson = gson.fromJson(step.getInputParameters(), JsonObject.class);
            requestBody = stepInputParameterJson.toString();

        }
    
    	private void detailsVm(Step step) {
		String url = step.getUrl();
		ConcurrentHashMap<String, String> map = operationMap.get(step.getOperation().getId());
		for (Entry<String, String> entry : map.entrySet()) {
			url = url.replace('#' + Constants.INSTANCE_ID, entry.getValue());
			url = url.replace('#' + Constants.PROVIDER_ID, step.getOperation().getProviderId() + "");
			url = url.replace('#' + Constants.USER_ID, step.getOperation().getUserId());
			step.setUrl(url);
			stepRepository.save(step);

		}

	}

    protected void setResponse(int stepId, int operationId, String entity) {
		resultMap.put(operationId + "_" + stepId, entity);
	}

    protected void getInstanceIdFromResponse(int opertaionId, int stepId) {
		JsonObject resultJson = gson.fromJson(resultMap.get(opertaionId + "_" + stepId),
				JsonObject.class);
		String instanceId = resultJson.getAsJsonObject().get("id").getAsString();
		ConcurrentHashMap<String, String> headersMap = new ConcurrentHashMap<String, String>();
		headersMap.put(Constants.INSTANCE_ID, instanceId);
		operationMap.put(opertaionId, headersMap);
	}

	protected void getIdFromResponse(int operationId, int stepId) {
		JsonObject resultJson = gson.fromJson(resultMap.get(operationId + "_" + stepId),
				JsonObject.class);
		String id = resultJson.getAsJsonObject().get("id").getAsString();
		storeStepData(step, "id", id);
	}
    public void storeStepData(Step step, String key, String value) {
        ConcurrentHashMap<String, String> headersMap;
        if (operationMap.containsKey(step.getOperation().getId()))
            headersMap = operationMap.get(step.getOperation().getId());
        else
            headersMap = new ConcurrentHashMap<String, String>();
        headersMap.put(key, value);
        operationMap.put(step.getOperation().getId(), headersMap);
    }

	protected void checkStatus(Step step, int operationId, String entity) {
		JsonObject serverJson = gson.fromJson(entity, JsonObject.class);
		JsonObject result = gson.fromJson(serverJson.getAsJsonObject().get("instance").getAsString(), JsonObject.class);

		if (result.getAsJsonObject("state").getAsJsonObject().get("name").getAsString().equalsIgnoreCase("pending")) {
			try {
				step.setStatus(1);
				stepRepository.save(step);
				TimeUnit.SECONDS.sleep(5);
				executeSteps(step.getStepId(), operationId);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		} else if (result.getAsJsonObject("state").getAsJsonObject().get("name").getAsString()
				.equalsIgnoreCase("error")) {
			step.setResult(entity);
			step.setRollbackUrl("check status");
			updateStep(step, Constants.FAIL);
			setStepFail(step);
			updateOperation(entity, operationId, Constants.FAIL);
		} else if (result.getAsJsonObject("state").getAsJsonObject().get("name").getAsString()
				.equalsIgnoreCase("running")) {
			step.setResult(serverJson.getAsJsonObject().get("instance").getAsString());
			updateStep(step, Constants.COMPLETED);
		}
	}
    public void setStepFail(Step step) {
        stepRepository.setStepToFail(step.getStepId());
    }

}
