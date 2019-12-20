package com.discovery.assesment.controller;

import com.discovery.assesment.model.Planet;
import com.discovery.assesment.model.Traffic;
import com.discovery.assesment.service.TrafficService;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/traffic")
public class TrafficController {

    TrafficService trafficService;

    public TrafficController(TrafficService trafficService) {
        this.trafficService = trafficService;
    }

    @GetMapping
    public ModelAndView showTrafficList(ModelMap modelMap) {
        final List<Traffic> all = trafficService.getAll();
        ModelAndView modelAndView = new ModelAndView("traffics");
        modelAndView.addObject("traffics", all);
        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView getById(@PathVariable String id) {
        Planet trafficModel = new Planet();
        List<Traffic> all = trafficService.getAll();
        Optional<Traffic> traffic = all.stream().filter(routes -> routes.getRouteId().toString().equalsIgnoreCase(id)).findAny();
        ModelAndView modelAndView = new ModelAndView("trafficupdate");
        modelAndView.addObject("traffic", traffic.get());
        modelAndView.addObject("trafficModel", trafficModel);
        modelAndView.addObject("trafficList",all);
        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteTraffic(@PathVariable String id)
    {
       boolean deleteTraffic =  trafficService.deleteTraffic(id);
        ModelAndView modelAndView = new ModelAndView();
        if (deleteTraffic) {
            List<Traffic> all = trafficService.getAll();
            modelAndView.setViewName("traffics");
            modelAndView.addObject("traffics", all);
            return modelAndView;
        }
        modelAndView.addObject("validationMessage", "Failed to remove traffic please try again");
        modelAndView.setViewName("error");
        return modelAndView;
    }

    @RequestMapping("/new")
    public ModelAndView addTraffic(Traffic traffic) {
        List<Traffic> all = trafficService.getAll();
        ModelAndView modelAndView = new ModelAndView("trafficadd");
        modelAndView.addObject("traffic", new Traffic());
        modelAndView.addObject("trafficList", all);
        return modelAndView;
    }

    @PostMapping("/update")
    public ModelAndView updateTraffic(Traffic traffic, @ModelAttribute Planet pathModel, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        traffic.setPlanetDestination(pathModel.getDestination());
        traffic.setPlanetOrigin(pathModel.getSource());
        if (validateRequest(traffic, modelAndView, null)) return modelAndView;

        trafficService.save(traffic);
        List<Traffic> all = trafficService.getAll();
        modelAndView.addObject("traffics", all);
        modelAndView.setViewName("traffics");
        return modelAndView;
    }

    @PostMapping
    public ModelAndView addTraffic(@ModelAttribute Traffic traffic, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        List<Traffic> all = trafficService.getAll();
        populateTrafficDetails(traffic, all);
        Optional<Traffic> any = all.stream().filter(routes1 -> routes1.getPlanetOrigin().equalsIgnoreCase(traffic.getPlanetOrigin())
                && routes1.getPlanetDestination().equalsIgnoreCase(traffic.getPlanetDestination())).findAny();

        if (validateRequest(traffic, modelAndView, any)) return modelAndView;

        Traffic traffic1 = trafficService.save(traffic);
        all.add(traffic1);
        modelAndView.addObject("traffics", all);
        modelAndView.setViewName("traffics");
        return modelAndView;
    }

    public boolean validateRequest(@ModelAttribute Traffic traffic, ModelAndView modelAndView, Optional<Traffic> any) {
        if (any!=null && any.isPresent()) {
            modelAndView.setViewName("error");
            modelAndView.addObject("validationMessage", "Traffic already exist please verify your entry and try again");
            return true;
        }

        if (traffic!=null && traffic.getPlanetDestination().equalsIgnoreCase(traffic.getPlanetOrigin()))
        {
            modelAndView.setViewName("error");
            modelAndView.addObject("validationMessage", "Source and destination should not be the same");
            return true;
        }
        return false;
    }

    private void populateTrafficDetails(Traffic traffic, List<Traffic> all) {
        Long routeId = all
                .stream()
                .max(Comparator.comparing(Traffic::getRouteId)).get().getRouteId() + 1;
        traffic.setRouteId(routeId);
    }
}
