package com.kendie.handlers;

import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.kendie.game.Game;
import com.kendie.states.GameState;
import com.kendie.states.Play;


public class GameStateManager {
	private Game game;
	private Stack<GameState> gameStates;

	// States
	private GameState _currentState = null;
	private GameState _nextState = null;
	private GameState _oldState = null;
	
	public static final int PLAY = 912837;

	public GameStateManager(Game game) {
		this.game = game;
		gameStates = new Stack<GameState>();
		_nextState = getState(PLAY);
		gameStates.push(_nextState); // TODO: change this
	}

	public Game game() {
		return game;
	}

	public void update(float dt) {
		gameStates.peek().update(dt);
	}

	public void render(float dt) {
		gameStates.peek().render(dt);
	}

	public GameState getState(int state) {
		if (state == PLAY)
			return new Play(this);
		return null;
	}

	public GameState getCurrentState() {
		return _currentState;
	}
	
	public void setState(int state) {
		popState();
		_nextState = getState(state);
		pushState(_nextState);
	}

	public void pushState(GameState state) {
		gameStates.push(state);

	}

	public void popState() {
		GameState g = gameStates.pop();
		g.dispose();
	}
	
	public void performPendingAssetsUnloading() {
		// Unload old state if there was one and it's not the same one as the current one
		if (_oldState != null && _oldState != _currentState) {
			_oldState.unload();
			_oldState = null;
		}
	}
	
	public void performPendingStateChange() {
		if (_nextState != null) {
			if (_currentState != null) {
				// Pause current state
				_currentState.pause();
			}
			// Cancel input processor
			Gdx.input.setInputProcessor(null);
			
			// Schedule resource unload
			_oldState = _currentState;
			
			// Assign new state
			_currentState = _nextState;
			
			// Load new state
			_currentState.load();
			
			// Nullify scheduled state change
			_nextState = null;
			
			// Resume state
			_currentState.resume();
			
			// Listen to back key (android)
			Gdx.input.setCatchBackKey(true);
		}
	}
}
