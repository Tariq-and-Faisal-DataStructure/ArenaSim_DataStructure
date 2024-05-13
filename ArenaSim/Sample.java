package ArenaSim_DataStructure.ArenaSim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

public class Sample extends Application {
    private Timeline gameLoop;
    public static boolean endgameflag;
    private Stage primaryStage;
    public static Main_menu mainMenu;
    public static List<Player> players = new ArrayList<>();
    public static List<Player> enemies = new ArrayList<>();
    private List<Obstacles> obstacles;
    private List<Player> playersMarkedForRemoval = new ArrayList<>();
    private List<Player> enemiesMarkedForRemoval = new ArrayList<>();
    private static List<MovableObstacles> movableObstacles;
    final double minX = 0.0;
    final double maxX = 1280.0; // Width of the arena
    final double minY = 0.0;
    final double maxY = 720.0; // Height of the arena
    private static boolean gameStart = false;
    public static Player dummy = new Player();
    public static Player enemyofSpecialPlayer;

    

    // Hard codded obstacles:
    public static Circle rockCircle = new Circle(300, 300, 30, Color.BROWN);
    public static Obstacles rock = new Obstacles(0, "Rock", rockCircle);

    public static Circle holeCircle = new Circle(400, 400, 30, Color.BLACK);
    public static Obstacles hole = new Obstacles(1000000000, "Hole", holeCircle);

    public static Circle undergroundshaftCircle = new Circle(500, 300, 30, Color.GRAY);
    public static Obstacles undergroundshaft = new Obstacles(40, "UnderGroundShaft", undergroundshaftCircle);

    public static Circle meteorCircle = new Circle(50, 50, 70, Color.ORANGE);
    public static MovableObstacles meteor = new MovableObstacles(1000000000, "Meteor", 0, meteorCircle);

    // public static Circle movingShaftCircle = new Circle(600, 300, 30, Color.DARKGRAY);
    // public static MovableObstacles movingShaft = new MovableObstacles(40, "MovingUnderGroundShaft", 0,
    //         movingShaftCircle);

    public static Scene createArenaScene(Stage primaryStage,
            List<CharacterSelection.CharacterAttributes> playerCharacters,
            List<CharacterSelection.CharacterAttributes> enemyCharacters) {
        List<Player> localPlayers = new ArrayList<>();
        List<Player> localEnemies = new ArrayList<>();
        Scene mainMenuScene = mainMenu.createMainMenuContent();
        Arena arena = new Arena(primaryStage, mainMenuScene);
        Pane root = arena.setupArena(localPlayers, localEnemies);
        root.getChildren().addAll(rockCircle, holeCircle, undergroundshaftCircle);

        // Add to the list and to the root pane
        root.getChildren().addAll(meteorCircle);

        // Create players and enemies via CharacerSelection class
        for (CharacterSelection.CharacterAttributes charAttr : playerCharacters) {
            if (!charAttr.isSpecialPlayer) {
                Circle blueTeamCircle = new Circle(charAttr.xAxis, charAttr.yAxis, 10, Color.BLUE);
                Player blueTeam = new Player(charAttr.health, charAttr.attackSpeed, charAttr.damage,
                        charAttr.movementSpeed, charAttr.name, blueTeamCircle);
                blueTeam.setReadLocation(new Point2D(charAttr.xAxis, charAttr.yAxis));
                root.getChildren().add(blueTeamCircle);
                localPlayers.add(blueTeam);
                HealthBar playerHealthBar = blueTeam.getHealthBar(); // Assuming getHealthBar() method is defined in
                                                                     // Player
                root.getChildren().add(playerHealthBar.getBar());
                
            } else {
                Circle blueTeamCircle = new Circle(charAttr.xAxis, charAttr.yAxis, 10, Color.BLUEVIOLET);
                SpecialPlayer blueTeam = new SpecialPlayer(charAttr.health, charAttr.attackSpeed, charAttr.damage,
                        charAttr.movementSpeed, charAttr.name, blueTeamCircle);
                blueTeam.setReadLocation(new Point2D(charAttr.xAxis, charAttr.yAxis));
                root.getChildren().add(blueTeamCircle);
                localPlayers.add(blueTeam);
                HealthBar playerHealthBar = blueTeam.getHealthBar(); // Assuming getHealthBar() method is defined in
                                                                     // Player
                root.getChildren().add(playerHealthBar.getBar());
            }
        }

        for (CharacterSelection.CharacterAttributes charAttr : enemyCharacters) {
            if (!charAttr.isSpecialPlayer) {
                Circle redTeamCircle = new Circle(charAttr.xAxis, charAttr.yAxis, 10, Color.RED);
                Player redTeam = new Player(charAttr.health, charAttr.attackSpeed, charAttr.damage,
                        charAttr.movementSpeed, charAttr.name, redTeamCircle);
                redTeam.setReadLocation(new Point2D(charAttr.xAxis, charAttr.yAxis));
                root.getChildren().add(redTeamCircle);
                localEnemies.add(redTeam);
                HealthBar enemyHealthBar = redTeam.getHealthBar();
                root.getChildren().add(enemyHealthBar.getBar());
            } else {
                Circle redTeamCircle = new Circle(charAttr.xAxis, charAttr.yAxis, 10, Color.PURPLE);
                SpecialPlayer redTeam = new SpecialPlayer(charAttr.health, charAttr.attackSpeed, charAttr.damage,
                        charAttr.movementSpeed, charAttr.name, redTeamCircle);
                redTeam.setReadLocation(new Point2D(charAttr.xAxis, charAttr.yAxis));
                root.getChildren().add(redTeamCircle);
                localEnemies.add(redTeam);
                HealthBar enemyHealthBar = redTeam.getHealthBar();
                root.getChildren().add(enemyHealthBar.getBar());
            }
        }
        // Initialize movements and attacks
       
        players = localPlayers; // Assign to class member
        enemies = localEnemies; // Assign to class member
        return new Scene(root, 1280, 720);
    }

