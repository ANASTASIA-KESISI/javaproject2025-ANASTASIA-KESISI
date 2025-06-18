import java.util.*;

public class Monster extends Fighter {

    public Monster(int row, int col, int attack, int defense, int healing) {
        super(row, col, attack, defense, healing);
    }

    @Override
    public void takeTurn(char[][] map, Player player, List<Entity> others) {
        int[][] dirs = {
            {-1,-1},{-1,0},{-1,1},
            {0,-1},       {0,1},
            {1,-1},{1,0},{1,1}
        };
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
        return other instanceof Monster;
    }

    public String getEntityInfoAsString(){
        return "Monster at (" + (this.getCol() + 1) + ", " + (this.getRow() + 1) + ") - HP: " + this.getHealth()+ " - Healings: " + this.getHealing() + "\n";
    }
    
}

