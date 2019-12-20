package com.discovery.assesment.util;

import com.discovery.assesment.model.PlanetNames;
import com.discovery.assesment.model.Routes;
import com.discovery.assesment.model.Traffic;
import com.discovery.assesment.service.PlanetService;
import com.discovery.assesment.service.RouteService;
import com.discovery.assesment.service.TrafficService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DataImport implements ApplicationListener<ApplicationReadyEvent> {
    private static final String DATA_FILE_PATH = "/Data.xlsx";

    private PlanetService planetService;
    private RouteService routeService;
    private TrafficService trafficService;

    public DataImport(PlanetService planetService, RouteService routeService, TrafficService trafficService) {
        this.planetService = planetService;
        this.routeService = routeService;
        this.trafficService = trafficService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Workbook workbook = null;
        try {
            workbook = readWorkBook();
        } catch (IOException e) {
           log.error("IO Exception when reading excel sheet",e);
        } catch (InvalidFormatException e) {
            log.error("Invalid Format Exception when reading excel sheet",e);
        }
        writeDataToDB(workbook);
    }

    public void writeDataToDB(Workbook workbook) {
        workbook.forEach(sheet -> {
            switch (sheet.getSheetName())
            {
                case "PlanetNames":
                    savePlanets(sheet);
                    break;
                case "Routes":
                    saveRoutes(sheet);
                    break;
                case "Traffic":
                    saveTraffic(sheet);
                    break;
                default:
                    log.info("Sheet names not matched");
            }
        });
    }

    public void saveTraffic(Sheet sheet) {
        List<Traffic> traffic = new ArrayList<>();
        for(int i=sheet.getFirstRowNum()+1;i<=sheet.getLastRowNum();i++) {
            Row ro=sheet.getRow(i);
            Traffic traffic1 = new Traffic();
            Double numericCellValue = ro.getCell(0).getNumericCellValue();
            traffic1.setRouteId(numericCellValue.longValue());
            traffic1.setPlanetOrigin(ro.getCell(1).getStringCellValue());
            traffic1.setPlanetDestination(ro.getCell(2).getStringCellValue());
            traffic1.setTrafficDelay(ro.getCell(3).getNumericCellValue());
            traffic.add(traffic1);
        }
        trafficService.saveAll(traffic);
    }

    public void saveRoutes(Sheet sheet) {
        List<Routes> routes = new ArrayList<>();
        for(int i=sheet.getFirstRowNum()+1;i<=sheet.getLastRowNum();i++) {
            Routes route = new Routes();
            Row ro=sheet.getRow(i);
            Double numericCellValue = ro.getCell(0).getNumericCellValue();
            route.setRouteId(numericCellValue.longValue());
            route.setPlanetOrigin(ro.getCell(1).getStringCellValue());
            route.setPlanetDestination(ro.getCell(2).getStringCellValue());
            route.setDistance(ro.getCell(3).getNumericCellValue());
            routes.add(route);
        }
        routeService.saveRoutes(routes);
    }

    public void savePlanets(Sheet sheet) {
        List<PlanetNames> planets = new ArrayList<>();
        for(int i=sheet.getFirstRowNum()+1;i<=sheet.getLastRowNum();i++) {
            PlanetNames planetNames = new PlanetNames();
            Row ro=sheet.getRow(i);
            planetNames.setPlanetNode(ro.getCell(0).getStringCellValue());
            planetNames.setPlanetName(ro.getCell(1).getStringCellValue());
            planets.add(planetNames);
        }
        planetService.savePlanets(planets);
    }

    private Workbook readWorkBook() throws IOException, InvalidFormatException {
        Workbook workbook;
        InputStream inputStream = TypeReference.class.getResourceAsStream(DATA_FILE_PATH);
        File f = Files.createTempFile("temp", "xlsx").toFile();
        try (FileOutputStream out = new FileOutputStream(f)) {
            IOUtils.copy(inputStream, out);
        }
        workbook = WorkbookFactory.create(f);
        inputStream.close();
        return workbook;
    }
}
