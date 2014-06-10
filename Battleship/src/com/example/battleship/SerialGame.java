package com.example.battleship;

import java.util.HashMap;

public class SerialGame {
 
 
 public String gameID;
 public Boolean open = true;
 public int numPlayers = 2;
 public int maxPlayers = 2;
 public int turn = 0;
 
 public String playA;
 public String playB;
 public String boatA;
 public String boatB;
 
 public String result;
 
 SerialGame(){
	 open = true;
	 numPlayers = 2;
	 maxPlayers = 2;
	 turn = 0;
 }

 public HashMap<String, String> toHash(){
  HashMap<String, String> m = new HashMap<String, String>();
  m.put("gameID", gameID);
  m.put("open", String.valueOf(open));
  m.put("numPlayers", String.valueOf(numPlayers));
  m.put("maxPlayers", String.valueOf(maxPlayers));
  m.put("turn", String.valueOf(turn));
  m.put("playA", playA);
  m.put("playB", playB);
  m.put("boatA", boatA);
  m.put("boatB", boatB);
  return m;
 }
}