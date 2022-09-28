package in.arvindsri82.cloud;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

import java.util.HashMap;
import java.util.Map;

public class PublishTopic {

    public static void main(String[] args) {

        String topicArn = "arn:aws:sns:ap-south-1:244544013937:MyFirstTopic";
        String message = " { " +
                " \"purpose\" : \" Testing Lambda Invocation\" " +
                " \"category\" : \"Blah Blah\"" +
                " \"author\" : \" Arvind\" " +
                " }";


        SnsClient snsClient = SnsClient.builder().region(Region.AP_SOUTH_1).credentialsProvider(ProfileCredentialsProvider.create()).build();

        Map<String, MessageAttributeValue> messageAttributes = new HashMap();
        messageAttributes.put("category", MessageAttributeValue.builder().stringValue("Prod").dataType("String").build());

        pubTopic(snsClient, message,messageAttributes, topicArn);
        snsClient.close();

    }

    public static void pubTopic(SnsClient snsClient, String message, Map<String, MessageAttributeValue> messageAttributes, String topicArn) {

        try {
            PublishRequest request = PublishRequest.builder().message(message).messageAttributes(messageAttributes).topicArn(topicArn).build();

            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
