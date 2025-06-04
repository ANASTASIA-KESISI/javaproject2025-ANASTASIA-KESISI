public abstract class Entity {
    protected int row, col;
    protected int health = 3;
    protected final int maxHealth = 3;

    public Entity(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }

    public void move(int newRow, int newCol) {
        this.row = newRow;
        this.col = newCol;
    }

    public boolean isAdjacent(Entity other) {
        int dr = Math.abs(this.row - other.row);
        int dc = Math.abs(this.col - other.col);
        return (dr <= 1 && dc <= 1) && !(dr == 0 && dc == 0);
    }

    public abstract boolean isAlly(Entity other);
}

