package com.discovery.assesment.service;

import com.discovery.assesment.model.Routes;
import com.discovery.assesment.model.Node;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public interface RouteService {
    boolean saveRoutes(List<Routes> routes);
    TreeMap<String, Set<Node>> getAll();
    @Cacheable(value = "routes")
    List<Routes> getAllRoutes();
    @CacheEvict(value = "routes", allEntries = true)
    boolean deleteRoute(String id);
    @CacheEvict(value = "routes", allEntries = true)
    Routes saveRoute(Routes routes);
}
