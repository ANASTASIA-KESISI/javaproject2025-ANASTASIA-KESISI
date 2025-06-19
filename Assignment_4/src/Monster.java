import java.util.*;

public class Monster extends Fighter {
    //available repositioning ---  up/down/left/right AND diagonical
    private final int[][] dirs = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};

    public Monster(int row, int col, int attack, int defense, int healing) {
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
        return other instanceof Monster;
    }

    public String getEntityInfoAsString(){
        return "Monster at (" + (this.getCol() + 1) + ", " + (this.getRow() + 1) + ") - HP: " + this.getHealth()+ " - Healings: " + this.getHealing() + "\n";
    }
    
}

