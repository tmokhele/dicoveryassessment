package com.discovery.assesment.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Routes")
@Data
public class Routes {
    @Id
    private Long routeId;
    private String planetOrigin;
    private String planetDestination;
    private double distance;
    public double timeDelay;
}
