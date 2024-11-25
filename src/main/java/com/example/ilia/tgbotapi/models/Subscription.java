package com.example.ilia.tgbotapi.models;

import com.example.ilia.tgbotapi.SubscriptionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Subscription {
    @Id
    private String id;

    @DBRef
    private User owner;

    private SubscriptionType type;

    private LocalDateTime expirationDate;



}
