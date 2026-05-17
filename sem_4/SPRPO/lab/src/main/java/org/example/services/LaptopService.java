package org.example.services;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.models.Laptop;
import org.example.models.LaptopType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Getter
public class LaptopService {
    private List<Laptop> laptops;
    private static final Logger logger =
            LoggerFactory.getLogger(LaptopService.class);

    public LaptopService(List<Laptop> laptops) {
        this.laptops = new ArrayList<>(laptops);
    }

    public List<Laptop> getUserLaptops(String username) {
        return laptops.stream()
                .filter(l -> l.getOwner()
                .equals(username)).toList();
    }

    public Laptop getLaptopByModel(String username, String model) {
        return laptops.stream()
                .filter(l -> l.getOwner().equals(username))
                .filter(l -> l.getModel().equals(model))
                .findFirst()
                .orElse(null);
                //.orElseThrow(() -> new NoSuchElementException("No laptop with this model"));
    }

    public Laptop getLaptopByIndex(String username, int index) {
        List<Laptop> userLaptops = getUserLaptops(username);
        if (index < 0 || index >= userLaptops.size()) {
            return null;
        }
        return userLaptops.get(index);
    }

    public boolean addLaptop(String user, String model, String specs, double price,
                          LocalDate date, LocalTime time, String image, LaptopType type) {
        if (model.isEmpty()) {
            logger.error("Model name can't be empty string");
            System.out.println("Model name can't be empty string");
            return false;
        }
        if (specs.isEmpty()) {
            logger.error("Specifications can't be empty string");
            System.out.println("Specifications can't be empty string");
            return false;
        }
        if (price <= 0) {
            logger.error("Invalid price: " + price);
            System.out.println("Price must be bigger than 0");
            return false;
        }
        laptops.add(new Laptop(user, model, specs, price, date, time, image, type));
        return true;
    }

    public boolean deleteLaptop(String user, int index) {
        List<Laptop> userLaptops = getUserLaptops(user);
        if (index < 0 || index >= userLaptops.size()) {
            return false;
        }
        Laptop laptopToDelete = userLaptops.get(index);
        laptops.remove(laptopToDelete);
        return true;
    }

    public boolean editLaptop(String user, int index, String model, String specs, double price,
                              LocalDate date, LocalTime time, String image, LaptopType type) {
        List<Laptop> userLaptops = getUserLaptops(user);
        if (index < 0 || index >= userLaptops.size()) {
            return false;
        }
        if (model.isEmpty()) {
            logger.error("Model name can't be empty string");
            System.out.println("Model name can't be empty string");
            return false;
        }
        if (specs.isEmpty()) {
            logger.error("Specifications can't be empty string");
            System.out.println("Specifications can't be empty string");
            return false;
        }
        if (price <= 0) {
            logger.error("Invalid price: " + price);
            System.out.println("Price must be bigger than 0");
            return false;
        }
        Laptop laptop = userLaptops.get(index);
        laptop.setModel(model);
        laptop.setSpecs(specs);
        laptop.setPrice(price);
        laptop.setDate(date);
        laptop.setTime(time);
        laptop.setImage(image);
        laptop.setType(type);
        return true;
    }
}