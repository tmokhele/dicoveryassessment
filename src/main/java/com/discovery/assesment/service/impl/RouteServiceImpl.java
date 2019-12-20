package com.discovery.assesment.service.impl;

import com.discovery.assesment.model.Routes;
import com.discovery.assesment.model.Node;
import com.discovery.assesment.repo.RoutesRepository;
import com.discovery.assesment.service.RouteService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouteServiceImpl implements RouteService {
    RoutesRepository routesRepository;

    public RouteServiceImpl(RoutesRepository routesRepository) {
        this.routesRepository = routesRepository;
    }

    @Override
    public boolean saveRoutes(List<Routes> routes) {
        routesRepository.saveAll(routes);
        return true;
    }

    @Override
    public TreeMap<String, Set<Node>> getAll() {
        List<Routes> list = new ArrayList<>();
        routesRepository.findAll().forEach(list::add);
        return mapRoutes(list);

    }

    @Override
    public List<Routes> getAllRoutes() {
        List<Routes> list = new ArrayList<>();
        routesRepository.findAll().forEach(list::add);
        return list;
    }

    @Override
    public boolean deleteRoute(String id) {
        routesRepository.deleteById(Long.valueOf(id));
        return true;
    }

    @Override
    public Routes saveRoute(Routes routes) {
        return routesRepository.save(routes);
    }

    private TreeMap<String, Set<Node>> mapRoutes(List<Routes> list) {
        TreeMap<String, Set<Node>> stringSetMap = new TreeMap<>();
        for (Routes routes : list) {
            if (!stringSetMap.containsKey(routes.getPlanetOrigin())) {
                Set<Node> nodes = new HashSet<>();
                Node node = new Node();
                node.setDistance(routes.getDistance());
                node.setName(routes.getPlanetDestination());
                nodes.add(node);
                stringSetMap.put(routes.getPlanetOrigin(), nodes);
            } else {
                Set<Node> nodes = stringSetMap.get(routes.getPlanetOrigin());
                Node node = new Node();
                node.setDistance(routes.getDistance());
                node.setName(routes.getPlanetDestination());
                nodes.add(node);
            }
        }
        return mapNeighbouringNodes(stringSetMap);
    }

    private TreeMap<String, Set<Node>> mapNeighbouringNodes(TreeMap<String, Set<Node>> stringSetMap) {
        HashSet<Node> dependencies = null;
        for (Map.Entry<String, Set<Node>> entry : stringSetMap.entrySet()) {
            String key = entry.getKey();
            for (Node node : stringSetMap.get(key)) {
                dependencies = new HashSet<>();
                Set<Node> tasks1 = stringSetMap.get(node.toString());
                if (tasks1 != null) {
                    dependencies.addAll(tasks1);
                    node.setNeighbouringNodes(dependencies);
                }
            }
        }
        return stringSetMap;
    }
}
