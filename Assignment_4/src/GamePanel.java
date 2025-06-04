import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements KeyListener {
    private final int TILE_SIZE = 40;
    private int rows, cols;
    private char[][] map;

    private Player player;
    private List<Entity> entities = new ArrayList<>();

    private Image landImg, treeImg, riverImg, playerImg, knightImg, monsterImg;

    public GamePanel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        setPreferredSize(new Dimension(cols * TILE_SIZE, rows * TILE_SIZE));
        setFocusable(true);
        addKeyListener(this);
        loadImages();
        generateMap();
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
}
