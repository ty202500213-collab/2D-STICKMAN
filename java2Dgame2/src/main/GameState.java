package main;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameState {
    public String winner = "";
    public List<int[]> particles = new ArrayList<>();
    public List<String[]> hitLabels = new ArrayList<>();
    
    public void reset() {
        winner = "";
        particles.clear();
        hitLabels.clear();
    }
    
    public void spawnParticles(int x, int y) {
        for (int i = 0; i < 10; i++) {
            int dx = (int)(Math.random() * 10 - 5);
            int dy = (int)(Math.random() * -5 - 1);
            particles.add(new int[]{x, y, 20 + (int)(Math.random() * 12), dx, dy, 0});
        }
    }
    
    // Cyan swirl burst for tornado kick — tag=2
    public void spawnTornadoParticles(int x, int y) {
        for (int i = 0; i < 26; i++) {
            double angle = Math.random() * Math.PI * 2;
            int speed = 4 + (int)(Math.random() * 7);
            int dx = (int)(Math.cos(angle) * speed);
            int dy = (int)(Math.sin(angle) * speed) - 4;
            particles.add(new int[]{x, y, 32 + (int)(Math.random() * 14), dx, dy, 2});
        }
    }

    // Special golden burst for uppercut hits — tag=1 marks uppercut particles
    public void spawnUppercutParticles(int x, int y) {
        for (int i = 0; i < 22; i++) {
            int dx = (int)(Math.random() * 16 - 8);
            int dy = (int)(Math.random() * -14 - 4);  // mostly upward
            particles.add(new int[]{x, y, 30 + (int)(Math.random() * 16), dx, dy, 1});
        }
    }
    
    public void addHitLabel(int x, int y, String text) {
        hitLabels.add(new String[]{String.valueOf(x), String.valueOf(y), "28", text});
    }
    
    public void updateEffects() {
        // Update particles
        particles.removeIf(p -> p[2] <= 0);
        for (int[] p : particles) {
            p[0] += p[3];
            p[1] += p[4];
            p[4]++; // gravity
            p[2]--; // lifetime
        }
        
        // Update hit labels
        hitLabels.removeIf(l -> Integer.parseInt(l[2]) <= 0);
        for (String[] l : hitLabels) {
            l[1] = String.valueOf(Integer.parseInt(l[1]) - 1);
            l[2] = String.valueOf(Integer.parseInt(l[2]) - 1);
        }
    }
}