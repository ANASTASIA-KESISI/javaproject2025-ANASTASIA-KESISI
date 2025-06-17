import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import javax.swing.Timer;

public class GamePanel extends JPanel implements KeyListener {
    public static final int TILE_SIZE = 40;
    private int rows, cols;
    private char[][] map;

    private Player player;
    private List<Entity> entities = new ArrayList<>();

    private Image landImg, treeImg, riverImg, playerImg, knightImg, monsterImg;

    // Animation system
    public Timer animationTimer;
    public List<Animation> activeAnimations = new ArrayList<>();
    public boolean isAnimating = false;

    public GamePanel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        setPreferredSize(new Dimension(cols * TILE_SIZE, rows * TILE_SIZE));
        setFocusable(true);
        addKeyListener(this);
        loadImages();
        generateMap();

        // Initialize animation timer
        animationTimer = new Timer(16, e -> updateAnimations()); // ~60 FPS
    }

    private void loadImages() {
        try {
            landImg = ImageIO.read(new File("resources/land.jpg"));
            treeImg = ImageIO.read(new File("resources/tree.jpg"));
            riverImg = ImageIO.read(new File("resources/river.jpg"));
            playerImg = ImageIO.read(new File("resources/player.png"));
            knightImg = ImageIO.read(new File("resources/knight.png"));
            monsterImg = ImageIO.read(new File("resources/monster.png"));
        } catch (IOException e) {
            System.out.println("Error loading images: " + e.getMessage());
        }
    }

    private void generateMap() {
        map = new char[rows][cols];
        Random rand = new Random();
        double area = rows * cols;
        double density = 1.0 - Math.min(0.6, 500.0 / area);

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                double chance = rand.nextDouble();
                if (chance < density * 0.2) map[r][c] = 'T';
                else if (chance < density * 0.4) map[r][c] = 'R';
                else map[r][c] = '.';
            }

        // Add player
        int pr = rows / 2, pc = cols / 2;
        player = new Player(pr, pc);
        map[pr][pc] = '.';
        entities.clear();
        entities.add(player);

        // Add Knights and Monsters (equal number)
        for (int i = 0; i < (rows * cols)/15; i++) {
        //for (int i = 0; i < 1; i++) {
            entities.add(
                        new Knight(
                                    rand.nextInt(rows),
                                    rand.nextInt(cols),
                                    rand.nextInt(3) + 1,
                                    rand.nextInt(2) + 1,
                                    rand.nextInt(3)
                        )
            );
            entities.add(
                        new Monster(
                                    rand.nextInt(rows),
                                    rand.nextInt(cols),
                                    rand.nextInt(3) + 1,
                                    rand.nextInt(2) + 1,
                                    rand.nextInt(3)
                        )
            );
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw map
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                Image tile;
                switch (map[r][c]) {
                    case 'T': tile = treeImg; break;
                    case 'R': tile = riverImg; break;
                    default: tile = landImg;
                }
                g.drawImage(tile, c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
            }

        // Draw entities
        for (Entity e : entities) {
            Image img = null;
            if (e instanceof Player) img = playerImg;
            else if (e instanceof Knight) img = knightImg;
            else if (e instanceof Monster) img = monsterImg;

            g.drawImage(img, e.getCol() * TILE_SIZE, e.getRow() * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
            
            // Draw health
            g.setColor(Color.WHITE);
            g.fillRect(e.getCol() * TILE_SIZE, e.getRow() * TILE_SIZE, 15, 6);
            g.setColor(Color.RED);
            g.fillRect(e.getCol() * TILE_SIZE, e.getRow() * TILE_SIZE, ((Fighter.class.isAssignableFrom(e.getClass())) ? ((Fighter) e).getHealth() * 5 : 0), 6);
        }

        // Draw animations on top
        for (Animation animation : activeAnimations) {
            animation.render(g2d);
        }
    }

    private void updateAnimations() {
        activeAnimations.removeIf(Animation::update);
        
        if (activeAnimations.isEmpty() && animationTimer.isRunning()) {
            animationTimer.stop();
            isAnimating = false;
        }
        
        repaint();
    }

    private void addAnimation(Animation animation) {
        activeAnimations.add(animation);
        if (!animationTimer.isRunning()) {
            isAnimating = true;
            animationTimer.start();
        }
    }

    private void processTurn() {
        System.out.println("*******START OF ROUND********");
        // NPCs move
        for (Entity e : entities)
            if (e instanceof Fighter && !(e instanceof Player))
                ((Fighter) e).takeTurn(map, player, entities);
        
        repaint();

        // Combat / Heal
        /*for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                Entity a = entities.get(i), b = entities.get(j);
                if (a.isAdjacent(b) && a instanceof Fighter fa && b instanceof Fighter fb) {
                    if (a.isAlly(b)) {
                        fa.heal(fb);
                        fb.heal(fa);
                    } else {
                        System.out.println(fa + " attacks " + fb + " (health after: " + fb.getHealth() + ")");
                        System.out.println("fa after health: " + fa.getHealth());
                        System.out.println("fb after health: " + fb.getHealth());
                        System.out.println("attack: " + fa.getAttack());
                        System.out.println("defese: " + fa.getDefense());
                        System.out.println("attack: " + fb.getAttack());
                        System.out.println("defese: " + fb.getDefense());
                        fa.attack(fb);
                        fb.attack(fa);
                        System.out.println("fa after health: " + fa.getHealth());
                        System.out.println("fb after health: " + fb.getHealth());
                    }
                }
            }
        }*/

        for (Entity a : entities) {
            for (Entity b : entities) {
                //Entity a = entities.get(i), b = entities.get(j);
                if(a == b) continue;

                if (a.isAdjacent(b) && a instanceof Fighter fa && b instanceof Fighter fb) {
                    if (a.isAlly(b)) {
                        System.out.println(fa + " heals " + fb );
                        System.out.println(fa.getHealing() );
                        System.out.println("before:" + fb.getHealth() );
                        // Add healing animation
                        addAnimation(new HealAnimation(fa, fb));
                        fa.heal(fb);
                        //fb.heal(fa);
                        System.out.println("after: " + fb.getHealth() );
                    } else {
                        System.out.println(fa + " attacks " + fb );
                        System.out.println("fa after health: " + fa.getHealth());
                        System.out.println("fb after health: " + fb.getHealth());
                        System.out.println("attack: " + fa.getAttack());
                        System.out.println("defese: " + fa.getDefense());
                        System.out.println("attack: " + fb.getAttack());
                        System.out.println("defese: " + fb.getDefense());

                        // Add attack animation
                        addAnimation(new AttackAnimation(fa, fb, TILE_SIZE));
                        fa.attack(fb);
                        //fb.attack(fa);
                        System.out.println("fa after health: " + fa.getHealth());
                        System.out.println("fb after health: " + fb.getHealth());
                    }
                }
            }
        }

        // Remove dead
        entities.removeIf(e -> e instanceof Fighter && ((Fighter) e).getHealth() <= 0);

        // Check for game over
        boolean monstersExist = entities.stream().anyMatch(e -> e instanceof Monster);
        boolean knightsExist = entities.stream().anyMatch(e -> e instanceof Knight);

        System.out.println("*******END OF ROUND********");

        if (!monstersExist || !knightsExist) {
            String message = !monstersExist && !knightsExist ? "It's a tie!" : !monstersExist && knightsExist ? "Knights won!" : "Monsters won!";
            int result = JOptionPane.showConfirmDialog(this, message + " Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                System.exit(0);
            }
        }
    }


    private boolean isFree(int row, int col) {
        if (row < 0 || col < 0 || row >= rows || col >= cols || map[row][col] != '.') return false;
        for (Entity e : entities)
            if (e.getRow() == row && e.getCol() == col) return false;
        return true;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            Object[] options = {"Stats", "Exit"};
            int choice = JOptionPane.showOptionDialog(
                this,
                "Pause Menu",
                "Game Paused",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );

            if (choice == 0) {
                showStats();
            } else if (choice == 1) {
                System.exit(0);
            }

            return;
        }

        int newRow = player.getRow(), newCol = player.getCol();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: newRow--; break;
            case KeyEvent.VK_DOWN: newRow++; break;
            case KeyEvent.VK_LEFT: newCol--; break;
            case KeyEvent.VK_RIGHT: newCol++; break;
        }

        if (isFree(newRow, newCol)) {
            player.move(newRow, newCol);
            processTurn();  // One full turn after player moves
        }
    }

    public void restartGame() {
        // Stop any running animations
        activeAnimations.clear();
        if (animationTimer.isRunning()) {
            animationTimer.stop();
        }
        isAnimating = false;
        generateMap();
        repaint();
    }

    private void showStats() {
        StringBuilder stats = new StringBuilder();
        int knightCount = 0, monsterCount = 0;

        for (Entity e : entities) {
            if (e instanceof Knight) {
                knightCount++;
                stats.append("Knight at (")
                    .append(e.getRow()).append(", ").append(e.getCol())
                    .append(") - HP: ").append(((Fighter) e).getHealth()).append("\n");
            } else if (e instanceof Monster) {
                monsterCount++;
                stats.append("Monster at (")
                    .append(e.getRow()).append(", ").append(e.getCol())
                    .append(") - HP: ").append(((Fighter) e).getHealth()).append("\n");
            }
        }

        stats.insert(0, "Knights: " + knightCount + "\nMonsters: " + monsterCount + "\n\n");

        JOptionPane.showMessageDialog(this, stats.toString(), "Game Stats", JOptionPane.INFORMATION_MESSAGE);
    }


    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    /*private abstract class Animation {
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

    private class AttackAnimation extends Animation {
        private Entity target;
        private float shakeIntensity = 8.0f;
        
        public AttackAnimation(Entity attacker, Entity target) {
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
                g2d.fillRect(entity.getCol() * TILE_SIZE, entity.getRow() * TILE_SIZE, 
                           TILE_SIZE, TILE_SIZE);
                
                // Flash on target
                g2d.fillRect(target.getCol() * TILE_SIZE, target.getRow() * TILE_SIZE, 
                           TILE_SIZE, TILE_SIZE);
            }
            g2d.setComposite(originalComposite);
            
            // Screen shake effect for target
            if (progress < 0.5f) {
                Random rand = new Random((long)(progress * 1000));
                float shake = shakeIntensity * (1.0f - progress * 2);
                int shakeX = (int)(rand.nextGaussian() * shake);
                int shakeY = (int)(rand.nextGaussian() * shake);
                
                // Apply shake to target rendering (this would need to be handled in main render)
                // For now, we'll add impact particles
                drawImpactParticles(g2d, target, progress);
            }
        }
        
        private void drawImpactParticles(Graphics2D g2d, Entity target, float progress) {
            if (progress < 0.3f) {
                Random rand = new Random((long)(progress * 1000));
                g2d.setColor(Color.ORANGE);
                
                int centerX = target.getCol() * TILE_SIZE + TILE_SIZE / 2;
                int centerY = target.getRow() * TILE_SIZE + TILE_SIZE / 2;
                
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

    private class HealAnimation extends Animation {
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
                int centerX = entity.getCol() * TILE_SIZE + TILE_SIZE / 2;
                int centerY = entity.getRow() * TILE_SIZE + TILE_SIZE / 2;
                int radius = (int)(TILE_SIZE * (0.5 + progress * 0.5));
                
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
            
            int centerX = entity.getCol() * TILE_SIZE + TILE_SIZE / 2;
            int baseY = entity.getRow() * TILE_SIZE + TILE_SIZE;
            
            for (int i = 0; i < 6; i++) {
                int x = centerX + (int)(rand.nextGaussian() * 10);
                int y = baseY - (int)(progress * TILE_SIZE * 1.5) + (int)(rand.nextGaussian() * 5);
                
                float particleAlpha = 1.0f - progress;
                if (particleAlpha > 0) {
                    AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, particleAlpha);
                    g2d.setComposite(ac);
                    g2d.fillOval(x - 2, y - 2, 4, 4);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
            }
        }
    }*/
}
