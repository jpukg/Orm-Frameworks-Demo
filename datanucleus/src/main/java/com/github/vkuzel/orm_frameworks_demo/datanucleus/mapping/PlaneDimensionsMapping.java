package com.github.vkuzel.orm_frameworks_demo.datanucleus.mapping;

import com.github.vkuzel.orm_frameworks_demo.transport.DetailPlaneDimensions;
import org.datanucleus.store.rdbms.mapping.java.SingleFieldMapping;

public class PlaneDimensionsMapping extends SingleFieldMapping {
    @Override
    public Class getJavaType() {
        return DetailPlaneDimensions.class;
    }
}
