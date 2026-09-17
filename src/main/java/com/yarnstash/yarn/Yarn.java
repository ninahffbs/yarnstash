package com.yarnstash.yarn;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "yarn")
public class Yarn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String brand;

    @Column(nullable = false, length = 120)
    private String colorway;

    @Column(length = 200)
    private String fiber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private YarnWeight weight;

    @Column(nullable = false)
    private int skeins;

    @Column(nullable = false)
    private int yardsPerSkein;

    private LocalDate purchasedOn;

    public Yarn() {
    }

    public Yarn(Long id, String brand, String colorway, String fiber, YarnWeight weight, int skeins, int yardsPerSkein, LocalDate purchasedOn) {
        this.id = id;
        this.brand = brand;
        this.colorway = colorway;
        this.fiber = fiber;
        this.weight = weight;
        this.skeins = skeins;
        this.yardsPerSkein = yardsPerSkein;
        this.purchasedOn = purchasedOn;
    }

    public int getTotalYards() {
        return skeins * yardsPerSkein;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColorway() {
        return colorway;
    }

    public void setColorway(String colorway) {
        this.colorway = colorway;
    }

    public YarnWeight getWeight() {
        return weight;
    }

    public void setWeight(YarnWeight weight) {
        this.weight = weight;
    }

    public String getFiber() {
        return fiber;
    }

    public void setFiber(String fiber) {
        this.fiber = fiber;
    }

    public int getSkeins() {
        return skeins;
    }

    public void setSkeins(int skeins) {
        this.skeins = skeins;
    }

    public int getYardsPerSkein() {
        return yardsPerSkein;
    }

    public void setYardsPerSkein(int yardsPerSkein) {
        this.yardsPerSkein = yardsPerSkein;
    }

    public LocalDate getPurchasedOn() {
        return purchasedOn;
    }

    public void setPurchasedOn(LocalDate purchasedOn) {
        this.purchasedOn = purchasedOn;
    }
}