    @Override
    public void start(Stage primaryStage) {
        endgameflag = false;
        this.primaryStage = primaryStage; // Assign the passed stage to the class variable

        movableObstacles = Arrays.asList(meteor);
        obstacles = Arrays.asList(rock, hole, undergroundshaft);

        mainMenu = new Main_menu(primaryStage);
        System.out.println("in start");
        Scene mainMenuScene = mainMenu.createMainMenuContent();
        primaryStage.setTitle("Battle Arena");
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();

        initializeGameplay();
    }

    // main loop 1 calls main loop 2
    private void initializeGameplay() {
        Timeline gameLoop = new Timeline(new KeyFrame(javafx.util.Duration.millis(16), e -> updateGame()));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }

    public void resetGameConditions() {
        // Stop the game loop if it's running
        if (gameLoop != null) {
            gameLoop.stop();
        }

        for(Player player:players){
            if(player instanceof SpecialPlayer){
                ((SpecialPlayer)player).setIsRunning(false); // reset running condition if game ends for players
                ((SpecialPlayer)player).setIsOneManStanding(false); // reset condition for checking if the special player is alone
            }
        }
        
        for(Player enemy:enemies){
            if(enemy instanceof SpecialPlayer){
                ((SpecialPlayer)enemy).setIsRunning(false); // reset running condition if game ends for enemies
                ((SpecialPlayer)enemy).setIsOneManStanding(false); // reset condition for checking if the special player is alone
            }
        }
        // Clear player and enemy lists
        players.clear();
        enemies.clear();

        // Reset any game-specific flags
        gameStart = false;

        // Optionally reset any other game-related states or counters
        // For example, resetting score, health, or other game mechanics
        // resetScore(); // If you have a method or logic to reset scores

        // Log reset for debugging purposes
        System.out.println("Game conditions have been reset");
    }

    // main loop 2 calls main loop 3
    private void updateGame() {
        // Update all blue team members
        updateAllCharacters(players, enemies);
        // Update all red team members
        updateAllCharacters(enemies, players);
        
        // Check if the game should end
        checkEndConditions();
    }

