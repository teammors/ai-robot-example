package com.teammors.robot.example.agent;

import com.alibaba.fastjson.JSONObject;
import com.teammors.robot.example.utils.UnicodeDecoder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DifyChatClient {
    private final OkHttpClient client;
    private final String apiUrl;
    private final String apiKey;

    public DifyChatClient(String apiUrl, String apiKey) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;

        log.info("=== Dify config ===");
        log.info("API URL: {}", apiUrl);
        log.info("API Key: {}", apiKey);
        log.info("==== Dify config ok ===");

        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                .build();
    }

    /**
     * Synchronous call to Dify Customer Service API
     */
    public String askDifyAgentSync(String userQuery) throws IOException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("inputs", new JSONObject());
        requestBody.put("query", userQuery);
        requestBody.put("response_mode", "blocking");
        requestBody.put("user", "teammors-robot");

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
            return response.body().string();
        }
    }

    /**
     * Synchronous call and return of the decoded answer
     */
    public String askDifyAndDecodeSync(String userQuery) throws IOException {
        String response = askDifyAgentSync(userQuery);
        return UnicodeDecoder.parseAndDecodeAnswer(response);
    }

    /**
     * Parse the complete Dify response
     */
    public DifyResponse parseDifyResponse(String jsonResponse) {
        try {
            JSONObject json = JSONObject.parseObject(jsonResponse);
            if (json == null) {
                throw new RuntimeException("Invalid JSON response");
            }

            DifyResponse response = new DifyResponse();
            response.setEvent(json.getString("event"));
            response.setTaskId(json.getString("task_id"));
            response.setMessageId(json.getString("message_id"));
            response.setConversationId(json.getString("conversation_id"));

            // Decoding the answer field
            String encodedAnswer = json.getString("answer");
            response.setRawAnswer(encodedAnswer);
            response.setAnswer(UnicodeDecoder.decodeUnicode(encodedAnswer));

            // Parse metadata
            JSONObject metadata = json.getJSONObject("metadata");
            if (metadata != null) {
                JSONObject usage = metadata.getJSONObject("usage");
                if (usage != null) {
                    response.setPromptTokens(usage.getInteger("prompt_tokens"));
                    response.setCompletionTokens(usage.getInteger("completion_tokens"));
                    response.setTotalTokens(usage.getInteger("total_tokens"));
                    response.setTotalPrice(usage.getDouble("total_price"));
                    response.setCurrency(usage.getString("currency"));
                }
            }

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Dify response: " + e.getMessage(), e);
        }
    }

    public String waitAIResponse(String question) {
        String decodedAnswer = "";
        try {
            decodedAnswer = askDifyAndDecodeSync(question);
        } catch (Exception e) {
            log.error("An exception occurred while waiting for AI response: ", e);
        }
        return decodedAnswer;
    }

    public static void main(String[] args) {
        DifyChatClient client = new DifyChatClient(
                "http://<dAPI domain name or IP address>/v1/chat-messages",
                "your-api-key"
        );

        try {
            String decodedAnswer = client.askDifyAndDecodeSync("Introduce Teammors");
            System.out.println(decodedAnswer);

            String rawResponse = client.askDifyAgentSync("What's the weather like today?");

            DifyResponse parsedResponse = client.parseDifyResponse(rawResponse);
            System.out.println("Parsing result:");
            System.out.println("Conversation ID: " + parsedResponse.getConversationId());
            System.out.println("Message ID: " + parsedResponse.getMessageId());
            System.out.println("Raw encoded answer: " + parsedResponse.getRawAnswer());
            System.out.println("Decoded answer: " + parsedResponse.getAnswer());
            System.out.println("Total tokens: " + parsedResponse.getTotalTokens());
            System.out.println("Total cost: " + parsedResponse.getTotalPrice() + " " + parsedResponse.getCurrency());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Dify response data class
     */
    public static class DifyResponse {
        private String event;
        private String taskId;
        private String messageId;
        private String conversationId;
        private String answer;
        private String rawAnswer;
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
        private Double totalPrice;
        private String currency;

        // getters and setters
        public String getEvent() { return event; }
        public void setEvent(String event) { this.event = event; }
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        public String getRawAnswer() { return rawAnswer; }
        public void setRawAnswer(String rawAnswer) { this.rawAnswer = rawAnswer; }
        public Integer getPromptTokens() { return promptTokens; }
        public void setPromptTokens(Integer promptTokens) { this.promptTokens = promptTokens; }
        public Integer getCompletionTokens() { return completionTokens; }
        public void setCompletionTokens(Integer completionTokens) { this.completionTokens = completionTokens; }
        public Integer getTotalTokens() { return totalTokens; }
        public void setTotalTokens(Integer totalTokens) { this.totalTokens = totalTokens; }
        public Double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        @Override
        public String toString() {
            return String.format(
                    "DifyResponse[\n  Answer: %s\n  Conversation ID: %s\n  Token usage: %d\n  Cost: %.6f %s\n]",
                    answer, conversationId, totalTokens, totalPrice, currency
            );
        }
    }

    // Other methods remain unchanged...
    public void askDifyAgentAsync(String userQuery, Callback callback) {
        // Implementation remains unchanged
    }

    public void askDifyAgentStreaming(String userQuery, StreamingCallback callback) {
        // Implementation remains unchanged
    }

    public interface StreamingCallback {
        void onData(String data);
        void onError(Exception e);
        void onComplete();
    }
}