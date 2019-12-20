package com.discovery.assesment.model;

import lombok.Data;

@Data
public class Planet {
    private String source ="A";
    private String destination;
    private String selectedPlanet;
    private String id;
    private String planetName;
    private String endPlanet;
    private String thePath;
    private boolean undirectedGraph;
    private boolean trafficAllowed;
    private double distance;
}
