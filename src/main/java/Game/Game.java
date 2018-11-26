package Game;

import Controllers.BoardController;
import Controllers.MenuController;
import NetworkClient.NetworkClient;
import card.BasicCard;
import card.BasicCreatureCard;
import card.EKeyword;
import card.SpecialAbilityCreatureCard;
import javafx.application.Platform;
import javafx.stage.Stage;
import main.Main2;
import menu.GameMenu;
//import net.bytebuddy.implementation.bytecode.Duplication;
import player.Player;
import repository.QueryHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {
    private static Game instance = null;
    
    QueryHandler queryHandler;
    private int started = 0;
    
    public String loginName;
    public String enemyName;
    private GameMenu gameMenu;
    // Player
    private Player player;
    private List<BasicCard> playerTableCards = new ArrayList<>();
    private int playerGraveyard;
    private int playerDeck = 20;
    private int turn;
    private int round;
    private int enemyTurn;

    public int getRound() {
        return round;
    }

    public int getEnemyGraveyard() {
        return enemyGraveyard;
    }

    public int getEnemyHand() {
        return enemyHand;
    }

    public int getEnemyDeck() {
        return enemyDeck;
    }

    public int getEnemyHealth() {
        return enemyHealth;
    }

    public int getEnemyMana() {
        return enemyMana;
    }

    // Enemy player
    private List<BasicCard> enemyTableCards;
    private int enemyGraveyard;
    private int enemyHand;
    private int enemyDeck = 20;
    private int enemyHealth = 20;
    private int enemyMana = 1;

    BoardController boardController;

    public void setBoardController(BoardController boardController) {
        this.boardController = boardController;
    }
    private Game() {
        //gameMenu = new GameMenu();
        queryHandler = new QueryHandler();
        playerTableCards = new ArrayList<>();
        enemyTableCards = new ArrayList<>();
    }
    
    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }
    
    public List<BasicCard> getPlayerTableCards() {
        return playerTableCards;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public int checkCombatTurn(int playerTurn) {
        if(turn == playerTurn && turn == 1) {
            return 0;
        } else if(turn != playerTurn && turn == 1) {
            return turn;
        } else if(turn == playerTurn && turn == 0) {
            return 1;
        } else {
            return turn;
        }
        //return turn == playerTurn ? 1 : playerTurn;
    }

    /*public void setPlayer(Player player) {
        this.player = player;
    }*/
    
    public int getPlayerGraveyard() {
        return playerGraveyard;
    }
    
    public void incrementPlayerGraveyard() {
        ++playerGraveyard;
    }
    
    public int getPlayerDeck() {
        return playerDeck;
    }
    
    public void incrementPlayerDeck() {
        ++playerDeck;
    }
    
    public void decrementPlayerDeck() {
        --playerDeck;
    }
    
    public void setPlayerDeck(int deck) {
        playerDeck = deck;
    }
    
    public int getTurn() {
        return turn;
    }
    
    public int getStarted() {
        return started;
    }

    public List<BasicCard> getEnemyTableCards() {
        return enemyTableCards;
    }
    
    public void receiveCommand(String serverOutput) throws IOException {
        if(serverOutput.startsWith("LOGIN")) {
            try {
                login(serverOutput);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(serverOutput.substring(3).startsWith("DEALT_CARDS") && (Integer.parseInt(serverOutput.substring(1, 2)) == player.getPlayerTurn())) {
            dealtCards(serverOutput);
        } else if(serverOutput.equals("STARTED")) {
            ++started;
            if(started == 2) {
                startGame();
            }
        }else if(serverOutput.startsWith("ROUND")) {
            endTurn(serverOutput);
        }else if (serverOutput.startsWith("DRAW_CARD")) {
            dealtCard(serverOutput);
        }else if(serverOutput.substring(3).startsWith("PLACE_CREATURE_SUCCESS")){
            placeSuccess(serverOutput);
        }else if(serverOutput.substring(3).startsWith("PLACE_CREATURE_FAILURE") && Integer.parseInt(serverOutput.substring(1, 2)) == player.getPlayerTurn()) {
            placeFailure(serverOutput);
        }else if(serverOutput.substring(3).startsWith("USE_MAGIC_CREATURE_SUCCESS") && Integer.parseInt(serverOutput.substring(1, 2)) == player.getPlayerTurn()){
            useSuccess(serverOutput);
        }else if(serverOutput.substring(3).startsWith("USE_MAGIC_CREATURE_FAILURE") && Integer.parseInt(serverOutput.substring(1, 2)) == player.getPlayerTurn()){
            useFailure(serverOutput);
        }else if (serverOutput.substring(3).startsWith("ATTACK_RESULT_FAILURE") && Integer.parseInt(serverOutput.substring(1, 2)) == player.getPlayerTurn()){
            attackFailure(serverOutput);
        }else if (serverOutput.substring(3).startsWith("ATTACK_RESULT_SUCCESS")){
            attackSuccess(serverOutput);
        }else if (serverOutput.substring(3).startsWith("CONSUMED")) {
            changeConsumed(Integer.parseInt(serverOutput.substring(18)));
        }else if(serverOutput.equals("ENEMY_HAND DECREMENT")) {
            --enemyHand;
        }else if(serverOutput.equals("ENEMY_HAND INCREMENT")) {
            ++enemyHand;
        }else if(serverOutput.equals("ENEMY_DECK DECREMENT")) {
            --enemyDeck;
        }else if(serverOutput.equals("PLAYER_DECK DECREMENT")) {
            --playerDeck;
        }else if(serverOutput.substring(3).startsWith("GRAVEYARD")) {
            incrementGraveyard(serverOutput);
        }else if (serverOutput.substring(3).startsWith("REMOVE_FROM_TABLE")) {
            removeFromTable(serverOutput);
        }else if (serverOutput.substring(3).startsWith("CREATURE_HP")) {
            changeCreatureHp(serverOutput);
        } else if(serverOutput.startsWith("ENEMY_MANA")) {
            enemyMana = Integer.parseInt(serverOutput.substring(11));
        }
        platform();
    }
    
    private void platform() {
        Platform.runLater(() -> {
            try {
                boardController.updateAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        
        });
    }

    private void changeCreatureHp(String serverOutput) {
        String[] chunks = serverOutput.split(" ");
        if (Integer.parseInt(chunks[0].substring(1, 2)) == player.getPlayerTurn()) {
            ((BasicCreatureCard)playerTableCards.get(Integer.parseInt(chunks[2]))).setHealth(Integer.parseInt(chunks[3]));
        } else {
            ((BasicCreatureCard)enemyTableCards.get(Integer.parseInt(chunks[2]))).setHealth(Integer.parseInt(chunks[3]));
        }
        /*Platform.runLater(() -> {
            try {
                boardController.updateAll();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });*/
    }

    private void removeFromTable(String serverOutput) {
        String[] chunks = serverOutput.split(" ");
        if (Integer.parseInt(chunks[0].substring(1)) == player.getPlayerTurn()) {
            playerTableCards.remove(Integer.parseInt(chunks[2]));
        } else {
            enemyTableCards.remove(Integer.parseInt(chunks[2]));
        }
        /*Platform.runLater(() -> {
            try {
                boardController.updateAll();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });*/
    }

    private void changeConsumed(int index) {
        playerTableCards.get(index).setIsConsumed(!playerTableCards.get(index).getIsConsumed());
    }

    private void incrementGraveyard(String serverOutput) {
        if(Integer.parseInt(serverOutput.substring(1, 2)) == player.getPlayerTurn()) {
            ++playerGraveyard;
        } else {
            ++enemyGraveyard;
        }
        /*Platform.runLater(() -> {
            try {
                boardController.updateAll();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });*/
    }

    // TODO REMOVE GRAVEYARD INCREMENT AND MOVE IT CHANGE ON STRING RECEIVE FROM SERVER
    private void useSuccess(String serverOutput) {
        String[] chunks = serverOutput.split(" ");
        System.out.printf("You used %s\n", player.getHand().get(Integer.parseInt(chunks[2])).getName());
        player.getHand().remove(Integer.parseInt(chunks[2]));
        /*Platform.runLater(() -> {
            try {
                boardController.updateAll();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });*/
        //Game.getInstance().incrementPlayerGraveyard();

    }
    private void useFailure(String serverOutput){
        String[] chunks = serverOutput.split(" ");
        if(chunks[2].equals("NO_MANA")) {
            System.out.printf("Not enough mana to use %s\n", player.getHand().get(Integer.parseInt(chunks[1])).getName());
        } else if(chunks[3].equals("NO_ENEMY_CREATURES")) {
            System.out.printf("No enemy creature to attack with %s\n",player.getHand().get(Integer.parseInt(chunks[1])).getName());
        }
    }

    private void dealtCard(String serverOutput) {
        String[] chunks = serverOutput.split(" ");
        //int id = Integer.parseInt(chunks[1]);
        String[] innerChunks = chunks[1].split("_");

        if (innerChunks[0].startsWith("S")) {
            player.getHand().add(queryHandler.fetchSpecialAbilityCreatureCardId(Integer.parseInt(innerChunks[1])));
        }else if (innerChunks[0].startsWith("B")) {
            player.getHand().add(queryHandler.fetchCreatureCardId(Integer.parseInt(innerChunks[1])));
        }else if (innerChunks[0].startsWith("M")) {
            player.getHand().add(queryHandler.fetchMagicCardId(Integer.parseInt(innerChunks[1])));
        }
        /*
        if (queryHandler.fetchCheckCardType(id) == 0) {
            player.getHand().add(queryHandler.fetchCreatureCardId(id));
        } else if (queryHandler.fetchCheckCardType(id) == 1) {
            player.getHand().add(queryHandler.fetchSpecialAbilityCreatureCardId(id));
        }*/
        /*Platform.runLater(() -> {
            try {
                boardController.updateAll();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });*/
    }
    
    private void dealtCards(String serverOutput) {
        String cards = serverOutput.substring(15);
        String[] chunks = cards.split(", ");
        for(int i = 0; i < chunks.length; ++i) {
            String[] innerChunks = chunks[i].split("_");

            if (chunks[i].startsWith("S")) {
                player.getHand().add(queryHandler.fetchSpecialAbilityCreatureCardId(Integer.parseInt(innerChunks[1])));
            }else if (chunks[i].startsWith("B")) {
                player.getHand().add(queryHandler.fetchCreatureCardId(Integer.parseInt(innerChunks[1])));
            }else if (chunks[i].startsWith("M")) {
                player.getHand().add(queryHandler.fetchMagicCardId(Integer.parseInt(innerChunks[1])));
            }
            /*cardIndices[i] = Integer.parseInt(chunks[i]);
            if (queryHandler.fetchCheckCardType(cardIndices[i]) == 0) {
                player.getHand().add(queryHandler.fetchCreatureCardId(cardIndices[i]));
            } else if (queryHandler.fetchCheckCardType(cardIndices[i]) == 1) {
                player.getHand().add(queryHandler.fetchSpecialAbilityCreatureCardId(cardIndices[i]));
            }*/
        }
        for(int i = 0; i < player.getHand().size(); ++i) {
            System.out.println(player.getHand().get(i));
        }


            /*Platform.runLater(() -> {
            try {
                boardController.updateAll();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });*/


        /*try {
            gameMenu = new GameMenu();
            gameMenu.rootMenu.start();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void placeSuccess(String serverOutput){
        String[] chunks = serverOutput.split(" ");
        int index = Integer.parseInt(chunks[2]);
        if (player.getPlayerTurn() == Integer.parseInt(chunks[0].substring(1))) {
            if (player.getHand().get(index) instanceof BasicCreatureCard) {
                BasicCreatureCard card = (BasicCreatureCard)player.getHand().get(index);
                player.getHand().remove(index);
                Game.getInstance().getPlayerTableCards().add(card);
                player.setMana(Integer.parseInt(chunks[4]));
            } else if (player.getHand().get(index) instanceof SpecialAbilityCreatureCard) {
                SpecialAbilityCreatureCard card = (SpecialAbilityCreatureCard) player.getHand().get(index);
                player.getHand().remove(index);
                Game.getInstance().getPlayerTableCards().add(card);
                if(((SpecialAbilityCreatureCard)Game.getInstance().getPlayerTableCards().get(Game.getInstance().getPlayerTableCards().size() - 1)).getKeyword() == EKeyword.COOLDOWN) {
                    ((SpecialAbilityCreatureCard) Game.getInstance().getPlayerTableCards().get(Game.getInstance().getPlayerTableCards().size() - 1)).decrementAbilityValue();
                }
                player.setMana(Integer.parseInt(chunks[4]));
            }
            
            System.out.printf("P%s placed %s\n", turn,  Game.getInstance().getPlayerTableCards().get(Game.getInstance().getPlayerTableCards().size() - 1).getName());
        } else {
            if (queryHandler.fetchCheckCardType(index) == 0) {
                BasicCreatureCard card = queryHandler.fetchCreatureCardId(index);
                //Game.getInstance().enemyHand --;
                Game.getInstance().getEnemyTableCards().add(card);
                enemyMana = Integer.parseInt(chunks[4]);
            } else if (queryHandler.fetchCheckCardType(index) == 1) {
                SpecialAbilityCreatureCard card = queryHandler.fetchSpecialAbilityCreatureCardId(index);
                //Game.getInstance().enemyHand --;
                Game.getInstance().getEnemyTableCards().add(card);
                enemyMana = Integer.parseInt(chunks[4]);
            }

            System.out.printf("P%s placed %s\n", turn,  Game.getInstance().getEnemyTableCards().get(Game.getInstance().getEnemyTableCards().size() - 1).getName());
        }

            /*Platform.runLater(() -> {
            try {
                boardController.updateAll();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });*/
    }

    private void placeFailure(String serverOutput){
        String[] chunks = serverOutput.split(" ");
        if(chunks[3].equals("NO_MANA")){
            System.out.printf("Not enough mana to place %s\n", player.getHand().get(Integer.parseInt(chunks[2])));
        }else if(chunks[3].equals("NO_ROOM")) {
            System.out.printf("Not enough room on table to place %s\n", player.getHand().get(Integer.parseInt(chunks[2])));
        }
    }

    /** TODO */
    private void attackFailure(String serverOutput) {
        String[] chunks = serverOutput.split(" ");
        String[] innerChunks = chunks[2].split("_");

        if (chunks[2].equals("CREATURE_OUT_OF_BOUNDS")) {
            System.out.println("Chosen index is not a card");
        } else if (chunks[2].equals("CARDS_ON_TABLE")) {
            System.out.println("There are enemy creatures currently on table");
        } else if (chunks[2].equals("NO_ENEMY_CARDS_ON_TABLE")) {
            System.out.println("No enemy creature to attack");
        } else if (innerChunks[3].equals("CONSUMED")) {
            System.out.printf("Failure, %s is consumed",
                    Game.getInstance().getPlayerTableCards().get(Integer.parseInt(innerChunks[1])).getName());
        }
    }

    /** TODO */
    private void attackSuccess(String serverOutput) throws IOException {
        String[] chunks = serverOutput.split(" ");
        String[] innerChunks = chunks[2].split("_");

        //if (chunks[2].startsWith("P" + player.getPlayerTurn())) {
        //    player.setHealth(Integer.parseInt(chunks[4]));
        //    System.out.printf("Player %s took damage and has HP: %s\n", player.getName(), Integer.parseInt(chunks[4]));
        //} else
            if (innerChunks[1].startsWith("DEAD") && chunks[2].startsWith("P" + player.getPlayerTurn())) {
            //TODO Add loss to highscore (in player)
            System.out.println("You died, GAME OVER!");
            boardController.gameOver("You died, GAME OVER!");
            System.exit(0);
            } else if(innerChunks[1].startsWith("DEAD") && chunks[2].startsWith("P" + checkCombatTurn(player.getPlayerTurn()))) {
                //TODO Add win to highscore (in player)
                System.out.println("Enemy player died, YOU WON!");
                boardController.gameOver("Enemy player died, YOU WON!");
                System.exit(0);
            }
         //else if(chunks[2].startsWith("P" + checkCombatTurn(player.getPlayerTurn()))) {//Game.getInstance().checkCombatTurn())) {
            //enemyHealth = Integer.parseInt(chunks[4]);
            //System.out.printf("Enemy Player took damage and has HP: %s\n", Integer.parseInt(chunks[4]));
        //}
        else if (chunks[2].startsWith("CARD")) {
            String[] playerCard = chunks[2].split("_");
            String[] enemyCard = chunks[6].split("_");
            
            int playerHP = Integer.parseInt(chunks[4]);
            int enemyHP = Integer.parseInt(chunks[8]);
            int playerIndex = Integer.parseInt(playerCard[1]);
            int enemyIndex = Integer.parseInt(enemyCard[2]);
                System.out.println(serverOutput);
            
            
            //Player attack
            if(serverOutput.startsWith("P" + player.getPlayerTurn())) {
                ((BasicCreatureCard)playerTableCards.get(playerIndex)).setHealth(playerHP);//Integer.parseInt(playerCard[1]))).setHealth(Integer.parseInt(chunks[4]));
                System.out.printf("Your card %s took damage and have HP: %s",
                        playerTableCards.get(playerIndex).getName(),//Integer.parseInt(playerCard[1])).getName(),
                        ((BasicCreatureCard)playerTableCards.get(playerIndex)).getHealth());//Integer.parseInt(playerCard[1]))).getHealth());
                ((BasicCreatureCard)enemyTableCards.get(enemyIndex)).setHealth(enemyHP);
                System.out.println(((BasicCreatureCard) enemyTableCards.get(enemyIndex)).getHealth());
                System.out.println(((BasicCreatureCard)playerTableCards.get(playerIndex)).getHealth());
            } else if(serverOutput.startsWith("P" + checkCombatTurn(player.getPlayerTurn()))) {
                ((BasicCreatureCard)enemyTableCards.get(playerIndex)).setHealth(playerHP);//Integer.parseInt(enemyCard[2]))).setHealth(Integer.parseInt(chunks[8]));
                ((BasicCreatureCard)playerTableCards.get(enemyIndex)).setHealth(enemyHP);
                System.out.println(((BasicCreatureCard) playerTableCards.get(enemyIndex)).getHealth());
                System.out.println(((BasicCreatureCard)enemyTableCards.get(playerIndex)).getHealth());
                System.out.printf("Your card %s took damage and have HP: %s",
                        enemyTableCards.get(enemyIndex).getName(),//Integer.parseInt(enemyCard[2])).getName(),
                        ((BasicCreatureCard)enemyTableCards.get(enemyIndex)).getHealth());//Integer.parseInt(enemyCard[2]))).getHealth());
            }
            
            /*playerHP = Integer.parseInt(chunks[8]);
            enemyHP = Integer.parseInt(chunks[4]);
            playerIndex = Integer.parseInt(enemyCard[2]);
            enemyIndex = Integer.parseInt(playerCard[1]);
    
                //playerCard = chunks[6].split("_");
                //enemyCard = chunks[2].split("_");
            
            //Enemy attack
            if(enemyCard[0].startsWith("P" + checkCombatTurn(player.getPlayerTurn()))) {
                ((BasicCreatureCard)enemyTableCards.get(enemyIndex)).setHealth(enemyHP);//Integer.parseInt(enemyCard[1]))).setHealth(Integer.parseInt(chunks[4]));
                System.out.printf("Enemy card %s took damage and have HP: %s",
                        enemyTableCards.get(enemyIndex).getName(),//Integer.parseInt(enemyCard[1])).getName(),
                        ((BasicCreatureCard)enemyTableCards.get(enemyIndex)).getHealth());//Integer.parseInt(enemyCard[1]))).getHealth());
            } else if (playerCard[0].startsWith("P" + player.getPlayerTurn())) {
                ((BasicCreatureCard)playerTableCards.get(playerIndex)).setHealth(playerHP);//Integer.parseInt(playerCard[2]))).setHealth(Integer.parseInt(chunks[8]));
                System.out.printf("Enemy card %s took damage and have HP: %s",
                        playerTableCards.get(playerIndex).getName(),//Integer.parseInt(playerCard[2])).getName(),
                        ((BasicCreatureCard)playerTableCards.get(playerIndex)).getHealth());//Integer.parseInt(playerCard[2]))).getHealth());
            }*/
            
            
        } else if (chunks[2].startsWith("P" + player.getPlayerTurn())) {
            player.setHealth(Integer.parseInt(chunks[4]));
            System.out.printf("Player %s took damage and has HP: %s\n", player.getName(), Integer.parseInt(chunks[4]));
        } else if(chunks[2].startsWith("P" + checkCombatTurn(player.getPlayerTurn()))) {//Game.getInstance().checkCombatTurn())) {
            enemyHealth = Integer.parseInt(chunks[4]);
            System.out.printf("Enemy Player took damage and has HP: %s\n", Integer.parseInt(chunks[4]));
        }

        /*Platform.runLater(() -> {
            try {
                boardController.updateAll();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });*/
    }

    private void login(String serverOutput) {
        String [] chunks = serverOutput.split (" ");
        String start = "";
        if(chunks[1].equals(loginName)) {//chunks[1].equals("john@hotmail.se")) {
            int playerId = Integer.parseInt(chunks[2].substring(3));
            int playerTurn = Integer.parseInt(chunks[3].substring(7));
            String playerName = chunks[4].substring(5);
            player = new Player(playerId, playerName, playerTurn);
            System.out.printf("%s %s %s", player.getId(), player.getName(), player.getPlayerTurn());
            if(chunks.length > 5) {
                sendStart();
            }
        } else {
            enemyName = chunks[4].substring(5);
            if(chunks.length > 5) {
                sendStart();
            }
        }
    }
    
    private void sendStart() {
        try {
            try {
                Platform.runLater( () -> {
                    try {
                        MenuController.renderGame();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            NetworkClient.getInstance().sendMessageToServer("STARTED");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void startGame() {
        try {
            NetworkClient.getInstance().sendMessageToServer("START_CARDS");
            NetworkClient.getInstance().sendMessageToServer("END_TURN");
            //gameMenu.rootMenu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void endTurn(String serverOutput) throws IOException {
        String[] chunks = serverOutput.split(" ");
        turn = Integer.parseInt(chunks[3]);
        round = Integer.parseInt(chunks[1]);
        
        for(BasicCard card: playerTableCards) {
            if(card instanceof SpecialAbilityCreatureCard && ((SpecialAbilityCreatureCard) card).getKeyword() == EKeyword.COOLDOWN && ((SpecialAbilityCreatureCard) card).getAbilityValue() > 0) {
                System.out.println(((SpecialAbilityCreatureCard) card).getAbilityValue());
                ((SpecialAbilityCreatureCard)card).decrementAbilityValue();
            } else if(card instanceof  SpecialAbilityCreatureCard && ((SpecialAbilityCreatureCard) card).getKeyword() == EKeyword.COOLDOWN && ((SpecialAbilityCreatureCard) card).getAbilityValue() == 0) {
                card.setIsConsumed(false);
            } else {
                card.setIsConsumed(false);
            }
        }
        
        /*for (BasicCard card : playerTableCards) {
            card.setIsConsumed(false);
        }*/
        if(round > 0){
            NetworkClient.getInstance().sendMessageToServer(String.format("P%s_DRAW", player.getPlayerTurn()));
        }
        player.setMana(round);
        enemyMana = round;
        if (turn == player.getPlayerTurn()) {
            System.out.printf("---------- %s's TURN ----------\n", player.getName());
        }

        /*Platform.runLater(() -> {
            try {
                boardController.updateAll();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });*/
    }
}
