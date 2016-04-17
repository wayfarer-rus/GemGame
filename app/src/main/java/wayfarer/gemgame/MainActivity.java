package wayfarer.gemgame;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import wayfarer.gemgame.game.Playground;
import wayfarer.gemgame.game.util.PlaygroundUtil;
import wayfarer.gemgame.gl.GemGameGLSurfaceView;
import wayfarer.gemgame.mesh.Mesh;
import wayfarer.gemgame.util.TextureHelper;

public class MainActivity extends FragmentActivity {
    public static final String TAG = "MainActivity";
    public static final String FRAGTAG = "ImmersiveModeFragment";

    private GemGameGLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance
        mGLView = new GemGameGLSurfaceView(this);
        realGameSetup();

        // Set it as the ContentView for this Activity
        setContentView(mGLView);

        if (getSupportFragmentManager().findFragmentByTag(FRAGTAG) == null ) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ImmersiveModeFragment fragment = new ImmersiveModeFragment();
            transaction.add(fragment, FRAGTAG);
            transaction.commit();
        }
    }

    private void realGameSetup() {
        // set camera position
        mGLView.getRenderer().setCamera(0, 0, 10);
        // clear texture cache
        TextureHelper.resetTextureCache();
        Mesh background = new Mesh(this, R.raw.per_pixel_fragment_shader, R.raw.per_pixel_vertex_shader, R.drawable.beach_sand_backgroung);
        Playground playground = PlaygroundUtil.calculateHexCircle(this, 0, 0, 0, 10, 0.2f);
        // background
        background.setPosition(0.0f, 0.0f, -0.1f);
        background.setScaling(25f, 25f, 1f);
        mGLView.getRenderer().addMesh(background);
        // hexes
        mGLView.getRenderer().addAllMeshes(playground.getHexes());
    }

    private void gameSetup() {
        Log.d(TAG, "+ enter gameSetup()");
        // set camera position
        mGLView.getRenderer().setCamera(0, 0, 5);

        // clear texture cache
        TextureHelper.resetTextureCache();
        // Create test mesh
        Mesh background = new Mesh(this, R.raw.per_pixel_fragment_shader, R.raw.per_pixel_vertex_shader, R.drawable.beach_sand_backgroung);
        Mesh blueHexMesh = new Mesh(this, R.raw.per_pixel_fragment_shader, R.raw.per_pixel_vertex_shader, R.drawable.blue_hex_800);
        Mesh redHexMesh = new Mesh(this, R.raw.per_pixel_fragment_shader, R.raw.per_pixel_vertex_shader, R.drawable.red_hex_800);
        Mesh whiteHexMesh = new Mesh(this, R.raw.per_pixel_fragment_shader, R.raw.per_pixel_vertex_shader, R.drawable.opaque_hex_800);
        // background
        background.setPosition(0.0f, 0.0f, -0.1f);
        background.setScaling(20f, 20f, 1f);
        // hexes
        whiteHexMesh.setPosition(0.0f, 0.0f, 0.0f);
        blueHexMesh.setPosition(-2.0f, 0.0f, 0.0f);
        redHexMesh.setPosition(2.0f, 0.0f, 0.0f);
        // Add the plane to the renderer.
        mGLView.getRenderer().addMesh(background);
        mGLView.getRenderer().addMesh(blueHexMesh);
        mGLView.getRenderer().addMesh(whiteHexMesh);
        mGLView.getRenderer().addMesh(redHexMesh);
        Log.d(TAG, "- leave gameSetup()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }
}
