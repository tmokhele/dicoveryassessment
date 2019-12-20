package com.discovery.assesment.service;

import com.discovery.assesment.model.Traffic;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface TrafficService {
    @CacheEvict(value = "traffic", allEntries = true)
    boolean saveAll(List<Traffic> trafficList);
    @Cacheable(value = "traffic")
    List<Traffic> getAll();
    @CacheEvict(value = "traffic", allEntries = true)
    Traffic save(Traffic traffic);
    @CacheEvict(value = "traffic", allEntries = true)
    boolean deleteTraffic(String id);
}
