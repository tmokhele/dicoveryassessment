package com.discovery.assesment.model;

import lombok.Data;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Traffic")
@Data
public class Traffic {
    @Id
    private Long routeId;
    private String planetOrigin;
    private String planetDestination;
    private double trafficDelay;
}
