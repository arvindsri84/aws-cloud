package in.arvindsri82.cloud;

import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.RefreshPreferences;
import com.amazonaws.services.autoscaling.model.StartInstanceRefreshRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

public class InstanceRefreshOnSNSEvent implements RequestHandler<SNSEvent, String> {

    public String handleRequest(SNSEvent snsEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Event received ..");
        logger.log(snsEvent.toString());


        var asgName = System.getenv("ASG_NAME");
        logger.log("Autoscaling group name " + asgName);

        var client = AmazonAutoScalingClient.builder().build();
        var request = new StartInstanceRefreshRequest();
        request.setAutoScalingGroupName(asgName);
        request.setStrategy("Rolling");

        var refreshPreference = new RefreshPreferences();
        refreshPreference.setMinHealthyPercentage(50);
        refreshPreference.setInstanceWarmup(300);
        request.setPreferences(refreshPreference);

        client.startInstanceRefresh(request);
        logger.log("Event Processed ..");

        return "Success";
    }

}
