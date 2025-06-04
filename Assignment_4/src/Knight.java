import java.util.*;

public class Knight extends Fighter {

    public Knight(int row, int col, int attack, int defense, int healing) {
        super(row, col, attack, defense, healing);
    }

    @Override
    public void takeTurn(char[][] map, Player player, List<Entity> others) {
        int[][] dirs = { {0,1}, {1,0}, {0,-1}, {-1,0} };
        Collections.shuffle(Arrays.asList(dirs));

        for (int[] d : dirs) {
            int newRow = row + d[0];
            int newCol = col + d[1];

            if (isFree(map, newRow, newCol, others)) {
                move(newRow, newCol);
                break;
            }
        }
    }

    @Override
    public boolean isAlly(Entity other) {
        return other instanceof Knight || other instanceof Player;
    }

    private boolean isFree(char[][] map, int r, int c, List<Entity> entities) {
        if (r < 0 || c < 0 || r >= map.length || c >= map[0].length) return false;
        for (Entity e : entities)
            if (e != this && e.getRow() == r && e.getCol() == c)
                return false;
        return map[r][c] == '.';
    }
}

