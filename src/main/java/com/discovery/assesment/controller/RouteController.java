package com.discovery.assesment.controller;

import com.discovery.assesment.model.Planet;
import com.discovery.assesment.model.Routes;
import com.discovery.assesment.service.RouteService;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/route")
public class RouteController {

    RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public ModelAndView showRouteList(ModelMap modelMap) {
        final List<Routes> all = routeService.getAllRoutes();
        ModelAndView modelAndView = new ModelAndView("routes");
        modelAndView.addObject("routes", all);
        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteRouteByRouteId(@PathVariable String id) {
        boolean routeDeleted = routeService.deleteRoute(id);
        ModelAndView modelAndView = new ModelAndView();
        final List<Routes> all = routeService.getAllRoutes();
        if (routeDeleted) {
            Optional<Routes> any = all.stream().filter(routes -> routes.getRouteId().toString().equalsIgnoreCase(id)).findAny();
            all.remove(any);
            modelAndView.setViewName("routes");
            modelAndView.addObject("routes", all);
            return modelAndView;
        }
        modelAndView.addObject("validationMessage", "Failed to remove route please try again");
        modelAndView.setViewName("error");
        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editRoute(@PathVariable String id) {
        Planet routeModel = new Planet();
        ModelAndView modelAndView = new ModelAndView();
        List<Routes> allRoutes = routeService.getAllRoutes();
        Optional<Routes> routeToEdit = allRoutes.stream().filter(routes -> routes.getRouteId().toString().equalsIgnoreCase(id)).findAny();
        routeModel.setSource(routeToEdit.get().getPlanetOrigin());
        routeModel.setDestination(routeToEdit.get().getPlanetDestination());
        routeModel.setDistance(routeToEdit.get().getDistance());
        modelAndView.addObject("route", routeToEdit.get());
        modelAndView.addObject("routeModel", routeModel);
        modelAndView.addObject("routeList", allRoutes);
        modelAndView.setViewName("routeupdate");
        return modelAndView;
    }

    @PostMapping
    public ModelAndView addRoute(Routes routes, @ModelAttribute Planet routeModel, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        if (validateRoute(routeModel, modelAndView)) return modelAndView;
        final List<Routes> all = routeService.getAllRoutes();
        populateRouteDetails(routes,all,routeModel);
        Optional<Routes> any = all.stream().filter(routes1 -> routes1.getPlanetOrigin().equalsIgnoreCase(routes.getPlanetOrigin())
                && routes1.getPlanetDestination().equalsIgnoreCase(routes.getPlanetDestination())).findAny();
        if (any.isPresent()) {
            modelAndView.setViewName("error");
            modelAndView.addObject("validationMessage", "Route already exist please verify your entry and try again");
            return modelAndView;
        }
        Routes routes1 = routeService.saveRoute(routes);
        all.add(routes1);
        modelAndView.addObject("routes", all);
        modelAndView.setViewName("routes");
        return modelAndView;
    }

    public boolean validateRoute(@ModelAttribute Planet routeModel, ModelAndView modelAndView) {
        if (routeModel.getSource().equalsIgnoreCase(routeModel.getDestination())) {
            modelAndView.addObject("validationMessage", "Route Source and Destination should not be the same");
            modelAndView.setViewName("error");
            return true;
        }
        return false;
    }

    @PostMapping("/update")
    public ModelAndView updateRoute(Routes routes, @ModelAttribute Planet routeModel, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        routes.setPlanetOrigin(routeModel.getSource());
        routes.setPlanetDestination(routeModel.getDestination());
        routes.setDistance(routeModel.getDistance());
        if (validateRoute(routeModel, modelAndView)) return modelAndView;
        routeService.saveRoute(routes);
        final List<Routes> all = routeService.getAllRoutes();
        modelAndView.setViewName("routes");
        modelAndView.addObject("routes", all);
        return modelAndView;
    }

    private void populateRouteDetails(Routes routes,List<Routes> all,Planet routeModel)
    {
        Long routeId = all
                .stream()
                .max(Comparator.comparing(Routes::getRouteId)).get().getRouteId()+1;
        routes.setRouteId(routeId);
        routes.setPlanetDestination(routeModel.getDestination());
        routes.setPlanetOrigin(routeModel.getSource());
        routes.setPlanetDestination(routeModel.getDestination());
    }
}
