package com.discovery.assesment.controller;

import com.discovery.assesment.model.Planet;
import com.discovery.assesment.model.PlanetNames;
import com.discovery.assesment.model.Routes;
import com.discovery.assesment.service.CriticalPathService;
import com.discovery.assesment.service.PlanetService;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("api/route")
public class CriticalPathMapController {

    PlanetService planetService;
    CriticalPathService criticalPathService;

    public CriticalPathMapController(PlanetService planetService,  CriticalPathService criticalPathService) {
        this.planetService = planetService;
        this.criticalPathService = criticalPathService;
    }

    @RequestMapping("/home")
    public ModelAndView showWelcomePage(ModelMap modelMap) {
        final List<PlanetNames> all = planetService.getAll();
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("selectedPlanet", new Planet());
        modelAndView.addObject("planets", all);
        return modelAndView;
    }

    @RequestMapping("/criticalpath")
    public ModelAndView showCriticalPathPage(ModelMap modelMap) {
        final List<PlanetNames> all = planetService.getAll();
        PlanetNames planetNames = all.get(0);
        ModelAndView modelAndView = new ModelAndView("criticalpath");
        Planet planet = new Planet();
        planet.setPlanetName(planetNames.getPlanetName());
        modelAndView.addObject("planet", planet);
        modelAndView.addObject("planets", all);

        return modelAndView;
    }
    @RequestMapping("/planet")
    public ModelAndView addPlanet(ModelMap modelMap)
    {
        PlanetNames planet = new PlanetNames();
        ModelAndView modelAndView = new ModelAndView("planetadd");
        modelAndView.addObject("planet",planet);
        return modelAndView;
    }

    @RequestMapping("/new")
    public ModelAndView addRoute(ModelMap modelMap)
    {
        ModelAndView modelAndView = new ModelAndView("routeadd");
        Planet sh = new Planet();
        final List<PlanetNames> all = planetService.getAll();
        modelAndView.addObject("edge", new Routes());
        modelAndView.addObject("edgeModel", sh);
        modelAndView.addObject("routeList", all);
        return modelAndView;
    }

    @PostMapping("/critical")
    public ModelAndView mapCriticalPath(@ModelAttribute("planet") Planet planetNames) {
        ModelAndView modelAndView = new ModelAndView("result");
        planetNames.setThePath(criticalPathService.calculatePath(planetNames));
        modelAndView.addObject("result",planetNames);
        return modelAndView;
    }

}
