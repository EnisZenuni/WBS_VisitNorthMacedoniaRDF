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

    public List<City> getCities() {
        Model model = ModelFactory.createDefaultModel();
        model.read("src/main/java/com/example/webbaziranisistemiprojekt/11updated_ontologyVeles.ttl", "TURTLE");

        List<City> cities = new ArrayList<>();

        StmtIterator cityIterator = model.listStatements(null, RDF.type, model.getResource("http://www.semanticweb.org/visitNorthMacedonia#City"));
        while (cityIterator.hasNext()) {
            Resource cityResource = cityIterator.nextStatement().getSubject();
            City city = createCityFromResource(cityResource, model);
            cities.add(city);
        }

        return cities;
    }

    private City createCityFromResource(Resource cityResource, Model model) {
        City city = new City();

        Property labelProperty = RDFS.label;
        Property descriptionProperty = model.createProperty("http://www.semanticweb.org/visitNorthMacedonia#Description");

        city.setName(getPropertyValue(cityResource, labelProperty, model));
        city.setDescription(getPropertyValue(cityResource, descriptionProperty, model));
        city.setAccommodations(getAccommodationsForCity(cityResource, model));
        city.setAttractions(getAttractionsForCity(cityResource, model));

        return city;
    }


    private List<Accommodation> getAccommodationsForCity(Resource cityResource, Model model) {
        List<Accommodation> accommodations = new ArrayList<>();

        StmtIterator accommodationIterator = model.listStatements(null, RDF.type, model.getResource("http://www.semanticweb.org/visitNorthMacedonia#Accommodation"));
        while (accommodationIterator.hasNext()) {
            Resource accommodationResource = accommodationIterator.nextStatement().getSubject();
            if (accommodationResource.hasProperty(model.getProperty("http://www.semanticweb.org/visitNorthMacedonia#locatedIn"), cityResource)) {
                Accommodation accommodation = createAccommodationFromResource(accommodationResource, model);
                accommodations.add(accommodation);
            }
        }

        return accommodations;
    }

    private Accommodation createAccommodationFromResource(Resource accommodationResource, Model model) {
        Accommodation accommodation = new Accommodation();

        Property labelProperty = RDFS.label;
        Property descriptionProperty = model.createProperty("http://www.semanticweb.org/visitNorthMacedonia#Description");
        Property imageProperty = model.createProperty("http://www.semanticweb.org/visitNorthMacedonia#Image");
        Property ratingProperty = model.createProperty("http://www.semanticweb.org/visitNorthMacedonia#Rating");

        accommodation.setName(getPropertyValue(accommodationResource, labelProperty, model));
        accommodation.setDescription(getPropertyValue(accommodationResource, descriptionProperty, model));
        accommodation.setImage(getPropertyValue(accommodationResource, imageProperty, model));
        accommodation.setRating(Float.parseFloat(getPropertyValue(accommodationResource, ratingProperty, model)));

        return accommodation;
    }


    private List<Attraction> getAttractionsForCity(Resource cityResource, Model model) {
        List<Attraction> attractions = new ArrayList<>();

        StmtIterator attractionIterator = model.listStatements(null, RDF.type, model.getResource("http://www.semanticweb.org/visitNorthMacedonia#Attraction"));
        while (attractionIterator.hasNext()) {
            Resource attractionResource = attractionIterator.nextStatement().getSubject();
            if (attractionResource.hasProperty(model.getProperty("http://www.semanticweb.org/visitNorthMacedonia#locatedIn"), cityResource)) {
                Attraction attraction = createAttractionFromResource(attractionResource, model);
                attractions.add(attraction);
            }
        }

        return attractions;
    }

    private Attraction createAttractionFromResource(Resource attractionResource, Model model) {
        Attraction attraction = new Attraction();

        Property descriptionProperty = model.createProperty("http://www.semanticweb.org/visitNorthMacedonia#Description");
        Property imageProperty = model.createProperty("http://www.semanticweb.org/visitNorthMacedonia#Image");
        Property ratingProperty = model.createProperty("http://www.semanticweb.org/visitNorthMacedonia#Rating");

        attraction.setDescription(getPropertyValue(attractionResource, descriptionProperty, model));
        attraction.setImage(getPropertyValue(attractionResource, imageProperty, model));
        attraction.setRating(Float.parseFloat(getPropertyValue(attractionResource, ratingProperty, model)));

        return attraction;
    }


    private String getPropertyValue(Resource resource, Property property, Model model) {
        Statement statement = resource.getProperty(property);
        if (statement != null && statement.getObject().isLiteral()) {
            return statement.getLiteral().getString();
        }
        return null;
    }
}
