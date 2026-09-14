package com.yarnstash.yarn;

public record YarnResponse(Long id, String brand, String colorway, String fiber, YarnWeight weight, int skeins, int yardsPerSkein, int totalYards) {
    public static YarnResponse from(Yarn yarn) {
        return new YarnResponse(yarn.getId(), yarn.getBrand(), yarn.getColorway(), yarn.getFiber(), yarn.getWeight(), yarn.getSkeins(), yarn.getYardsPerSkein(), yarn.getTotalYards());
    }
}
