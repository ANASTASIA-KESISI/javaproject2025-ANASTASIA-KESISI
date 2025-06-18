public class Player extends Entity {
    public Player(int row, int col) {
        super(row, col);
    }

    @Override
    public boolean isAlly(Entity other) {
        return other instanceof Knight || other instanceof Player;
    }

    @Override
    public String getEntityInfoAsString(){
        return "";
    }
}

