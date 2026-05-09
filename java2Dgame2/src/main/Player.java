package main;

import java.awt.*;

public class Player {
    float x, y;
    float vx = 0, vy = 0;
    boolean onGround = false;
    int hp = 150;
    int facing = 1;
    int punchTimer = 0, kickTimer = 0, uppercutTimer = 0;
    int hitCooldown = 0;
    String anim = "idle";
    int flashTimer = 0;
    boolean isMoving = false;
    boolean uppercutHitThisFrame = false;     // used by GamePanel to spawn effects
    boolean tornadoKickHitThisFrame = false;  // used by GamePanel to spawn effects
    int tornadoKickTimer = 0;
    int staggerTimer = 0;  // when >0, player staggers (hit animation, slowed)
    boolean isBlocking = false;  // true when block key held and on ground
    int blockFlashTimer = 0;     // visual feedback when block absorbs a hit
    
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
        uppercutTimer = 0;
        tornadoKickTimer = 0;
        hitCooldown = 0;
        staggerTimer = 0;
        isBlocking = false;
        blockFlashTimer = 0;
        anim = "idle";
        flashTimer = 0;
        isMoving = false;
    }
    
    public void update(boolean left, boolean right, boolean down, boolean punch, boolean kick, boolean block, Player opponent) {
        uppercutHitThisFrame = false;
        tornadoKickHitThisFrame = false;
        boolean noAttackNow = uppercutTimer == 0 && punchTimer == 0 && kickTimer == 0 && tornadoKickTimer == 0;
        isBlocking = block && onGround && noAttackNow && staggerTimer == 0;
        if (blockFlashTimer > 0) blockFlashTimer--;
        // Determine if "forward" is pressed (moving toward opponent)
        boolean forwardPressed = (x < opponent.x && right) || (x > opponent.x && left);
        
        // Uppercut: forward + down + punch, must be on ground, no other attack active
        boolean uppercutInput = forwardPressed && down && punch;
        // Tornado kick: in the air + forward + kick
        boolean tornadoKickInput = !onGround && forwardPressed && kick;
        
        // Movement with wall collisions
        isMoving = false;
        if (isBlocking) {
            vx = 0;  // locked in place while blocking
        } else if (staggerTimer > 0) {
            // stagger: minimal control
        } else {
            if (left)       { vx = -moveSpeed; facing = -1; isMoving = true; }
            else if (right) { vx =  moveSpeed; facing =  1; isMoving = true; }
            else            { vx = 0; }
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
        
        // Attacks — special moves take priority (can't attack while blocking)
        boolean noAttackActive = uppercutTimer == 0 && punchTimer == 0 && kickTimer == 0 && tornadoKickTimer == 0;
        if (isBlocking) {
            anim = "block";
        } else if (tornadoKickInput && tornadoKickTimer == 0 && kickTimer == 0 && punchTimer == 0 && uppercutTimer == 0) {
            tornadoKickTimer = 40;
            anim = "tornadoKick";
        } else if (uppercutInput && noAttackActive && onGround) {
            uppercutTimer = 36;
            anim = "uppercut";
        } else if (punch && noAttackActive && !uppercutInput) {
            punchTimer = 28;
            anim = "punch";
        } else if (kick && noAttackActive && !tornadoKickInput) {
            kickTimer = 35;
            anim = "kick";
        }
        
        if (tornadoKickTimer > 0) {
            tornadoKickTimer--;
            if (tornadoKickTimer == 0 && anim.equals("tornadoKick")) anim = "idle";
        }
        if (uppercutTimer > 0) {
            uppercutTimer--;
            if (uppercutTimer == 0 && anim.equals("uppercut")) anim = "idle";
        }
        if (punchTimer > 0) {
            punchTimer--;
            if (punchTimer == 0 && anim.equals("punch")) anim = "idle";
        }
        if (kickTimer > 0) {
            kickTimer--;
            if (kickTimer == 0 && anim.equals("kick")) anim = "idle";
        }
        if (staggerTimer > 0) {
            staggerTimer--;
            if (staggerTimer == 0 && anim.equals("stagger")) anim = "idle";
        }
        
        // Hit detection
        if (opponent.hitCooldown == 0) {
            float dist = Math.abs(x - opponent.x);
            float punchRange = 80, kickRange = 95, uppercutRange = 70, tornadoRange = 105;
            
            if (anim.equals("uppercut") && uppercutTimer == 22 && dist < uppercutRange) {
                opponent.takeDamageUppercut(22, 18, x, y);
            }
            if (anim.equals("tornadoKick") && tornadoKickTimer == 24 && dist < tornadoRange) {
                opponent.takeDamageTornadoKick(22, 18, x, y);
            }
            if (anim.equals("punch") && punchTimer == 18 && dist < punchRange) {
                opponent.takeDamage(5, 10, x, y);
            }
            if (anim.equals("kick") && kickTimer == 22 && dist < kickRange) {
                opponent.takeDamage(12, 14, x, y);
            }
        }
        
        if (hitCooldown > 0) hitCooldown--;
        if (flashTimer > 0) flashTimer--;
    }
    
    public void takeDamage(int damage, int flashTime, float attackerX, float attackerY) {
        if (isBlocking) {
            // Block: chip damage only (20%), no stagger, small pushback
            hp -= Math.max(1, damage / 5);
            blockFlashTimer = 12;
            hitCooldown = 15;
            float knockbackDir = (x < attackerX) ? -1 : 1;
            vx = knockbackDir * 2;
            return;
        }
        hp -= damage;
        flashTimer = flashTime;
        hitCooldown = 28;
        staggerTimer = 20;
        anim = "stagger";
        float knockbackDir = (x < attackerX) ? -1 : 1;
        vx = knockbackDir * 5;
        vy = -3;
    }
    
    public void takeDamageUppercut(int damage, int flashTime, float attackerX, float attackerY) {
        if (isBlocking) {
            // Uppercut chip damage (25%) — guard is partially broken upward
            hp -= Math.max(2, damage / 4);
            blockFlashTimer = 16;
            hitCooldown = 20;
            float knockbackDir = (x < attackerX) ? -1 : 1;
            vx = knockbackDir * 3;
            vy = -5;  // slight lift even when blocked
            return;
        }
        hp -= damage;
        flashTimer = flashTime;
        hitCooldown = 45;
        staggerTimer = 40;
        anim = "stagger";
        uppercutHitThisFrame = true;
        float knockbackDir = (x < attackerX) ? -1 : 1;
        vx = knockbackDir * 9;
        vy = -18;
    }
    
    public void takeDamageTornadoKick(int damage, int flashTime, float attackerX, float attackerY) {
        if (isBlocking) {
            // Tornado kick chip damage (20%) — pushes back even through block
            hp -= Math.max(2, damage / 5);
            blockFlashTimer = 16;
            hitCooldown = 20;
            float knockbackDir = (x < attackerX) ? -1 : 1;
            vx = knockbackDir * 4;
            return;
        }
        hp -= damage;
        flashTimer = flashTime;
        hitCooldown = 45;
        staggerTimer = 40;
        anim = "stagger";
        tornadoKickHitThisFrame = true;
        float knockbackDir = (x < attackerX) ? -1 : 1;
        vx = knockbackDir * 11;  // wide sideways launch
        vy = -10;
    }
    
    public Rectangle getHitbox() {
        return new Rectangle((int)x - 20, (int)y - 100, 40, 100);
    }
}
