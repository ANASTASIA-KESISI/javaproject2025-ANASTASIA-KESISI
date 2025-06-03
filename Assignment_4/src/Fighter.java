public abstract class Fighter extends Entity {
    protected int attack;  // 1–3
    protected int defense; // 1–2

    public Fighter(int row, int col, int attack, int defense) {
        super(row, col);
        this.attack = attack;
        this.defense = defense;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public void heal(Fighter ally) {
        if (ally.health < ally.maxHealth) {
            ally.health++;
        }
    }

    public void attack(Fighter enemy) {
        int damage = Math.max(0, this.defense - enemy.attack);
        System.out.println("Damage to " + enemy + " : " + damage );
        enemy.health = Math.max(1, enemy.health - damage);
    }

    public abstract void takeTurn(char[][] map, Player player, java.util.List<Entity> others);
}

