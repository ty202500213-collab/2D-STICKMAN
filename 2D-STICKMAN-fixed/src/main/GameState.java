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
            particles.add(new int[]{x, y, 20 + (int)(Math.random() * 12), dx, dy});
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