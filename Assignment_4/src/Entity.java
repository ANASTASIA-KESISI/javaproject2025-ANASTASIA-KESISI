//enum to control the NPC direction and display the correct image
enum Direction {
    LEFT, RIGHT
}
public abstract class Entity {
    protected int row, col;
    protected int health = 3;
    protected final int maxHealth = 3;
    protected Direction direction = Direction.LEFT; // Default facing left

    public Entity(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }

    //update position and direction
    public void move(int newRow, int newCol) {
        if (newCol < col) {
            direction = Direction.LEFT;
        } else if (newCol > col) {
            direction = Direction.RIGHT;
        }

        this.row = newRow;
        this.col = newCol;
    }

    //check if the given entity is next to this entity
    public boolean isAdjacent(Entity other) {
        int dr = Math.abs(this.row - other.row);
        int dc = Math.abs(this.col - other.col);
        return (dr <= 1 && dc <= 1) && !(dr == 0 && dc == 0);
    }

    public abstract boolean isAlly(Entity other);
    public abstract String getEntityInfoAsString();
}

