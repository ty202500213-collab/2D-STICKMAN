package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Renderer {
    private int screenWidth, screenHeight;
    private BufferedImage menuBg = null;
    
    public Renderer(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        
        try {
            menuBg = ImageIO.read(new File("resources/boxing_bg.jpg"));
        } catch (Exception e) {
            menuBg = null;
        }
    }
    
    public void drawMenu(Graphics2D g2, Rectangle playButton, boolean hoveringPlay) {
        int w = screenWidth, h = screenHeight;
        
        if (menuBg != null) {
            g2.drawImage(menuBg, 0, 0, w, h, null);
            g2.setColor(new Color(0, 0, 0, 130));
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
        
        // Play button
        drawButton(g2, playButton, "\u25B6  PLAY", hoveringPlay, new Color(220, 40, 40), new Color(160, 20, 20));
        
        // Controls
        g2.setFont(new Font("Courier New", Font.PLAIN, 11));
        g2.setColor(new Color(200, 180, 180, 200));
        String[] hints = {
            "P1: A / D  Move    W  Jump    H  Punch    J  Kick",
            "P2: \u2190 / \u2192  Move   \u2191  Jump   NUM4 Punch   NUM5 Kick"
        };
        FontMetrics fmh = g2.getFontMetrics();
        g2.drawString(hints[0], (w - fmh.stringWidth(hints[0])) / 2, h - 36);
        g2.drawString(hints[1], (w - fmh.stringWidth(hints[1])) / 2, h - 18);
    }
    
    public void drawTimerSelect(Graphics2D g2, Rectangle btn60, Rectangle btn99, Rectangle btnInf,
                                Rectangle startFightBtn, Rectangle backBtn, boolean hovering60, boolean hovering99,
                                boolean hoveringInf, boolean hoveringStart, boolean hoveringBack,
                                int selectedTimer, boolean infinite) {
        int w = screenWidth, h = screenHeight;
        
        g2.setPaint(new GradientPaint(0, 0, new Color(15, 10, 10), 0, h, new Color(30, 10, 10)));
        g2.fillRect(0, 0, w, h);
        
        // Grid
        g2.setColor(new Color(255, 60, 60, 12));
        for (int x = 0; x < w; x += 48) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 48) g2.drawLine(0, y, w, y);
        
        // Heading
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
        
        // Timer buttons
        drawTimerBox(g2, btn60, "60", "SECONDS", hovering60, !infinite && selectedTimer == 60);
        drawTimerBox(g2, btn99, "99", "SECONDS", hovering99, !infinite && selectedTimer == 99);
        drawTimerBox(g2, btnInf, "\u221E", "INFINITE", hoveringInf, infinite);
        
        // Selection
        g2.setFont(new Font("Courier New", Font.BOLD, 13));
        g2.setColor(new Color(200, 200, 200));
        String sel = infinite ? "Selected: Infinite" : "Selected: " + selectedTimer + " seconds";
        FontMetrics fmsel = g2.getFontMetrics();
        g2.drawString(sel, (w - fmsel.stringWidth(sel)) / 2, h / 2 + 100);
        
        // Start button
        drawButton(g2, startFightBtn, "\u2694  START FIGHT", hoveringStart, new Color(220, 40, 40), new Color(160, 20, 20));
        
        // Back button
        g2.setColor(hoveringBack ? new Color(255, 100, 100) : new Color(150, 80, 80));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 8, 8);
        g2.setFont(new Font("Courier New", Font.BOLD, 13));
        g2.setColor(hoveringBack ? new Color(255, 200, 200) : new Color(180, 130, 130));
        FontMetrics fmb = g2.getFontMetrics();
        String back = "\u25C0 BACK";
        g2.drawString(back, backBtn.x + (backBtn.width - fmb.stringWidth(back)) / 2,
                     backBtn.y + (backBtn.height + fmb.getAscent() - fmb.getDescent()) / 2);
    }
    
    public void drawGame(Graphics2D g2, Player p1, Player p2, GameState state, int timer, boolean infinite, int fps) {
        int w = screenWidth, h = screenHeight;
        
        // Background
        g2.setPaint(new GradientPaint(0, 0, new Color(15, 15, 30), 0, h, new Color(25, 35, 55)));
        g2.fillRect(0, 0, w, h);
        
        // Ring ropes
        int ropeY1 = 380, ropeY2 = 340;
        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(180, 30, 30));
        g2.drawLine(0, ropeY1, w, ropeY1);
        g2.drawLine(0, ropeY2, w, ropeY2);
        
        // Corner posts
        int[] postX = {30, w - 30};
        for (int px : postX) {
            g2.setColor(new Color(60, 60, 200));
            g2.fillRect(px - 4, 290, 8, 170);
        }
        
        // Ground
        g2.setPaint(new GradientPaint(0, 430, new Color(80, 50, 30), 0, h, new Color(40, 25, 10)));
        g2.fillRect(0, 430, w, h - 430);
        g2.setColor(new Color(120, 80, 50));
        g2.fillRect(0, 430, w, 6);
        
        // Particles
        for (int[] p : state.particles) {
            float alpha = Math.max(0, p[2] / 30f);
            g2.setColor(new Color(1f, 0.6f, 0.1f, alpha));
            g2.fillOval(p[0] - 4, p[1] - 4, 8, 8);
        }
        
        // Draw stickmen
        drawStickman(g2, (int)p1.x, (int)p1.y, p1.facing, p1.anim, p1.flashTimer > 0, 1, p1.isMoving);
        drawStickman(g2, (int)p2.x, (int)p2.y, p2.facing, p2.anim, p2.flashTimer > 0, 2, p2.isMoving);
        
        // Hit labels
        g2.setFont(new Font("Courier New", Font.BOLD, 16));
        for (String[] l : state.hitLabels) {
            int lx = Integer.parseInt(l[0]), ly = Integer.parseInt(l[1]), life = Integer.parseInt(l[2]);
            float a = Math.min(1f, life / 14f);
            g2.setColor(new Color(1f, 0.9f, 0.1f, a));
            FontMetrics flm = g2.getFontMetrics();
            g2.drawString(l[3], lx - flm.stringWidth(l[3]) / 2, ly);
        }
        
        // Player labels
        g2.setFont(new Font("Courier New", Font.BOLD, 12));
        g2.setColor(new Color(180, 220, 255));
        drawCenteredText(g2, "P1", (int)p1.x, (int)p1.y - 110);
        g2.setColor(new Color(255, 180, 160));
        drawCenteredText(g2, "P2", (int)p2.x, (int)p2.y - 110);
        
        // HUD
        drawHealthBar(g2, 24, 18, p1.hp, 150, new Color(0, 210, 80), "PLAYER 1");
        drawHealthBar(g2, w - 258, 18, p2.hp, 150, new Color(255, 80, 80), "PLAYER 2");
        
        // Timer
        drawTimer(g2, timer, infinite, fps);
    }
    
    public void drawGameOver(Graphics2D g2, Player p1, Player p2, GameState state, Rectangle playButton,
                            boolean hovering, int timer, boolean infinite, int fps) {
        drawGame(g2, p1, p2, state, timer, infinite, fps);
        
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
        
        drawButton(g2, playButton, "\u21BA  PLAY AGAIN", hovering, new Color(220, 40, 40), new Color(160, 20, 20));
    }
    
    // Stickman drawing with updated animations
    private void drawStickman(Graphics2D g2, int x, int y, int facing, String anim, boolean flash, int playerNum, boolean isMoving) {
        Color bodyColor = flash ? new Color(255, 70, 70) : (playerNum == 1 ? new Color(100, 180, 255) : new Color(255, 130, 100));
        Color outlineColor = flash ? new Color(255, 30, 30) : (playerNum == 1 ? new Color(40, 110, 210) : new Color(200, 70, 50));
        Color gloveColor = new Color(210, 30, 30);
        Color gloveHighlight = new Color(255, 80, 80);
        
        Stroke thick = new BasicStroke(3.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(thick);
        
        // Head
        g2.setColor(outlineColor);
        g2.drawOval(x - 15, y - 97, 30, 30);
        g2.setColor(bodyColor);
        g2.fillOval(x - 14, y - 96, 28, 28);
        
        int eyeOffX = facing > 0 ? 3 : -3;
        g2.setColor(outlineColor);
        g2.fillOval(x - 5 + eyeOffX, y - 86, 3, 3);
        g2.fillOval(x + 2 + eyeOffX, y - 86, 3, 3);
        
        // Body
        g2.setColor(bodyColor);
        g2.drawLine(x, y - 68, x, y - 35);
        
        // Legs - only animate when moving
        long t = System.currentTimeMillis();
        double legSwing = isMoving ? Math.sin(t / 160.0) * 18 : 0;
        
        if (anim.equals("kick")) {
            int kickDir = facing;
            g2.drawLine(x, y - 35, x - kickDir * 12, y - 10);
            g2.drawLine(x - kickDir * 12, y - 10, x - kickDir * 14, y);
            
            int thighEndX = x + kickDir * 22;
            int thighEndY = y - 52;
            g2.drawLine(x, y - 35, thighEndX, thighEndY);
            
            int shinEndX = x + kickDir * 68;
            int shinEndY = y - 58;
            g2.drawLine(thighEndX, thighEndY, shinEndX, shinEndY);
            g2.drawLine(shinEndX, shinEndY, shinEndX + kickDir * 8, shinEndY + 5);
        } else {
            // Standing or walking legs
            int l1x = x + (int)(-legSwing * 0.5), l1y = y - 5;
            int l2x = x + (int)(legSwing * 0.5);
            g2.drawLine(x, y - 35, l1x - 13, l1y);
            g2.drawLine(l1x - 13, l1y, l1x - 15, y);
            g2.drawLine(x, y - 35, l2x + 13, l1y);
            g2.drawLine(l2x + 13, l1y, l2x + 15, y);
        }
        
        // Arms - locked in fighting stance unless attacking
        if (anim.equals("punch")) {
            int punchDir = facing;
            // Guard arm
            int gArmX = x - punchDir * 18, gArmY = y - 62;
            int gForeX = x - punchDir * 20, gForeY = y - 50;
            g2.drawLine(x, y - 65, gArmX, gArmY);
            g2.drawLine(gArmX, gArmY, gForeX, gForeY);
            
            g2.setColor(gloveColor);
            g2.fillOval(gForeX - 8, gForeY - 8, 16, 16);
            g2.setColor(gloveHighlight);
            g2.drawOval(gForeX - 8, gForeY - 8, 16, 16);
            
            // Punching arm
            g2.setColor(bodyColor);
            g2.setStroke(thick);
            int elbowX = x + punchDir * 22, elbowY = y - 63;
            int fistX = x + punchDir * 60, fistY = y - 63;
            g2.drawLine(x, y - 65, elbowX, elbowY);
            g2.drawLine(elbowX, elbowY, fistX, fistY);
            
            drawGlove(g2, fistX, fistY, punchDir, gloveColor, gloveHighlight);
        } else if (anim.equals("kick")) {
            // Balance arms
            g2.setColor(bodyColor);
            int balArmLen = 28;
            g2.drawLine(x, y - 65, x - facing * 5 - balArmLen, y - 60);
            g2.drawLine(x - facing * 5 - balArmLen, y - 60, x - facing * 5 - balArmLen - 10, y - 50);
            g2.drawLine(x, y - 65, x + facing * 5 + balArmLen, y - 60);
            g2.drawLine(x + facing * 5 + balArmLen, y - 60, x + facing * 5 + balArmLen + 10, y - 50);
        } else {
            // Fighting stance - locked position
            g2.setColor(bodyColor);
            g2.setStroke(thick);
            
            // Front hand (guard up)
            int guardX = x + facing * 22;
            int guardY = y - 58;
            g2.drawLine(x, y - 65, guardX, guardY);
            g2.drawLine(guardX, guardY, guardX + facing * 5, guardY - 8);
            
            // Back hand (ready position)
            int backX = x - facing * 18;
            int backY = y - 52;
            g2.drawLine(x, y - 65, backX, backY);
            g2.drawLine(backX, backY, backX - facing * 5, backY - 5);
            
            // Gloves
            g2.setStroke(new BasicStroke(2.5f));
            g2.setColor(gloveColor);
            g2.fillOval(guardX - 7 + facing * 5, guardY - 15, 14, 14);
            g2.fillOval(backX - 7 - facing * 5, backY - 12, 14, 14);
            g2.setColor(gloveHighlight);
            g2.drawOval(guardX - 7 + facing * 5, guardY - 15, 14, 14);
            g2.drawOval(backX - 7 - facing * 5, backY - 12, 14, 14);
        }
        
        g2.setStroke(new BasicStroke(1));
    }
    
    private void drawGlove(Graphics2D g2, int cx, int cy, int dir, Color main, Color highlight) {
        int gw = 22, gh = 18;
        g2.setColor(main);
        g2.fillOval(cx - gw / 2, cy - gh / 2, gw, gh);
        g2.setColor(highlight);
        g2.fillOval(cx - gw / 2 + 3, cy - gh / 2 + 3, gw / 2, gh / 2);
        
        int bandX = cx - dir * (gw / 2);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        g2.drawLine(bandX, cy - 6, bandX, cy + 6);
        
        g2.setColor(new Color(120, 0, 0));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(cx - gw / 2, cy - gh / 2, gw, gh);
    }
    
    private void drawHealthBar(Graphics2D g2, int x, int y, int hp, int maxHp, Color barColor, String label) {
        int bw = 234, bh = 24;
        g2.setColor(new Color(15, 15, 25));
        g2.fillRoundRect(x, y, bw, bh, 10, 10);
        g2.setColor(new Color(70, 90, 110));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(x, y, bw, bh, 10, 10);
        
        int fw = (int)((hp / (float)maxHp) * (bw - 4));
        Color low = hp < 40 ? new Color(255, 40, 40) : barColor;
        g2.setPaint(new GradientPaint(x + 2, y + 2, low.brighter(), x + 2, y + bh - 2, low.darker()));
        g2.fillRoundRect(x + 2, y + 2, Math.max(0, fw), bh - 4, 7, 7);
        
        g2.setFont(new Font("Courier New", Font.BOLD, 11));
        g2.setColor(Color.WHITE);
        drawCenteredText(g2, label + "  " + hp + "/150", x + bw / 2, y + bh - 5);
    }
    
    private void drawTimer(Graphics2D g2, int timerFrames, boolean infinite, int fps) {
        String timeStr;
        if (infinite) {
            timeStr = "\u221E";
        } else {
            int secs = timerFrames / fps;
            int mins = secs / 60;
            secs = secs % 60;
            timeStr = String.format("%d:%02d", mins, secs);
        }
        
        g2.setFont(new Font("Courier New", Font.BOLD, 28));
        FontMetrics fm = g2.getFontMetrics();
        int tx = (screenWidth - fm.stringWidth(timeStr)) / 2;
        
        int bw = fm.stringWidth(timeStr) + 32, bh = 38;
        int bx = (screenWidth - bw) / 2, by = 10;
        g2.setColor(new Color(15, 15, 25));
        g2.fillRoundRect(bx, by, bw, bh, 10, 10);
        
        boolean urgent = !infinite && timerFrames < 10 * fps;
        g2.setColor(urgent ? new Color(220, 50, 50) : new Color(80, 100, 130));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(bx, by, bw, bh, 10, 10);
        
        g2.setColor(urgent ? new Color(255, 80, 80) : new Color(220, 230, 255));
        g2.drawString(timeStr, tx, by + 30);
    }
    
    private void drawButton(Graphics2D g2, Rectangle btn, String text, boolean hover, Color c1, Color c2) {
        if (hover) {
            g2.setColor(new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 35));
            g2.fillRoundRect(btn.x - 6, btn.y - 6, btn.width + 12, btn.height + 12, 22, 22);
        }
        
        g2.setPaint(new GradientPaint(btn.x, btn.y, hover ? c1.brighter() : c1, btn.x, btn.y + btn.height, c2));
        g2.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 14, 14);
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(hover ? c1.brighter() : new Color(255, 120, 120));
        g2.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 14, 14);
        
        g2.setFont(new Font("Courier New", Font.BOLD, 22));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text,
            btn.x + (btn.width - fm.stringWidth(text)) / 2,
            btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2);
    }
    
    private void drawTimerBox(Graphics2D g2, Rectangle box, String big, String label, boolean hover, boolean selected) {
        Color bg = selected ? new Color(160, 30, 30) : (hover ? new Color(60, 20, 20) : new Color(25, 10, 10));
        Color border = selected ? new Color(255, 80, 80) : (hover ? new Color(200, 60, 60) : new Color(100, 40, 40));
        
        g2.setColor(bg);
        g2.fillRoundRect(box.x, box.y, box.width, box.height, 12, 12);
        g2.setStroke(new BasicStroke(selected ? 2.5f : 1.5f));
        g2.setColor(border);
        g2.drawRoundRect(box.x, box.y, box.width, box.height, 12, 12);
        
        g2.setFont(new Font("Courier New", Font.BOLD, 46));
        g2.setColor(selected ? Color.WHITE : new Color(220, 160, 160));
        FontMetrics fmb = g2.getFontMetrics();
        g2.drawString(big, box.x + (box.width - fmb.stringWidth(big)) / 2,
                     box.y + box.height / 2 + 4);
        
        g2.setFont(new Font("Courier New", Font.PLAIN, 11));
        g2.setColor(selected ? new Color(255, 180, 180) : new Color(140, 80, 80));
        FontMetrics fms = g2.getFontMetrics();
        g2.drawString(label, box.x + (box.width - fms.stringWidth(label)) / 2,
                     box.y + box.height / 2 + 26);
    }
    
    private void drawCenteredText(Graphics2D g2, String text, int cx, int y) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, cx - fm.stringWidth(text) / 2, y);
    }
}