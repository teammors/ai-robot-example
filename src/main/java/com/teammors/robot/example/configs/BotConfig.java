package com.teammors.robot.example.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ai-config")
public class BotConfig {

    // Robot token
    private String robotToken;

    //Agent Type
    private int agentType;

    // Dify
    private DifyConfig dify;

    // Coze
    private CozeConfig coze;

    // FastGPT
    private FastGPTConfig fastgpt;

    private SpringAIConfig springAI;

    // Dify
    public static class DifyConfig {
        private String apiUrl;
        private String apiKey;

        // getters and setters
        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    }

    // Coze
    public static class CozeConfig {
        private String apiUrl;
        private String accessToken;
        private String botId;
        private String userId;

        // getters and setters
        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

        public String getBotId() { return botId; }
        public void setBotId(String botId) { this.botId = botId; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }

    // FastGPT
    public static class FastGPTConfig {
        private String apiUrl;
        private String apiKey;

        // getters and setters
        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    }

    // SpringAI
    public static class SpringAIConfig {
        private String apiUrl;
        private String apiKey;

        // getters and setters
        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    }

    // getters and setters for main class
    public String getRobotToken() { return robotToken; }
    public void setRobotToken(String robotToken) { this.robotToken = robotToken; }

    public int getAgentType() { return agentType; }
    public void setAgentType(int agentType) { this.agentType = agentType; }

    public DifyConfig getDify() { return dify; }
    public void setDify(DifyConfig dify) { this.dify = dify; }

    public CozeConfig getCoze() { return coze; }
    public void setCoze(CozeConfig coze) { this.coze = coze; }

    public FastGPTConfig getFastgpt() { return fastgpt; }
    public void setFastgpt(FastGPTConfig fastgpt) { this.fastgpt = fastgpt; }

    public SpringAIConfig getSpringAI() { return springAI; }
    public void setSpringAI(SpringAIConfig springAIConfig) {
        this.springAI =  springAIConfig;
    }
}