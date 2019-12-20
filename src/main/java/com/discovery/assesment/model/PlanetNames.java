package com.discovery.assesment.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "PlanetNames")
@Data
public class PlanetNames {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String planetName;
    private String planetNode;
}
