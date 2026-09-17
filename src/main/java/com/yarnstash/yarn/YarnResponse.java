package com.yarnstash.yarn;

import java.time.LocalDate;

public record YarnResponse(
        Long id,
        String brand,
        String colorway,
        String fiber,
        YarnWeight weight,
        int skeins,
        int yardsPerSkein,
        LocalDate purchasedOn,
        int totalYards,
        int allocatedYards,
        int availableYards) {
    public static YarnResponse from(Yarn yarn, int allocatedYards) {
        return new YarnResponse(
                yarn.getId(),
                yarn.getBrand(),
                yarn.getColorway(),
                yarn.getFiber(),
                yarn.getWeight(),
                yarn.getSkeins(),
                yarn.getYardsPerSkein(),
                yarn.getPurchasedOn(),
                yarn.getTotalYards(),
                allocatedYards,
                yarn.getTotalYards() - allocatedYards);
    }
}
