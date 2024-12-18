package com.epam.training.service;

import com.epam.training.dto.deployment.DeploymentResponse;

public interface DialApiProxy {

    DeploymentResponse getDeployments();
}
