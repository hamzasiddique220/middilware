package com.backend.security.config;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import com.backend.security.model.aws.Aws;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;

@Configuration
public class AWSConfig {

    // @Autowired
    // private ConfigUtility configUtil;


    // public CostExplorerClient amazonCostExplorer(GeneralDTO generalRequest) {
    //     AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(generalRequest.getAccessKey(),
    //             generalRequest.getSecretKey());
    //     return CostExplorerClient.builder().region(Region.of(generalRequest.getRegion()))
    //             .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
    //             .build();
    // }

    // public DynamoDbClient amazonDynamoOB(GeneralDTO generalRequest) {
    //     AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(generalRequest.getAccessKey(),
    //             generalRequest.getSecretKey());
    //     return DynamoDbClient.builder().region(Region.of(generalRequest.getRegion()))
    //             .credentialsProvider(StaticCredentialsProvider.create(awsCredentials)).build();
    // }

    // public ElasticLoadBalancingV2Client elasticLoadBalancingV2Client(GeneralDTO generalRequest) {
    //     GeneralDTO aws = generateRequest(generalRequest);
    //     AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(aws.getAccessKey(), aws.getSecretKey());
    //     return ElasticLoadBalancingV2Client.builder().region(Region.of(aws.getRegion()))
    //             .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
    //             .build();
    // }

    // public ApplicationAutoScalingClient applicationAutoScalingClient(GeneralDTO generalRequest) {
    //     GeneralDTO aws = generateRequest(generalRequest);
    //     AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(aws.getAccessKey(), aws.getSecretKey());
    //     return ApplicationAutoScalingClient.builder().region(Region.of(aws.getRegion()))
    //             .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
    //             .build();
    // }

    // public S3Client amazonS3Async(GeneralDTO generalRequest) {
    //     GeneralDTO aws = generateRequest(generalRequest);
    //     AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(aws.getAccessKey(), aws.getSecretKey());
    //     return S3Client.builder().region(Region.of(aws.getRegion()))
    //             .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
    //             .build();
    // }

    // public S3ControlClient s3ControlClient(GeneralDTO generalRequest) {
    //     AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(generalRequest.getAccessKey(),
    //             generalRequest.getSecretKey());
    //     return S3ControlClient.builder().region(Region.of(generalRequest.getRegion()))
    //             .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
    //             .build();
    // }

    public Ec2Client getEc2Client(Aws aws) {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(aws.getAccessKey(), aws.getSecretKey());
        return Ec2Client.builder().region(Region.of(aws.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    public CloudWatchClient getCloudWatchClient(Aws aws) {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(aws.getAccessKey(), aws.getSecretKey());
        return CloudWatchClient.builder()
                .region(Region.of(aws.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    // public S3Client getS3Client(GeneralDTO generalRequest) {
    //     GeneralDTO aws = generateRequest(generalRequest);
    //     AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(aws.getAccessKey(), aws.getSecretKey());
    //     return S3Client.builder().region(Region.of(aws.getRegion()))
    //             .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
    //             .build();
    // }

    ////////////// Below methods are used by only current class  /////////////////

    // public GeneralDTO setCredentials(JsonObject jsonObject) {
    //     GeneralDTO generalDTO = new GeneralDTO();
    //     generalDTO.setAccessKey(jsonObject.get("accessKey").getAsString());
    //     generalDTO.setSecretKey(jsonObject.get("secretKey").getAsString());
    //     generalDTO.setRegion(jsonObject.get("region").getAsString());
    //     return generalDTO;
    // }

    // public GeneralDTO generateRequest(GeneralDTO generalRequest) {
    //     if (generalRequest.getAccessKey() != null) {
    //         String accessKey = AES.decrypt(generalRequest.getAccessKey(), configUtil.getProperty("encrypt_key"));
    //         String secretKey = AES.decrypt(generalRequest.getSecretKey(), configUtil.getProperty("encrypt_key"));
    //         generalRequest.setAccessKey(accessKey.replace(" ", "+"));
    //         generalRequest.setSecretKey(secretKey.replace(" ", "+"));
    //         generalRequest.setRegion(generalRequest.getRegion());
    //     } else {
    //         AWSUserProject AWSUserProject = awsUserProjectRepository.findUserByUUID(generalRequest.getUserId(),
    //                 generalRequest.getProviderId());
    //         if (AWSUserProject != null) {
    //             String accessKey = AES.decrypt(AWSUserProject.getAccessKey(), configUtil.getProperty("encrypt_key"));
    //             String secretKey = AES.decrypt(AWSUserProject.getSecretKey(), configUtil.getProperty("encrypt_key"));
    //             generalRequest.setAccessKey(accessKey.replace(" ", "+"));
    //             generalRequest.setSecretKey(secretKey.replace(" ", "+"));
    //             generalRequest.setRegion(generalRequest.getRegion() != null ? generalRequest.getRegion() : AWSUserProject.getRegion());
    //         }
    //     }
    //     return generalRequest;
    // }

    // public Collection<TagSpecification> addTag(List<TagDTO> tags, ResourceType resourceType) {
    //     List<TagSpecification> tagSpecifications = new ArrayList<>();
    //     List<Tag> tagList = new ArrayList<>();
    //     for (TagDTO tag : tags) {
    //         Tag nameTag = Tag.builder()
    //                 .key(tag.getKey())
    //                 .value(tag.getValue())
    //                 .build();
    //         tagList.add(nameTag);
    //     }
    //     tagSpecifications.add(TagSpecification.builder()
    //             .resourceType(resourceType)
    //             .tags(tagList).build());
    //     return tagSpecifications;
    // }

}

