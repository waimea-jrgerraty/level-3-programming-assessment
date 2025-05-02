# Plan for Testing the Program

The test plan lays out the actions and data I will use to test the functionality of my program.

Terminology:

- **VALID** data values are those that the program expects
- **BOUNDARY** data values are at the limits of the valid range
- **INVALID** data values are those that the program should reject

---

## Win / Loss state

I need to ensure that the end of the game can be reached, and the failure condition (death) works.

**VALID**

- Defeating the drowned king in the finale chapter of the story will conclude the game and put you into freeroam
- If your health drops to 0 during a combat sequence, you will die and the program will terminate

### Test Data To Use

- To test the failure condition, I will purposefully lose to Captain Rourke by only using Swing.
- To test the victory condition, I will just play through the entire game from start to finish, and document if I can
  reach the completion state. I would use the debug system to skip to the end, but I need to test whole story anyways.

### Expected Test Result

- The game should inform me I died, and close itself after a few seconds.
- I should be able to reach the end of the game through normal gameplay, and from there be able to move around the map
  without any more quests.

---

## Direction pathfinding

As part of the directions system where the NPCs direct you around the map, there need to be some guidelines for the
pathfinding algorithm. This system is used to direct players to the next part of the story, such as telling the player
how to get from Northbury to Dunmarch (through Balmoral).

**VALID**

- Any path returned by the algorithm should be the shortest possible path, not just a possible path

**BOUNDARY**

- Paths with a distance of 1 (current location, target location) should function correctly

**INVALID**

- A path that is not the shortest is Invalid
- The program should not be returning empty paths

### Test Data To Use

- I will complete the second chapter of the story (at Northbury) and look at the given path between Northbury and
  Dunmarch.
- To test the boundary case, I will complete the Ironforge chapter, as Ironforge and Cinderholm are adjacent.

### Expected Test Result

- I should be told to go from:
    - Northbury to Balmoral
    - Balmoral to Dunmarch
  As this should be the shortest path between the two locations
- I should be told to go from Ironforge to Cinderholm

---

## Movement system

I need to ensure the movement system functions correctly, only allowing you to move to connected locations, and only
while you are in free roam mode.

**VALID**

- When in free roam, you should be able to use the Action button to move to a neighbouring Location (e.g., Balmoral to
  Northbury)
- Anywhere in the dropdown list of locations should be a valid location, and accepted when pressing the Move button

**INVALID**

- Selecting nothing in the dropdown should cause the move button to be disabled

### Test Data To Use

- For the first valid case, I will attempt to move from Balmoral to Northbury after the tutorial is over
- For the second valid case, I will check if every location in the dropdown list at Northbury allows me to press the
  Move button.
- For the invalid case, I will attempt to not select anything in the dropdown menu, and check if the Move button is
  disabled. The JComboBox likely already prevents me from selecting nothing however.

### Expected Test Result

- I should move from Balmoral to Northbury (triggering the second chapter)
- I should be able to move to every location in the dropdown list from Northbury
- Either I should not be able to select an invalid location, or selecting an invalid location will result in the Move
  button being disabled.

---

## Combat System

The combat system needs to handle attack cooldowns. Powerful attacks have longer cooldowns before you can use them again
to make combat more interesting.

**VALID**

- You should be able to use a move that is not on cooldown

**Boundary**

- Moves that have a cooldown of 1 turn remaining should be treated as invalid
- Moves with no cooldown should always be treated as valid

**INVALID**

- Selecting a move that is currently on cooldown should disable the Attack button

### Test Data To Use

*All tests will be performed on Captain Rourke*

- To test the valid case, I use a move not currently on cooldown
- To test the first boundary case, I will try to use a move with 1 turn of cooldown remaining
- To test the second boundary case, I will try to use the basic move (Swing with the battered sword) two times in a row
- To test the invalid case, I use a more powerful move (Tornado with the battered sword) and attempt to use it
  again the next turn.

### Expected Test Result

- Selecting the move not on cooldown should allow me to press the Attack button
- Selecting the move with 1 turn of cooldown remaining should disable the Attack button
- I should be able to attack twice in a row with the basic move
- I should be able to use Tornado once, but it will not work on the next turn

---

## Action Button

As the action button is dual purpose, used for both the combat menu and movement menu, I need to ensure you can not
perform invalid moves with it.

**VALID**

- In free roam, the action button should toggle the movement menu.
- In combat, the action button should toggle the combat menu.

**INVALID**

- While in a sequence, but not in combat, the action button should be disabled and neither of the menus should be
  opened.
- After defeating your opponent in combat, you should not be able to open the attack menu in the brief time it takes to
  conclude the combat sequence.

### Test Data To Use

- To test the valid cases, I will do the tutorial and travel to Northbury, as this relies on both menus working to
  achieve.
- To test the first invalid case, I will attempt to press the action button in a sequence (not in combat)
- To test the second invalid case, I will attempt to open the combat menu immediately after dropping Captain Rourke's
  health to 0.

### Expected Test Result

- I should be able to reach Northbury playing normally, and the action button should close the movement/combat menu when
  pressing it a second time.
- I should not be able to press the action button in a sequence while not in combat as it should be disabled.
- I should not be able to press the action button when it isn't my turn in combat or at the end of combat, as it should
  be disabled.