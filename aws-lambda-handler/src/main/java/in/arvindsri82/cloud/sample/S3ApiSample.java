package in.arvindsri82.cloud.sample;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class S3ApiSample {

    public static void main(String[] args) throws IOException {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).build();

        var s3Object = s3.getObject("arvindsri82-lambda-functions","account_definitions.json");
        byte[] bytes = s3Object.getObjectContent().readAllBytes();

        byte[] resultBytes = toogleActivePassive(bytes,"dev_account_u2");
        var result = new String(resultBytes);
        System.out.println(result);

        s3.putObject("arvindsri82-lambda-functions","account_definitions.json", result);
    }

    private static byte[] toogleActivePassive(byte[] jsonBytes, String account) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        var jsonMap = objectMapper.readValue(jsonBytes, HashMap.class);

        var accountMap = selectAccount(jsonMap, account);
        String activeAccount = accountMap.get("active").toString();
        String passiveAccount = accountMap.get("passive").toString();

        if(!account.equalsIgnoreCase(activeAccount)){
            throw new RuntimeException("Account is not active!");
        }

        // toggle active and passive accounts
        accountMap.put("active",passiveAccount);
        accountMap.put("passive",activeAccount);

        return objectMapper.writeValueAsBytes(jsonMap);
    }

    private static HashMap selectAccount(HashMap jsonMap, final String account) {
        Optional<Object> selected =
                jsonMap.values().stream().filter(x -> ((HashMap)x).containsValue(account)).findFirst();
        if(selected.isEmpty()){
            throw new RuntimeException("Account not found!");
        }
        return (HashMap) selected.get();
    }

}
