package com.treasureisland.ship;

import com.treasureisland.player.Player;
import java.util.Random;

public class PlayerShip implements java.io.Serializable{
  private static final PlayerShip playerShip = new PlayerShip();
  public final String playerShipName = "The Black Pearl";
  public Player player = Player.getInstance();
  public Integer playerShipHealth = 100;
  public Integer playerAttackStrength = new Random().nextInt(100);

  private PlayerShip() {}

  public static PlayerShip getInstance() {
    return playerShip;
  }

  public String getPlayerShipName() {
    return playerShipName;
  }

  public Integer getPlayerShipHealth() {
    return playerShipHealth;
  }

  public void setPlayerShipHealth(Integer playerShipHealth) {
    this.playerShipHealth = playerShipHealth;
  }

  public void attackEnemyShip(EnemyShip enemyShip) {
    System.out.println(
        playerShipName
            + " attacked "
            + enemyShip.getEnemyShipName()
            + " for "
            + playerAttackStrength
            + " damage.");
    enemyShip.setEnemyShipHealth(enemyShip.getEnemyShipHealth() - playerAttackStrength);

    if (enemyShip.getEnemyShipHealth() <= 0) {
      System.out.println("You defeated " + enemyShip.getEnemyShipName());
    }
  }

  public void defendPlayerShip(EnemyShip enemyShip) {
    int result = enemyShip.enemyAttackStrength - getPlayerShipHealth();
    if (result <= 0) {
      System.out.println(enemyShip.getEnemyShipName() + " did no damage.");
    } else {
      setPlayerShipHealth(getPlayerShipHealth() - result);
      System.out.println(enemyShip.getEnemyShipName() + " did " + result + " damage");
    }
  }
}
