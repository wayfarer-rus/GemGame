package wayfarer.gemgame.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Project GemGame
 * Created by wayfarer on 12/6/15.
 */
public class Playground {
    private List<Hexagon> mHexesList = new ArrayList<>();
    private List<PlaygroundCell> mCellsList = new ArrayList<>();

    public List<Hexagon> getHexes() {
        return mHexesList;
    }

    public List<PlaygroundCell> getCells() {
        return mCellsList;
    }
}
