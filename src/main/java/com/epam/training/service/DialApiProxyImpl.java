package com.epam.training.service;

import com.epam.training.dto.deployment.Deployment;
import com.epam.training.dto.deployment.DeploymentResponse;
import com.epam.training.dto.deployment.DeploymentStatus;
import com.epam.training.exception.DialApiProxyException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DialApiProxyImpl implements DialApiProxy {

    private static final String LOAD_DEPLOYMENTS_ERROR_MESSAGE = "Unable to retrieve deployment names";

    private static final String API_KEY_HEADER = "Api-Key";

    private final String deploymentsUrl;

    private final String apiKey;

    private final List<String> predefinedDeploymentNames;

    private final ObjectMapper objectMapper;

    public DialApiProxyImpl(@Value("${openai.deployments.endpoint}") String deploymentsUrl,
                            @Value("${openai.key}") String apiKey,
                            @Value("${openai.predefined.deployments}") List<String> predefinedDeploymentNames,
                            ObjectMapper objectMapper) {
        this.deploymentsUrl = deploymentsUrl;
        this.apiKey = apiKey;
        this.predefinedDeploymentNames = predefinedDeploymentNames;
        this.objectMapper = objectMapper;
    }

    @Override
    public DeploymentResponse getDeployments() {
        DeploymentResponse supportedDeployments = loadSupportedDeployments();
        Set<Deployment> deployments = supportedDeployments.data().stream()
                .filter(deployment -> predefinedDeploymentNames.contains(deployment.id()) &&
                        DeploymentStatus.SUCCESS.getStatus().equals(deployment.status()))
                .collect(Collectors.toSet());

        if (deployments.isEmpty()) {
            throw new DialApiProxyException("Deployments are not available");
        }

        return new DeploymentResponse(deployments);
    }

    private DeploymentResponse loadSupportedDeployments() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(deploymentsUrl))
                .header(API_KEY_HEADER, apiKey)
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), DeploymentResponse.class);
        } catch (Exception ex) {
            log.error(LOAD_DEPLOYMENTS_ERROR_MESSAGE, ex);
            throw new DialApiProxyException(LOAD_DEPLOYMENTS_ERROR_MESSAGE, ex);
        }
    }
}
