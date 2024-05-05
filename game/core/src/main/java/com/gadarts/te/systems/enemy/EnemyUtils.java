package com.gadarts.te.systems.enemy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Bresenham2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.LinkedHashSet;

import static com.gadarts.te.components.ComponentsMapper.characterDecal;

public final class EnemyUtils {
    private static final Vector2 auxVector2_1 = new Vector2();
    private static final Vector2 auxVector2_2 = new Vector2();
    private static final Bresenham2 bresenham = new Bresenham2();

    public static LinkedHashSet<GridPoint2> findAllNodesToTarget(Entity enemy,
                                                                 LinkedHashSet<GridPoint2> output,
                                                                 boolean removeEdgeNodes,
                                                                 Entity target) {
        output.clear();
        Vector2 src = characterDecal.get(enemy).getNodePosition(auxVector2_1);
        Vector2 dst = characterDecal.get(target).getNodePosition(auxVector2_2);
        output.clear();

        Array<GridPoint2> srcToDst = bresenham.line((int) src.x, (int) src.y, (int) dst.x, (int) dst.y);
        if (srcToDst.size > 1 && removeEdgeNodes) {
            srcToDst.removeIndex(0);
            srcToDst.removeIndex(srcToDst.size - 1);
        }

        Array<GridPoint2> dstToSrc;

        dstToSrc = bresenham.line((int) dst.x, (int) dst.y, (int) src.x, (int) src.y);
        if (dstToSrc.size > 1 && removeEdgeNodes) {
            dstToSrc.removeIndex(0);
            dstToSrc.removeIndex(dstToSrc.size - 1);
        }

        for (int i = srcToDst.size - 1; i >= 0; i--) {
            output.add(srcToDst.get(i));
        }
        for (int i = dstToSrc.size - 1; i >= 0; i--) {
            output.add(dstToSrc.get(i));
        }

        return output;
    }
}
