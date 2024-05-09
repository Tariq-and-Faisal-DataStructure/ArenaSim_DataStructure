package ArenaSim_DataStructure.ArenaSim;


import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

public class SpecialPlayer extends Player {
    private boolean isRunning;
    private long runEndTime;
    private long cooldownEndTime;
    private static final long RUN_DURATION = 10000; // 10 seconds
    private static final long COOLDOWN_DURATION = 15000; // 15 seconds
    private static  PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(); // Data structure
    private static LinkedList<Integer> priorites = new LinkedList<>();
    
    
    
    public SpecialPlayer(){}

    public SpecialPlayer(float health, float atk_speed, float damage, int movementSpeed, String name, Circle shape) {
        super(health, atk_speed, damage, movementSpeed, name, shape);
        super.updateHealthBar();
        this.isRunning = false;
        this.runEndTime = 0;
        this.cooldownEndTime = 0;
        priorites.add(2); // attack
        priorites.add(1); // run away when health below 25%
    }

    // Sorting Algorithim
    public static void pqSort() {
        int n = priorites.size();
        // Assuming LinkedList.remove() here simulates priorites.remove(priorites.first())
        for (int j = 0; j < n; j++) {
            Integer element = priorites.removeFirst(); // Removes the first element, which is equivalent to priorites.remove(priorites.first())
            priorityQueue.add(element); // Use add instead of insert
        }
        for (int j = 0; j < n; j++) {
            Integer element = priorityQueue.poll(); // Retrieves and removes the head of the queue, the smallest element
            priorites.addLast(element); // Adds the element at the end of the list
        }
    }

     // iterate over enemies to check if any of them is attacking me
    // public void checkAttackingMe(List<Player> enemies){
    //     for(Player enemy:enemies){
    //         if(enemy.getAttackWho() == this){
    //             this.setAtackingMe(enemy);;
    //         }
    //     }

    // }

    @Override
    public void updatePriority(Player enemy){
        if(this.getHealth() < this.getMaxHealth() * 0.25 && this.getAttackingMe() != null){
            pqSort();
        }
        else{
            priorityQueue.clear();
            priorityQueue.addAll(priorites);
        }

    }
    
 

    // calls the methods based on the priority
    public void handlePriority(){

        if(this.priorityQueue.peek() == 1){
            this.shouldStartRunning();
        }
        else if(this.priorityQueue.peek() == 2){
            stopRunning();
        }
    }
    
    // when running, move to opposite direction of the attacker
    public void moveTowardsOppositeDirection(double targetX, double targetY){
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

        
        // Move in the opposite direction
         double moveX = -normX * this.getMovmentSpeed(); 
         double moveY = -normY * this.getMovmentSpeed();
         this.setReadLocation(new Point2D(currentX + moveX, currentY + moveY));
         
         Platform.runLater(() -> {
            getShape().setCenterX(currentX + moveX);
            getShape().setCenterY(currentY + moveY);
        });
    }

   

    public void updateRunningStatus() {
        // Start running if the conditions are met and the player is not already running
        if(this.priorityQueue.peek() == 1){
            startRunning();
        }
        else if(this.priorityQueue.peek() == 2){
            stopRunning();
        }
    }
    
    private boolean shouldStartRunning() {
        // The player should start running if their health is below 25%,
        // they are not already running, and the cooldown has passed
        return getHealth() <= getMaxHealth() * 0.25 && !isRunning && System.currentTimeMillis() > cooldownEndTime;
    }
    
    private void startRunning() {
        // Set isRunning to true and determine when the running should end
        isRunning = true;
    }
    
    private void stopRunning() {
        // Set isRunning to false and start the cooldown
        isRunning = false;
    }

    // Override getDamage if damage changes when running
    @Override
    public float getDamage() {
        if (isRunning) {
            return super.getDamage() * 3; 
        }
        return super.getDamage();
    }

    public void update() {
        handlePriority();
    }

    public boolean getIsRunning(){
        return isRunning;
    }

    public void setIsRunning(boolean condition){
        this.isRunning = condition;
    }
   
    
}