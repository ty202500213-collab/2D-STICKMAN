package main;

import java.awt.*;

public class Player {
    float x, y;
    float vx = 0, vy = 0;
    boolean onGround = false;
    int hp = 150;
    int facing = 1;
    int punchTimer = 0, kickTimer = 0;
    int hitCooldown = 0;
    String anim = "idle";
    int flashTimer = 0;
    boolean isMoving = false;
    
    final float gravity = 0.6f;
    final float moveSpeed = 3.5f;
    final float groundY = 430;
    final float minX = 30;
    final float maxX = 738;
    final float wallBounce = 0.5f;
    
    public Player(float x, float y, int facing) {
        this.x = x;
        this.y = y;
        this.facing = facing;
    }
    
    public void reset(float x, float y, int facing) {
        this.x = x;
        this.y = y;
        this.facing = facing;
        vx = 0;
        vy = 0;
        onGround = true;
        hp = 150;
        punchTimer = 0;
        kickTimer = 0;
        hitCooldown = 0;
        anim = "idle";
        flashTimer = 0;
        isMoving = false;
    }
    
    public void update(boolean left, boolean right, boolean punch, boolean kick, Player opponent) {
        // Movement with wall collisions
        isMoving = false;
        if (left) {
            vx = -moveSpeed;
            facing = -1;
            isMoving = true;
        } else if (right) {
            vx = moveSpeed;
            facing = 1;
            isMoving = true;
        } else {
            vx = 0;
        }
        
        // Gravity
        vy += gravity;
        y += vy;
        
        // Ground collision
        if (y >= groundY) {
            y = groundY;
            vy = 0;
            onGround = true;
        } else {
            onGround = false;
        }
        
        // Apply horizontal movement with wall collisions
        x += vx;
        if (x < minX) {
            x = minX;
            vx = Math.abs(vx) * wallBounce;
        } else if (x > maxX) {
            x = maxX;
            vx = -Math.abs(vx) * wallBounce;
        }
        
        // Auto-face opponent
        if (x < opponent.x) {
            facing = 1;
        } else {
            facing = -1;
        }
        
        // Attacks
        if (punch && punchTimer == 0 && kickTimer == 0) {
            punchTimer = 28;
            anim = "punch";
        }
        if (kick && kickTimer == 0 && punchTimer == 0) {
            kickTimer = 35;
            anim = "kick";
        }
        
        if (punchTimer > 0) {
            punchTimer--;
            if (punchTimer == 0 && anim.equals("punch")) anim = "idle";
        }
        if (kickTimer > 0) {
            kickTimer--;
            if (kickTimer == 0 && anim.equals("kick")) anim = "idle";
        }
        
        // Hit detection
        if (opponent.hitCooldown == 0) {
            float dist = Math.abs(x - opponent.x);
            float punchRange = 80, kickRange = 95;
            
            if (anim.equals("punch") && punchTimer == 18 && dist < punchRange) {
                opponent.takeDamage(5, 10, x, y);
            }
            if (anim.equals("kick") && kickTimer == 22 && dist < kickRange) {
                opponent.takeDamage(12, 14, x, y);
            }
        }
        
        if (hitCooldown > 0) hitCooldown--;
        if (flashTimer > 0) flashTimer--;
        
        // Set idle animation if not doing anything
        if (anim.equals("idle") && isMoving && onGround) {
            // Could add walking animation here
        }
    }
    
    public void takeDamage(int damage, int flashTime, float attackerX, float attackerY) {
        hp -= damage;
        flashTimer = flashTime;
        hitCooldown = 28;
        // Knockback effect
        float knockbackDir = (x < attackerX) ? -1 : 1;
        vx = knockbackDir * 5;
        vy = -3;
    }
    
    public Rectangle getHitbox() {
        return new Rectangle((int)x - 20, (int)y - 100, 40, 100);
    }
}