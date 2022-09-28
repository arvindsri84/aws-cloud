package in.arvindsri82.cloud;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

public class SNSEventHandler implements RequestHandler<SNSEvent, String> {

    public String handleRequest(SNSEvent snsEvent, Context context) {
        LambdaLogger logger = context.getLogger();

        logger.log("Event Received ..");
        logger.log(snsEvent.toString());

        /*

            {[{sns: {messageAttributes: {category={type: String,value: Prod}},
            signingCertUrl: https://sns.ap-south-1.amazonaws.com/XXXX.pem,
            messageId: 3007840f-79b9-5224-bc52-dfa1e777d167,
            message:  {  "purpose" : " Testing Lambda Invocation"  "category" : "Blah Blah" "author" : " Arvind"  },
            unsubscribeUrl: https://sns.ap-south-1.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=XXXXX,
            type: Notification,signatureVersion: 1,
            signature:XXXX,
            timestamp: 2022-09-28T19:12:14.904Z,
            topicArn: arn:aws:sns:ap-south-1:244544013937:MyFirstTopic},eventVersion: 1.0,eventSource: aws:sns,
            eventSubscriptionArn: arn:aws:sns:ap-south-1:244544013937:MyFirstTopic:09f66101-d3b9-4a49-b84f-921c30527253}]}

         */
        return "Event Received";
    }

}