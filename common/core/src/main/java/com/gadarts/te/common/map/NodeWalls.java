package com.gadarts.te.common.map;

import com.gadarts.te.common.assets.texture.SurfaceTextures;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodeWalls {
    private Wall eastWall;
    private Wall southWall;
    private Wall westWall;
    private Wall northWall;

    public boolean isEmpty( ) {
        return isWallEmpty(eastWall) && isWallEmpty(southWall) && isWallEmpty(westWall) && isWallEmpty(northWall);
    }

    private boolean isWallEmpty(Wall wall) {
        return wall == null || wall.getDefinition() == SurfaceTextures.MISSING;
    }
}
