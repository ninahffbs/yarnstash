package com.yarnstash.project;

import com.yarnstash.yarn.Yarn;
import com.yarnstash.yarn.YarnWeight;

public record AllocationResponse(
        Long id,
        Long yarnId,
        String brand,
        String colorway,
        YarnWeight weight,
        int yardsUsed
) {
    public static AllocationResponse from(ProjectYarn allocation) {
        Yarn yarn = allocation.getYarn();
        return new AllocationResponse(
                allocation.getId(),
                yarn.getId(),
                yarn.getBrand(),
                yarn.getColorway(),
                yarn.getWeight(),
                allocation.getYardsUsed()
        );
    }
}
