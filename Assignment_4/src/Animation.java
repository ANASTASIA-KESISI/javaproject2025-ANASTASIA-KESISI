import java.awt.Graphics2D;

public abstract class Animation {
        protected int duration;
        protected int elapsed;
        protected Entity entity;
        
        public Animation(Entity entity, int duration) {
            this.entity = entity;
            this.duration = duration;
            this.elapsed = 0;
        }
        
        public boolean update() {
            elapsed += 16; // Timer interval
            return elapsed >= duration;
        }
        
        public abstract void render(Graphics2D g2d);
        public float getProgress() { return Math.min(1.0f, (float)elapsed / duration); }
    }
