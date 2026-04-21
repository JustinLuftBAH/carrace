package com.carrace.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    
    @Value("${kafka.topic.race-events:race-events}")
    private String raceEventsTopic;
    
    @Value("${kafka.topic.bet-events:bet-events}")
    private String betEventsTopic;
    
    @Value("${kafka.topic.payout-events:payout-events}")
    private String payoutEventsTopic;
    
    @Value("${kafka.topic.leaderboard-updates:leaderboard-updates}")
    private String leaderboardUpdatesTopic;
    
    @Bean
    public NewTopic raceEventsTopic() {
        return TopicBuilder.name(raceEventsTopic)
            .partitions(6)
            .replicas(3)
            .build();
    }
    
    @Bean
    public NewTopic betEventsTopic() {
        return TopicBuilder.name(betEventsTopic)
            .partitions(6)
            .replicas(3)
            .build();
    }
    
    @Bean
    public NewTopic payoutEventsTopic() {
        return TopicBuilder.name(payoutEventsTopic)
            .partitions(6)
            .replicas(3)
            .build();
    }
    
    @Bean
    public NewTopic leaderboardUpdatesTopic() {
        return TopicBuilder.name(leaderboardUpdatesTopic)
            .partitions(6)
            .replicas(3)
            .build();
    }
}
