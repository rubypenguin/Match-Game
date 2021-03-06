package com.kendie.states;

import java.util.ArrayList;
import java.util.Collections;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.kendie.TweenAccessors.SpriteAccessor;
import com.kendie.entities.FloatingScore;
import com.kendie.game.Game;
import com.kendie.handlers.Animation;
import com.kendie.handlers.Background;
import com.kendie.handlers.Board;
import com.kendie.handlers.Coord;
import com.kendie.handlers.GameStateManager;
import com.kendie.handlers.LanguagesManager;
import com.kendie.handlers.MultipleMatch;

public class Play extends GameState {
	private boolean debug = false;

	public enum State {
		Loading, InitialGems, Wait, SelectedGem, ChangingGems, DisappearingGems, AppearingGems, DisappearingBoard, TimeFinished, ShowingScoreTable
	};

	private static final Vector2 gemsInitial = new Vector2(60, 0);

	private OrthographicCamera b2dCam;

	private Background backgrounds;
	// private HUD hud;

	// current gamestate
	State cState;

	// size of each block
	private int blockWidth;
	private int blockHeight;

	// selected squares
	private Coord selectedSquareFirst;
	private Coord selectedSquareSecond;
	private Board.Direction direction;
	private boolean clicking;

	// game board
	private Board board1;
	private Board board2;
	private int boardCol;
	private int boardRow;
	// points and gems matches
	private MultipleMatch groupedSquares1;
	private MultipleMatch groupedSquares2;
	private int points;
	private int multiplier = 0;
	private String txtTime;

	// Game elements textures
	private TextureRegion imgBoard;
	private TextureRegion imgWhite;
	private TextureRegion imgRed;
	private TextureRegion imgPurple;
	private TextureRegion imgOrange;
	private TextureRegion imgGreen;
	private TextureRegion imgYellow;
	private TextureRegion imgBlue;
	private TextureRegion _imgSelector;

	// Animations
	private double animTime;
	private double animTotalTime;
	private double animTotalInitTime;

	// GUI Buttons
	// private Button _hintButton;
	// private Button _resetButton;
	// private Button _exitButton;
	// private Button _musicButton;

	// Background textures
	private TextureRegion imgBackground;
	private TextureRegion imgScoreBackground;
	private TextureRegion imgTimeBackground;

	// Fonts
	private BitmapFont _fontTime;
	private BitmapFont _fontScore;
	private BitmapFont _fontText;
	private BitmapFont _fontLoading;
	public static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;,{}\"`'<>";

	// Starting time
	private double _remainingTime;

	// SFX and music
	private Sound _match1SFX;
	private Sound _match2SFX;
	private Sound _match3SFX;
	private Sound _selectSFX;
	private Sound _fallSFX;
	private Music _song;

	// Floating scores
	private ArrayList<FloatingScore> floatingScores;

	// Particle effects
	private ParticleEffect _effect;
	private ArrayList<ParticleEffect> _effects;

	// Mouse pos
	private Vector3 mousePos = null;

	// Language manager
	private LanguagesManager lang;

	// Scores table
	// private ScoreTable _scoreTable;

	// Tween stuff
	private TweenManager manager;
	private ArrayList<Sprite> sprites1;
	private ArrayList<Sprite> sprites2;
	// private Value alpha = new Value();

	public static int level;

	public Play(GameStateManager gsm) {
		super(gsm);

		// Languages manager
		lang = LanguagesManager.getInstance();

		cState = State.Loading;

		// Load and sync loading banner
		BitmapFontLoader.BitmapFontParameter fontParameters = new BitmapFontLoader.BitmapFontParameter();
		// fontParameters.flip = true;
		AssetManager assetManager = game.getAssetManager();
		assetManager.load("data/loadingFont.fnt", BitmapFont.class,
				fontParameters);
		assetManager.finishLoading();
		_fontLoading = assetManager.get("data/loadingFont.fnt",
				BitmapFont.class);
		// create board
		blockWidth = 60;
		blockHeight = 60;

		boardCol = 7;//Game.V_WIDTH / blockWidth;
		boardRow = 6;//Game.V_WIDTH / blockHeight;
		board1 = new Board(boardCol, boardRow);
		board2 = new Board(boardCol, boardRow);

		selectedSquareFirst = new Coord(-1, -1);
		selectedSquareSecond = new Coord(-1, -1);

		// Mouse pos
		mousePos = new Vector3();

		// Tween Engine
		manager = new TweenManager();
		sprites1 = new ArrayList<Sprite>();
		sprites2 = new ArrayList<Sprite>();
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		// create background
		// backgrounds = new Background(imgBackground, cam, 0f);
		init();
	}

	@Override
	public void handleInput() {
		// TODO Auto-generated method stub
	}

