package com.example.webbaziranisistemiprojekt;

import com.example.webbaziranisistemiprojekt.TTLModel.Accommodation;
import com.example.webbaziranisistemiprojekt.TTLModel.Attraction;
import com.example.webbaziranisistemiprojekt.TTLModel.City;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TTLService {
    private static final String NS_PREFIX = "http://www.semanticweb.org/visitNorthMacedonia#";

    public List<City> getCities() {
        Model model = loadModelFromTTLFile();
        List<City> cities = new ArrayList<>();

        ResIterator cityIterator = model.listSubjectsWithProperty(RDF.type, model.getResource(NS_PREFIX + "City"));
        while (cityIterator.hasNext()) {
            Resource cityResource = cityIterator.nextResource();
            City city = createCityFromResource(cityResource, model);
            cities.add(city);
        }

        return cities;
    }

    public City getCity(String cityName) {
        Model model = loadModelFromTTLFile();

        ResIterator cityIterator = model.listSubjectsWithProperty(RDF.type, model.getResource(NS_PREFIX + "City"));
        while (cityIterator.hasNext()) {
            Resource cityResource = cityIterator.nextResource();
            String name = getPropertyValue(cityResource, RDFS.label, model);
            if (name != null && name.equalsIgnoreCase(cityName)) {
                System.out.println("City Resource URI: " + cityResource.getURI());
                System.out.println("City Resource Label: " + name);
                return createCityFromResource(cityResource, model);
            }
        }

        return null; // City not found in the TTL data
    }


    //TODO WORKS WHEN U HARDCODE the city
//    public City getCity(String cityName) {
//        Model model = loadModelFromTTLFile();
//
//        ResIterator cityIterator = model.listSubjectsWithProperty(RDF.type, model.getResource(NS_PREFIX + "City"));
//        while (cityIterator.hasNext()) {
//            Resource cityResource = cityIterator.nextResource();
//            String name = getPropertyValue(cityResource, RDFS.label, model);
//            if (name != null && name.equalsIgnoreCase("Veles")) {
//                System.out.println("City Resource URI: " + cityResource.getURI());
//                System.out.println("City Resource Label: " + name);
//                return createCityFromResource(cityResource, model);
//            }
//        }
//
//        return null; // City not found in the TTL data
//    }










//    public City getCity(String cityName) {
//        Model model = loadModelFromTTLFile();
//        Resource cityResource = model.getResource(NS_PREFIX + cityName);
//        if (cityResource == null) {
//            return null; // City not found in the TTL data
//        }
//        System.out.println("getCity +++++ City resource: " + cityResource);
//
//        return createCityFromResource(cityResource, model);
//    }

    private City createCityFromResource(Resource cityResource, Model model) {
        City city = new City();

        Property labelProperty = RDFS.label;
        Property descriptionProperty = model.createProperty(NS_PREFIX + "Description");
        Property attractionsProperty = model.createProperty(NS_PREFIX + "hasAttraction");

        city.setName(getPropertyValue(cityResource, labelProperty, model));
        city.setDescription(getPropertyValue(cityResource, descriptionProperty, model));
        city.setAccommodations(getAccommodationsForCity(cityResource, model)); // Updated line
        city.setAttractions(getAttractionsForCity(cityResource, attractionsProperty, model));

        //System.out.println("Create City From Resource City name: " + city.getName());
        //System.out.println("Create City From Resource  City description: " + city.getDescription());
        return city;
    }




    private List<Accommodation> getAccommodationsForCity(Resource cityResource, Model model) {
        List<Accommodation> accommodations = new ArrayList<>();

        StmtIterator stmtIterator = model.listStatements(null, model.getProperty(NS_PREFIX + "locatedIn"), cityResource);
        while (stmtIterator.hasNext()) {
            Resource accommodationResource = stmtIterator.nextStatement().getSubject();
            Accommodation accommodation = createAccommodationFromResource(accommodationResource, model);
            accommodations.add(accommodation);
            System.out.println("getAccommodationsForCity: " + accommodation.getName());
        }

        return accommodations;
    }


    private List<Attraction> getAttractionsForCity(Resource cityResource, Property attractionsProperty, Model model) {
        List<Attraction> attractions = new ArrayList<>();

        StmtIterator stmtIterator = model.listStatements(cityResource, attractionsProperty, (RDFNode) null);
        while (stmtIterator.hasNext()) {
            Resource attractionResource = stmtIterator.nextStatement().getObject().asResource();
            Attraction attraction = createAttractionFromResource(attractionResource, model);
            attractions.add(attraction);
            //TODO the name and other attribute probably do not exist
            //System.out.println("Attraction desc: " + attraction.getDescription());
            System.out.println("Attraction name: " + attraction.getName());
        }

        return attractions;
    }


    private Accommodation createAccommodationFromResource(Resource accommodationResource, Model model) {
        Accommodation accommodation = new Accommodation();

        Property labelProperty = RDFS.label;
        Property descriptionProperty = model.createProperty(NS_PREFIX + "Description");
        Property imageProperty = model.createProperty(NS_PREFIX + "Image");
        Property ratingProperty = model.createProperty(NS_PREFIX + "Rating");

        accommodation.setName(getPropertyValue(accommodationResource, labelProperty, model));
        accommodation.setDescription(getPropertyValue(accommodationResource, descriptionProperty, model));
        accommodation.setImage(getPropertyValue(accommodationResource, imageProperty, model));
        accommodation.setRating(Float.parseFloat(getPropertyValue(accommodationResource, ratingProperty, model)));

        return accommodation;
    }



    private Attraction createAttractionFromResource(Resource attractionResource, Model model) {
        Attraction attraction = new Attraction();

        Property labelProperty = RDFS.label;
        Property descriptionProperty = model.createProperty(NS_PREFIX + "Description");
        Property imageProperty = model.createProperty(NS_PREFIX + "Image");
        Property ratingProperty = model.createProperty(NS_PREFIX + "Rating");

        attraction.setName(getPropertyValue(attractionResource, labelProperty, model));
        attraction.setDescription(getPropertyValue(attractionResource, descriptionProperty, model));
        attraction.setImage(getPropertyValue(attractionResource, imageProperty, model));

        String ratingValue = getPropertyValue(attractionResource, ratingProperty, model);
        if (ratingValue != null) {
            try {
                attraction.setRating(Float.parseFloat(ratingValue));
            } catch (NumberFormatException e) {
                // Handle parsing error if needed
                attraction.setRating(0.0f); // Default rating value
            }
        } else {
            attraction.setRating(0.0f); // Default rating value if "ratingValue" is null
        }

        return attraction;
    }





    private String getPropertyValue(Resource resource, Property property, Model model) {
        Statement statement = resource.getProperty(property);
        if (statement != null && statement.getObject().isLiteral()) {
            return statement.getLiteral().getString();
        }
        return null;
    }

    private Model loadModelFromTTLFile() {
        Model model = ModelFactory.createDefaultModel();
        model.read("src/main/java/com/example/webbaziranisistemiprojekt/11updated_ontologyVeles.ttl", "TURTLE");
        return model;
    }
}
