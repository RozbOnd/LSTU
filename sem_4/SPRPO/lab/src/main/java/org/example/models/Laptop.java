package org.example.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;

@Getter
@Setter
@EqualsAndHashCode
public class Laptop {

    private String owner;
    private String model;
    private String specs;
    private double price;

    private LocalDate date;
    private LocalTime time;
    private String image;
    private LaptopType type;

    public Laptop() {}

    public Laptop(String owner, String model, String specs, double price,
                  LocalDate date, LocalTime time, String image, LaptopType type) {

        this.owner = owner;
        this.model = model;
        this.specs = specs;
        this.price = price;
        this.date = date;
        this.time = time;
        this.image = image;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Model: " + model +
                ", Specs: " + specs +
                ", Price: " + price +
                ", Date: " + date +
                ", Time: " + time +
                //", Image: " + image +
                ", Type: " + type;
    }

    @JsonIgnore
    public byte[] getImageDecoded() {
        return Base64.getDecoder().decode(this.image);
    }
}