package ArenaSim_DataStructure.ArenaSim;


import java.util.Comparator;
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
    private static PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(); // Data structure
    private static LinkedList<Integer> priorites = new LinkedList<>();
    Player player = new Player();
    
    
    public SpecialPlayer(){}

    public SpecialPlayer(float health, float atk_speed, float damage, int movementSpeed, String name, Circle shape) {
        super(health, atk_speed, damage, movementSpeed, name, shape);
        super.updateHealthBar();
        this.isRunning = false;
        this.runEndTime = 0;
        this.cooldownEndTime = 0;
        priorites.offer(1); // attack
        priorites.offer(2); // run away when health below 25%
       // this.priorityQueue = new PriorityQueue<>(createComparator());
        System.out.println("size: " + priorityQueue.size());
       
    }

    // show the content of PriorityQueue
    public static void printPriorityQueue() {
        System.out.println("Priority Queue Contents:");
        PriorityQueue<Integer> copy = new PriorityQueue<>(priorityQueue); // Make a copy to avoid modifying the original
        while (!copy.isEmpty()) {
            System.out.print(copy.poll() + " ");
        }
        System.out.println(); // Move to the next line after printing all elements
    }

    public static void pqSort() {
         //System.out.println(priorityQueue.peek());
        // Create a copy of the priorities list to work with
        LinkedList<Integer> tempPriorities = new LinkedList<>(priorites);
        int n = tempPriorities.size();
    
        // Clear and use the class-level priorityQueue
        priorityQueue.clear();
        priorityQueue = new PriorityQueue<>(Comparator.reverseOrder());

        for (int j = 0; j < n; j++) {
            Integer element = tempPriorities.removeFirst(); // Remove from the temporary list
            priorityQueue.add(element); // Add to the priority queue
        }
    
        tempPriorities.clear(); // Clear the temporary list to reuse it for sorted elements
    
        for (int j = 0; j < n; j++) {
            Integer element = priorityQueue.poll(); // Retrieve and remove the smallest element from the queue
            tempPriorities.addLast(element); // Add the sorted element to the temporary list
        }
    
        // Refill the class-level priorityQueue with sorted elements
        priorityQueue.addAll(tempPriorities);
        
    }
    
    // keeps the default setup
    public static void pqSortDescending() {
       

        // Create a copy of the priorities list to work with
        LinkedList<Integer> tempPriorities = new LinkedList<>(priorites);
        int n = tempPriorities.size();
    
        // Clear the class-level priorityQueue and use a new PriorityQueue with reverse order
        priorityQueue.clear();
        //priorityQueue = new PriorityQueue<>(Comparator.reverseOrder());
    
        for (int j = 0; j < n; j++) {
            Integer element = tempPriorities.removeFirst(); // Remove from the temporary list
            priorityQueue.add(element); // Add to the priority queue
        }
    
        tempPriorities.clear(); // Clear the temporary list to reuse it for sorted elements
    
        for (int j = 0; j < n; j++) {
            Integer element = priorityQueue.poll(); // Retrieve and remove the largest element from the queue
            tempPriorities.addLast(element); // Add the sorted element to the temporary list
        }
    
        // Refill the class-level priorityQueue with sorted elements
        priorityQueue.addAll(tempPriorities);
    
        // Update the original list with sorted elements in descending order
        priorites = tempPriorities;

    }


    @Override
    public void updatePriority(Player enemy){
       
        if(this.getHealth() < this.getMaxHealth() * 0.25 && enemy.getAttackWho() == this){
            pqSort(); // make the priority run away
        }
        // In the case of running and no one is attacking the player, make the priority attack.
        else if(this.getHealth() < this.getMaxHealth() * 0.25 && enemy.getAttackWho() != this){
            priorityQueue.clear();  
            priorityQueue.offer(1);

        }
        else{
            pqSortDescending(); // keep the default setup
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
        // Ensure there is at least one element in the queue before accessing it
        if (!priorityQueue.isEmpty()) {
            Integer currentPriority = priorityQueue.peek();
            // if priority peek is 2 start running
            if(currentPriority == 2){ 
                startRunning();
            }
            else{
                stopRunning();
            }
        } else {
            // Handle the case where the queue is empty
            System.out.println("Priority queue is empty. Cannot update running status.  "+ priorityQueue.size());
        }
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
        updateRunningStatus();
    }

    public boolean getIsRunning(){
        return isRunning;
    }

    public void setIsRunning(boolean condition){
        this.isRunning = condition;
    }
   
    
}