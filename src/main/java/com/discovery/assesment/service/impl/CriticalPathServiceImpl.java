package com.discovery.assesment.service.impl;

import com.discovery.assesment.helper.PathHelper;
import com.discovery.assesment.model.Planet;
import com.discovery.assesment.model.PlanetNames;
import com.discovery.assesment.model.Routes;
import com.discovery.assesment.model.Traffic;
import com.discovery.assesment.service.CriticalPathService;
import com.discovery.assesment.service.PlanetService;
import com.discovery.assesment.service.RouteService;
import com.discovery.assesment.service.TrafficService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CriticalPathServiceImpl implements CriticalPathService {
    RouteService routeService;
    TrafficService trafficService;
    PlanetService planetService;
    private List<PlanetNames> planets;
    private List<Routes> routes;
    private List<Routes> edges;
    private Set<PlanetNames> visitedVertices;
    private Set<PlanetNames> unvisitedVertices = new HashSet<>();
    private Map<PlanetNames, PlanetNames> previousPaths;
    private Map<PlanetNames, Double> distance;

    public CriticalPathServiceImpl(RouteService routeService, TrafficService trafficService, PlanetService planetService) {
        this.routeService = routeService;
        this.planetService = planetService;
        this.trafficService = trafficService;
    }

    @Override
    public String calculatePath(Planet planetNames) {
        StringBuilder path = new StringBuilder();
        List<PlanetNames> planets = planetService.getAll();
        List<Routes> allRoutes = routeService.getAllRoutes();
        List<Traffic> traffics = trafficService.getAll();
        PathHelper pathHelper = initializeHelper(planets, allRoutes, traffics);
        pathHelper.processTraffics();
        PlanetNames source = planets.get(0);
        PlanetNames destination = getSelectedDestinationByNode(planetNames, planets);
        planetNames.setPlanetName(source.getPlanetName());
        planetNames.setEndPlanet(destination.getPlanetName());
        if (planetNames.isTrafficAllowed()) {
            pathHelper.setTrafficAllowed(true);
        }
        if (planetNames.isUndirectedGraph()) {
            pathHelper.setUndirectedGraph(true);
        }

        this.planets = new ArrayList<>(pathHelper.getPlanets());
        if (pathHelper.isTrafficAllowed()) {
            pathHelper.processTraffics();
        }
        if (pathHelper.isUndirectedGraph()) {
            this.edges = new ArrayList<>(pathHelper.getUndirectedEdges());
        } else {
            this.edges = new ArrayList<>(pathHelper.getRoutes());
        }

        initializePlanets(pathHelper);
        this.run(source);
        mapCriticalPath(path, destination);

        return path.toString();
    }

    public PlanetNames getSelectedDestinationByNode(Planet planetNames, List<PlanetNames> planets) {
        Optional<PlanetNames> any = planets.stream()
                .filter(planetNames1 -> planetNames1.getId().toString().equalsIgnoreCase(planetNames.getSelectedPlanet()))
                .findAny();
        return any.orElse(null);
    }

    public PathHelper initializeHelper(List<PlanetNames> planets, List<Routes> allRoutes, List<Traffic> traffics) {
        return new PathHelper(planets,
                    allRoutes, traffics, false, false);
    }

    public void mapCriticalPath(StringBuilder path, PlanetNames destination) {
        LinkedList<PlanetNames> paths = getPath(destination);
        if (paths != null) {
            for (PlanetNames v : paths) {
                path.append(v.getPlanetName()).append(" (").append(v.getPlanetNode()).append(")");
                path.append("\t");
            }
        } else {
            StringBuilder append = path.append("PATH_NOT_AVAILABLE" + destination.getPlanetName());
            path.append(".");
        }
    }

    private LinkedList<PlanetNames> getPath(PlanetNames target) {
        LinkedList<PlanetNames> path = new LinkedList<>();
        PlanetNames step = target;
        if (previousPaths.get(step) == null) return null;
        path.add(step);
        while (previousPaths.get(step) != null) {
            step = previousPaths.get(step);
            path.add(step);
        }
        Collections.reverse(path);
        return path;
    }


    private void run(PlanetNames source) {
        distance = new HashMap<>();
        previousPaths = new HashMap<>();
        visitedVertices = new HashSet<>();
        distance.put(source, 0.0);
        unvisitedVertices.add(source);
        while (unvisitedVertices.size() > 0) {
            PlanetNames currentVertex = getVertexWithLowestDistance(unvisitedVertices);
            visitedVertices.add(currentVertex);
            unvisitedVertices.remove(currentVertex);
            evaluateNeighborsWithMinimalDistances(currentVertex);
        }
    }

    private void evaluateNeighborsWithMinimalDistances(PlanetNames currentVertex) {
        List<PlanetNames> adjacentVertices = getNeighbors(currentVertex);
        for (PlanetNames target : adjacentVertices) {
            Double alternateDistance = getShortestDistance(currentVertex) + getDistance(currentVertex, target);
            if (alternateDistance < getShortestDistance(target)) {
                distance.put(target, alternateDistance);
                previousPaths.put(target, currentVertex);
                unvisitedVertices.add(target);
            }
        }
    }

    private List<PlanetNames> getNeighbors(PlanetNames currentVertex) {
        List<PlanetNames> neighbors = new ArrayList<>();
        for (Routes edge : edges) {
            PlanetNames destination = fromId(edge.getPlanetDestination());
            if (edge.getPlanetOrigin().equals(currentVertex.getPlanetNode()) && !isVisited(destination)) {
                neighbors.add(destination);
            }
        }
        return neighbors;
    }


    public PlanetNames fromId(final String str) {
        for (PlanetNames v : planets) {
            if (v.getPlanetNode().equalsIgnoreCase(str)) {
                return v;
            }
        }
        PlanetNames islandVertex = new PlanetNames();
        islandVertex.setPlanetNode(str);
        islandVertex.setPlanetName("Island " + str);
        return islandVertex;
    }

    private PlanetNames getVertexWithLowestDistance(Set<PlanetNames> vertexes) {
        PlanetNames lowestVertex = null;
        for (PlanetNames vertex : vertexes) {
            if (lowestVertex == null) {
                lowestVertex = vertex;
            } else if (getShortestDistance(vertex) < getShortestDistance(lowestVertex)) lowestVertex = vertex;
        }
        return lowestVertex;
    }

    private boolean isVisited(PlanetNames vertex) {
        return visitedVertices.contains(vertex);
    }

    private Double getShortestDistance(PlanetNames destination) {
        Double d = distance.get(destination);
        if (d == null) {
            return Double.POSITIVE_INFINITY;
        } else {
            return d;
        }
    }

    private Double getDistance(PlanetNames source, PlanetNames target) {
        for (Routes edge : edges) {
            if (edge.getPlanetOrigin().equals(source.getPlanetNode()) && edge.getPlanetDestination().equals(target.getPlanetNode())) {
                return edge.getDistance() + edge.getTimeDelay();
            }
        }
        throw new RuntimeException("Error: Something went wrong!");
    }

    public void initializePlanets(PathHelper pathHelper) {
        this.planets = new ArrayList<>(pathHelper.getPlanets());
        if (pathHelper.isTrafficAllowed()) {
            pathHelper.processTraffics();
        }
        if (pathHelper.isUndirectedGraph()) {
            this.routes = new ArrayList<>(pathHelper.getUndirectedEdges());
        } else {
            this.routes = new ArrayList<>(pathHelper.getRoutes());
        }
    }

}
