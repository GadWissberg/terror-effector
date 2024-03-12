package com.gadarts.te.common.definitions;

import com.gadarts.te.common.assets.model.Models;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WallObjects implements EnvObjectDefinition {
    WALL_WITH_RAILING("Wall with railing", Models.WALL_WITH_RAILING),
    WALL_WITH_RAILING_HALF("Wall with railing - Edge", Models.WALL_WITH_RAILING_HALF);

    private final String displayName;
    private final Models modelDefinition;

}
