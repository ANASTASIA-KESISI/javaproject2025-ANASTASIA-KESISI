public abstract class Fighter extends Entity {
    protected int attack;  // 1–3
    protected int defense; // 1–2
    protected int healing; // 0-2

    public Fighter(int row, int col, int attack, int defense, int healing) {
        super(row, col);
        this.attack = attack;
        this.defense = defense;
        this.healing = healing;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getHealing(){
        return healing;
    }

    public Boolean heal(Fighter ally) {
        if (this.healing > 0 && ally.health < ally.getMaxHealth()) {
            ally.health++;
            this.healing--;
            return true;
        }
        return false;
    }

    public Boolean attack(Fighter enemy) {
        if(enemy.defense <= this.attack){
            int damage = Math.abs(enemy.defense - this.attack);
            System.out.println("Damage to " + enemy + " : " + damage );
            enemy.health =  enemy.health - damage < 0 ? 0 : enemy.health - damage;
            return true;
        }
        System.out.println("No attack to " + enemy + " because attack is " + this.attack + " and the enemy defense is: " + enemy.getDefense() );
        return false;
        
    }

    public abstract void takeTurn(char[][] map, Player player, java.util.List<Entity> others);
    public abstract String getEntityInfoAsString();
}

