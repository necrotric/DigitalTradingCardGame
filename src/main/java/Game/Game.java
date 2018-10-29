package Game;

import card.BasicCard;
import player.Player;
import repository.DummyDB;

import java.util.List;

public class Game {
    DummyDB dummy = new DummyDB();
    private List<BasicCard> playerATableCards = dummy.database; // Remove A when multiplayer
    private List<BasicCard> playerBTableCards = dummy.database; // Remove when multiplayer
    
    private Player playerA; // Remove A when multiplayer
    private Player playerB; // Remove when multiplayer
    
    private int playerAGraveyard = 5; // Remove A when multiplayer
    private int playerBGraveyard = 2; // Remove when multiplayer
    
    private int playerADeck = 8; // Remove A when multiplayer
    private int playerBDeck = 10; // Remove when multiplayer
    
    public List<BasicCard> getPlayerATableCards() {
        return playerATableCards;
    }

    public List<BasicCard> getPlayerBTableCards() {
        return playerBTableCards;
    }
    
    public Player getPlayerA() {
        return playerA;
    }
    
    public Player getPlayerB() {
        return playerB;
    }
    
    public int getPlayerAGraveyard() {
        return playerAGraveyard;
    }
    
    public int getPlayerBGraveyard() {
        return playerBGraveyard;
    }
    
    public int getPlayerADeck() {
        return playerADeck;
    }
    
    public int getPlayerBDeck() {
        return playerBDeck;
    }
    //TODO add when changing to multiplayer
    /*
    private List<BasicCard> EnemyTableCards;
    private int enemyGraveyard;
    private int enemyHand;
    privaet int enemyDeck
    private int enemyHealth;
    private int enemyMana;
    */
    
}