package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Renderer {
    private int screenWidth, screenHeight;
    private BufferedImage menuBg = null;

    public Renderer(int screenWidth, int screenHeight) {
        this.screenWidth  = screenWidth;
        this.screenHeight = screenHeight;

        // Try loading the new PNG background
        String[] paths = {
            "resources/boxing_bg.png",
            "../../resources/boxing_bg.png",
            "C:/Users/RML/Downloads/2D-STICKMAN-main/resources/BG_BOX.png"
        };
        for (String path : paths) {
            try {
                BufferedImage img = ImageIO.read(new File(path));
                if (img != null) { menuBg = img; break; }
            } catch (Exception ignored) {}
        }
    }

    // ------------------------------------------------------------------ MENU
    public void drawMenu(Graphics2D g2,
                         Rectangle playButton,    boolean hoveringPlay,
                         Rectangle controlsButton, boolean hoveringControls,
                         Rectangle quitButton,    boolean hoveringQuit) {
        int w = screenWidth, h = screenHeight;

        if (menuBg != null) {
            g2.drawImage(menuBg, 0, 0, w, h, null);
            // Dark overlay so text pops
            g2.setColor(new Color(0, 0, 0, 155));
            g2.fillRect(0, 0, w, h);
        } else {
            g2.setPaint(new GradientPaint(0, 0, new Color(10, 10, 20), 0, h, new Color(30, 10, 10)));
            g2.fillRect(0, 0, w, h);
        }

        // Title
        Font titleFont = new Font("Courier New", Font.BOLD, 58);
        g2.setFont(titleFont);
        String title = "2D STICKMAN";
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(title)) / 2, ty = 90;

        for (int i = 8; i >= 1; i--) {
            g2.setColor(new Color(200, 50, 50, 18));
            g2.drawString(title, tx + i, ty + i);
            g2.drawString(title, tx - i, ty + i);
        }
        g2.setColor(new Color(255, 230, 230));
        g2.drawString(title, tx, ty);

        // Subtitle
        g2.setFont(new Font("Courier New", Font.BOLD, 16));
        g2.setColor(new Color(220, 80, 80));
        String sub = "2-PLAYER LOCAL FIGHTER";
        FontMetrics fms = g2.getFontMetrics();
        g2.drawString(sub, (w - fms.stringWidth(sub)) / 2, ty + 28);

        // Buttons
        drawButton(g2, playButton,     "\u25B6  PLAY",      hoveringPlay,
                   new Color(220, 40, 40), new Color(160, 20, 20));
        drawButton(g2, controlsButton, "\uD83C\uDFAE  CONTROLS", hoveringControls,
                   new Color(40, 100, 200), new Color(20, 60, 140));
        drawButton(g2, quitButton,     "\u2715  QUIT GAME", hoveringQuit,
                   new Color(80, 80, 80),  new Color(40, 40, 40));
    }

    // -------------------------------------------------------------- CONTROLS
    public void drawControls(Graphics2D g2, Rectangle backBtn, boolean hoveringBack) {
        int w = screenWidth, h = screenHeight;

        // Background
        if (menuBg != null) {
            g2.drawImage(menuBg, 0, 0, w, h, null);
            g2.setColor(new Color(0, 0, 0, 185));
            g2.fillRect(0, 0, w, h);
        } else {
            g2.setPaint(new GradientPaint(0, 0, new Color(10, 10, 20), 0, h, new Color(30, 10, 10)));
            g2.fillRect(0, 0, w, h);
        }

        // Title
        g2.setFont(new Font("Courier New", Font.BOLD, 38));
        g2.setColor(new Color(255, 220, 220));
        String heading = "CONTROLS";
        FontMetrics fmh = g2.getFontMetrics();
        g2.drawString(heading, (w - fmh.stringWidth(heading)) / 2, 70);

        // Divider line
        g2.setColor(new Color(200, 50, 50, 180));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(w / 2 - 180, 82, w / 2 + 180, 82);

        // Panel positions
        int panelW = 290, panelH = 290;
        int p1x = w / 2 - panelW - 20;
        int p2x = w / 2 + 20;
        int panelY = 100;

        // --- Player 1 Panel ---
        drawControlPanel(g2, p1x, panelY, panelW, panelH,
            "PLAYER 1", new Color(100, 180, 255), new Color(30, 70, 140),
            new String[][]{
                {"\u2190 / A",      "Move Left"},
                {"\u2192 / D",      "Move Right"},
                {"W",               "Jump"},
                {"H",               "Punch"},
                {"J",               "Kick"},
            });

        // --- Player 2 Panel ---
        drawControlPanel(g2, p2x, panelY, panelW, panelH,
            "PLAYER 2", new Color(255, 130, 100), new Color(140, 50, 30),
            new String[][]{
                {"\u2190 Arrow",    "Move Left"},
                {"\u2192 Arrow",    "Move Right"},
                {"\u2191 Arrow",    "Jump"},
                {"Numpad 4",        "Punch"},
                {"Numpad 5",        "Kick"},
            });

        // General tip
        g2.setFont(new Font("Courier New", Font.ITALIC, 12));
        g2.setColor(new Color(180, 160, 160));
        String tip = "Tip: Press ESC during a fight to return to the menu.";
        FontMetrics fmt = g2.getFontMetrics();
        g2.drawString(tip, (w - fmt.stringWidth(tip)) / 2, panelY + panelH + 30);

        // Back button
        drawBackButton(g2, backBtn, hoveringBack);
    }

    private void drawControlPanel(Graphics2D g2, int x, int y, int pw, int ph,
                                   String title, Color accent, Color dark,
                                   String[][] rows) {
        // Panel background
        g2.setColor(new Color(15, 15, 30, 210));
        g2.fillRoundRect(x, y, pw, ph, 16, 16);
        g2.setColor(accent);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(x, y, pw, ph, 16, 16);

        // Header bar
        g2.setPaint(new GradientPaint(x, y, dark, x, y + 38, dark.darker()));
        g2.fillRoundRect(x, y, pw, 38, 16, 16);
        g2.fillRect(x, y + 22, pw, 16);

        g2.setFont(new Font("Courier New", Font.BOLD, 17));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, x + (pw - fm.stringWidth(title)) / 2, y + 26);

        // Rows
        int rowY = y + 58;
        int rowH = (ph - 58 - 10) / rows.length;
        for (int i = 0; i < rows.length; i++) {
            // Alternating row tint
            if (i % 2 == 0) {
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillRoundRect(x + 6, rowY - 14, pw - 12, rowH, 6, 6);
            }

            // Key badge
            g2.setColor(accent);
            g2.setStroke(new BasicStroke(1.5f));
            int badgeW = 90;
            g2.fillRoundRect(x + 12, rowY - 13, badgeW, 22, 8, 8);
            g2.setColor(dark.darker());
            g2.drawRoundRect(x + 12, rowY - 13, badgeW, 22, 8, 8);

            g2.setFont(new Font("Courier New", Font.BOLD, 12));
            g2.setColor(Color.WHITE);
            FontMetrics fmk = g2.getFontMetrics();
            g2.drawString(rows[i][0],
                x + 12 + (badgeW - fmk.stringWidth(rows[i][0])) / 2,
                rowY + 4);

            // Action label
            g2.setFont(new Font("Courier New", Font.PLAIN, 13));
            g2.setColor(new Color(210, 210, 210));
            g2.drawString(rows[i][1], x + 112, rowY + 4);

            rowY += rowH;
        }
    }

    // --------------------------------------------------------------- TIMER SELECT
    public void drawTimerSelect(Graphics2D g2, Rectangle btn60, Rectangle btn99, Rectangle btnInf,
                                Rectangle startFightBtn, Rectangle backBtn,
                                boolean hovering60, boolean hovering99, boolean hoveringInf,
                                boolean hoveringStart, boolean hoveringBack,
                                int selectedTimer, boolean infinite) {
        int w = screenWidth, h = screenHeight;

        g2.setPaint(new GradientPaint(0, 0, new Color(15, 10, 10), 0, h, new Color(30, 10, 10)));
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(255, 60, 60, 12));
        for (int x = 0; x < w; x += 48) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 48) g2.drawLine(0, y, w, y);

        g2.setFont(new Font("Courier New", Font.BOLD, 40));
        g2.setColor(new Color(255, 220, 220));
        String heading = "SELECT ROUND TIME";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(heading, (w - fm.stringWidth(heading)) / 2, h / 2 - 105);

        g2.setFont(new Font("Courier New", Font.PLAIN, 13));
        g2.setColor(new Color(180, 120, 120));
        String sub = "choose how long the fight lasts";
        FontMetrics fms = g2.getFontMetrics();
        g2.drawString(sub, (w - fms.stringWidth(sub)) / 2, h / 2 - 76);

        drawTimerBox(g2, btn60,  "60",       "SECONDS",  hovering60,  !infinite && selectedTimer == 60);
        drawTimerBox(g2, btn99,  "99",       "SECONDS",  hovering99,  !infinite && selectedTimer == 99);
        drawTimerBox(g2, btnInf, "\u221E",   "INFINITE", hoveringInf, infinite);

        g2.setFont(new Font("Courier New", Font.BOLD, 13));
        g2.setColor(new Color(200, 200, 200));
        String sel = infinite ? "Selected: Infinite" : "Selected: " + selectedTimer + " seconds";
        FontMetrics fmsel = g2.getFontMetrics();
        g2.drawString(sel, (w - fmsel.stringWidth(sel)) / 2, h / 2 + 100);

        drawButton(g2, startFightBtn, "\u2694  START FIGHT", hoveringStart,
                   new Color(220, 40, 40), new Color(160, 20, 20));

        drawBackButton(g2, backBtn, hoveringBack);
    }

    // ------------------------------------------------------------------ GAME
    public void drawGame(Graphics2D g2, Player p1, Player p2, GameState state,
                         int timer, boolean infinite, int fps,
                         Rectangle exitBtn, boolean hoveringExit) {
        int w = screenWidth, h = screenHeight;

        g2.setPaint(new GradientPaint(0, 0, new Color(15, 15, 30), 0, h, new Color(25, 35, 55)));
        g2.fillRect(0, 0, w, h);

        int ropeY1 = 380, ropeY2 = 340;
        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(180, 30, 30));
        g2.drawLine(0, ropeY1, w, ropeY1);
        g2.drawLine(0, ropeY2, w, ropeY2);

        int[] postX = {30, w - 30};
        for (int px : postX) {
            g2.setColor(new Color(60, 60, 200));
            g2.fillRect(px - 4, 290, 8, 170);
        }

        g2.setPaint(new GradientPaint(0, 430, new Color(80, 50, 30), 0, h, new Color(40, 25, 10)));
        g2.fillRect(0, 430, w, h - 430);
        g2.setColor(new Color(120, 80, 50));
        g2.fillRect(0, 430, w, 6);

        for (int[] p : state.particles) {
            float alpha = Math.max(0, p[2] / 30f);
            g2.setColor(new Color(1f, 0.6f, 0.1f, alpha));
            g2.fillOval(p[0] - 4, p[1] - 4, 8, 8);
        }

        drawStickman(g2, (int)p1.x, (int)p1.y, p1.facing, p1.anim, p1.flashTimer > 0, 1, p1.isMoving);
        drawStickman(g2, (int)p2.x, (int)p2.y, p2.facing, p2.anim, p2.flashTimer > 0, 2, p2.isMoving);

        g2.setFont(new Font("Courier New", Font.BOLD, 16));
        for (String[] l : state.hitLabels) {
            int lx = Integer.parseInt(l[0]), ly = Integer.parseInt(l[1]), life = Integer.parseInt(l[2]);
            float a = Math.min(1f, life / 14f);
            g2.setColor(new Color(1f, 0.9f, 0.1f, a));
            FontMetrics flm = g2.getFontMetrics();
            g2.drawString(l[3], lx - flm.stringWidth(l[3]) / 2, ly);
        }

        g2.setFont(new Font("Courier New", Font.BOLD, 12));
        g2.setColor(new Color(180, 220, 255));
        drawCenteredText(g2, "P1", (int)p1.x, (int)p1.y - 110);
        g2.setColor(new Color(255, 180, 160));
        drawCenteredText(g2, "P2", (int)p2.x, (int)p2.y - 110);

        drawHealthBar(g2, 24,        18, p1.hp, 150, new Color(0,   210, 80),  "PLAYER 1");
        drawHealthBar(g2, w - 258,   18, p2.hp, 150, new Color(255,  80, 80),  "PLAYER 2");

        drawTimer(g2, timer, infinite, fps);
        drawExitButton(g2, exitBtn, hoveringExit);
    }

    // ------------------------------------------------------------ GAME OVER
    public void drawGameOver(Graphics2D g2, Player p1, Player p2, GameState state,
                             Rectangle playButton, boolean hovering,
                             int timer, boolean infinite, int fps,
                             Rectangle exitBtn, boolean hoveringExit) {
        drawGame(g2, p1, p2, state, timer, infinite, fps, exitBtn, hoveringExit);

        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setFont(new Font("Courier New", Font.BOLD, 46));
        FontMetrics fm = g2.getFontMetrics();
        int tx = (screenWidth - fm.stringWidth(state.winner)) / 2;
        for (int i = 6; i >= 1; i--) {
            g2.setColor(new Color(255, 180, 0, 20));
            g2.drawString(state.winner, tx + i, screenHeight / 2 - 20 + i);
        }
        g2.setColor(new Color(255, 210, 40));
        g2.drawString(state.winner, tx, screenHeight / 2 - 20);

        drawButton(g2, playButton, "\u21BA  PLAY AGAIN", hovering,
                   new Color(220, 40, 40), new Color(160, 20, 20));
        drawExitButton(g2, exitBtn, hoveringExit);
    }

    // --------------------------------------------------------- SHARED HELPERS
    private void drawExitButton(Graphics2D g2, Rectangle btn, boolean hover) {
        g2.setColor(hover ? new Color(200, 40, 40) : new Color(100, 20, 20));
        g2.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g2.setColor(hover ? new Color(255, 100, 100) : new Color(160, 60, 60));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g2.setFont(new Font("Courier New", Font.BOLD, 13));
        g2.setColor(hover ? Color.WHITE : new Color(220, 160, 160));
        FontMetrics fm = g2.getFontMetrics();
        String t = "\u2715 MENU";
        g2.drawString(t, btn.x + (btn.width - fm.stringWidth(t)) / 2,
                      btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2);
    }

    private void drawBackButton(Graphics2D g2, Rectangle backBtn, boolean hover) {
        g2.setColor(hover ? new Color(255, 100, 100) : new Color(150, 80, 80));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 8, 8);
        g2.setFont(new Font("Courier New", Font.BOLD, 13));
        g2.setColor(hover ? new Color(255, 200, 200) : new Color(180, 130, 130));
        FontMetrics fmb = g2.getFontMetrics();
        String back = "\u25C0 BACK";
        g2.drawString(back,
            backBtn.x + (backBtn.width - fmb.stringWidth(back)) / 2,
            backBtn.y + (backBtn.height + fmb.getAscent() - fmb.getDescent()) / 2);
    }

    private void drawStickman(Graphics2D g2, int x, int y, int facing, String anim,
                               boolean flash, int playerNum, boolean isMoving) {
        Color bodyColor    = flash ? new Color(255, 70, 70)
                           : (playerNum == 1 ? new Color(100, 180, 255) : new Color(255, 130, 100));
        Color outlineColor = flash ? new Color(255, 30, 30)
                           : (playerNum == 1 ? new Color(40,  110, 210) : new Color(200,  70,  50));
        Color gloveColor   = new Color(210, 30, 30);
        Color gloveHL      = new Color(255, 80, 80);

        Stroke thick = new BasicStroke(3.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(thick);

        g2.setColor(outlineColor);
        g2.drawOval(x - 15, y - 97, 30, 30);
        g2.setColor(bodyColor);
        g2.fillOval(x - 14, y - 96, 28, 28);

        int eyeOffX = facing > 0 ? 3 : -3;
        g2.setColor(outlineColor);
        g2.fillOval(x - 5 + eyeOffX, y - 86, 3, 3);
        g2.fillOval(x + 2 + eyeOffX, y - 86, 3, 3);

        g2.setColor(bodyColor);
        g2.drawLine(x, y - 68, x, y - 35);

        long t = System.currentTimeMillis();
        double legSwing = isMoving ? Math.sin(t / 160.0) * 18 : 0;

        if (anim.equals("kick")) {
            int kd = facing;
            g2.drawLine(x, y - 35, x - kd * 12, y - 10);
            g2.drawLine(x - kd * 12, y - 10, x - kd * 14, y);
            int thX = x + kd * 22, thY = y - 52;
            g2.drawLine(x, y - 35, thX, thY);
            int shX = x + kd * 68, shY = y - 58;
            g2.drawLine(thX, thY, shX, shY);
            g2.drawLine(shX, shY, shX + kd * 8, shY + 5);
        } else {
            int l1x = x + (int)(-legSwing * 0.5), l1y = y - 5;
            int l2x = x + (int)(legSwing  * 0.5);
            g2.drawLine(x, y - 35, l1x - 13, l1y);
            g2.drawLine(l1x - 13, l1y, l1x - 15, y);
            g2.drawLine(x, y - 35, l2x + 13, l1y);
            g2.drawLine(l2x + 13, l1y, l2x + 15, y);
        }

        if (anim.equals("punch")) {
            int pd = facing;
            int gAx = x - pd * 18, gAy = y - 62, gFx = x - pd * 20, gFy = y - 50;
            g2.drawLine(x, y - 65, gAx, gAy);
            g2.drawLine(gAx, gAy, gFx, gFy);
            g2.setColor(gloveColor);  g2.fillOval(gFx - 8, gFy - 8, 16, 16);
            g2.setColor(gloveHL);     g2.drawOval(gFx - 8, gFy - 8, 16, 16);
            g2.setColor(bodyColor);   g2.setStroke(thick);
            int ex = x + pd * 22, ey = y - 63, fx = x + pd * 60, fy = y - 63;
            g2.drawLine(x, y - 65, ex, ey);
            g2.drawLine(ex, ey, fx, fy);
            drawGlove(g2, fx, fy, pd, gloveColor, gloveHL);
        } else if (anim.equals("kick")) {
            g2.setColor(bodyColor);
            int bal = 28;
            g2.drawLine(x, y - 65, x - facing * 5 - bal, y - 60);
            g2.drawLine(x - facing * 5 - bal, y - 60, x - facing * 5 - bal - 10, y - 50);
            g2.drawLine(x, y - 65, x + facing * 5 + bal, y - 60);
            g2.drawLine(x + facing * 5 + bal, y - 60, x + facing * 5 + bal + 10, y - 50);
        } else {
            g2.setColor(bodyColor); g2.setStroke(thick);
            int gx = x + facing * 22, gy = y - 58;
            g2.drawLine(x, y - 65, gx, gy);
            g2.drawLine(gx, gy, gx + facing * 5, gy - 8);
            int bx2 = x - facing * 18, by2 = y - 52;
            g2.drawLine(x, y - 65, bx2, by2);
            g2.drawLine(bx2, by2, bx2 - facing * 5, by2 - 5);
            g2.setStroke(new BasicStroke(2.5f));
            g2.setColor(gloveColor);
            g2.fillOval(gx - 7 + facing * 5,  gy  - 15, 14, 14);
            g2.fillOval(bx2 - 7 - facing * 5, by2 - 12, 14, 14);
            g2.setColor(gloveHL);
            g2.drawOval(gx - 7 + facing * 5,  gy  - 15, 14, 14);
            g2.drawOval(bx2 - 7 - facing * 5, by2 - 12, 14, 14);
        }

        g2.setStroke(new BasicStroke(1));
    }

    private void drawGlove(Graphics2D g2, int cx, int cy, int dir, Color main, Color hl) {
        int gw = 22, gh = 18;
        g2.setColor(main); g2.fillOval(cx - gw/2, cy - gh/2, gw, gh);
        g2.setColor(hl);   g2.fillOval(cx - gw/2 + 3, cy - gh/2 + 3, gw/2, gh/2);
        int bx = cx - dir * (gw/2);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        g2.drawLine(bx, cy - 6, bx, cy + 6);
        g2.setColor(new Color(120, 0, 0));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(cx - gw/2, cy - gh/2, gw, gh);
    }

    private void drawHealthBar(Graphics2D g2, int x, int y, int hp, int maxHp, Color bar, String label) {
        int bw = 234, bh = 24;
        g2.setColor(new Color(15, 15, 25));
        g2.fillRoundRect(x, y, bw, bh, 10, 10);
        g2.setColor(new Color(70, 90, 110));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(x, y, bw, bh, 10, 10);
        int fw = (int)((hp / (float)maxHp) * (bw - 4));
        Color low = hp < 40 ? new Color(255, 40, 40) : bar;
        g2.setPaint(new GradientPaint(x+2, y+2, low.brighter(), x+2, y+bh-2, low.darker()));
        g2.fillRoundRect(x+2, y+2, Math.max(0, fw), bh-4, 7, 7);
        g2.setFont(new Font("Courier New", Font.BOLD, 11));
        g2.setColor(Color.WHITE);
        drawCenteredText(g2, label + "  " + hp + "/150", x + bw/2, y + bh - 5);
    }

    private void drawTimer(Graphics2D g2, int timerFrames, boolean infinite, int fps) {
        String ts;
        if (infinite) {
            ts = "\u221E";
        } else {
            int secs = timerFrames / fps, mins = secs / 60;
            secs %= 60;
            ts = String.format("%d:%02d", mins, secs);
        }
        g2.setFont(new Font("Courier New", Font.BOLD, 28));
        FontMetrics fm = g2.getFontMetrics();
        int tx = (screenWidth - fm.stringWidth(ts)) / 2;
        int bw = fm.stringWidth(ts) + 32, bh = 38;
        int bx = (screenWidth - bw) / 2, by = 10;
        g2.setColor(new Color(15, 15, 25));
        g2.fillRoundRect(bx, by, bw, bh, 10, 10);
        boolean urgent = !infinite && timerFrames < 10 * fps;
        g2.setColor(urgent ? new Color(220, 50, 50) : new Color(80, 100, 130));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(bx, by, bw, bh, 10, 10);
        g2.setColor(urgent ? new Color(255, 80, 80) : new Color(220, 230, 255));
        g2.drawString(ts, tx, by + 30);
    }

    private void drawButton(Graphics2D g2, Rectangle btn, String text, boolean hover, Color c1, Color c2) {
        if (hover) {
            g2.setColor(new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 35));
            g2.fillRoundRect(btn.x-6, btn.y-6, btn.width+12, btn.height+12, 22, 22);
        }
        g2.setPaint(new GradientPaint(btn.x, btn.y, hover ? c1.brighter() : c1, btn.x, btn.y+btn.height, c2));
        g2.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 14, 14);
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(hover ? c1.brighter() : new Color(255, 120, 120));
        g2.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 14, 14);
        g2.setFont(new Font("Courier New", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text,
            btn.x + (btn.width  - fm.stringWidth(text)) / 2,
            btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2);
    }

    private void drawTimerBox(Graphics2D g2, Rectangle box, String big, String label,
                               boolean hover, boolean selected) {
        Color bg     = selected ? new Color(160,30,30) : (hover ? new Color(60,20,20) : new Color(25,10,10));
        Color border = selected ? new Color(255,80,80) : (hover ? new Color(200,60,60) : new Color(100,40,40));
        g2.setColor(bg);
        g2.fillRoundRect(box.x, box.y, box.width, box.height, 12, 12);
        g2.setStroke(new BasicStroke(selected ? 2.5f : 1.5f));
        g2.setColor(border);
        g2.drawRoundRect(box.x, box.y, box.width, box.height, 12, 12);
        g2.setFont(new Font("Courier New", Font.BOLD, 46));
        g2.setColor(selected ? Color.WHITE : new Color(220,160,160));
        FontMetrics fmb = g2.getFontMetrics();
        g2.drawString(big, box.x + (box.width - fmb.stringWidth(big)) / 2, box.y + box.height/2 + 4);
        g2.setFont(new Font("Courier New", Font.PLAIN, 11));
        g2.setColor(selected ? new Color(255,180,180) : new Color(140,80,80));
        FontMetrics fms = g2.getFontMetrics();
        g2.drawString(label, box.x + (box.width - fms.stringWidth(label)) / 2, box.y + box.height/2 + 26);
    }

    private void drawCenteredText(Graphics2D g2, String text, int cx, int y) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, cx - fm.stringWidth(text) / 2, y);
    }
}