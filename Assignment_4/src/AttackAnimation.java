import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class AttackAnimation extends Animation {
        private Entity target;
        //private float shakeIntensity = 8.0f;
        
        public AttackAnimation(Entity attacker, Entity target, int TILE_SIZE) {
            super(attacker, 600); // 600ms animation
            this.target = target;
        }
        
        @Override
        public void render(Graphics2D g2d) {
            float progress = getProgress();
            
            // Red flash effect on both entities
            AlphaComposite originalComposite = (AlphaComposite) g2d.getComposite();
            float alpha = (float)(0.6 * Math.sin(progress * Math.PI * 6)); // Flashing effect
            if (alpha > 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.setColor(Color.RED);
                
                // Flash on attacker
                g2d.fillRect(entity.getCol() * GamePanel.TILE_SIZE, entity.getRow() * GamePanel.TILE_SIZE, 
                           GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
                
                // Flash on target
                g2d.fillRect(target.getCol() * GamePanel.TILE_SIZE, target.getRow() * GamePanel.TILE_SIZE, 
                           GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
            }
            g2d.setComposite(originalComposite);
            
            // Screen shake effect for target
            if (progress < 0.5f) {
                //Random rand = new Random((long)(progress * 1000));
                //float shake = shakeIntensity * (1.0f - progress * 2);
                //int shakeX = (int)(rand.nextGaussian() * shake);
                //int shakeY = (int)(rand.nextGaussian() * shake);
                
                // Apply shake to target rendering (this would need to be handled in main render)
                // For now, we'll add impact particles
                drawImpactParticles(g2d, target, progress);
            }
        }
        
        private void drawImpactParticles(Graphics2D g2d, Entity target, float progress) {
            if (progress < 0.3f) {
                //Random rand = new Random((long)(progress * 1000));
                g2d.setColor(Color.ORANGE);
                
                int centerX = target.getCol() * GamePanel.TILE_SIZE + GamePanel.TILE_SIZE / 2;
                int centerY = target.getRow() * GamePanel.TILE_SIZE + GamePanel.TILE_SIZE / 2;
                
                for (int i = 0; i < 8; i++) {
                    double angle = (Math.PI * 2 * i) / 8.0;
                    int distance = (int)(15 * progress / 0.3f);
                    int x = centerX + (int)(Math.cos(angle) * distance);
                    int y = centerY + (int)(Math.sin(angle) * distance);
                    g2d.fillOval(x - 2, y - 2, 4, 4);
                }
            }
        }
    }
