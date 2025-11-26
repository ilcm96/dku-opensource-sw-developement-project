package com.dku.project.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpClient;

@Configuration
public class ChatClientConfig {

    @Value("${llm.model}")
    private String model;

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.build();
    }

    @Bean
    @Primary
    public WebClient.Builder openAiWebClientBuilder(HttpClient openAiHttpClient) {
        return WebClient.builder().clientConnector(new JdkClientHttpConnector(openAiHttpClient));
    }

    @Bean
    public HttpClient openAiHttpClient() {
        return HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    }

    @Bean
    @Primary
    public RestClient.Builder openAiRestClientBuilder(HttpClient openAiHttpClient) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(openAiHttpClient);
        return RestClient.builder().requestFactory(requestFactory);
    }

    @Bean
    public OpenAiChatOptions streamChatOptions() {
        return OpenAiChatOptions.builder()
                .model(model)
                .streamUsage(true)
                .build();
    }

    @Bean
    public OpenAiChatOptions nonStreamChatOptions() {
        return OpenAiChatOptions.builder()
                .model(model)
                .streamUsage(false)
                .build();
    }
}
