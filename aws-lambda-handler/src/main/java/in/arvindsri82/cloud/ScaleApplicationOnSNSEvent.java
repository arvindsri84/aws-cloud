package in.arvindsri82.cloud;

import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

public class ScaleApplicationOnSNSEvent implements RequestHandler<SNSEvent, String> {

    public String handleRequest(SNSEvent snsEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Event received ..");
        logger.log(snsEvent.toString());

        var client = AmazonAutoScalingClient.builder().build();
        var asgName = getAutoScalingGroupName(client);
        logger.log("Autoscaling group name " + asgName);

        var capacity = getCapacity();
        logger.log("Autoscaling group new capacity " + capacity);

        var request = new UpdateAutoScalingGroupRequest();
        request.setDesiredCapacity(capacity);
        request.setMinSize(capacity);
        request.setMaxSize(capacity);
        request.setAutoScalingGroupName(asgName);

        var result = client.updateAutoScalingGroup(request);
        logger.log("Event Processed ..");

        return "Success";
    }


    private String getAutoScalingGroupName(AmazonAutoScaling client) {
        var asgName = System.getenv("ASG_NAME");
        if (asgName == null || asgName.trim().length() == 0) {
            var asgTag = System.getenv("ASG_TAG_VALUE");

            var filter = new Filter().withName("tag-value").withValues(asgTag);
            var request = new DescribeAutoScalingGroupsRequest();
            request.getFilters().add(filter);

            var result = client.describeAutoScalingGroups(request);
            asgName = result.getAutoScalingGroups().get(0).getAutoScalingGroupName();
        }
        return asgName;
    }


    private Integer getCapacity() {
        var capacity = System.getenv("ASG_CAPACITY");
        if (capacity == null || capacity.trim().length() == 0) {
            return 0;
        }
        return Integer.valueOf(capacity);
    }
}
