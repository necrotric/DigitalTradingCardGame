package server;

import card.BasicCard;
import card.BasicCreatureCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import player.Player;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void receiveCommand() {
    }

    Server server = new Server();

    @Test
    void rollDice() {
        for(int i = 0; i <= 10000; ++i) {
            assertThat(server.rollDice(1,6)).isBetween(1, 6);
        }
    }

    @Test
    void dealCards() {
    }

    @Test
    void dealCard() {
    }

    @Test
    void sendCard() {
    }

    @Test
    void placeCard() {
    }

    @Test
    void attackEnemyPlayer() {
    }

    @Test
    void attackEnemyCreature() {
    }

    @Test
    void healPlayer() {
    }

    @Test
    void healCreature() {
    }

    @Test
    void checkPlayerAlive() {
        Player p = new Player(1, "Gary",3, null);
        assertTrue(server.checkPlayerAlive(p));
        p.setHealth(0);
        assertFalse(server.checkPlayerAlive(p));
        p.setHealth(5);
        assertTrue(server.checkPlayerAlive(p));
    }

    @Test
    void checkCreatureAlive() {
        BasicCreatureCard creature = new BasicCreatureCard(2, "abc", "abc", "abc", 5, 2, 3);
        assertTrue(server.checkCreatureAlive(creature));
        creature.setHealth(0);
        assertFalse(server.checkCreatureAlive(creature));
        creature.setHealth(8);
        assertTrue(server.checkCreatureAlive(creature));
        BasicCreatureCard creature2 = new BasicCreatureCard(3, "abc", "abc", "abc", 0, 2, 1);
        assertFalse(server.checkCreatureAlive(creature2));
    }

    @Test
    void moveToGraveyard() {
    }

    @Test
    void endTurn() {
    }

    @Test
    void quitGame() {
    }
}