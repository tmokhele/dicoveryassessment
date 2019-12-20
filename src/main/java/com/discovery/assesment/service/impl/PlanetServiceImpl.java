package com.discovery.assesment.service.impl;

import com.discovery.assesment.model.PlanetNames;
import com.discovery.assesment.repo.PlanetNamesRepository;
import com.discovery.assesment.service.PlanetService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlanetServiceImpl implements PlanetService {
    PlanetNamesRepository planetNamesRepository;

    public PlanetServiceImpl(PlanetNamesRepository planetNamesRepository) {
        this.planetNamesRepository = planetNamesRepository;
    }

    @Override
    public boolean savePlanets(List<PlanetNames> planets) {
        planetNamesRepository.saveAll(planets);
        return false;
    }

    @Override
    public List<PlanetNames> getAll() {
        List<PlanetNames> list = new ArrayList<>();
        planetNamesRepository.findAll().forEach(list::add);
        return list;
    }

    @Override
    public PlanetNames savePlanet(PlanetNames planetNames) {
        return planetNamesRepository.save(planetNames);
    }

    @Override
    public boolean deletePlanet(String id) {
        planetNamesRepository.deleteById(Long.valueOf(id));
        return true;
    }

    @Override
    public PlanetNames updatePlanet(PlanetNames planetNames) {
        return planetNamesRepository.save(planetNames);
    }
}
