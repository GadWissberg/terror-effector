package com.gadarts.te.common.definitions;

import com.gadarts.te.common.assets.model.Models;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Obstacles implements EnvObjectDefinition {
    CRATE_0("Big Crate", Models.CRATE_0_CLEAN),
    CRATE_0_UCI("Big Crate with UCI Logo", Models.CRATE_0_UCI),
    CRATE_1("Small Crate", Models.CRATE_1_CLEAN),
    CRATE_1_UCI("Small Crate with UCI Logo", Models.CRATE_1_UCI),
    RAILING_0("Railing", Models.RAILING_0);

    private final String displayName;
    private final Models modelDefinition;

}
