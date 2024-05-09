package ArenaSim_DataStructure.ArenaSim;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;


public class Player extends Map implements Updatable{
    private float maxHealth;
    private float health;
    private float atk_speed;
    private float BaseAttackSpeed;
    private float movmentSpeed;
    private int getBaseMovementSpeed;
    private String name;
    private Circle Shape;
    private volatile boolean isAttacking = false;
    private volatile Player currentTarget;
    private HealthBar healthBar;
    private Player atack_who;
    private Player atacking_me;
    private int stop;


    public Player(){}

    public Player(float maxHealth, float BaseAttackSpeed, float damage, int getBaseMovementSpeed, String name, Circle Shape){
        super();
        this.health = maxHealth;
        this.maxHealth = maxHealth;
        this.BaseAttackSpeed = BaseAttackSpeed;
        this.atk_speed = BaseAttackSpeed;
        this.getBaseMovementSpeed = getBaseMovementSpeed;
        this.movmentSpeed = getBaseMovementSpeed;
        this.name = name;
        this.Shape = Shape;
        this.setDamage(damage);
        this.healthBar = new HealthBar(this.health, 50, 5,0, 0);  // Example initialization
    }
    

    public boolean checkPlayerCollision(Circle character1, Circle character2) {
    double dx = character2.getCenterX() - character1.getCenterX();
    double dy = character2.getCenterY() - character1.getCenterY();
    double distance = Math.sqrt(dx * dx + dy * dy);
    return distance < (character1.getRadius() + character2.getRadius());
}

    
    

     public Player findClosestOponent(List<Player> enemies, Player currentPlayer) {
        Player closestPlayer = null;
        double closestDistance = Double.MAX_VALUE;
    
        for (Player enemy : enemies) {
            double distance = calculateDistance(currentPlayer.getShape(), enemy.getShape());
    
            if (distance < closestDistance) {
                closestDistance = distance;
                closestPlayer = enemy;
            }
        }
    
        this.atack_who = closestPlayer;
        return closestPlayer;
    }
    
    public double calculateDistance(Circle c1, Circle c2) {
        double dx = c1.getCenterX() - c2.getCenterX();
        double dy = c1.getCenterY() - c2.getCenterY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    

    public void attack(Player target) {
        setHealth(takeDamage(target.getHealth(), this.getDamage()));
    }
    
    public void moveTowards(double targetX, double targetY) {
        Point2D currentLocation = this.getReadLocation();
        double currentX = currentLocation.getX();
        double currentY = currentLocation.getY();
    
        double dx = targetX - currentX;
        double dy = targetY - currentY;
    
        double length = Math.sqrt(dx * dx + dy * dy);
        
        // Prevent division by zero if target and current positions are the same
        if (length == 0) {
            return; // Player has reached the target, no movement needed
        }

        

    
        double normX = dx / length;
        double normY = dy / length;
    
        double moveX = normX * this.getMovmentSpeed();
        double moveY = normY * this.getMovmentSpeed();

        

        this.setReadLocation(new Point2D(currentX + moveX, currentY + moveY));    
        // Update the player's position
        Platform.runLater(() -> {
            Shape.setCenterX(currentX + moveX);
            Shape.setCenterY(currentY + moveY);
        });
    }

    
    public void updateHealthBar() {
        this.healthBar.updateHealth(this.health, this.maxHealth);
        // Assuming getReadLocation() returns the current position of the player
        Point2D currentPosition = this.getReadLocation();
        this.healthBar.updatePosition(currentPosition.getX(), currentPosition.getY());
    }

    public void initializePlayer(double initialX, double initialY) {
            this.setReadLocation(new Point2D(initialX, initialY));
    }
    


    
        public boolean AtackingMe(Player enemy){
            if(enemy.atack_who == this){
                return true;
            }
           else{
            return false;
           }
        }

        public void updatePriority(Player enemy){}

        public void UpdateAtackingMe(){}

        public void checkAttackingMe(List<Player> enemies){
            for(Player enemy:enemies){
                if(enemy.getAttackWho() == this){
                    this.setAtackingMe(enemy);;
                }
            }
    
        }

    public Player getAttackWho(){
        return this.atack_who;
    }

    @Override
    public Circle getShape() {
        return this.Shape;
    }

    public HealthBar getHealthBar() {
        return this.healthBar;
    }

    public float getHealth(){
        return health;
    }

    public float getAtk_speed() {
        return this.atk_speed;
    }

    public float getMovmentSpeed(){
        return movmentSpeed;
    }

    public String getName(){
        return name;
    }

    public boolean getIsAtacking(){
        return isAttacking;
    }

    public float getMaxHealth(){
        return maxHealth;
    }

    public Player getCurrentTarget() {
        return currentTarget;
    }   

    public int getBaseMovementSpeed(){
        return getBaseMovementSpeed;

    }

    public float getBaseAttackSpeed(){
        return BaseAttackSpeed;
    }

    public Player getEnimesRefrense(Player enemy){
        this.atack_who = enemy;
        return enemy;

    }

    public Player getAttackingMe(){
        return this.atacking_me;
    }

    public void setHealth(float health){
        this.health = health;
    }

    public void setAtk_speed(float atk_speed) {
        this.atk_speed = atk_speed;
    }

    public void setMovmentSpeed(float movmentSpeed){
        this.movmentSpeed = movmentSpeed;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setIsAtacking(boolean isAttacking){
        this.isAttacking = isAttacking;
    }

    public void setMaxHealth(float maxHealth){
        this.maxHealth = maxHealth;
    }

    public void setCurrentTarget(Player currentTarget) {
        this.currentTarget = currentTarget;
    }

    public void setAtackingMe(Player enemy){
        this.atacking_me = enemy;
    }
    
    public void setAtackwho(Player enemy){
        this.atack_who= enemy;
    }

}