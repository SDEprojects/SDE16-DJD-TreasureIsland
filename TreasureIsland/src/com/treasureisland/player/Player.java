package com.treasureisland.player;

import com.treasureisland.OnlyOneScanner;
import com.treasureisland.SaveLoadGame;
import com.treasureisland.TreasureIslandGameplay;
import com.treasureisland.island.Island;
import com.treasureisland.island.IsleFactory;
import com.treasureisland.items.Item;
import com.treasureisland.items.Vendor;
import com.treasureisland.map.MainMap;
import com.treasureisland.scene.Scene;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Player implements Serializable {
  private static Player player;
  private final Vendor vendor;
  private final List<Item> vendorItems;
  public List<Item> playerInventory;

  public ArrayList<String> playerClues = new ArrayList<>();
  public ArrayList<String> playerTreasures = new ArrayList<>();
  public String[] clues = {
    "Go " + Color.ANSI_YELLOW.getValue() + "East" + Color.ANSI_RESET.getValue(),
    "Go " + Color.ANSI_PURPLE.getValue() + "South" + Color.ANSI_RESET.getValue(),
    "Go " + Color.ANSI_GREEN.getValue() + "West" + Color.ANSI_RESET.getValue(),
    "Go " + Color.ANSI_BLUE.getValue() + "North" + Color.ANSI_RESET.getValue()
  };

  private String playerName;

  private Scene currentScene;
  private Island currentIsland;

  private Boolean hasIslandItem = false;
  private Integer playerCoins = 10;
  private Integer playerHealth = 100;
  private Integer playerAttackStrength = new Random().nextInt(75);
  private SaveLoadGame saveLoadGame;

  private String input;
  private transient Scanner scanner = OnlyOneScanner.getTheOneScanner();

  /*
   * =============================================
   * ============= Constructors ==================
   * =============================================
   */
  public Player() {
    vendor = new Vendor();
    vendorItems = vendor.getVendorItems();
    playerInventory = new ArrayList<Item>();
  }

  public static Player getInstance() {
    if (player == null) {
      player = new Player();
    }
    return player;
  }

  /*
   * =============================================
   * =========== Business Methods ================
   * =============================================
   */

  // Method to display collected treasures by player.
  public void iterateThroughPlayerTreasureRewards() {
    if (playerTreasures.size() == 0) {
      System.out.println(
          "\nYou didn't collect any treasures till now. Explore the islands, get the clues and collect some!!");
    } else {
      System.out.println("\nBelow are the Treasures that you have collected so far,");
      String leftAlignFormat = "| %-15s |%n";

      System.out.format("+-----------------+%n");
      System.out.format("| Reward          |%n");
      System.out.format("+-----------------+%n");

      for (String reward : playerTreasures) {
        System.out.printf(leftAlignFormat, reward);
      }
      System.out.format("+-----------------+%n\n");
    }
  }


  /**
   * Adds or substracts coins from the Player's coins
   *
   * @param coins -> Integer
   */
  public void coinManager(Integer coins) {
    if (coins.equals(0)) {
      System.out.println("Nothing was found!");
    }
    if (coins > 0) {
      playerCoins += coins;
      System.out.println(
          "You found " + coins + " coins. You now have a total of " + getPlayerCoins() + " coins");
    }
    if (coins < 0) {
      playerCoins -= coins;
      System.out.println(
          "You spent " + coins + " gold. You now have a total of " + getPlayerCoins() + " coins.");
    }
  }

  // Player visits the Vendor All Items from the Vendor are printed out
  public void playerVisitsVendor() {
    vendor.vendorIntroduction();

    input = scanner.nextLine();
    vendor.purchaseItems(scanner, this, input);
  }

  /**
   * This looks like this is generates the number of coins found when the player "investigates"
   * things
   */
  public void playerCoinGenerator() {
    Random rand = new Random();
    int upperBoundofCoins = 51;
    int coins = rand.nextInt(upperBoundofCoins);
    if (coins <= 0) {
      System.out.println("Nothing was found ");
      return;
    }
    coinManager(coins);
  }

  // Checks Player Health and prints out death art if player is dead.
  public void playerHealthCheck() throws InterruptedException {
    if (this.getPlayerHealth() <= 0) {
      playerDeathArt();
      playerDeathOptions();
    }
  }

  // When player enters into game - start the process
  public void processMovement(String islandDestination) {
    String directionOptions =
        "Where would you like to go?\n "
            + "-Type \"N\": North\n "
            + "-Type \"S\": South\n "
            + "-Type \"W\": West\n "
            + "-Type \"E\": East\n "
            + "-Type \"Save\": Save Game\n "
            + "-Type \"Chart\": Game Chart\n "
            + "-Type \"Map\": Island Map\n";

    try {
      while (!hasIslandItem) {
        System.out.println(directionOptions);
        String direction = scanner.nextLine().trim();
        MainMap main = new MainMap();
        switch (direction.toLowerCase()) {
          case "save":
            SaveLoadGame.saveGame();
            System.out.println("We saved your game state!!");
            System.out.println(
                "But You cannot run forever my friend."
                    + Color.ANSI_RED.getValue()
                    + "Black Beard "
                    + Color.ANSI_RESET.getValue()
                    + "will find you!!!");
            System.out.println("Sleep well for it may be your last night.");
            System.out.println("Goodbye for now.");
            System.exit(0);
            break;
          case "chart":
            main.mainMap();
            break;
          case "map":
            if ("rumRunnerisle".equalsIgnoreCase(islandDestination)) {
              main.rumRunner();
            } else if ("portRoyal".equalsIgnoreCase(islandDestination)) {
              main.portRoyal();
            } else if ("islaCruces".equalsIgnoreCase(islandDestination)) {
              main.islaCruces();
            } else if ("islademuerta".equalsIgnoreCase(islandDestination)) {
              main.islaDeMuerta();
            } else {
              main.mainMap();
            }
            break;
          case "north":
          case "n":
          case "south":
          case "s":
          case "east":
          case "e":
          case "west":
          case "w":
            this.currentScene = IsleFactory.islandLocationFactory(direction, islandDestination);
            System.out.println("\nYou are now at the " + this.currentScene.getName());
            Thread.sleep(500);
            playerInfoConsoleOutput();
            Thread.sleep(1000);
            playerInteractionOptions(direction,islandDestination);
            break;
          default:
            System.out.println("Invalid input, please try again. ");
            processMovement(islandDestination);
            break;
        }
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  // Method for player interaction options
  public void playerInteractionOptions(String direction, String islandDestination)
      throws IOException, InterruptedException {
    String input = "";

    String interactionOptions =
        "\nWhat would you like to do?\n "
            + " -Type \"T\": Talk\n "
            + " -Type \"L\": Look Around\n "
            + " -Type \"R\": See Treasure Rewards\n "
            + " -Type \"M\": Look at the Map\n "
            + " -Type \"INV\": Inventory\n "
            + " -Type \"G\": Grab Item\n "
            + " -Type \"E\": Exit This Location\n ";

    String interactOptionsWithVendor =
        "\nWhat would you like to do? \n "
            + " -Type \"T\": Talk\n "
            + " -Type \"L\": Look Around\n "
            + " -Type \"R\": See Treasure Rewards\n "
            + " -Type \"M\": Look at the Map\n "
            + " -Type \"V\": Visit the Vendor\n "
            + " -Type \"INV\": Inventory\n "
            + " -Type \"G\": Grab Item\n "
            + " -Type \"E\": Exit This Location\n ";

    playerHealthCheck();

    if ("w".equalsIgnoreCase(direction)) {
      System.out.println(interactOptionsWithVendor);
    } else {
      System.out.println(interactionOptions);
    }

    input = scanner.nextLine().trim();

    switch (input.toLowerCase()) {
      case "talk":
      case "t":
        playerInfoConsoleOutput();
        currentScene.talkToNPC(this);
        playerInteractionOptions(direction, islandDestination);
        break;
      case "look":
      case "l":
        playerInfoConsoleOutput();
        currentScene.lookAroundLocation(this);
        playerInteractionOptions(direction, islandDestination);
        break;
      case "reward":
      case "r":
        playerInfoConsoleOutput();
        iterateThroughPlayerTreasureRewards();
        playerInteractionOptions(direction, islandDestination);
        break;
      case "map":
      case "m":
        MainMap main = new MainMap();
        if ("rumRunnerisle".equalsIgnoreCase(islandDestination)) {
          main.rumRunner();
        } else if ("portRoyal".equalsIgnoreCase(islandDestination)) {
          main.portRoyal();
        } else if ("islaCruces".equalsIgnoreCase(islandDestination)) {
          main.islaCruces();
        } else if ("islademuerta".equalsIgnoreCase(islandDestination)) {
          main.islaDeMuerta();
        } else {
          main.mainMap();
        }
        break;
      case "vendor":
      case "v":
        if ("w".equalsIgnoreCase(direction)) {
          playerInfoConsoleOutput();
          currentScene.vendor(this);
        } else {
          System.out.println("Invalid input, please try again.");
        }
        playerInteractionOptions(direction, islandDestination);
        break;
      case "inventory":
      case "inv":
        printInventoryItems();
        playerInteractionOptions(direction, islandDestination);
        break;
      case "grab":
      case "g":
        grabItemFromInventory();
        playerInteractionOptions(direction, islandDestination);
        break;
      case "exit":
      case "e":
        break;
      default:
        System.out.println("Invalid input, please try again.");
        playerInteractionOptions(input, islandDestination);
        break;
    }
  }

  // Prints all Inventory Items in a Table */
  public void printInventoryItems() {
    String leftAlignFormat = "| %-15s | %-4d   |%n";

    System.out.format("+-----------------+--------+%n");
    System.out.format("| Name            | Health |%n");
    System.out.format("+-----------------+--------+%n");

    for (Item item : playerInventory) {
      System.out.printf(leftAlignFormat, item.getItemName(), item.getHealthValue());
    }
    System.out.format("+-----------------+--------+%n\n");
  }

  // Method for grab item from Inventory and it will increase player's health
  public void grabItemFromInventory() {
    if (playerInventory.size() == 0) {
      System.out.println("Sorry!! There is nothing in your inventory to grab.");
    } else {
      String instructions =
          "You have below available things you can have"
              + ".\nSimply type the name of the item you want to grab and press \"enter\".\n";
      System.out.println(instructions);
      printInventoryItems();
      input = scanner.nextLine();
      Item findItem = Item.findByName(playerInventory, input.trim().toLowerCase());
      if (findItem != null) {
        if (findItem.getHealthValue() == 0) {
          System.out.println(
              "This is one of the clues that you got while visiting different locations.");
        } else {
          this.setPlayerHealth(this.getPlayerHealth() + findItem.getHealthValue());
          System.out.println(
              "You grabbed "
                  + findItem.getItemName()
                  + ". Your health is now "
                  + this.getPlayerHealth()
                  + ".");
          playerInventory.remove(findItem);
        }
      } else {
        System.out.println("You can't grab that item. The item is not in your inventory.");
      }
    }
  }

  // Player and Pirate Fight Sequence - START
  public void attackPirate(Pirate pirate) {
    System.out.println(
        "\n"
            + Color.ANSI_GREEN.getValue()
            + getPlayerName()
            + Color.ANSI_RESET.getValue()
            + " attacked "
            + Color.ANSI_RED.getValue()
            + pirate.getPirateName()
            + Color.ANSI_RESET.getValue()
            + " for "
            + playerAttackStrength
            + " damage.");
    pirate.setPirateHealth(pirate.getPirateHealth() - playerAttackStrength);
  }

  public void defendPlayer(Pirate pirate) {
    int result = pirate.pirateAttackStrength - getPlayerHealth();
    if (result <= 0) {
      System.out.println(
          Color.ANSI_RED.getValue()
              + pirate.getPirateName()
              + Color.ANSI_RESET.getValue()
              + " did no damage.");
    } else {
      setPlayerHealth(getPlayerHealth() - result);
      System.out.println(
          Color.ANSI_RED.getValue()
              + pirate.getPirateName()
              + Color.ANSI_RESET.getValue()
              + " did "
              + result
              + " damage");
    }
  }
  // Player and Pirate Fight Sequence - END

  // Player's Death Art
  public void playerDeathArt() {
    System.out.println(
        "\n" + Color.ANSI_RED.getValue() + getCrossBones() + Color.ANSI_RESET.getValue());

    System.out.println(
        Color.ANSI_RED.getValue()
            + Color.ANSI_BOLD.getValue()
            + "You dead!"
            + Color.ANSI_RESET.getValue());
  }

  // Method to give options after player died
  public void playerDeathOptions() throws InterruptedException {
    System.out.println("\nWould you like to play again?\n -Type \"Y\": Yes\n -Type \"N\": No");

    input = scanner.nextLine().trim().toLowerCase();

    if ("y".equals(input) || "yes".equals(input)) {
      new TreasureIslandGameplay().chosePlayerName();
    } else if ("n".equals(input) || "no".equals(input)) {
      System.out.println("Thank you for playing!!");
      System.exit(0);

    } else {
      System.out.println("Invalid Input, Try Again!!");
      playerDeathOptions();
    }
  }

  // Display player info to Console
  public void playerInfoConsoleOutput() {
    System.out.println(
        "\n"
            + "___________________________________________________________"
            + "\n"
            + "     "
            + Color.ANSI_PURPLE.getValue()
            + "Health"
            + Color.ANSI_RESET.getValue()
            + ": "
            + this.getPlayerHealth()
            + "\n"
            + "     "
            + Color.ANSI_YELLOW.getValue()
            + "Coins"
            + Color.ANSI_RESET.getValue()
            + ": "
            + this.getPlayerCoins()
            + "\n"
            + "     "
            + Color.ANSI_GREEN.getValue()
            + "Current Location"
            + Color.ANSI_RESET.getValue()
            + ": "
            + getCurrentScene().getName()
            + "\n"
            + "___________________________________________________________\n");
  }

  // Method to iterate through Clues
  public void iterateThroughClues() {
    switch (currentScene.getName()) {
      case "Rum Distillery":
      case "Royal Lodge":
        System.out.println(
            "\napply what you got"
                + Color.ANSI_YELLOW.getValue()
                + "\nClue#1 "
                + Color.ANSI_RESET.getValue()
                + clues[0]
                + "\n"
                + Color.ANSI_BLUE.getValue()
                + "Clue#2 "
                + Color.ANSI_RESET.getValue()
                + clues[3]);
        System.out.println(
            Color.ANSI_GREEN.getValue()
                + "Clue#3"
                + Color.ANSI_RESET.getValue()
                + " = "
                + Color.ANSI_YELLOW.getValue()
                + "Clue#1"
                + Color.ANSI_RESET.getValue()
                + " + "
                + Color.ANSI_BLUE.getValue()
                + "Clue#2"
                + Color.ANSI_RESET.getValue()
                + " .... "
                + "\n"
                + "\nwill reveal the island to kick off...");

        break;
      case "Crimson Beach Bar":
        System.out.println(
            Color.ANSI_YELLOW.getValue()
                + "Clue#1"
                + Color.ANSI_RESET.getValue()
                + " What is the cabin number Jojo said to look after?");

        break;
      case "Abandoned Distillery":
        System.out.println(
            Color.ANSI_BLUE.getValue()
                + "Clue#2"
                + Color.ANSI_RESET.getValue()
                + " How many Antique coins are there?");

        break;
      case "Sugar Cane Field":
      case "Tikki Lounge":
        System.out.println(
            Color.ANSI_RED.getValue()
                + "\nSecret Code: "
                + Color.ANSI_GREEN.getValue()
                + "Clue#3"
                + Color.ANSI_RESET.getValue());
        break;
      case "Ship Graveyard":
        System.out.println(
            Color.ANSI_YELLOW.getValue()
                + "Clue#1"
                + Color.ANSI_RESET.getValue()
                + "What is the name of the old and abandoned legendary ship you found in Ship Graveyard?");

        break;
      case "Sunset Restaurant":
        System.out.println(
            Color.ANSI_BLUE.getValue()
                + "Clue#2"
                + Color.ANSI_RESET.getValue()
                + " Which Room number you found out when talking to Tom?");
        break;
    }
  }

  /** @return String of Red Cross Bones image when player dies */
  public String getCrossBones() {
    return "                     .ed\"\"\"\" \"\"\"$$$$be.\n"
        + "                   -\"           ^\"\"**$$$e.\n"
        + "                 .\"                   '$$$c\n"
        + "                /                      \"4$$b\n"
        + "               d  3                      $$$$\n"
        + "               $  *                   .$$$$$$\n"
        + "              .$  ^c           $$$$$e$$$$$$$$.\n"
        + "              d$L  4.         4$$$$$$$$$$$$$$b\n"
        + "              $$$$b ^ceeeee.  4$$ECL.F*$$$$$$$\n"
        + "  e$\"\"=.      $$$$P d$$$$F $ $$$$$$$$$- $$$$$$\n"
        + " z$$b. ^c     3$$$F \"$$$$b   $\"$$$$$$$  $$$$*\"      .=\"\"$c\n"
        + "4$$$$L        $$P\"  \"$$b   .$ $$$$$...e$$        .=  e$$$.\n"
        + "^*$$$$$c  %..   *c    ..    $$ 3$$$$$$$$$$eF     zP  d$$$$$\n"
        + "  \"**$$$ec   \"   %ce\"\"    $$$  $$$$$$$$$$*    .r\" =$$$$P\"\"\n"
        + "        \"*$b.  \"c  *$e.    *** d$$$$$\"L$$    .d\"  e$$***\"\n"
        + "          ^*$$c ^$c $$$      4J$$$$$% $$$ .e*\".eeP\"\n"
        + "             \"$$$$$$\"'$=e....$*$$**$cz$$\" \"..d$*\"\n"
        + "               \"*$$$  *=%4.$ L L$ P3$$$F $$$P\"\n"
        + "                  \"$   \"%*ebJLzb$e$$$$$b $P\"\n"
        + "                    %..      4$$$$$$$$$$ \"\n"
        + "                     $$$e   z$$$$$$$$$$%\n"
        + "                      \"*$c  \"$$$$$$$P\"\n"
        + "                       .\"\"\"*$$$$$$$$bc\n"
        + "                    .-\"    .$***$$$\"\"\"*e.\n"
        + "                 .-\"    .e$\"     \"*$c  ^*b.\n"
        + "          .=*\"\"\"\"    .e$*\"          \"*bc  \"*$e..\n"
        + "        .$\"        .z*\"               ^*$e.   \"*****e.\n"
        + "        $$ee$c   .d\"                     \"*$.        3.\n"
        + "        ^*$E\")$..$\"                         *   .ee==d%\n"
        + "           $.d$$$*                           *  J$$$e*\n"
        + "            \"\"\"\"\"                              \"$$$\"";
  }

  /*
   * =============================================
   * =========== Accessor Methods ================
   * =============================================
   */

  // SET METHODS

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  public void setPlayerHealth(Integer playerHealth) {
    this.playerHealth = playerHealth;
  }

  public void setPlayerCoins(int playerCoins) {
    this.playerCoins = playerCoins;
  }

  public void setCurrentIsland(Island currentIsland) {
    this.currentIsland = currentIsland;
  }

  // GET METHODS
  public Island getCurrentIsland() {
    return currentIsland;
  }

  public Integer getPlayerHealth() {
    return this.playerHealth;
  }

  public Integer getPlayerCoins() {
    return this.playerCoins;
  }

  public String getPlayerName() {
    return this.playerName;
  }

  public Scene getCurrentScene() {
    return currentScene;
  }

  public void setCurrentScene(Scene currentScene) {
    this.currentScene = currentScene;
  }

  public Boolean getHasIslandItem() {
    return hasIslandItem;
  }

  public void setHasIslandItem(Boolean hasIslandItem) {
    this.hasIslandItem = hasIslandItem;
  }

  public void setPlayerCoins(Integer playerCoins) {
    this.playerCoins = playerCoins;
  }

  public Integer getPlayerAttackStrength() {
    return playerAttackStrength;
  }

  public void setPlayerAttackStrength(Integer playerAttackStrength) {
    this.playerAttackStrength = playerAttackStrength;
  }

  public SaveLoadGame getSaveLoadGame() {
    return saveLoadGame;
  }

  public void setSaveLoadGame(SaveLoadGame saveLoadGame) {
    this.saveLoadGame = saveLoadGame;
  }

  public String getInput() {
    return input;
  }

  public void setInput(String input) {
    this.input = input;
  }

  public Scanner getScanner() {
    return scanner;
  }

  public void setScanner(Scanner scanner) {
    this.scanner = scanner;
  }

  @Override
  public String toString() {
    return "Player{"
        + "vendor="
        + vendor
        + ", vendorItems="
        + vendorItems
        + ", playerClues="
        + playerClues
        + ", clues="
        + Arrays.toString(clues)
        + ", currentScene="
        + currentScene
        + ", hasIslandItem="
        + hasIslandItem
        + ", input='"
        + input
        + '\''
        + ", scanner="
        + scanner
        + ", playerName='"
        + playerName
        + '\''
        + ", playerCoins="
        + playerCoins
        + ", playerHealth="
        + playerHealth
        + ", saveLoadGame="
        + saveLoadGame
        + ", crossBones='"
        + getCrossBones()
        + '\''
        + '}';
  }
}
