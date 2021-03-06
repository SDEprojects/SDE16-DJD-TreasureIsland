package com.treasureisland.scene;

import com.treasureisland.TreasureIslandGameplay;
import com.treasureisland.items.Item;
import com.treasureisland.player.PirateFightSequence;
import com.treasureisland.player.Player;

import java.util.Map;
import java.util.Scanner;

public class CrimsonBeachBar extends Scene {

  public CrimsonBeachBar(String sceneName) {
    super(sceneName);
  }


  @Override
  public void talkToNPC(Player player) throws InterruptedException {
    storylineProgression("TI.txt", "JStart", "JEnd");
    Map<String, Boolean> availablePirates = TreasureIslandGameplay.getInstance().getAvailablePirates();
    if(availablePirates.get("Crimson Beach Bar")) {
      Thread.sleep(1000);
      System.out.println("\n");
      PirateFightSequence.getInstance().PlayerAndPirateFightSequence(player);
    }
  }

  @Override
  public void lookAroundLocation(Player player) throws InterruptedException {
    storylineProgression("TI.txt",  "CBStart", "CBEnd");
    storylineProgression("TI.txt",  "PTStart", "PTEnd");

    player.setPlayerHealth(player.getPlayerHealth() - 20);
    System.out.println("\n");
    player.iterateThroughClues();
  }

  @Override
  public void vendor(Player player) {

  }

}