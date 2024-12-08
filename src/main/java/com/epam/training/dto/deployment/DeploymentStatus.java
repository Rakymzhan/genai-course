package com.epam.training.dto.deployment;

import lombok.Getter;

@Getter
public enum DeploymentStatus {

    SUCCESS("succeeded");

    private final String status;

    DeploymentStatus(String status) {
        this.status = status;
    }
}
