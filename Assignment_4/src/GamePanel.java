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
    private Random rand = new Random();

    private Player player;
    private List<Entity> entities = new ArrayList<>();

    private Image landImg, treeImg, riverImg, playerImg_left, playerImg_right, knightImg_left, knightImg_right, monsterImg_left, monsterImg_right;

    // Animation system
    public Timer animationTimer;
    public List<Animation> activeAnimations = new ArrayList<>();
    public boolean isAnimating = false;

    //Constructor: generate map, characters and animations
    public GamePanel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        setPreferredSize(new Dimension(cols * TILE_SIZE, rows * TILE_SIZE));
        setFocusable(true);
        addKeyListener(this);
        loadImages();
        generateMap();

        // Initialize animation timer
        animationTimer = new Timer(16, e -> updateAnimations()); 
    }

    private void loadImages() {
        try {
            if(System.console() != null){
                //enable this to compile and run manually from terminal
                landImg = ImageIO.read(new File("_img_land.jpg"));
                treeImg = ImageIO.read(new File("_img_tree.jpg"));
                riverImg = ImageIO.read(new File("_img_river.jpg"));
                playerImg_left = ImageIO.read(new File("_img_player_left.png"));
                knightImg_left = ImageIO.read(new File("_img_knight_left.png"));
                monsterImg_left = ImageIO.read(new File("_img_monster_left.png"));
                playerImg_right = ImageIO.read(new File("_img_player_right.png"));
                knightImg_right = ImageIO.read(new File("_img_knight_right.png"));
                monsterImg_right = ImageIO.read(new File("_img_monster_right.png"));
            }
            
        } catch (IOException e) {
            System.out.println("Error loading images: " + e.getMessage());
        }
    }

    private void generateMap() {
        map = new char[rows][cols];
        //Random rand = new Random();
        double area = rows * cols;
        //control density of rivers and trees in the map
        double density = 1.0 - Math.min(0.6, 500.0 / area);

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                double chance = rand.nextDouble();
                if (chance < density * 0.2) map[r][c] = 'T';
                else if (chance < density * 0.4) map[r][c] = 'R';
                else map[r][c] = '.';
            }

        // Add player in the center
        int pr = rows / 2, pc = cols / 2;
        player = new Player(pr, pc);
        map[pr][pc] = '.';
        entities.clear();
        entities.add(player);

        // Add Knights and Monsters (equal number)
        for (int i = 0; i < (rows * cols)/15; i++) {
            Knight k = new Knight(
                                    rand.nextInt(rows),
                                    rand.nextInt(cols),
                                    rand.nextInt(3) + 1,
                                    rand.nextInt(2) + 1,
                                    rand.nextInt(3)
                        );
            //if the position is blocked by a tree or river, reposition
            while(!isFree(map,k.getRow(), k.getCol(),entities)){
                k.row = rand.nextInt(rows);
                k.col = rand.nextInt(cols);
            }
            entities.add(k);

            Monster m = new Monster(
                                    rand.nextInt(rows),
                                    rand.nextInt(cols),
                                    rand.nextInt(3) + 1,
                                    rand.nextInt(2) + 1,
                                    rand.nextInt(3)
                        );
            //if the position is blocked by a tree or river, reposition
            while(!isFree(map,m.getRow(), m.getCol(),entities)){
                m.row = rand.nextInt(rows);
                m.col = rand.nextInt(cols);
            }
            entities.add(m);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //used for the animations
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
            if (e instanceof Player) {
                img = e.getDirection() == Direction.LEFT ? playerImg_left : playerImg_right;
            } else if (e instanceof Knight) {
                img = e.getDirection() == Direction.LEFT ? knightImg_left : knightImg_right;
            } else if (e instanceof Monster) {
                img = e.getDirection() == Direction.LEFT ? monsterImg_left : monsterImg_right;
            }
            
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
        List<Animation> toRemove = new ArrayList<>();
        for (Animation animation : activeAnimations) {
            if (animation.update()) {
                toRemove.add(animation);
            }
        }
        activeAnimations.removeAll(toRemove);
        
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
                if(rand.nextBoolean()){
                    System.out.println(e + " decides to play this turn " );
                    ((Fighter) e).takeTurn(map, player, entities);
                }
                else{
                    System.out.println(e + " decides to skip this turn " );
                }
                
        
        repaint();

        for (Entity a : entities) {
            for (Entity b : entities) {
                if(a == b) continue;

                if (a.isAdjacent(b) && a instanceof Fighter fa && b instanceof Fighter fb) {
                    //decide randomly if the fighter is going to attack the enemy/heal the ally
                    if(rand.nextBoolean()) { 
                        if (a.isAlly(b)) {
                            System.out.println(fa + " heals " + fb );
                            System.out.println(fa + " number of healings: " + fa.getHealing());
                            System.out.println("before:" + fb.getHealth() );
                            if(fa.heal(fb)){
                                // Add healing animation
                                addAnimation(new HealAnimation(fa, fb));
                            }
                            System.out.println("after: " + fb.getHealth() );
                        } else {
                            System.out.println(fa + " attacks " + fb );
                            System.out.println(fa + " after health: " + fa.getHealth());
                            System.out.println(fb + " after health: " + fb.getHealth());
                            System.out.println("attack: " + fa.getAttack());
                            System.out.println("defense: " + fa.getDefense());
                            System.out.println("attack: " + fb.getAttack());
                            System.out.println("defense: " + fb.getDefense());
                            if(fa.attack(fb)){
                                // Add attack animation
                                addAnimation(new AttackAnimation(fa, fb));
                            }
                            System.out.println(fa + " after health: " + fa.getHealth());
                            System.out.println(fb + " after health: " + fb.getHealth());
                        }
                    }
                    else{
                        System.out.println(fa + " skips attack/heal");
                    }
                }
            }
        }

        // Remove dead
        //entities.removeIf(e -> e instanceof Fighter && ((Fighter) e).getHealth() <= 0);
        List<Entity> toRemove = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof Fighter && ((Fighter) e).getHealth() <= 0) {
                toRemove.add(e);
            }
        }
        entities.removeAll(toRemove);

        // Check for game over
        boolean monstersExist = false;
        for (Entity e : entities) {
            if (e instanceof Monster) {
                monstersExist = true;
                break;
            }
        }

        boolean knightsExist = false;
        for (Entity e : entities) {
            if (e instanceof Knight) {
                knightsExist = true;
                break;
            }
        }

        System.out.println("*******END OF ROUND********");

        if (!monstersExist || !knightsExist) {
            System.out.println("*******GAME ENDED********");
            String message = !monstersExist && !knightsExist ? "It's a tie!" : !monstersExist && knightsExist ? "Knights won!" : "Monsters won!";
            int result = JOptionPane.showConfirmDialog(this, message + " Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                System.exit(0);
            }
        }
    }

    public static boolean isFree(char[][] map, int r, int c, List<Entity> entities){
        if (r < 0 || c < 0 || r >= map.length || c >= map[0].length || map[r][c] != '.') return false;
        for (Entity e : entities)
            if (e.getRow() == r && e.getCol() == c) return false;
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
            case KeyEvent.VK_UP: newRow--;  break;
            case KeyEvent.VK_DOWN: newRow++; break;
            case KeyEvent.VK_LEFT: newCol--; break;
            case KeyEvent.VK_RIGHT: newCol++; break;
        }

        if (GamePanel.isFree(map,newRow, newCol, entities)) {
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
        int knightCount = 0, monsterCount = 0, knightTotalHealth = 0, monsterTotalHealth = 0;

        for (Entity e : entities) {
            if (e instanceof Knight) {
                knightCount++;
                knightTotalHealth += e.getHealth(); 
                stats.append(e.getEntityInfoAsString());
            } else if (e instanceof Monster) {
                monsterCount++;
                monsterTotalHealth += e.getHealth();
                stats.append(e.getEntityInfoAsString());
            }
        }

        stats.insert(0, "Knights: " + knightCount + " Total Health: " + knightTotalHealth +  "\nMonsters: " + monsterCount + " Total Health: "+ monsterTotalHealth +"\n\n");

        JOptionPane.showMessageDialog(this, stats.toString(), "Game Stats", JOptionPane.INFORMATION_MESSAGE);
    }


    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

}
