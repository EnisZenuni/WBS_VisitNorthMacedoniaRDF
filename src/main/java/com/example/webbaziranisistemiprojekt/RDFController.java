package com.example.webbaziranisistemiprojekt;

import com.example.webbaziranisistemiprojekt.TTLModel.Accommodation;
import com.example.webbaziranisistemiprojekt.TTLModel.Attraction;
import com.example.webbaziranisistemiprojekt.TTLModel.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@Controller
public class RDFController {

    @Autowired
    private TTLService ttlService;

    @GetMapping("/")
    public String showCities(Model model) {
        model.addAttribute("cities", ttlService.getCities());
        return "city_detail";
    }

    @GetMapping("/city/{cityName}")
    public String getCityDetails(@PathVariable String cityName, Model model) {
        City city = ttlService.getCity(cityName);
        model.addAttribute("city", city);
        return "city";
    }

}

