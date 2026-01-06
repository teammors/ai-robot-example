package com.teammors.robot.example.agent;

import com.alibaba.fastjson.JSONObject;
import com.teammors.robot.example.comm.CommParameters;
import com.teammors.robot.example.utils.UnicodeDecoder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SpringAIClient {
    private final OkHttpClient client;
    private final String apiUrl;
    private final String apiKey;

    public SpringAIClient(String apiUrl, String apiKey) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;

        log.info("=== SpringAI config ===");
        log.info("API URL: {}", apiUrl);
        log.info("API Key: {}", apiKey);
        log.info("==== SpringAI config ok ===");

        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                .build();
    }

    /**
     * Synchronous call to SpringAI Customer Service API
     */
    public String askSpringAISync(String userQuery,String userId) throws IOException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("question", userQuery);
        requestBody.put("history", CommParameters.instance().getChatHistory().get(userId));

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(RequestBody.create(
                        requestBody.toString(),
                        MediaType.get("application/json; charset=utf-8")
                ))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                throw new IOException("HTTP " + response.code() + " - " + errorBody);
            }
            JSONObject responseBody = JSONObject.parseObject(response.body().string());

            JSONObject aChatUser = new JSONObject();
            aChatUser.put("role", "user");
            aChatUser.put("content", userQuery);


            JSONObject aChatAns = new JSONObject();
            aChatAns.put("role", "assistant");
            String answer = responseBody.get("answer").toString();
            answer = answer.replaceAll("\\*\\s{3}\\*{2}", "");
            answer = answer.replaceAll("\\*\\*", "");
            answer = answer.replaceAll("###", "");
            aChatAns.put("content", answer);



            if(CommParameters.instance().getChatHistory().containsKey(userId)){
                CommParameters.instance().getChatHistory().get(userId).add(aChatUser);
                CommParameters.instance().getChatHistory().get(userId).add(aChatAns);
            }else {
                List<JSONObject> list = new ArrayList<>();
                list.add(aChatUser);
                list.add(aChatAns);
                CommParameters.instance().getChatHistory().put(userId, list);
            }


            return answer;
        }
    }

}