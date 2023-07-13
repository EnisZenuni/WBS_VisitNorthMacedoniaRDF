package com.example.webbaziranisistemiprojekt.TTLModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class City {
    private String name;
    private String description;
    private List<Accommodation> accommodations;
    private List<Attraction> attractions;

    // Constructors, getters, and setters
}

