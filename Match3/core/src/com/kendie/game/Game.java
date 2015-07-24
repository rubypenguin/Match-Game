package com.kendie.game;

import java.util.HashMap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.kendie.handlers.GameStateManager;
import com.kendie.handlers.LanguagesManager;
import com.kendie.handlers.MyInput;
import com.kendie.states.GameState;

public class Game extends ApplicationAdapter {
	public static final String TITLE = "Match3";
	public static final int V_WIDTH = 480;
	public static final int V_HEIGHT = 800;
	public static final int SCALE = 1;
	public static final float STEP = 1 / 60f;

	private AssetManager assetManager = null;

	private SpriteBatch sb;
	private OrthographicCamera cam;
	private OrthographicCamera hudCam;

	private GameStateManager gsm;

	// Mouse pointer
	private TextureRegion imgMouse = null;
	private Vector3 mousePos = null;

	public enum Platform {Desktop, Android, Web};
	public static Platform platform = Platform.Desktop;

	// Language manager
	private LanguagesManager lang = null;

	// States
	private HashMap<String, GameState> _states = null;





	@Override
	public void create() {

		lang = LanguagesManager.getInstance();
		// Mouse pos
		mousePos = new Vector3();

		// Create assets manager
		assetManager = new AssetManager();

		// Load general assets
		assetManager.load("data/handCursor.png", Texture.class);
		assetManager.finishLoading();

		// Get assets
		imgMouse = new TextureRegion(assetManager.get("data/handCursor.png",
				Texture.class));
		// imgMouse.flip(false, true);

		// Sprite batch
		sb = new SpriteBatch();

		// Ortographic camera
		cam = new OrthographicCamera();
		cam.setToOrtho(false, V_WIDTH, V_HEIGHT);

		// hud cam
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, V_WIDTH, V_HEIGHT);

		// Mouse hidden
		if (platform != Platform.Android) {
			Gdx.input.setCursorCatched(false);
		}

		gsm = new GameStateManager(this);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (platform != Platform.Android) {
			Gdx.graphics.setTitle(TITLE + " -- FPS: "
					+ Gdx.graphics.getFramesPerSecond());
		}
		if (gsm.getCurrentState() != null) {
			gsm.update(Gdx.graphics.getDeltaTime());
		}

		// Start rendering
		sb.begin();

		sb.setProjectionMatrix(cam.combined);

		gsm.render(Gdx.graphics.getDeltaTime());

		//MyInput.update();

		// Perform pending memory unloading, safely
		gsm.performPendingAssetsUnloading();

		// Perform pending state changes, memory safe
		gsm.performPendingStateChange();

		// draw the custom mouse
		if (platform != Platform.Android) {
			mousePos.x = Gdx.input.getX();
			mousePos.y = Gdx.input.getY();
			cam.unproject(mousePos);
			sb.draw(imgMouse, mousePos.x, mousePos.y - imgMouse.getRegionHeight());
		}
		// end rendering
		sb.end();
	}

	public void dispose() {
		assetManager.dispose();
	}

	public void resize(int w, int h) {
	}

	public void pause() {
	}

	public void resume() {
	}

	public SpriteBatch getSpriteBatch() {
		return sb;
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public OrthographicCamera getCamera() {
		return cam;
	}

	public OrthographicCamera getHUDCamera() {
		return hudCam;
	}
}
