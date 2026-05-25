package com.company.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JobMessage implements Serializable {

    private UUID jobId;
    private String jobType;
    private String parameter;
    private long timestamp;

    // Конструкторы, геттеры и сеттеры
    public JobMessage() {}

    public JobMessage(String jobType, String parameter) {
        this.jobId = UUID.randomUUID();
        this.jobType = jobType;
        this.parameter = parameter;
        this.timestamp = System.currentTimeMillis();
    }

    public Object getJobType() {
        return jobType;
    }

    public Object getParameter() {
        return parameter;
    }

    public Object getJobId() {
        return jobId;
    }

    // Геттеры и сеттеры для всех полей...
}