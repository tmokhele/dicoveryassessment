package com.discovery.assesment.service.impl;

import com.discovery.assesment.model.Traffic;
import com.discovery.assesment.repo.TrafficRepository;
import com.discovery.assesment.service.TrafficService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrafficServiceImpl implements TrafficService {
    TrafficRepository trafficRepository;

    public TrafficServiceImpl(TrafficRepository trafficRepository) {
        this.trafficRepository = trafficRepository;
    }

    @Override
    public boolean saveAll(List<Traffic> trafficList) {
        Iterable<Traffic> traffic = trafficRepository.saveAll(trafficList);
        return true;
    }

    @Override
    public List<Traffic> getAll() {
        List<Traffic> list = new ArrayList<>();
        trafficRepository.findAll().forEach(list::add);
        return list;
    }

    @Override
    public Traffic save(Traffic traffic) {
       return trafficRepository.save(traffic);
    }

    @Override
    public boolean deleteTraffic(String id) {
        trafficRepository.deleteById(Long.valueOf(id));
        return true;
    }
}
