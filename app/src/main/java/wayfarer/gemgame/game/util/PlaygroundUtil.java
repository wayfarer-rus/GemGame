package wayfarer.gemgame.game.util;

import android.content.Context;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import wayfarer.gemgame.game.Hexagon;
import wayfarer.gemgame.game.Playground;
import wayfarer.gemgame.game.PlaygroundCell;

import static ch.lambdaj.Lambda.exists;
import static ch.lambdaj.Lambda.filter;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;

/**
 * Project GemGame
 * Created by wayfarer on 12/6/15.
 */
public class PlaygroundUtil {
    private static final String TAG = "MainActivity";

    private PlaygroundUtil() {}

    /**
     * Builds Playground object.
     *
     * @param x - X of the playground center
     * @param y - Y of the playground center
     * @param z - Z of the playground center
     * @param radius - radius in hexes.
     *            R = 0 - circle with 1 hex
     *            R = 1 - circle with 6 hexes around 1 hex
     * @param delta - space between hexes
     * @return Playground object that contains Hex Meshes and Playground Grid
     */
    public static Playground calculateHexCircle(Context c, float x, float y, float z, int radius, float delta) {
        Playground result = new Playground();
        final float HEX_SIZE = 1.0f;
        float h = HEX_SIZE * MathUtil.sin(60);
        CoordList coordList = new CoordList();

        if (radius == 0) delta = 0;
        // result coord array
        coordList.placing.add(new Point3D(x, y, z));
        coordList.grid.add(new Point3D(0, 0, 0));
        // temporary coord array. always recalculated
        CoordList circle = new CoordList();
        circle.placing.add(new Point3D(x, y, z));
        circle.grid.add(new Point3D(0, 0, 0));

        for (int i = 1; i <= radius; i++) {
            circle = buildHexCircle(circle, h, delta);
            List<Point3D> tmp = filter(not(isIn(coordList.placing)), circle.placing);
            coordList.placing.addAll(tmp);
            tmp = filter(not(isIn(coordList.grid)), circle.grid);
            coordList.grid.addAll(tmp);
        }

        if (c != null) {
            for (int i = 0; i < coordList.grid.size(); ++i) {
                Point3D e = coordList.grid.get(i);
                Point3D placingPoint3d = coordList.placing.get(i);
                HexagonBuilder hb = new HexagonBuilder("hex" + i, c, placingPoint3d);
                hb.setRadius(HEX_SIZE).setTexture(wayfarer.gemgame.R.drawable.opaque_hex_800);
                hb.setFragmentShader(wayfarer.gemgame.R.raw.per_pixel_fragment_shader);
                hb.setVertexShader(wayfarer.gemgame.R.raw.per_pixel_vertex_shader);
                Hexagon hex = hb.build();
                Log.d(TAG, "Hexagon_" + hex.getId() + ": " + hex.getPoint());
                result.getHexes().add(hex);
                result.getCells().add(new PlaygroundCell("hex" + i, (int) e.x, (int) e.y, (int) e.z));
            }
        }

        return result;
    }

    private static CoordList buildHexCircle(CoordList coordList, float h, float delta) {
        CoordList result = new CoordList();

        for (int i = 0; i < coordList.placing.size(); ++i) {
            Point3D n = coordList.placing.get(i);
            Point3D gridPoint = coordList.grid.get(i);
            // get points for hexes around current point
            if (n != null) {
                double z = n.z;
                // up
                double x = n.x;
                double y = n.y + 2 * h + delta;
                Point3D p = new Point3D(x, y, z);

                if (!exists(result.placing, equalTo(p))) {
                    result.placing.add(p);
                    result.grid.add(new Point3D(gridPoint.x, gridPoint.y + 1, gridPoint.z - 1));
                }
                // + 60 grad
                x = n.x + (2 * h + delta) * MathUtil.cos(30);
                y = n.y + (2 * h + delta) * MathUtil.sin(30);
                p = new Point3D(x, y, z);

                if (!exists(result.placing, equalTo(p))) {
                    result.placing.add(p);
                    result.grid.add(new Point3D(gridPoint.x + 1, gridPoint.y, gridPoint.z - 1));
                }
                // + 60 grad
                x = n.x + (2 * h + delta) * MathUtil.cos(30);
                y = n.y - (2 * h + delta) * MathUtil.sin(30);
                p = new Point3D(x, y, z);

                if (!exists(result.placing, equalTo(p))) {
                    result.placing.add(p);
                    result.grid.add(new Point3D(gridPoint.x + 1, gridPoint.y - 1, gridPoint.z));
                }
                // down
                x = n.x;
                y = n.y - (2 * h + delta);
                p = new Point3D(x, y, z);

                if (!exists(result.placing, equalTo(p))) {
                    result.placing.add(p);
                    result.grid.add(new Point3D(gridPoint.x, gridPoint.y - 1, gridPoint.z + 1));
                }
                // + 60 grad
                x = n.x - (2 * h + delta) * MathUtil.cos(30);
                y = n.y - (2 * h + delta) * MathUtil.sin(30);
                p = new Point3D(x, y, z);

                if (!exists(result.placing, equalTo(p))) {
                    result.placing.add(p);
                    result.grid.add(new Point3D(gridPoint.x - 1, gridPoint.y, gridPoint.z + 1));
                }
                // + 60 grad
                x = n.x - (2 * h + delta) * MathUtil.cos(30);
                y = n.y + (2 * h + delta) * MathUtil.sin(30);
                p = new Point3D(x, y, z);

                if (!exists(result.placing, equalTo(p))) {
                    result.placing.add(p);
                    result.grid.add(new Point3D(gridPoint.x - 1, gridPoint.y + 1, gridPoint.z));
                }
            }
        }

        return result;
    }

    private static class CoordList {
        public List<Point3D> placing = new LinkedList<>();
        public List<Point3D> grid = new LinkedList<>();
    }
}
