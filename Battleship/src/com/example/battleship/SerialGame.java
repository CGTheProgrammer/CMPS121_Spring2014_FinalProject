package com.example.battleship;

public class SerialGame {
	
	SerialGame(){};
	
	public Boolean open = true;
	public int numPlayers = 2;
	public int maxPlayers = 2;
	public int turn = 0;
	
	public int[][] playA = new int[10][10];
	public int[][] playB = new int[10][10];
	
}