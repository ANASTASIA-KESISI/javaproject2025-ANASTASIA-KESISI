import java.util.*;

public class Knight extends Fighter {
    //available repositioning --- only up/down/left/right and no diagonical
    private final int[][] dirs = { {0,1}, {1,0}, {0,-1}, {-1,0} };

    public Knight(int row, int col, int attack, int defense, int healing) {
        super(row, col, attack, defense, healing);
    }

    //get random direction and move entity if the new position is free
    @Override
    public void takeTurn(char[][] map, Player player, List<Entity> others) {
        Collections.shuffle(Arrays.asList(dirs));

        for (int[] d : dirs) {
            int newRow = row + d[0];
            int newCol = col + d[1];

            if (GamePanel.isFree(map, newRow, newCol, others)) {
                move(newRow, newCol);
                break;
            }
        }
    }

    @Override
    public boolean isAlly(Entity other) {
        return other instanceof Knight || other instanceof Player;
    }

    public String getEntityInfoAsString(){
        return "Knight at (" + (this.getCol() + 1) + ", " + (this.getRow() + 1) + ") - HP: " + this.getHealth()+ " - Healings: " + this.getHealing() + "\n";
    }
    
}

