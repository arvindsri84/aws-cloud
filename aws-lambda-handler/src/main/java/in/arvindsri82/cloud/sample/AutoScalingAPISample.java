package in.arvindsri82.cloud.sample;

import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.*;

public class AutoScalingAPISample {

    public static void main(String[] args) {
        var client = AmazonAutoScalingClient.builder().build();
        describeAll(client);

        var asgName = describeByTagValue(client);
        //updateCapacity(client, asgName, 0);
        updateCapacity(client, asgName, 2);
    }

    private static String describeByTagValue(AmazonAutoScaling client) {
        var filter = new Filter().withName("tag-value").withValues("sys-monit-autoscaling-group");
        var request = new DescribeAutoScalingGroupsRequest();
        request.getFilters().add(filter);
        var result = client.describeAutoScalingGroups(request);
        var asgName = result.getAutoScalingGroups().get(0).getAutoScalingGroupName();
        System.out.println("Autoscaling Group name ( describe by tag value) : "  + asgName);
        return asgName;
    }

    private static void describeAll(AmazonAutoScaling client) {
        var result = client.describeAutoScalingGroups();
        for (AutoScalingGroup autoScalingGroup : result.getAutoScalingGroups()) {
            System.out.println(autoScalingGroup.getAutoScalingGroupARN());
            System.out.println(autoScalingGroup.getAutoScalingGroupName());
            System.out.println(autoScalingGroup.getTags());
        }
    }

    private static void updateCapacity(AmazonAutoScaling client, String asgName, int capacity) {

        var request = new UpdateAutoScalingGroupRequest();
        request.setDesiredCapacity(capacity);
        request.setMinSize(capacity);
        request.setMaxSize(capacity);
        request.setAutoScalingGroupName(asgName);

        var result = client.updateAutoScalingGroup(request);
        System.out.println(result);
    }
}
