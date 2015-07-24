package com.kendie.states;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kendie.game.Game;
import com.kendie.handlers.GameStateManager;

public class GameState extends Stage { // TODO: changed from InputAdapter to Stage here
	protected GameStateManager gsm;
	protected Game game;

	protected SpriteBatch sb;
	protected OrthographicCamera cam;
	protected OrthographicCamera hudCam;

	protected GameState(GameStateManager gsm) {
		this.gsm = gsm;
		game = gsm.game();
		sb = game.getSpriteBatch();
		cam = game.getCamera();
		hudCam = game.getHUDCamera();
	}

	public void assignResources() {
		game.getAssetManager().finishLoading();
	}

	// MEMORY MANAGEMENT
	public void load() {}

	public void unload() {}

	// GAME LOOP
	public void update(float dt){}

	public void render(float dt){}

	// LIFE - CYCLE
	public void pause(){}
	
	public void resume(){}
	
	public void handleInput(){}
	
	public void dispose(){}
}
