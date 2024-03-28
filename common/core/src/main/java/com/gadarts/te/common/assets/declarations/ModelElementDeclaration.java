package com.gadarts.te.common.assets.declarations;

import com.badlogic.gdx.math.Vector3;
import com.gadarts.te.common.assets.model.Models;

public interface ModelElementDeclaration extends ElementDeclaration {
    Models getModelDefinition( );

    default int getWidth( ) {
        return 1;
    }

    default int getDepth( ) {
        return 1;
    }

    default Vector3 getOffset(Vector3 output) {
        return output.setZero();
    }
}
