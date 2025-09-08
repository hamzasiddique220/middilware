package com.backend.security.controllers.aws.agent;
import com.backend.security.exception.BadRequestException;
import com.backend.security.service.aws.agent.AwsAIAgentService;
import com.backend.security.service.aws.agent.IntentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/agent")
@RequiredArgsConstructor
public class AwsAIAgentController {

    private final AwsAIAgentService awsAIAgentService;
    private final IntentService intentService;

    @PostMapping("/execute")
    public ResponseEntity<?> execute(@RequestBody Map<String, String> payload) {
        String prompt = payload.get("prompt");
        Map<String, String> intent = intentService.extractIntent(prompt);

        String action = intent.get("action");
        String instanceId = intent.get("instanceId");
        String volumeId = intent.get("volumeId");

        switch (action) {
            case "create_vm" -> {
                int cpu = Integer.parseInt(intent.getOrDefault("cpu", "2"));
                int ram = Integer.parseInt(intent.getOrDefault("ram", "4"));
                String vmId = awsAIAgentService.createVm(cpu, ram);
                return ResponseEntity.ok(Map.of("status", "VM Created", "vmId", vmId, "cpu", String.valueOf(cpu),
                        "ramGB", String.valueOf(ram)));
            }
            case "attach_volume" -> {
                require(instanceId, "instanceId (e.g., i-xxxxxxxxxxxxxxxxx) is required");
                require(volumeId, "volumeId (e.g., vol-xxxxxxxxxxxxxxxxx) is required");
                String result = awsAIAgentService.attachVolume(instanceId, volumeId);
                return ResponseEntity.ok(Map.of("status", result));
            }
            case "start_vm" -> {
                require(instanceId, "instanceId is required");
                String result = awsAIAgentService.startVm(instanceId);
                return ResponseEntity.ok(Map.of("status", result));
            }
            case "stop_vm" -> {
                require(instanceId, "instanceId is required");
                String result = awsAIAgentService.stopVm(instanceId);
                return ResponseEntity.ok(Map.of("status", result));
            }
            case "delete_vm" -> {
                require(instanceId, "instanceId is required");
                String result = awsAIAgentService.deleteVm(instanceId);
                return ResponseEntity.ok(Map.of("status", result));
            }
            case "list_vm" -> {
                return awsAIAgentService.listVm();
            }
            default -> {
                throw new BadRequestException(
                        "Could not understand your intent. Try: 'create vm 2 cpu 4 gb', 'start vm i-..', 'attach volume vol-.. to i-..', 'list vms'.");
            }
        }
    }

    private void require(String val, String messageIfNull) {
        if (val == null || val.isBlank())
            throw new BadRequestException(messageIfNull);
    }
}
