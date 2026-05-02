package main;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {
    
    // Screen settings
    final int originalTileSize = 16;
    final int scale = 3;
    final int tileSize = originalTileSize * scale;
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;
    
    // Game states
    final int STATE_MENU = 0;
    final int STATE_TIMERSELECT = 1;
    final int STATE_GAME = 2;
    final int STATE_GAMEOVER = 3;
    final int STATE_CONTROLS = 4;
    int gameState = STATE_MENU;
    
    // UI Elements
    Rectangle playButton     = new Rectangle(screenWidth / 2 - 110, screenHeight / 2 + 10,  220, 52);
    Rectangle playAgainButton = new Rectangle(screenWidth / 2 - 130, screenHeight / 2 + 60, 260, 56);
    Rectangle controlsButton = new Rectangle(screenWidth / 2 - 110, screenHeight / 2 + 75,  220, 52);
    Rectangle quitButton     = new Rectangle(screenWidth / 2 - 110, screenHeight / 2 + 140, 220, 46);

    Rectangle timerBtn60  = new Rectangle(screenWidth / 2 - 240, screenHeight / 2 - 60, 140, 140);
    Rectangle timerBtn99  = new Rectangle(screenWidth / 2 - 70,  screenHeight / 2 - 60, 140, 140);
    Rectangle timerBtnInf = new Rectangle(screenWidth / 2 + 100, screenHeight / 2 - 60, 140, 140);
    Rectangle startFightBtn = new Rectangle(screenWidth / 2 - 120, screenHeight / 2 + 110, 240, 58);
    Rectangle backBtn     = new Rectangle(30, 20, 90, 34);
    Rectangle exitBtn     = new Rectangle(screenWidth - 115, screenHeight - 44, 100, 34);

    boolean hoveringPlay     = false;
    boolean hoveringPlayAgain = false;
    boolean hoveringControls = false;
    boolean hoveringQuit     = false;
    boolean hovering60       = false;
    boolean hovering99       = false;
    boolean hoveringInf      = false;
    boolean hoveringStart    = false;
    boolean hoveringBack     = false;
    boolean hoveringExit     = false;

    // Timer
    int selectedTimerSeconds = 60;
    int timerFrames = 60 * 60;
    boolean timerInfinite = false;

    // Game loop
    Thread gameThread;
    final int FPS = 60;

    // Input
    Set<Integer> keysPressed = new HashSet<>();

    // Game objects
    Player player1;
    Player player2;
    GameState gameStateObj;
    Renderer renderer;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        player1 = new Player(150, 430, 1);
        player2 = new Player(580, 430, -1);
        gameStateObj = new GameState();
        renderer = new Renderer(screenWidth, screenHeight);

        setupInputListeners();
    }

    private void setupInputListeners() {
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keysPressed.add(e.getKeyCode());
                if (gameState == STATE_GAME) {
                    if (e.getKeyCode() == KeyEvent.VK_W && player1.onGround) {
                        player1.vy = -13f;
                        player1.onGround = false;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_UP && player2.onGround) {
                        player2.vy = -13f;
                        player2.onGround = false;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        exitToMenu();
                    }
                }
                if (gameState == STATE_CONTROLS) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        gameState = STATE_MENU;
                        repaint();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keysPressed.remove(e.getKeyCode());
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getPoint());
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleHover(e.getPoint());
            }
        });
    }

    void exitToMenu() {
        gameState = STATE_MENU;
        keysPressed.clear();
        repaint();
    }

    void quitGame() {
        System.exit(0);
    }

    void handleClick(Point p) {
        if (gameState == STATE_MENU) {
            if (playButton.contains(p)) {
                gameState = STATE_TIMERSELECT;
                repaint();
            }
            if (controlsButton.contains(p)) {
                gameState = STATE_CONTROLS;
                repaint();
            }
            if (quitButton.contains(p)) {
                quitGame();
            }
        } else if (gameState == STATE_CONTROLS) {
            if (backBtn.contains(p)) {
                gameState = STATE_MENU;
                repaint();
            }
        } else if (gameState == STATE_TIMERSELECT) {
            if (timerBtn60.contains(p)) {
                selectedTimerSeconds = 60;
                timerInfinite = false;
                repaint();
            }
            if (timerBtn99.contains(p)) {
                selectedTimerSeconds = 99;
                timerInfinite = false;
                repaint();
            }
            if (timerBtnInf.contains(p)) {
                timerInfinite = true;
                repaint();
            }
            if (startFightBtn.contains(p)) {
                startGame();
            }
            if (backBtn.contains(p)) {
                gameState = STATE_MENU;
                repaint();
            }
        } else if (gameState == STATE_GAME) {
            if (exitBtn.contains(p)) {
                exitToMenu();
            }
        } else if (gameState == STATE_GAMEOVER) {
            if (playAgainButton.contains(p)) {
                gameState = STATE_TIMERSELECT;
                repaint();
            }
            if (exitBtn.contains(p)) {
                exitToMenu();
            }
        }
    }

    void handleHover(Point p) {
        hoveringPlay      = gameState == STATE_MENU && playButton.contains(p);
        hoveringPlayAgain = gameState == STATE_GAMEOVER && playAgainButton.contains(p);
        hoveringControls = gameState == STATE_MENU && controlsButton.contains(p);
        hoveringQuit     = gameState == STATE_MENU && quitButton.contains(p);
        hovering60       = gameState == STATE_TIMERSELECT && timerBtn60.contains(p);
        hovering99       = gameState == STATE_TIMERSELECT && timerBtn99.contains(p);
        hoveringInf      = gameState == STATE_TIMERSELECT && timerBtnInf.contains(p);
        hoveringStart    = gameState == STATE_TIMERSELECT && startFightBtn.contains(p);
        hoveringBack     = (gameState == STATE_TIMERSELECT || gameState == STATE_CONTROLS) && backBtn.contains(p);
        hoveringExit     = (gameState == STATE_GAME || gameState == STATE_GAMEOVER) && exitBtn.contains(p);

        boolean anyHover = hoveringPlay || hoveringPlayAgain || hoveringControls || hoveringQuit || hovering60 || hovering99
                        || hoveringInf || hoveringStart || hoveringBack || hoveringExit;
        setCursor(anyHover ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
        repaint();
    }

    void startGame() {
        player1.reset(150, 430, 1);
        player2.reset(580, 430, -1);
        gameStateObj.reset();

        timerFrames = timerInfinite ? Integer.MAX_VALUE : selectedTimerSeconds * FPS;
        gameState = STATE_GAME;

        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    @Override
    public void run() {
        double interval = 1_000_000_000.0 / FPS;
        double nextDraw = System.nanoTime() + interval;

        while (gameState == STATE_GAME) {
            update();
            repaint();

            try {
                long rem = (long)((nextDraw - System.nanoTime()) / 1_000_000);
                if (rem > 0) Thread.sleep(rem);
                nextDraw += interval;
            } catch (InterruptedException ignored) {}
        }
    }

    void update() {
        if (gameState != STATE_GAME) return;

        if (!timerInfinite && timerFrames > 0) {
            timerFrames--;
            if (timerFrames <= 0) {
                if (player1.hp > player2.hp)      gameStateObj.winner = "PLAYER 1 WINS! (Time)";
                else if (player2.hp > player1.hp) gameStateObj.winner = "PLAYER 2 WINS! (Time)";
                else                              gameStateObj.winner = "DRAW!";
                gameState = STATE_GAMEOVER;
                return;
            }
        }

        boolean p1left  = keysPressed.contains(KeyEvent.VK_A);
        boolean p1right = keysPressed.contains(KeyEvent.VK_D);
        boolean p1punch = keysPressed.contains(KeyEvent.VK_H);
        boolean p1kick  = keysPressed.contains(KeyEvent.VK_J);

        boolean p2left  = keysPressed.contains(KeyEvent.VK_LEFT);
        boolean p2right = keysPressed.contains(KeyEvent.VK_RIGHT);
        boolean p2punch = keysPressed.contains(KeyEvent.VK_NUMPAD4);
        boolean p2kick  = keysPressed.contains(KeyEvent.VK_NUMPAD5);

        player1.update(p1left, p1right, p1punch, p1kick, player2);
        player2.update(p2left, p2right, p2punch, p2kick, player1);

        if (player1.hp <= 0 || player2.hp <= 0) {
            gameStateObj.winner = (player1.hp <= 0) ? "PLAYER 2 WINS! KO!" : "PLAYER 1 WINS! KO!";
            gameState = STATE_GAMEOVER;
        }

        gameStateObj.updateEffects();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        switch (gameState) {
            case STATE_MENU     -> renderer.drawMenu(g2, playButton, hoveringPlay,
                                        controlsButton, hoveringControls, quitButton, hoveringQuit);
            case STATE_CONTROLS -> renderer.drawControls(g2, backBtn, hoveringBack);
            case STATE_TIMERSELECT -> renderer.drawTimerSelect(g2, timerBtn60, timerBtn99, timerBtnInf,
                                        startFightBtn, backBtn, hovering60, hovering99, hoveringInf,
                                        hoveringStart, hoveringBack, selectedTimerSeconds, timerInfinite);
            case STATE_GAME     -> renderer.drawGame(g2, player1, player2, gameStateObj,
                                        timerFrames, timerInfinite, FPS, exitBtn, hoveringExit);
            case STATE_GAMEOVER -> renderer.drawGameOver(g2, player1, player2, gameStateObj,
                                        playAgainButton, hoveringPlayAgain, timerFrames, timerInfinite, FPS,
                                        exitBtn, hoveringExit);
        }

        g2.dispose();
    }
}