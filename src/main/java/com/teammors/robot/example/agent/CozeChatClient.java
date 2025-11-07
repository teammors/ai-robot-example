package com.teammors.robot.example.agent;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CozeChatClient {

    private final OkHttpClient client;
    private final String apiUrl ;
    private final String accessToken;
    private String cozeBotId;
    private String cozeUserId;

    public CozeChatClient(String apiUrl, String accessToken, String defaultBotId, String defaultUserId) {
        this.apiUrl = apiUrl;
        this.accessToken = accessToken;
        this.cozeBotId = defaultBotId;
        this.cozeUserId = defaultUserId;

        log.info("=== Coze config ===");
        log.info("API URL: {}", apiUrl);
        log.info("Access Token: {}", accessToken != null ?
                "Setted(" + accessToken.length() + "char)，Pree 10 char: " + accessToken.substring(0, Math.min(10, accessToken.length())) : "null");
        log.info("Bot ID: {}", defaultBotId);
        log.info("User ID: {}", defaultUserId);
        log.info("==== Coze config ok ===");

        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                .build();
    }

    /**
     * Streaming Coze API - Final Working Version
     */
    public void askCozeStreaming(String userQuery, String botId, String userId,
                                 String conversationId, StreamingCallback callback) {

        String jsonBody = buildRequestBody(userQuery, botId, userId, conversationId, true);

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(RequestBody.create(jsonBody, MediaType.get("application/json")))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .header("Accept", "text/event-stream")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError(new IOException("Unexpected code " + response));
                    return;
                }

                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        BufferedReader reader = new BufferedReader(body.charStream());
                        String line;

                        while ((line = reader.readLine()) != null) {
                            String trimmedLine = line.trim();
                            if (trimmedLine.startsWith("data: ")) {
                                String data = trimmedLine.substring(6).trim();
                                handleData(data, callback);
                            } else if (trimmedLine.startsWith("data:")) {
                                String data = trimmedLine.substring(5).trim();
                                handleData(data, callback);
                            }
                        }
                        callback.onComplete();
                    }
                } catch (Exception e) {
                    callback.onError(e);
                }
            }

            private void handleData(String data, StreamingCallback callback) {
                if (data.equals("[DONE]") || data.equals("\"[DONE]\"")) {
                } else if (!data.isEmpty()) {
                    callback.onData(data);
                }
            }
        });
    }


    /**
     * Extract plain text content from message data
     */
    public String extractTextContent(String data) {
        try {
            // 检查是否是包含实际文本内容的消息
            if (data.contains("\"content\"") &&
                    data.contains("\"type\":\"answer\"") &&
                    data.contains("\"role\":\"assistant\"")) {

                // 提取content字段的值
                int contentStart = data.indexOf("\"content\":\"") + 11;
                if (contentStart > 10) {
                    int contentEnd = data.indexOf("\"", contentStart);
                    if (contentEnd > contentStart) {
                        String content = data.substring(contentStart, contentEnd);
                        // 处理转义字符
                        return content.replace("\\\"", "\"")
                                .replace("\\\\", "\\")
                                .replace("\\n", "\n")
                                .replace("\\r", "\r")
                                .replace("\\t", "\t");
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Build the request body - Corrected according to the Coze API documentation
     */
    private String buildRequestBody(String userQuery, String botId, String userId,
                                    String conversationId, boolean stream) {

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"bot_id\":\"").append(botId).append("\",");
        sb.append("\"user_id\":\"").append(userId).append("\",");
        sb.append("\"stream\":").append(stream).append(",");
        sb.append("\"auto_save_history\":true,");

        if (conversationId != null && !conversationId.trim().isEmpty()) {
            sb.append("\"conversation_id\":\"").append(conversationId).append("\",");
        }

        sb.append("\"additional_messages\":[{");
        sb.append("\"role\":\"user\",");
        sb.append("\"content\":\"").append(escapeJsonString(userQuery)).append("\",");
        sb.append("\"content_type\":\"text\"");  // 添加 content_type
        sb.append("}]");
        sb.append("}");

        String requestBody = sb.toString();


        return requestBody;
    }

    /**
     * Escape special characters in JSON strings
     */
    private String escapeJsonString(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Streaming callback interface
     */
    public interface StreamingCallback {
        void onData(String data);
        void onError(Exception e);
        void onComplete();
    }


    public String waitAIResponse(String question) {
        CountDownLatch latch = new CountDownLatch(1);
        final StringBuilder completeMessage = new StringBuilder();

        try {
            log.info("=== Start Coze API ===");
            askCozeStreaming(question, cozeBotId, cozeUserId, null, new StreamingCallback() {
                @Override
                public void onData(String data) {
                    String textContent = extractTextContent(data);
                    if (textContent != null && !textContent.isEmpty()) {

                        if (textContent.contains("。") || textContent.length() > 10) {
                            completeMessage.setLength(0);
                            completeMessage.append(textContent);

                        } else {
                            completeMessage.append(textContent);

                        }
                    }
                }

                @Override
                public void onError(Exception e) {
                    log.error("Coze API Error: {}", e.getMessage());
                    latch.countDown();
                }

                @Override
                public void onComplete() {
                    log.info("=== Coze API Ok ===");
                    latch.countDown();
                }
            });

            latch.await(30, TimeUnit.SECONDS);

        } catch (Exception e) {
            log.error("An exception occurred while waiting for AI response.: ", e);
        }

        return completeMessage.toString();
    }


    public static void main(String[] args) {

        String cozeApiUrl = "https://api.coze.cn/v3/chat";
        String cozeAccessToken = "your-access-token";
        String cozeBotId = "bot-id";
        String cozeUserId = "user-id";

        CozeChatClient client = new CozeChatClient(cozeApiUrl, cozeAccessToken, cozeBotId, cozeUserId);

        try {

            log.info("=== Final Test ===");

            CountDownLatch latch = new CountDownLatch(1);
            final StringBuilder completeMessage = new StringBuilder();

            client.askCozeStreaming("Please introduce Teammors.", cozeBotId, cozeUserId, null, new StreamingCallback() {
                @Override
                public void onData(String data) {
                    String textContent = client.extractTextContent(data);
                    if (textContent != null && !textContent.isEmpty()) {
                        completeMessage.append(textContent);
                    }
                }

                @Override
                public void onError(Exception e) {
                    log.info(e.getMessage());
                    latch.countDown();
                }

                @Override
                public void onComplete() {
                    log.info("\n\n=== Full reply ===");
                    log.info(completeMessage.toString());
                    latch.countDown();
                }
            });

            latch.await(30, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