	@Override
	public void assignResources() {
		super.assignResources();

		AssetManager assetManager = game.getAssetManager();

		// Load fonts
		// _fontTime = assetManager.get("data/timeFont.fnt", BitmapFont.class);
		// _fontScore = assetManager.get("data/scoreFont.fnt",
		// BitmapFont.class);
		// _fontText = assetManager.get("data/normalFont.fnt",
		// BitmapFont.class);

		// Load textures
		//System.out.println(assetManager.isLoaded("data/board.png"));
		imgBackground = new TextureRegion(assetManager.get("data/background.png", Texture.class));
		//imgBoard = new TextureRegion(assetManager.get("data/board.png",	Texture.class));
		imgBoard = new TextureRegion(assetManager.get("data/boardTemp.png",	Texture.class));

		imgWhite = new TextureRegion(assetManager.get("data/gemwhite.png", Texture.class));
		imgRed = new TextureRegion(assetManager.get("data/gemred.png", Texture.class));
		imgPurple = new TextureRegion(assetManager.get("data/gempurple.png", Texture.class));
		imgOrange = new TextureRegion(assetManager.get("data/gemorange.png", Texture.class));
		imgGreen = new TextureRegion(assetManager.get("data/gemgreen.png", Texture.class));
		imgYellow = new TextureRegion(assetManager.get("data/gemyellow.png", Texture.class));
		imgBlue = new TextureRegion(assetManager.get("data/gemblue.png", Texture.class));

		/*
		 * _imgScoreBackground = new
		 * TextureRegion(assetManager.get("data/scoreBackground.png",
		 * Texture.class)); _imgSelector = new
		 * TextureRegion(assetManager.get("data/selector.png", Texture.class));
		 * _imgTimeBackground = new
		 * TextureRegion(assetManager.get("data/timeBackground.png",
		 * Texture.class));
		 *
		 *
		 * // Button textures and font TextureRegion buttonBackground = new
		 * TextureRegion(assetManager.get("data/buttonBackground.png",
		 * Texture.class)); TextureRegion buttonBackgroundClicked = new
		 * TextureRegion(assetManager.get("data/buttonBackgroundPressed.png",
		 * Texture.class)); TextureRegion iconHint = new
		 * TextureRegion(assetManager.get("data/iconHint.png", Texture.class));
		 * TextureRegion iconRestart = new
		 * TextureRegion(assetManager.get("data/iconRestart.png",
		 * Texture.class)); TextureRegion iconExit = new
		 * TextureRegion(assetManager.get("data/iconExit.png", Texture.class));
		 * TextureRegion iconMusic = new
		 * TextureRegion(assetManager.get("data/iconMusic.png", Texture.class));
		 *
		 * BitmapFont buttonFont = assetManager.get("data/normalFont.fnt",
		 * BitmapFont.class);
		 *
		 * _hintButton.setIcon(iconHint); _resetButton.setIcon(iconRestart);
		 * _exitButton.setIcon(iconExit); _musicButton.setIcon(iconMusic);
		 *
		 * _hintButton.setBackground(buttonBackground);
		 * _resetButton.setBackground(buttonBackground);
		 * _exitButton.setBackground(buttonBackground);
		 * _musicButton.setBackground(buttonBackground);
		 *
		 * _hintButton.setBackgroundClicked(buttonBackgroundClicked);
		 * _resetButton.setBackgroundClicked(buttonBackgroundClicked);
		 * _exitButton.setBackgroundClicked(buttonBackgroundClicked);
		 * _musicButton.setBackgroundClicked(buttonBackgroundClicked);
		 *
		 * _hintButton.setFont(buttonFont); _resetButton.setFont(buttonFont);
		 * _exitButton.setFont(buttonFont); _musicButton.setFont(buttonFont);
		 *
		 * // Load SFX and music _match1SFX =
		 * assetManager.get("data/match1.ogg", Sound.class); _match2SFX =
		 * assetManager.get("data/match2.ogg", Sound.class); _match3SFX =
		 * assetManager.get("data/match3.ogg", Sound.class); _selectSFX =
		 * assetManager.get("data/select.ogg", Sound.class); _fallSFX =
		 * assetManager.get("data/fall.ogg", Sound.class);
		 */

		//_song = assetManager.get("data/music1.ogg", Music.class);
		// Play music if it wasn't playing
		// if (!_song.isPlaying()) {
		// _song.setLooping(true);
		// _song.play();
		// }
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void update(float dt) {
		// LOADING STATE
		if (cState == State.Loading) {
			System.out.println("State.Loading");
			// If we finish loading, assign resources and change to FirstFlip state
			if (game.getAssetManager().update()) {
				assignResources();
				cState = State.InitialGems;
			}
			return;
		}

		// INITIAL GAME STATE
		if (cState == State.InitialGems) {
			System.out.println("State.InitialGems");
			if (animTime == 0) {
				// go through all of the squares
				for (int i = 0; i < boardCol; i++) {
					for (int j = 0; j < boardRow; j++) {
						int index = i * boardRow + j;
						Sprite img;
						img = new Sprite(getGemsColor(i, j, board1));
						sprites1.add(img);
						sprites1.get(index).setPosition(gemsInitial.x + i * blockWidth, j * blockHeight);
						img = new Sprite(getGemsColor(i, j, board2));
						sprites2.add(img);
						sprites2.get(index).setPosition(gemsInitial.x + i * blockWidth, Game.V_HEIGHT - (j + 1) * blockHeight);
						// draw
						if (sprites1.get(index) != null) {
							Tween.from(sprites1.get(index), SpriteAccessor.OPACITY, 0.5f).target(0).start(manager);
						}
						if (sprites2.get(index) != null) {
							Tween.from(sprites2.get(index), SpriteAccessor.OPACITY, 0.5f).target(0).start(manager);
						}
					}
				}
			}
			animTime += dt;
			// If animation ended
			if (animTime >= animTotalInitTime) {
				// Switch to next state (waiting for user input)
				cState = State.Wait;
				//board.endAnimation();
				// Reset animation step counter
				animTime = 0;
			}
		}

		// WAITING STATE
		if (cState == State.Wait) {
			// Multiplier must be 0
			multiplier = 0;
		}

		// SWAPPING GEMS STATE
		if (cState == State.ChangingGems) {
			int debugIndex;
			if (animTime == 0) {
				System.out.println("State.ChangingGems");

				if (direction == Board.Direction.RIGHT) {
					// keep y, right shift x
					int index1 = (boardCol - 1) * boardRow + selectedSquareFirst.y; // rightmost X
					float tempX = sprites1.get(0).getX(); // smallest X
					//Tween.to(sprites.get(index1), SpriteAccessor.SCALE_XY, .3f).target(0).ease(Quad.INOUT).start(manager);
					for (int i = 0; i < boardCol - 1 ; ++i) {
						index1 -= boardRow;
						Tween.to(sprites1.get(index1), SpriteAccessor.CPOS_XY, .3f).targetRelative(blockWidth, 0).ease(Quad.INOUT).start(manager);
						Collections.swap(sprites1, index1, index1 + boardRow);
						Tween.to(sprites2.get(index1), SpriteAccessor.CPOS_XY, .3f).targetRelative(blockWidth, 0).ease(Quad.INOUT).start(manager);
						Collections.swap(sprites2, index1, index1 + boardRow);
					}
					sprites1.get(index1).setX(tempX);
					Tween.from(sprites1.get(index1), SpriteAccessor.OPACITY, .3f).target(0).ease(Quad.INOUT).start(manager);
					sprites2.get(index1).setX(tempX);
					Tween.from(sprites2.get(index1), SpriteAccessor.OPACITY, .3f).target(0).ease(Quad.INOUT).start(manager);
				}
				if (direction == Board.Direction.LEFT) {
					// keep y, left shift x
					int index1 = (int) selectedSquareFirst.y; // leftmost X
					float tempX = sprites1.get((boardCol - 1) * boardRow).getX(); // biggest X
					for (int i = 0; i < boardCol - 1; ++i) {
						index1 += boardRow;
						Tween.to(sprites1.get(index1), SpriteAccessor.CPOS_XY, .3f).targetRelative(-blockWidth, 0).ease(Quad.INOUT).start(manager);
						Collections.swap(sprites1, index1, index1 - boardRow);
						Tween.to(sprites2.get(index1), SpriteAccessor.CPOS_XY, .3f).targetRelative(-blockWidth, 0).ease(Quad.INOUT).start(manager);
						Collections.swap(sprites2, index1, index1 - boardRow);
					}
					sprites1.get(index1).setX(tempX);
					Tween.from(sprites1.get(index1), SpriteAccessor.OPACITY, .3f).target(0).ease(Quad.INOUT).start(manager);
					sprites2.get(index1).setX(tempX);
					Tween.from(sprites2.get(index1), SpriteAccessor.OPACITY, .3f).target(0).ease(Quad.INOUT).start(manager);
				}
				if (direction == Board.Direction.UP) {
					// keep x, up shift y
					int index1 = (int) selectedSquareFirst.x * boardRow + boardRow - 1; // find the tallest Y
					float tempY1 = sprites1.get(0).getY(); // smallest Y
					float tempY2 = sprites2.get(0).getY();
					for (int i = 0; i < boardRow - 1; ++i) {
						index1--;
						Tween.to(sprites1.get(index1), SpriteAccessor.CPOS_XY, .3f).targetRelative(0, blockHeight).ease(Quad.INOUT).start(manager);
						Collections.swap(sprites1, index1, index1 + 1);
						Tween.to(sprites2.get(index1), SpriteAccessor.CPOS_XY, .3f).targetRelative(0, -blockHeight).ease(Quad.INOUT).start(manager);
						Collections.swap(sprites2, index1, index1 + 1);
					}
					sprites1.get(index1).setY(tempY1);
					Tween.from(sprites1.get(index1), SpriteAccessor.OPACITY, .3f).target(0).ease(Quad.INOUT).start(manager);
					sprites2.get(index1).setY(tempY2);
					Tween.from(sprites2.get(index1), SpriteAccessor.OPACITY, .3f).target(0).ease(Quad.INOUT).start(manager);
				}
				if (direction == Board.Direction.DOWN) {
					// keep x, down shift y
					int index1 = (int) selectedSquareFirst.x * boardRow; // find the lowest Y
					float tempY1 = sprites1.get(boardRow - 1).getY(); // biggest Y
					float tempY2 = sprites2.get(boardRow - 1).getY();
					for (int i = 0; i < boardRow - 1; ++i) {
						index1++;
						Tween.to(sprites1.get(index1), SpriteAccessor.CPOS_XY, .3f).targetRelative(0, -blockHeight).ease(Quad.INOUT).start(manager);
						Collections.swap(sprites1, index1, index1 - 1);
						Tween.to(sprites2.get(index1), SpriteAccessor.CPOS_XY, .3f).targetRelative(0, blockHeight).ease(Quad.INOUT).start(manager);
						Collections.swap(sprites2, index1, index1 - 1);
					}
					sprites1.get(index1).setY(tempY1);
					Tween.from(sprites1.get(index1), SpriteAccessor.OPACITY, .3f).target(0).ease(Quad.INOUT).start(manager);
					sprites2.get(index1).setY(tempY2);
					Tween.from(sprites2.get(index1), SpriteAccessor.OPACITY, .3f).target(0).ease(Quad.INOUT).start(manager);
				}
			}
			animTime += dt;

			// When animation ends
			if ((animTime += dt) >= animTotalTime) {
				// Switch to next state, gems start to disappear
				cState = State.DisappearingGems;

				// Swap gems in the board
				//board.swap((int) selectedSquareFirst.x, (int) selectedSquareFirst.y, (int) selectedSquareSecond.x,(int) selectedSquareSecond.y);
				// Shift gems in the board
				board1.shift((int) selectedSquareFirst.x, (int) selectedSquareFirst.y, direction);
				board2.shift((int) selectedSquareFirst.x, (int) selectedSquareFirst.y, direction);
				// Increase multiplier
				++multiplier;

				// Play matching sounds
				// playMatchSound();

				// Create floating scores for the matching group
				// createFloatingScores();

				// Reset animation step
				animTime = 0;
			}
		}
		// DISAPPEARING GEMS STATE
		else if (cState == State.DisappearingGems) {
			if (animTime == 0) {
				System.out.println("State.DisappearingGems");
				if (!groupedSquares1.isEmpty() || !groupedSquares2.isEmpty()) {
					if (!groupedSquares1.isEmpty()) {
						// Delete squares that were matched on the board
						for (int i = 0; i < groupedSquares1.size(); ++i) {
							for (int j = 0; j < groupedSquares1.get(i).size(); ++j) {
								int index1 = (int) groupedSquares1.get(i).get(j).x * boardRow + (int) groupedSquares1.get(i).get(j).y;
								//System.out.println("x is: " + groupedSquares1.get(i).get(j).x + " y is: " + groupedSquares1.get(i).get(j).y);
								//System.out.println("col is: " + boardCol + " index is: " + index1);
								Timeline.createParallel()
										.push(Tween.to(sprites1.get(index1), SpriteAccessor.SCALE_XY, 0.3f).target(0, 0))
												//.push(Tween.to(sprites.get(index1), SpriteAccessor.SCALE_XY, 0.3f).target(0, 0))
										.start(manager);
								board1.del((int) groupedSquares1.get(i).get(j).x, (int) groupedSquares1.get(i).get(j).y);
							}
						}
					}
					if (!groupedSquares2.isEmpty()) {
						// Delete squares that were matched on the board
						for (int i = 0; i < groupedSquares2.size(); ++i) {
							for (int j = 0; j < groupedSquares2.get(i).size(); ++j) {
								int index1 = (int) groupedSquares2.get(i).get(j).x * boardRow + (int) groupedSquares2.get(i).get(j).y;
								Timeline.createParallel()
										.push(Tween.to(sprites2.get(index1), SpriteAccessor.SCALE_XY, 0.3f).target(0, 0))
												//.push(Tween.to(sprites.get(index1), SpriteAccessor.SCALE_XY, 0.3f).target(0, 0))
										.start(manager);
								board2.del((int) groupedSquares2.get(i).get(j).x, (int) groupedSquares2.get(i).get(j).y);
							}
						}
					}
				}
				else {
					cState = State.Wait;
					animTime = 0;
					return;
				}
			}
			animTime += dt;
			// When anim ends
			if ((animTime += dt) >= animTotalTime) {
				// Switch to next state, gems falling
				cState = State.AppearingGems;

				// Redraw scoreboard with new points
				// redrawScoreBoard();

				// Calculate fall movements
				//board.calcFallMovements();

				// Apply changes to the board
				//board.applyFall();

				// Fill empty spaces
				//board.fillSpaces();

				// Reset animation counter
				animTime = 0;
			}
		}

		// GEMS APPEARING STATE
		else if (cState == State.AppearingGems) {
			if (animTime == 0) {
				System.out.println("State.AppearingGems");
				if (!groupedSquares1.isEmpty()) {
					for (int i = 0; i < groupedSquares1.size(); ++i) {
						for (int j = 0; j < groupedSquares1.get(i).size(); ++j) {
							int index1 = (int) groupedSquares1.get(i).get(j).x * boardRow + (int) groupedSquares1.get(i).get(j).y;
							board1.fillNew((int) groupedSquares1.get(i).get(j).x, (int) groupedSquares1.get(i).get(j).y); //warning: reversed x/y
							Sprite img;
							img = new Sprite(getGemsColor(groupedSquares1.get(i).get(j).x, groupedSquares1.get(i).get(j).y, board1));
							img.setPosition(gemsInitial.x + groupedSquares1.get(i).get(j).x * blockWidth, groupedSquares1.get(i).get(j).y * blockHeight);
							sprites1.get(index1).set(img);
							Timeline.createParallel()
									//.push(Tween.set(sprites.get(index1), SpriteAccessor.SCALE_XY).target(0))
									.push(Tween.to(sprites1.get(index1), SpriteAccessor.OPACITY, 0.3f).target(1))
									.push(Tween.to(sprites1.get(index1), SpriteAccessor.SCALE_XY, 0.3f).target(1, 1))
									.start(manager);
						}
					}
				}
				if (!groupedSquares2.isEmpty()) {
					for (int i = 0; i < groupedSquares2.size(); ++i) {
						for (int j = 0; j < groupedSquares2.get(i).size(); ++j) {
							int index1 = (int) groupedSquares2.get(i).get(j).x * boardRow + (int) groupedSquares2.get(i).get(j).y;
							board2.fillNew((int) groupedSquares2.get(i).get(j).x, (int) groupedSquares2.get(i).get(j).y); //warning: reversed x/y
							Sprite img;
							img = new Sprite(getGemsColor(groupedSquares2.get(i).get(j).x, groupedSquares2.get(i).get(j).y, board2));
							img.setPosition(gemsInitial.x + groupedSquares2.get(i).get(j).x * blockWidth, Game.V_HEIGHT - (groupedSquares2.get(i).get(j).y + 1) * blockHeight);
							sprites2.get(index1).set(img);
							Timeline.createParallel()
									//.push(Tween.set(sprites.get(index1), SpriteAccessor.SCALE_XY).target(0))
									.push(Tween.to(sprites2.get(index1), SpriteAccessor.OPACITY, 0.3f).target(1))
									.push(Tween.to(sprites2.get(index1), SpriteAccessor.SCALE_XY, 0.3f).target(1, 1))
									.start(manager);
						}
					}
				}
			}
			animTime += dt;
			// When animation ends
			if ((animTime += dt) >= animTotalTime) {
				// Play the fall sound fx
				// _fallSFX.play();

				// Switch to the next state (waiting)
				cState = State.Wait;

				// Reset animation counter
				animTime = 0;

				// Reset animation variables
				//board.endAnimation();

				// Check if there are matching groups
				groupedSquares1 = board1.check();
				groupedSquares2 = board2.check();
				// If there are further matches
				if (!groupedSquares1.isEmpty() || !groupedSquares2.isEmpty()) {
					// Increase the score multiplier
					++multiplier;

					// Create the floating scores
					// createFloatingScores();

					// Play matching sound
					// playMatchSound();

					// Go back to the gems-fading state
					cState = State.DisappearingGems;
				}
/*
				// If there are neither current solutions nor possible future solutions
				else if (board1.solutions().isEmpty()) {
					// Make the board disappear
					cState = State.DisappearingBoard;
					// board.gemsOutScreen();
				}
				*/
			}
		}

		// DISAPPEARING BOARD STATE because there were no possible movements
		else if (cState == State.DisappearingBoard) {
			if (animTime == 0) {
				System.out.println("State.DisappearingBoard");
				// go through all of the squares
				for (int i = 0; i < boardCol; i++) {
					for (int j = 0; j < boardRow; j++) {
						int index = i * boardCol + j;
						// draw
						if (sprites1.get(index) != null) {
							// Sprite imgSprite = new Sprite(img[i][j]);
							// imgSprite.draw(sb);
							//System.out.println("tween, x: " + i + " y: " + j);
							Tween.to(sprites1.get(index), SpriteAccessor.OPACITY, 0.8f).target(0).start(manager);
						}
					}
				}
			}
			animTime += dt;
			// When animation ends
			if(animTime >= animTotalInitTime) {
				// Switch to the initial state
				cState = State.InitialGems;

				// Generate a brand new board
				board1.generate();
				sprites1.clear();
				// Reset animation counter
				animTime = 0;
			}
		}

		// In this state, the time has finished, so we need to create a
		// ScoreBoard
		else if (cState == State.TimeFinished) {
			System.out.println("State.TimeFinished");
			// When animation ends
			// if((animTime += deltaT) >= animTotalInitTime){

			// Create a new score table
			// _scoreTable = new ScoreTable(_parent, _points);

			// Switch to the following state
			cState = State.ShowingScoreTable;

			// Reset animation counter
			// animTime = 0;
			// }
		}
	}

	@Override
	public void render(float dt) {

		// SpriteBatch batch = game.getSpriteBatch();

		// STATE LOADING
		if (cState == State.Loading && lang != null) {
			String loading = lang.getString("Loading...");

			GlyphLayout bounds = new GlyphLayout(_fontLoading, loading);
			_fontLoading.draw(sb, loading, (Game.V_WIDTH - bounds.width) / 2,
					(Game.V_HEIGHT - bounds.height) / 2 + bounds.height);
			return;
		}

		// draw background
		sb.draw(imgBackground, 0, 0);
		// sb.setProjectionMatrix(hudCam.combined);
		// for (int i = 0; i < backgrounds.length; i++) {
		// backgrounds.render(sb);
		// }

		// draw board
		sb.draw(imgBoard, 0, 0);

		// get the size of each block
		//blockWidth = imgBoard.getRegionWidth() / boardCol;
		//blockHeight = imgBoard.getRegionHeight() / boardRow;

		manager.update(dt);

		// tween the whole board
		for (int i = 0; i < boardCol; i++) {
			for (int j = 0; j < boardRow; j++) {
				int index = i * boardRow + j;
				if (sprites1.size() > 0) {
					//System.out.println(sprites.size());
					sprites1.get(index).draw(sb);
				}
				if (sprites2.size() > 0) {
					//System.out.println(sprites.size());
					sprites2.get(index).draw(sb);
				}
			}
		}

/*
				// If the mouse is over a gem
				if (overGem((int)mousePos.x, (int)mousePos.y)) {
					// Draw the selector over that gem
					Coord coord = getCoord((int)mousePos.x, (int)mousePos.y);
					sb.draw(_imgSelector,
							(int)gemsInitial.x + coord.x * 76,
							(int)gemsInitial.y + coord.y * 76);
				}



				// If a gem was previously clicked
				if(cState == State.SelectedGem){
					// Draw the tinted selector over it
					sb.setColor(1.0f, 0.0f, 1.0f, 1.0f);
					sb.draw(_imgSelector,
							(int)gemsInitial.x + selectedSquareFirst.x * 76,
							(int)gemsInitial.y + selectedSquareFirst.y * 76);
					sb.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				}
			}

			// If a hint is being shown
			if (_showingHint > 0.0) {
				// Get the opacity percentage
				float p = (float)(_showingHint / _animHintTotalTime);

				float x = gemsInitial.x + _coordHint.x * 76;
				float y = gemsInitial.y + _coordHint.y * 76;


				sb.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f - p));
				sb.draw(_imgSelector, x, y);
				sb.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
			}
		}*/
	}

	private TextureRegion getGemsColor(int c, int r, Board board) {
		TextureRegion localImg = null;
		// Check the type of each square and
		// save the proper image in the img pointer
		switch (board.getSquare(c, r).getType()) {
			//case sqWhite:
			//	localImg = imgWhite;
			//	break;
			case sqRed:
				localImg = imgRed;
				break;
			case sqPurple:
				localImg = imgPurple;
				break;
			//case sqOrange:
			//	localImg = imgOrange;
			//	break;
			case sqGreen:
				localImg = imgGreen;
				break;
			case sqYellow:
				localImg = imgYellow;
				break;
			case sqBlue:
				localImg = imgBlue;
				break;
			default:
				break;
		} // switch end
		return localImg;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == 0) { // Left mouse button clicked
			clicking = true;
			mousePos.x = screenX;
			mousePos.y = screenY;
			game.getCamera().unproject(mousePos);

			// Button
			/*
			 * if (_exitButton.isClicked((int)mousePos.x, (int)mousePos.y)) {
			 * _parent.changeState("StateMenu"); } else if
			 * (_hintButton.isClicked((int)mousePos.x, (int)mousePos.y)) {
			 * showHint(); } else if (_musicButton.isClicked((int)mousePos.x,
			 * (int)mousePos.y)) { if (_song.isPlaying()) {
			 * _musicButton.setText(_lang.getString("Turn on music"));
			 * _song.stop(); } else {
			 * _musicButton.setText(_lang.getString("Turn off music"));
			 * _song.setLooping(true); _song.play(); } } else if
			 * (_resetButton.isClicked((int)mousePos.x, (int)mousePos.y)) {
			 * _state = State.DisappearingBoard; gemsOutScreen(); resetGame(); }
			 * else
			 */
			if (overGem((int) mousePos.x, (int) mousePos.y)) { // gem got
				// clicked
				// _selectSFX.play();

				if (cState == State.Wait) {
					cState = State.SelectedGem;
					System.out.println("SelectedGem");
					Coord coord = getCoord((int) mousePos.x, (int) mousePos.y);
					selectedSquareFirst.x = coord.x;
					selectedSquareFirst.y = coord.y;
				}
/*
				else if (cState == State.SelectedGem) {
					if (!checkClickedSquare((int) mousePos.x, (int) mousePos.y)) {
						selectedSquareFirst.x = -1;
						selectedSquareFirst.y = -1;
						selectedSquareSecond.x = -1;
						selectedSquareSecond.y = -1;
						cState = State.Wait;
					}
				}
				*/
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == 0) { // Left mouse button clicked
			clicking = false;

			mousePos.x = screenX;
			mousePos.y = screenY;
			game.getCamera().unproject(mousePos);

			if (cState == State.SelectedGem) {

				Coord res = getCoord((int) mousePos.x, (int) mousePos.y);
				selectedSquareSecond.x = res.x;
				selectedSquareSecond.y = res.y;
				if (res != selectedSquareFirst) {
					checkClickedSquare((int) mousePos.x, (int) mousePos.y);
				}
				else {
					System.out.println(selectedSquareSecond.x + " " + selectedSquareSecond.y + " color: " + board1.getSquare(selectedSquareSecond.x, selectedSquareSecond.y).getType());
				}
			}

			// _hintButton.touchUp();
			// _musicButton.touchUp();
			// _exitButton.touchUp();
			// _resetButton.touchUp();
		}

		return false;
	}

	//TODO: adjust this
	private boolean overGem(int mX, int mY) {
		return (mX > gemsInitial.x
				&& mX < gemsInitial.x + imgBoard.getRegionWidth()
				&& mY > gemsInitial.y && mY < gemsInitial.y
				+ imgBoard.getRegionHeight());
	}

	private Coord getCoord(int mX, int mY) {
		return new Coord((mX - (int) gemsInitial.x) / blockWidth,
				(mY - (int) gemsInitial.y) / blockHeight);
	}

	private boolean checkClickedSquare(int mX, int mY) {
		//selectedSquareSecond = getCoord(mX, mY);

		// If gem is neighbour
		//if (Math.abs(selectedSquareFirst.x - selectedSquareSecond.x)
		//+ Math.abs(selectedSquareFirst.y - selectedSquareSecond.y) == 1) {
		if (Math.abs(selectedSquareFirst.x - selectedSquareSecond.x) + Math.abs(selectedSquareFirst.y - selectedSquareSecond.y) != 0) {
			if (selectedSquareFirst.y == selectedSquareSecond.y || selectedSquareFirst.x == selectedSquareSecond.x) {
				if (selectedSquareFirst.y == selectedSquareSecond.y) {
					if (selectedSquareFirst.x < selectedSquareSecond.x) {
						direction = Board.Direction.RIGHT;
					}
					if (selectedSquareFirst.x > selectedSquareSecond.x) {
						direction = Board.Direction.LEFT;
					}
				}
				if (selectedSquareFirst.x == selectedSquareSecond.x) {
					if (selectedSquareFirst.y < selectedSquareSecond.y) {
						direction = Board.Direction.UP;
					}
					if (selectedSquareFirst.y > selectedSquareSecond.y) {
						direction = Board.Direction.DOWN;
					}
				}
				System.out.println("board1: " + selectedSquareFirst.x + " " + selectedSquareFirst.y + " color: " + board1.getSquare(selectedSquareFirst.x, selectedSquareFirst.y).getType());
				System.out.println("board1: " + selectedSquareSecond.x + " " + selectedSquareSecond.y + " color: " + board1.getSquare(selectedSquareSecond.x, selectedSquareSecond.y).getType());
				System.out.println(direction);
				System.out.println("board2: " + selectedSquareFirst.x + " " + selectedSquareFirst.y + " color: " + board2.getSquare(selectedSquareFirst.x, selectedSquareFirst.y).getType());
				System.out.println("board2: " + selectedSquareSecond.x + " " + selectedSquareSecond.y + " color: " + board2.getSquare(selectedSquareSecond.x, selectedSquareSecond.y).getType());
				//check board 1
				Board tempBoard1 = new Board(board1);
				tempBoard1.shift(selectedSquareFirst.x, selectedSquareFirst.y, direction);
				groupedSquares1 = tempBoard1.check();
				//check board 2
				Board tempBoard2 = new Board(board2);
				Board.Direction tempDirection = direction;
				if (tempDirection == Board.Direction.UP) {
					tempDirection = Board.Direction.DOWN;
				}
				if (tempDirection == Board.Direction.DOWN) {
					tempDirection = Board.Direction.UP;
				}
				tempBoard2.shift(selectedSquareFirst.x, selectedSquareFirst.y, direction);
				groupedSquares2 = tempBoard2.check();
				// always goes to changingGems
				cState = State.ChangingGems;
				selectedSquareSecond.x = -1;
				selectedSquareSecond.y = -1;
				// If winning movement
				if (!groupedSquares1.isEmpty()) {
					return true;
				}
			}
		}
		selectedSquareSecond.x = -1;
		selectedSquareSecond.y = -1;
		return false;
	}

	public void load() {
		AssetManager assetManager = game.getAssetManager();

		// Load fonts
		BitmapFontLoader.BitmapFontParameter fontParameters = new BitmapFontLoader.BitmapFontParameter();
		// fontParameters.flip = true;

		// assetManager.load("data/timeFont.fnt", BitmapFont.class,
		// fontParameters);
		// assetManager.load("data/scoreFont.fnt", BitmapFont.class,
		// fontParameters);
		// assetManager.load("data/normalFont.fnt", BitmapFont.class,
		// fontParameters);
		// assetManager.load("data/menuFont.fnt", BitmapFont.class,
		// fontParameters);

		// Load textures
		assetManager.load("data/background.png", Texture.class);
		assetManager.load("data/board.png", Texture.class);
		assetManager.load("data/boardTemp.png", Texture.class);

		assetManager.load("data/scoreBackground.png", Texture.class);
		assetManager.load("data/buttonBackground.png", Texture.class);
		assetManager.load("data/buttonBackgroundPressed.png", Texture.class);
		assetManager.load("data/selector.png", Texture.class);
		assetManager.load("data/timeBackground.png", Texture.class);
		assetManager.load("data/gemwhite.png", Texture.class);
		assetManager.load("data/gemred.png", Texture.class);
		assetManager.load("data/gempurple.png", Texture.class);
		assetManager.load("data/gemorange.png", Texture.class);
		assetManager.load("data/gemgreen.png", Texture.class);
		assetManager.load("data/gemyellow.png", Texture.class);
		assetManager.load("data/gemblue.png", Texture.class);
		assetManager.load("data/iconHint.png", Texture.class);
		assetManager.load("data/iconRestart.png", Texture.class);
		assetManager.load("data/iconExit.png", Texture.class);
		assetManager.load("data/iconMusic.png", Texture.class);

		// Load SFX and music
		assetManager.load("data/match1.ogg", Sound.class);
		assetManager.load("data/match2.ogg", Sound.class);
		assetManager.load("data/match3.ogg", Sound.class);
		assetManager.load("data/select.ogg", Sound.class);
		assetManager.load("data/fall.ogg", Sound.class);
		assetManager.load("data/music1.ogg", Music.class);

		resetGame();
	}

	@Override
	public void unload() {
		// Set assets references to null
		imgBoard = null;
		imgWhite = null;
		imgRed = null;
		imgPurple = null;
		imgOrange = null;
		imgGreen = null;
		imgYellow = null;
		imgBlue = null;
		_imgSelector = null;
		imgBackground = null;
		imgScoreBackground = null;
		imgTimeBackground = null;
		_fontTime = null;
		_fontScore = null;
		_fontText = null;
		_match1SFX = null;
		_match2SFX = null;
		_match3SFX = null;
		_selectSFX = null;
		_fallSFX = null;
		_song = null;

		/*
		 * _hintButton.setIcon(null); _resetButton.setIcon(null);
		 * _exitButton.setIcon(null); _musicButton.setIcon(null);
		 *
		 * _hintButton.setBackground(null); _resetButton.setBackground(null);
		 * _exitButton.setBackground(null); _musicButton.setBackground(null);
		 *
		 * _hintButton.setFont(null); _resetButton.setFont(null);
		 * _exitButton.setFont(null); _musicButton.setFont(null);
		 */
		// Unload assets
		AssetManager assetManager = game.getAssetManager();
		assetManager.unload("data/timeFont.fnt");
		assetManager.unload("data/scoreFont.fnt");
		assetManager.unload("data/normalFont.fnt");
		assetManager.unload("data/menuFont.fnt");
		assetManager.unload("data/scoreBackground.png");
		assetManager.unload("data/buttonBackground.png");
		assetManager.unload("data/buttonBackgroundPressed.png");
		assetManager.unload("data/board.png");
		assetManager.unload("data/boardTemp.png");
		assetManager.unload("data/selector.png");
		assetManager.unload("data/timeBackground.png");
		assetManager.unload("data/gemwhite.png");
		assetManager.unload("data/gemred.png");
		assetManager.unload("data/gempurple.png");
		assetManager.unload("data/gemorange.png");
		assetManager.unload("data/gemgreen.png");
		assetManager.unload("data/gemyellow.png");
		assetManager.unload("data/gemblue.png");
		assetManager.unload("data/iconHint.png");
		assetManager.unload("data/iconRestart.png");
		assetManager.unload("data/iconExit.png");
		assetManager.unload("data/iconMusic.png");
		assetManager.unload("data/match1.ogg");
		assetManager.unload("data/match2.ogg");
		assetManager.unload("data/match3.ogg");
		assetManager.unload("data/select.ogg");
		assetManager.unload("data/fall.ogg");
		assetManager.unload("data/music1.ogg");
	}

	private void resetGame() {
		// Reset score
		points = 0;

		// Generate board
		board1.generate();
		board2.generate();

		// Redraw the scoreboard
		// redrawScoreBoard();

		// Restart the time (two minutes)
		// remainingTime = 120;
	}

	private void removeEndedParticles() {
		int numParticles = _effects.size();

		for (int i = 0; i < numParticles; ++i) {
			if (_effects.get(i).isComplete()) {
				_effects.remove(i);
				--i;
				--numParticles;
			}
		}
	}

	private void removeEndedFloatingScores() {
		int numScores = floatingScores.size();

		for (int i = 0; i < numScores; ++i) {
			if (floatingScores.get(i).isFinished()) {
				floatingScores.remove(i);
				--i;
				--numScores;
			}
		}
	}
	private void init() {
		// Initial animation state
		animTime = 0;

		// Steps for short animations
		animTotalTime = 0.4;

		// Steps for long animations
		animTotalInitTime = 1.0;

		// Steps for the hint animation
		//_animHintTotalTime = 1.0;

		// Reset the hint flag
		//_showingHint = -1;

		// Initial score multiplier
		//_multiplier = 1;

		// Reset the game to the initial values
		resetGame();
	}
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		cState = State.Loading;

	}
}
