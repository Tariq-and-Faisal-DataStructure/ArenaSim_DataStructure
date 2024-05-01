package Battle2;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

public class SpecialPlayer extends Player {
    private boolean isRunning;
    private long runEndTime;
    private long cooldownEndTime;
    private static final long RUN_DURATION = 10000; // 10 seconds
    private static final long COOLDOWN_DURATION = 15000; // 15 seconds
    
    
    public SpecialPlayer(){}

    public SpecialPlayer(float health, float atk_speed, float damage, int movementSpeed, String name, Circle shape) {
        super(health, atk_speed, damage, movementSpeed, name, shape);
        super.updateHealthBar();
        this.isRunning = false;
        this.runEndTime = 0;
        this.cooldownEndTime = 0;
    }


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
        if (shouldStartRunning()) {
            startRunning();
        } 
        // Stop running if the running time has elapsed
        else if (isRunning && System.currentTimeMillis() > runEndTime) {
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
        runEndTime = System.currentTimeMillis() + RUN_DURATION; // Set the duration for running
    }
    
    private void stopRunning() {
        // Set isRunning to false and start the cooldown
        isRunning = false;
        cooldownEndTime = System.currentTimeMillis() + COOLDOWN_DURATION; // Set the cooldown duration
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
        if (isRunning && System.currentTimeMillis() > runEndTime) {
            stopRunning();
        }
    }

    
    

    public boolean getIsRunning(){
        return isRunning;
    }

    public void setIsRunning(boolean condition){
        this.isRunning = condition;
    }
   
    
}