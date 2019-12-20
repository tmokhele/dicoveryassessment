package com.discovery.assesment.helper;

import com.discovery.assesment.model.PlanetNames;
import com.discovery.assesment.model.Routes;
import com.discovery.assesment.model.Traffic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PathHelper {
    private List<PlanetNames> planets;
    private List<Routes> routes;
    private List<Traffic> traffics;
    private boolean undirectedGraph;
    private boolean trafficAllowed;

    public void processTraffics() {
        if (traffics != null && !traffics.isEmpty()) {
            for (Traffic traffic : traffics) {
                for (Routes route : routes) {
                    if (route.getRouteId().toString().equalsIgnoreCase(traffic.getRouteId().toString())) {
                        if (route.getPlanetOrigin().equalsIgnoreCase(traffic.getPlanetOrigin()) && route.getPlanetDestination().equalsIgnoreCase(traffic.getPlanetDestination())) {
                            route.setTimeDelay(traffic.getTrafficDelay());
                        }
                    }
                }
            }
        }
    }
    public List<Routes> getUndirectedEdges() {
        List<Routes> undirectedEdges = new ArrayList();
        for (Routes fromEdge : routes) {
            Routes toEdge = copyAdjacentEdge(fromEdge);
            undirectedEdges.add(fromEdge);
            undirectedEdges.add(toEdge);
        }
        return undirectedEdges;
    }

    public Routes copyAdjacentEdge(Routes fromEdge) {
        Routes toEdge = new Routes();
        toEdge.setRouteId(fromEdge.getRouteId());
        toEdge.setPlanetOrigin(fromEdge.getPlanetOrigin());
        toEdge.setPlanetDestination(fromEdge.getPlanetDestination());
        toEdge.setDistance(fromEdge.getDistance());
        toEdge.setTimeDelay(fromEdge.getTimeDelay());
        return toEdge;
    }

}
