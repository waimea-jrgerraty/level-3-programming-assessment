# Plan for Testing the Program

The test plan lays out the actions and data I will use to test the functionality of my program.

Terminology:

- **VALID** data values are those that the program expects
- **BOUNDARY** data values are at the limits of the valid range
- **INVALID** data values are those that the program should reject

---

## Direction pathfinding

As part of the directions system where the NPCs direct you around the map, there need to be some guidelines for the 
pathfinding algorithm.

**VALID**
- From anywhere on the main map, you should be able to get directions to anywhere else on the main map
- Any path returned by the algorithm should be the shortest possible path, not just a possible path
- If an area of the map is closed off (say doing something destroys a passage way from one town to another), the 
pathfinding algorithm should respond to this change in the graph.

**BOUNDARY**
- A path from the current location to the current location should return a list with only the current location

**INVALID**
- We need to handle cases where the start destination and end destination are not connected through the graph, there may
be areas on the graph that are disconnected from the rest of the map, such as those where travelling to them involves
teleportation or other manual setting of the player location


### Test Data To Use

Details of test data and reasons for selection. Details of test data and reasons for selection. Details of test data and reasons for selection.

### Expected Test Result

Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen.

---

## Example Test Name

Example test description. Example test description. Example test description. Example test description. Example test description. Example test description.

### Test Data To Use

Details of test data and reasons for selection. Details of test data and reasons for selection. Details of test data and reasons for selection.

### Expected Test Result

Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen.

---