    // main loop 3 calls the updating methods
    private void updateAllCharacters(List<Player> characters, List<Player> targets) {
        List<Player> toRemove = new ArrayList<>();
        dummy.sortPlayers(characters);
        for (Player character : characters) {

            // check if the target is attacking this character
           character.checkAttackingMe(targets);
           // if character is a special player update the priorit based on the priority conditions
           if(character instanceof SpecialPlayer){
            for(Player target:targets){
                if(playersMarkedForRemoval.size() == characters.size() - 1){
                    ((SpecialPlayer)character).setIsOneManStanding(true);
                }
                    
                    character.updatePriority(target);
                    enemyofSpecialPlayer = character.getAttackingMe();

                    
            }
        }
            // Check for character death
            if (character.die(character.getHealth())) {
                toRemove.add(character);
                handlePlayerDeath(character);

                continue; // Skip the rest of the processing for dead characters
            }

            gameStart = true;
            Point2D newPosition = character.getReadLocation();
            double newX = Math.min(Math.max(newPosition.getX(), minX), maxX);
            double newY = Math.min(Math.max(newPosition.getY(), minY), maxY);
            character.setReadLocation(new Point2D(newX, newY));
            character.updateHealthBar();
            // Special handling for SpecialPlayer
            if (character instanceof SpecialPlayer) {
                SpecialPlayer specialPlayer = (SpecialPlayer) character;
                if(characters.get(0) != null && characters.get(0).getHealth() !=0 )
                specialPlayer.updateRunningStatus(characters.get(0));

                if (specialPlayer.getIsRunning()) {
                    if (character.getAttackingMe() != null) {
                        Circle targetCircle = character.getAttackingMe().getShape();
                        specialPlayer.moveTowardsOppositeDirection(targetCircle.getCenterX(),
                                targetCircle.getCenterY());
                    }
                
                    continue; // Skip the rest of the loop for the running SpecialPlayer
                }
            }
            
            Player closestTarget = character.findClosestOponent(targets, character);
         
            // closestTarget.AtackingMe(targets, closestTarget);
            if(character.getSummoneMe()){
                closestTarget = enemyofSpecialPlayer;
            }
            // Normal movement or resuming movement after running
            if (closestTarget != null && !character.getIsAtacking()) {
                Circle characterCircle = character.getShape();
                Circle targetCircle = closestTarget.getShape();

                boolean isCollision = character.checkPlayerCollision(characterCircle, targetCircle);

                if (!isCollision) {
                    character.moveTowards(targetCircle.getCenterX(), targetCircle.getCenterY());
                } else {
                    handleAttack(character, closestTarget);
                }
            }

            // Check for collisions with obstacles
            for (Obstacles obstacle : obstacles) {
                if (character.checkPlayerCollision(character.getShape(), obstacle.getShape())) {
                    System.out.println(obstacle.getObstacleName() + " deals damage to " + character.getName());
                    handleObstacleCollision(character, obstacle);
                }
            }
        }

        updateMovableObstacles();
        checkCollisionsWithMovableObstacles();

        characters.removeAll(playersMarkedForRemoval);
        targets.removeAll(enemiesMarkedForRemoval);

        // Clear the marked lists for the next iteration
        playersMarkedForRemoval.clear();
        enemiesMarkedForRemoval.clear();

    }

    // Mainly updates the position of Movable Obstacles
    private void updateMovableObstacles() {
        for (MovableObstacles obstacle : movableObstacles) {
            switch (obstacle.getObstacleName()) {
                case "MovingUnderGroundShaft":
                    // updatePosition method for linear movement
                    obstacle.updatePosition();
                    break;
                case "Meteor":
                    // meteor appearance and disappearance
                    handleMeteorBehavior(obstacle);
                    break;
            }
        }
    }

    private void handleMeteorBehavior(MovableObstacles obstacle) {
        obstacle.updateMeteor(); // Update meteor visibility and position
    }

    private void handleAttack(Player attacker, Player defender) {
        if (attacker.getIsAtacking() || attacker == defender) {

            return; // Skip if already attacking or self-targeted
        }
        

        attacker.setIsAtacking(true); // Mark attacker as attacking

        long attackDelay = (long) (1000 / attacker.getAtk_speed());
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
      
        executor.schedule(() -> {
            attacker.getEnimesRefrense(defender);// new
            if (!attacker.die(attacker.getHealth()) && !defender.die(defender.getHealth())) {
                defender.setHealth(defender.takeDamage(defender.getHealth(), attacker.getDamage()));
                Platform.runLater(() -> {
                    if (Arena.eventLog != null) {
                        Arena.eventLog.appendText(attacker.getName() + " attacked " + attacker.getAttackWho().getName() + "\n");
                    }
                });
            }
            attacker.setIsAtacking(false); // Reset attacking flag
            executor.shutdown(); // Terminate the scheduled task
        }, attackDelay, TimeUnit.MILLISECONDS);
        
    }

