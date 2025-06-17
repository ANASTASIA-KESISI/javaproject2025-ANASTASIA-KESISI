import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.util.Random;

public class HealAnimation extends Animation {

        public HealAnimation(Entity healer, Entity target) {
            super(target, 800); // 800ms animation
        }
        
        @Override
        public void render(Graphics2D g2d) {
            float progress = getProgress();
            
            // Green healing glow
            AlphaComposite originalComposite = (AlphaComposite) g2d.getComposite();
            float alpha = (float)(0.4 * Math.sin(progress * Math.PI));
            
            if (alpha > 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                
                // Create radial gradient for healing effect
                int centerX = entity.getCol() * GamePanel.TILE_SIZE + GamePanel.TILE_SIZE / 2;
                int centerY = entity.getRow() * GamePanel.TILE_SIZE + GamePanel.TILE_SIZE / 2;
                int radius = (int)(GamePanel.TILE_SIZE * (0.5 + progress * 0.5));
                
                RadialGradientPaint gradient = new RadialGradientPaint(
                    centerX, centerY, radius,
                    new float[]{0.0f, 1.0f},
                    new Color[]{new Color(0, 255, 0, 100), new Color(0, 255, 0, 0)}
                );
                
                g2d.setPaint(gradient);
                g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
            }
            
            g2d.setComposite(originalComposite);
            
            // Healing particles floating upward
            drawHealingParticles(g2d, progress);
        }
        
        private void drawHealingParticles(Graphics2D g2d, float progress) {
            Random rand = new Random(42); // Fixed seed for consistent particles
            g2d.setColor(Color.GREEN);
            
            int centerX = entity.getCol() * GamePanel.TILE_SIZE + GamePanel.TILE_SIZE / 2;
            int baseY = entity.getRow() * GamePanel.TILE_SIZE + GamePanel.TILE_SIZE;
            
            for (int i = 0; i < 6; i++) {
                int x = centerX + (int)(rand.nextGaussian() * 10);
                int y = baseY - (int)(progress * GamePanel.TILE_SIZE * 1.5) + (int)(rand.nextGaussian() * 5);
                
                float particleAlpha = 1.0f - progress;
                if (particleAlpha > 0) {
                    AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, particleAlpha);
                    g2d.setComposite(ac);
                    g2d.fillOval(x - 2, y - 2, 4, 4);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
            }
        }
    }
