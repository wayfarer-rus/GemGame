package wayfarer.gemgame;

import android.app.Application;
import android.test.ApplicationTestCase;

import wayfarer.gemgame.game.Playground;
import wayfarer.gemgame.game.util.PlaygroundUtil;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);

        Playground p = PlaygroundUtil.calculateHexCircle(null, 0, 0, 0, 10, 0.3f);
    }
}