    private void handlePlayerDeath(Player deadPlayer) {
        // Ensure removal of the player's graphical representation is done on the JavaFX
        // Application Thread
        Platform.runLater(() -> {
            Circle character = deadPlayer.getShape();
            HealthBar healthBar = deadPlayer.getHealthBar();
            if (character != null && character.getParent() instanceof Pane) {
                Pane gamePane = (Pane) character.getParent();
                gamePane.getChildren().remove(character);
                if (healthBar != null && healthBar.getBar().getParent() == gamePane) {
                    gamePane.getChildren().remove(healthBar.getBar()); // Remove health bar
                }

                // Update the event log with the death message
                if (Arena.eventLog != null) {
                    Arena.eventLog.appendText(deadPlayer.getName() + " has died.\n");
                }
            }
        });

        // Mark the player for removal instead of direct removal
        playersMarkedForRemoval.add(deadPlayer);
        enemiesMarkedForRemoval.add(deadPlayer);
        if (gameStart && (players.isEmpty() || enemies.isEmpty())) {
            endgameflag = true;
        }
        // Check if the game should end due to this death
        checkEndConditions();
    }

    private void handleObstacleCollision(Player player, Obstacles obstacle) {
        player.setHealth(player.takeDamage(player.getHealth(), obstacle.getDamage()));

        // Check if the player dies due to obstacle collision
        if (player.die(player.getHealth())) {
            handlePlayerDeath(player);
        }
    }

    private void checkCollisionsWithMovableObstacles() {
        for (MovableObstacles obstacle : movableObstacles) {
            if (obstacle.getObstacleName().equals("Meteor") && obstacle.getShape().isVisible()) {
                for (Player player : players) {
                    if (player.checkPlayerCollision(player.getShape(), obstacle.getShape())) {
                        // Apply meteor's damage to members of the blue team
                        player.setHealth(player.takeDamage(player.getHealth(), obstacle.getDamage()));
                        if (player.die(player.getHealth())) {
                            handlePlayerDeath(player);
                        }
                    }
                }

                for (Player enemy : enemies) {
                    if (enemy.checkPlayerCollision(enemy.getShape(), obstacle.getShape())) {
                        // Apply meteor's damage to the members of red team
                        enemy.setHealth(enemy.takeDamage(enemy.getHealth(), obstacle.getDamage()));
                        if (enemy.die(enemy.getHealth())) {
                            handlePlayerDeath(enemy);
                        }
                    }
                }
            }
        }

        for (MovableObstacles obstacle : movableObstacles) {
            if (obstacle.getObstacleName().equals("MovingUnderGroundShaft") && obstacle.getShape().isVisible()) {
                for (Player player : players) {
                    if (player.checkPlayerCollision(player.getShape(), obstacle.getShape())) {
                        // Apply meteor's damage to the members of blue team
                        player.setHealth(player.takeDamage(player.getHealth(), obstacle.getDamage()));
                        if (player.die(player.getHealth())) {
                            handlePlayerDeath(player);
                        }
                    }
                }

                for (Player enemy : enemies) {
                    if (enemy.checkPlayerCollision(enemy.getShape(), obstacle.getShape())) {
                        // Apply meteor's damage to the members of red team
                        enemy.setHealth(enemy.takeDamage(enemy.getHealth(), obstacle.getDamage()));
                        if (enemy.die(enemy.getHealth())) {
                            handlePlayerDeath(enemy);
                        }
                    }
                }
            }
        }
    }


    

   

    private void checkEndConditions() {
        if (gameStart && (players.isEmpty() || enemies.isEmpty())) {
             endGame();
        }
    }

    private void endGame() {
        Platform.runLater(() -> {
            resetGameConditions();
            Leaderboard leaderboard = new Leaderboard(primaryStage, mainMenu.createMainMenuContent());
            Scene leaderboardScene = leaderboard.createLeaderboardScene(new ArrayList<>(players),
                    new ArrayList<>(enemies));
            primaryStage.setScene(leaderboardScene);

        });
        endgameflag = false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}