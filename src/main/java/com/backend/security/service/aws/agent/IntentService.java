package com.backend.security.service.aws.agent;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IntentService {

    // AWS ID patterns
    private static final Pattern INSTANCE_ID = Pattern.compile("\\b(i-[a-f0-9]{17})\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern VOLUME_ID   = Pattern.compile("\\b(vol-[a-f0-9]{17})\\b", Pattern.CASE_INSENSITIVE);

    // numbers
    private static final Pattern CPU_PATTERN = Pattern.compile("\\b(\\d+)\\s*(cpu|vcpu|core)s?\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern RAM_PATTERN = Pattern.compile("\\b(\\d+)\\s*(gb|gig|gigabyte)s?\\s*(ram|memory)?\\b", Pattern.CASE_INSENSITIVE);

    // simple verbs
    private static final Pattern CREATE_VM   = Pattern.compile("\\b(create|launch|make)\\b.*\\b(vm|ec2|instance)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern START_VM    = Pattern.compile("\\b(start|boot)\\b.*\\b(vm|ec2|instance)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern STOP_VM     = Pattern.compile("\\b(stop|shutdown|halt)\\b.*\\b(vm|ec2|instance)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern DELETE_VM   = Pattern.compile("\\b(delete|terminate|remove|destroy)\\b.*\\b(vm|ec2|instance)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern LIST_VM     = Pattern.compile("\\b(list|show|get)\\b.*\\b(vm|ec2|instance|instances)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern ATTACH_VOL  = Pattern.compile("\\b(attach)\\b.*\\b(volume|ebs)\\b", Pattern.CASE_INSENSITIVE);

    public Map<String, String> extractIntent(String prompt) {
        Map<String, String> res = new HashMap<>();
        if (prompt == null) {
            res.put("action", "unknown");
            return res;
        }
        String p = prompt.trim();

        // action detection (priority order matters)
        if (ATTACH_VOL.matcher(p).find()) {
            res.put("action", "attach_volume");
        } else if (CREATE_VM.matcher(p).find()) {
            res.put("action", "create_vm");
        } else if (START_VM.matcher(p).find()) {
            res.put("action", "start_vm");
        } else if (STOP_VM.matcher(p).find()) {
            res.put("action", "stop_vm");
        } else if (DELETE_VM.matcher(p).find()) {
            res.put("action", "delete_vm");
        } else if (LIST_VM.matcher(p).find()) {
            res.put("action", "list_vm");
        } else {
            res.put("action", "unknown");
        }

        // ids & params
        Matcher mInst = INSTANCE_ID.matcher(p);
        if (mInst.find()) res.put("instanceId", mInst.group(1));

        Matcher mVol = VOLUME_ID.matcher(p);
        if (mVol.find()) res.put("volumeId", mVol.group(1));

        Matcher mCpu = CPU_PATTERN.matcher(p);
        if (mCpu.find()) res.put("cpu", mCpu.group(1));

        Matcher mRam = RAM_PATTERN.matcher(p);
        if (mRam.find()) res.put("ram", mRam.group(1)); // GB assumed

        return res;
    }
}


