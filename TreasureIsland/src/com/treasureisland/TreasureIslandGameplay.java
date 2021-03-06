package com.treasureisland;

import com.treasureisland.island.IslaCruces;
import com.treasureisland.island.IslaDeMuerta;
import com.treasureisland.island.Island;
import com.treasureisland.island.PortRoyal;
import com.treasureisland.island.RumRunnerIsle;
import com.treasureisland.items.Item;
import com.treasureisland.player.Color;
import com.treasureisland.player.Player;
import com.treasureisland.ship.ShipBattleSequence;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TreasureIslandGameplay implements Serializable {

  public static TreasureIslandGameplay treasureIslandGameplay;
  private final Island rumRunnerIsle;
  private final Island portRoyal;
  private final Island islaCruces;
  private final Island islaDeMuerta;
  private final Player player;
  private final ShipBattleSequence shipBattleSequence =
    new ShipBattleSequence();
  private final transient Scanner scanner = OnlyOneScanner.getTheOneScanner();
  private final Map<String, Boolean> availablePirates = new HashMap<>();
  private Island currentIsland;
  private String input;

  /*
   * =============================================
   * ============= Constructors ==================
   * =============================================
   */

  public TreasureIslandGameplay() {
    player = new Player();
    rumRunnerIsle = new RumRunnerIsle();
    portRoyal = new PortRoyal();
    islaCruces = new IslaCruces();
    islaDeMuerta = new IslaDeMuerta();

    islaCruces.connectSouth(rumRunnerIsle);
    // islaCruces.connectEast();

    portRoyal.connectSouth(rumRunnerIsle);
    portRoyal.connectEast(islaDeMuerta);
    islaDeMuerta.connectSouth(islaCruces);
    rumRunnerIsle.connectEast(islaCruces);

    availablePirates.put("Crimson Beach Bar", true);
  }

  public static TreasureIslandGameplay getInstance() {
    if (treasureIslandGameplay == null) {
      treasureIslandGameplay = new TreasureIslandGameplay();
    }
    return treasureIslandGameplay;
  }

  /*
   * =============================================
   * =========== Business Methods ================
   * =============================================
   */

  public void start() {
    String userInput = "";
    try {
      welcomeToTreasureIsland();
      islandSetUp(rumRunnerIsle);
      customGameplayOptions();

      currentIsland.enter(player);

      while (true) {
        String islandName = currentIsland.getIslandName();

        String whatIslandToGo =
          ("Port Royal".equals(islandName)) ? toRumRunner() : toPortRoyal();

        System.out.println(whatIslandToGo);

        userInput = scanner.nextLine().trim().toLowerCase();

        if ("y".equals(userInput) || "yes".equals(userInput)) {
          String directionToGo = ("Port Royal".equals(islandName)) ? "s" : "n";

          islandSetUp(currentIsland.changeIsland(directionToGo));
          currentIsland.leavingIslandShipPrint();

          shipBattleSequence.shipBattleAfterLeavingIsland(player, scanner);
        }
        else {
          displayStayMessage();
        }
        player.setCurrentIsland(currentIsland);
        currentIsland.enter(player);
      }

    }
    catch (InterruptedException e) {
      System.out.println("Oops! Please try again...\n");
      System.out.println(currentIsland.getDirectionOptions());
      e.printStackTrace();
    }
  }

  /**
   * Sets currentIsland to Rum Runner and
   */
  public void islandSetUp(Island anIsland) {
    currentIsland = anIsland;
    player.setHasIslandItem(false);
  }

  public void customGameplayOptions() throws InterruptedException {
    File gameState = new File(
      System.getProperty("user.dir") + "/TreasureIsland.ser");
    if (gameState.exists()) {
      System.out.println(
        "Would you like to\n "
          + "-Type \"L\": Load Existing Game?\n "
          + "-Type \"N\": Play New Game?\n ");
    } else {
      System.out.println("Would you like to Play New Game<N>?");
    }

    input = scanner.nextLine().trim().toLowerCase();
    switch (input) {
      case "l":
        treasureIslandGameplay = SaveLoadGame.loadGame();
        if (treasureIslandGameplay.player.getCurrentScene() == null) {
          treasureIslandGameplay.player
            .setCurrentScene(currentIsland.getCurrentScene());
        }
        if (treasureIslandGameplay.player.getPlayerName() == null) {
          treasureIslandGameplay.player.setPlayerName("Captain Jack");
        }
        System.out.println(
          "\nWelcome, " + treasureIslandGameplay.player.getPlayerName()
            + "\n \n");
        treasureIslandGameplay.player.playerInfoConsoleOutput();

        break;
      case "n":
        if (gameState.exists()) {
          gameState.delete();
        }
        chosePlayerName();
        break;
      case "s":
        if (gameState.exists()) {
          gameState.delete();
        }
        player.setPlayerName("Test Player");
        testIslandSelector();
        break;
      default:
        customGameplayOptions();
        break;
    }
  }

  /**
   * Player chooses name and is stored into playerName variable calls first
   * storyline txt file
   */
  public void chosePlayerName() throws InterruptedException {
    // welcomeToTreasureIsland();
    System.out.println("\nPlease enter your name: ");
    String input = scanner.nextLine();
    player.setPlayerName(input);

    String text =
      Color.ANSI_BLUE.getValue()
        + "\nAhoy "
        + Color.ANSI_GREEN.getValue()
        + player.getPlayerName()
        + Color.ANSI_BLUE.getValue()
        + "! "
        + " "
        + "Welcome to Treasure Island....well you are not there yet.  You "
        + "need to find it first.\n"
        + "\n"
        + "I know you said you put this life behind you, but rumor has it "
        + Color.ANSI_RED.getValue()
        + "Black Beard "
        + Color.ANSI_BLUE.getValue()
        + "has a bounty on your head!!\n"
        + "Don't worry friend, I have a way to satisfy his bounty.....if you "
        + "can survive his gang!!\n"
        + "\n"
        + "All you need to do is find the diamond.\n"
        + "\n"
        + "Playing this game is easy, talk to people around the Islands, Sail"
        + " between the Islands and walk between\n"
        + "the places.\n"
        + "\n"
        + "The clues will lead you on your path.  If you ever get "
        + "disorientated, type Map and it will clear your way.\n"
        + "\n"
        + "Your journey starts on Rum Runner Isle.  Good luck!"
        + Color.ANSI_RESET.getValue()
        + "\n"
        + "\n";
    int i;
    for (i = 0; i < text.length(); i++) {
      System.out.printf("%c", text.charAt(i));
      try {
        Thread.sleep(000); // 0.5s pause between characters
      }
      catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    }
  }


  public void rumRunnerIsle() {
    try {
      player.processMovement("rumRunnerisle");
      System.out.println("Leaving Rum Runners Isle \n \n");
      Thread.sleep(0);
      player.setHasIslandItem(false);
      shipBattleSequence.shipBattleAfterLeavingIsland(player, scanner);
      portRoyal();
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void portRoyal() throws InterruptedException {
    System.out.println("You made it to Port Royal");
    player.processMovement("portRoyal");
  }

  public void islaCruces() throws InterruptedException {
    System.out.println("At Isla Cruces");
    player.processMovement("islaCruces");
    System.out.println("Leaving Isla Cruces \n \n");
    Thread.sleep(5000);
    player.setHasIslandItem(false);
    shipBattleSequence.shipBattleAfterLeavingIsland(player, scanner);

    islaDeMuerta();
  }

  public void islaDeMuerta() throws InterruptedException {
    System.out.println("At Isla de Muerta");
    player.processMovement("islademuerta");
    System.out.println("Leaving Isla De Muerta \n \n");
    Thread.sleep(5000);
  }


  public void testIslandSelector() throws InterruptedException {
    System.out.println(
      "Which island would you like to play?\n"
        + "1) Rum Runner Isle \n"
        + "2) Port Royal \n"
        + "3) Isla Cruces \n"
        + "4) Isla de Muerta \n"
        + "5) Back to main");
    input = scanner.nextLine();
    if ("1".equals(input)) {
      rumRunnerIsle();
    }
    if ("2".equals(input)) {
      portRoyal();
    }
    if ("3".equals(input)) {
      islaCruces();
    }
    if ("4".equals(input)) {
      islaDeMuerta();
    }
    if ("5".equals(input)) {
      customGameplayOptions();
    }
    else {
      testIslandSelector();
    }
  }

  public void welcomeToTreasureIsland() {

    System.out.println(
      "\n"
        + Color.ANSI_GREEN.getValue()
        + "    $$$$$$$$                                                      "
        + "                         $$$$$$            $$                     "
        + "       $$\n"
        + "       $$                                                         "
        + "                           $$              $$                     "
        + "       $$\n"
        + "       $$      $$$$$$    $$$$$$    $$$$$$    $$$$$$$   $$    $$   "
        + "$$$$$$    $$$$$$           $$     $$$$$$$  $$   $$$$$$   $$$$$$$  "
        + "  $$$$$$$\n"
        + "       $$     $$    $$  $$    $$        $$  $$         $$    $$  "
        + "$$    $$  $$    $$          $$    $$        $$        $$  $$    $$"
        + "  $$    $$\n"
        + "       $$     $$        $$$$$$$$   $$$$$$$   $$$$$$    $$    $$  "
        + "$$        $$$$$$$$          $$     $$$$$$   $$   $$$$$$$  $$    $$"
        + "  $$    $$\n"
        + "       $$     $$        $$        $$    $$         $$  $$    $$  "
        + "$$        $$                $$          $$  $$  $$    $$  $$    $$"
        + "  $$    $$\n"
        + "       $$     $$         $$$$$$$   $$$$$$$  $$$$$$$     $$$$$$   "
        + "$$         $$$$$$$        $$$$$$  $$$$$$$   $$   $$$$$$$  $$    $$"
        + "   $$$$$$$\n"
        + "                                                                  "
        + "                                                                  "
        + "         \n"
        + "                                                                  "
        + "                                                                  "
        + "         \n"
        + "                                                                  "
        + "                                                                  "
        + "         \n"
        + Color.ANSI_RESET.getValue());
  }

  public String toRumRunner() {
    return "You see Rum Runner Isle to the West. Would you like to visit "
      + "it?\n-Type: Y\n-Type: N";
  }

  public String toPortRoyal() {
    return "You see Port Royal to the East. Would you like to visit "
      + "it?\n-Type: Y\n-Type: N";
  }

  private void displayStayMessage() {
    System.out.printf("Very well then. You are staying on %s.\n",
      currentIsland.getIslandName());
  }

  public Map<String, Boolean> getAvailablePirates() {
    return availablePirates;
  }

  public void setAvailablePirates(String key) {
    availablePirates.replace(key, false);
  }

  public void displayTreasureIslandStory() {
    System.out.println(
      "What! I have been to this island before, there are people inhabiting "
        + "this island!!\n"
        + "          How can treasure be on this island? It is supposed to be"
        + " the biggest treasure in the world.\n"
        + "          This is confusing, I have to figure out a way to make "
        + "some sense of this situation.\n"
        + "          Or else all this situation will be for nothing.\n"
        + "\n"
        + "\n"
        + "\n"
        + "          After revisiting the clues, I think I figured it out.\n"
        + "          I need to get to this hidden monastery and I will find "
        + "more info there.\n"
        + "\n"
        + "          Talking with the monks there, I found out that they had "
        + "a very sacred jewel stolen from them years back.\n"
        + "          Now that sounds interesting, the description they "
        + "provided matched with the jewel I posses.\n"
        + "\n"
        + "          Should I give this jewel back to the monastery?\n"
        + "\n"
        + "          I gave the jewel to monastery and in return they "
        + "provided me with a medium size box which had . . .\n"
        + "              Three keyholes in it!\n"
        + "              I took the box with me to open it privately.\n"
        + "              Upon opening it I found . . .\n"
        + "              Jewels, coins and the most treasured "
        + Color.ANSI_BOLD.getValue()
        + Color.ANSI_BLUE.getValue()
        + "50 carat Diamond"
        + Color.ANSI_RESET.getValue()
        + "!");
  }

  @Override
  public String toString() {
    return "TreasureIslandGameplay{"
      + "treasureIslandGameplay="
      + treasureIslandGameplay
      + ", rumRunnerIsle="
      + rumRunnerIsle
      + ", portRoyal="
      + portRoyal
      + ", islaCruces="
      + islaCruces
      + ", islaDeMuerta="
      + islaDeMuerta
      + ", player="
      + player
      + ", shipBattleSequence="
      + shipBattleSequence
      + ", scanner="
      + scanner
      + ", availablePirates="
      + availablePirates
      + ", currentIsland="
      + currentIsland
      + ", input='"
      + input
      + '\''
      + ", toRumRunner='"
      + toRumRunner()
      + '\''
      + ", toPortRoyal='"
      + toPortRoyal()
      + '\''
      + '}';
  }
}
