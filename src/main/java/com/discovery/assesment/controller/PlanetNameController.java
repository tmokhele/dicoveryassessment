package com.discovery.assesment.controller;

import com.discovery.assesment.model.PlanetNames;
import com.discovery.assesment.service.PlanetService;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/planet")
public class PlanetNameController {

    PlanetService planetService;

    public PlanetNameController(PlanetService planetService) {
        this.planetService = planetService;
    }

    @GetMapping
    public ModelAndView showPlanetList(ModelMap modelMap) {
        final List<PlanetNames> all = planetService.getAll();
        ModelAndView modelAndView = new ModelAndView("planets");
        modelAndView.addObject("planets", all);
        return modelAndView;
    }

    @PostMapping("/new")
    public ModelAndView addPlanet(PlanetNames planetNames) {
        ModelAndView modelAndView = new ModelAndView();
        List<PlanetNames> all = planetService.getAll();
        Optional<PlanetNames> any = all.stream().filter(planetNames1 -> (planetNames1.getPlanetName().equalsIgnoreCase(planetNames.getPlanetName())
                || planetNames1.getPlanetNode().equalsIgnoreCase(planetNames.getPlanetNode()))).findAny();
        if (!any.isPresent()) {
            PlanetNames planet = planetService.savePlanet(planetNames);
            all.add(planet);
            modelAndView.setViewName("planets");
            modelAndView.addObject("planets", all);
            return modelAndView;
        }
        modelAndView.addObject("validationMessage", "Planet you are trying to add already exists");
        modelAndView.setViewName("error");
        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deletePlanet(@PathVariable String id) {
        boolean planetDeleted = planetService.deletePlanet(id);
        ModelAndView modelAndView = new ModelAndView();
        List<PlanetNames> all = planetService.getAll();
        if (planetDeleted) {
            Optional<PlanetNames> any = all.stream().filter(planetNames -> planetNames.getId().toString().equalsIgnoreCase(id)).findAny();
            all.remove(any);
            modelAndView.setViewName("planets");
            modelAndView.addObject("planets", all);
            return modelAndView;
        }
        modelAndView.addObject("validationMessage", "Failed to remove planet please try again");
        modelAndView.setViewName("error");
        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView updatePlanet(@PathVariable String id) {
        List<PlanetNames> all = planetService.getAll();
        Optional<PlanetNames> any = all.stream().filter(planetNames -> planetNames.getId().toString().equalsIgnoreCase(id)).findAny();
        ModelAndView modelAndView = new ModelAndView("editplanet");
        modelAndView.addObject("planet", any.get());
        return modelAndView;
    }

    @PostMapping("/edit")
    public ModelAndView update(PlanetNames planetNames) {
        planetService.updatePlanet(planetNames);
        final List<PlanetNames> all = planetService.getAll();
        ModelAndView modelAndView = new ModelAndView("planets");
        modelAndView.addObject("planets", all);
        return modelAndView;
    }
}
