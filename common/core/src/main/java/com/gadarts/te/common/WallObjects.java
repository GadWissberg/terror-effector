package com.gadarts.te.common;

import com.gadarts.te.common.assets.model.Models;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WallObjects {
    WALL_WITH_RAILING("Wall with railing", Models.WALL_WITH_RAILING);

    private final String displayName;
    private final Models modelDefinition;

}
