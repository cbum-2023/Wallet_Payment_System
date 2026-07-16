package com.walletsystem.project.Wallet.Payment.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data   // generates getters, setters, toString, equals, hashCode
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage implements Serializable {
    private String to;
    private String subject;
    private String body;
}