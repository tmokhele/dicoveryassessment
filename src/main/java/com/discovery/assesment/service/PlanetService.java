package com.discovery.assesment.service;

import com.discovery.assesment.model.PlanetNames;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;


import java.util.List;

public interface PlanetService {
    boolean savePlanets(List<PlanetNames> planets);
    @Cacheable(value = "planets")
    List<PlanetNames> getAll();
    @CacheEvict(value = "planets", allEntries = true)
    PlanetNames savePlanet(PlanetNames planetNames);
    @CacheEvict(value = "planets", allEntries = true)
    boolean deletePlanet(String id);
    @CacheEvict(value = "planets", allEntries = true)
    PlanetNames updatePlanet(PlanetNames planetNames);
}
