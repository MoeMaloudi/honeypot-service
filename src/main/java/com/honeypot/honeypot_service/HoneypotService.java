package com.honeypot.honeypot_service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HoneypotService {

    private final DockerClient dockerClient;
    private int nextPort = 2222;

    public HoneypotService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public Map<String, String> createHoneypot() {
        int port = nextPort++;

        ExposedPort sshPort = ExposedPort.tcp(2222);
        Ports portBindings = new Ports();
        portBindings.bind(sshPort, Ports.Binding.bindPort(port));

        CreateContainerResponse container = dockerClient.createContainerCmd("cowrie/cowrie")
                .withExposedPorts(sshPort)
                .withHostConfig(HostConfig.newHostConfig().withPortBindings(portBindings))
                .withLabels(Map.of(
                    "app", "honeypot",
                    "port", String.valueOf(port)
                ))
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();

        Map<String, String> result = new HashMap<>();
        result.put("id", container.getId());
        result.put("port", String.valueOf(port));
        result.put("status", "running");

        return result;
    }

    public List<Container> listHoneypots() {
        return dockerClient.listContainersCmd()
                .withLabelFilter(Map.of("app", "honeypot"))
                .exec();
    }

    public void stopHoneypot(String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).exec();
    }
}