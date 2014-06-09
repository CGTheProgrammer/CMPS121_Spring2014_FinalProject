package com.example.battleship;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends ActionBarActivity {
	//The graph of the players area
	public Graph aGraph;									//A graph of the players ship positions
	public boolean[][] aAttacks;							//A graph of the positions that the player has attacked
	//The graph of the opponents area
	public Graph bGraph;									//A graph of the opponents ship positions
	public boolean[][] bAttacks;							//Keeps track of all of the attacks the opponent has made
	public boolean canAttack;								//Determines whether or not it is the players turn
	public int boats_remaining, op_boats_remaining;			//The number of undestroyed ships that the player currently has
	
	public int turn;
	public int sizeX, sizeY;				//Integers that represent the size of the screen the app is being run on
	//The main menu
	public View main;						//The main menu screen
	//The main game area
	public aView game;						//The main in game view the player sees when they are viewing their half of the board
	//The attack view
	public bView attack;					//The view the player sees when they are choosing the location for their attacks
	//The place view
	public placeView place;					//The view that allows the player to place their ships
	
	public int curView;						//Keeps track of which screen the user is currently on 0 = main menu, 2 = game
	public boolean singlePlayer;			//Determines the source of the opponent (another player == false, AI == true) 
	public AI ai;							//Artificial Intelligence that controls opponent during singlePlayer
	public Boat[] boats;					//An array that stores all of the players boats
	Canvas canvas;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //This is where we are creating our view
        game = new aView(getApplicationContext());
        attack = new bView(getApplicationContext());
        place = new placeView(getApplicationContext());
        setContentView(R.layout.activity_main);
        
        //Determining the size of the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        sizeX = size.x;
        sizeY = size.y;
        
        //Initializing variables
        aGraph = new Graph();
        bGraph = new Graph();
        
        aAttacks = new boolean[10][10];
        bAttacks = new boolean[10][10];
        
        boats = new Boat[5];
        boats[0] = new Boat(4, "battleship", 0);
        boats[1] = new Boat(3, "submarine", 0);
        boats[2] = new Boat(5, "aircraftcarrier", 0);
        boats[3] = new Boat(3, "destroyer", 0);
        boats[4] = new Boat(2, "patrol", 0);
        
        singlePlayer = false;
        
        canvas = new Canvas(Bitmap.createBitmap(sizeX,sizeY,Bitmap.Config.ARGB_8888));
        
		/////////////////////////////////////////////////////////////////////////////////
		//NOTE: THIS NEEDS TO BE CHANGED TO 17 WHEN THERE ARE 5 BOATS IN THE GAME////////
		/////////////////////////////////////////////////////////////////////////////////
        boats_remaining = 17;
        op_boats_remaining = 5;
        
        turn = 0;
        curView = 0;
        
        for(int i = 0; i < 10; i++){
        	for(int j = 0; j < 10; j++){
        		aAttacks[i][j] = false;
        		bAttacks[i][j] = false;
        	}
        }
        
        //Setting up the touch input for each of the views      
        
        //Setting up the system that senses touch input for the game view
        //Doesn't do that much as of 5-29-14 because there isn't much to be done
        game.setOnTouchListener(new View.OnTouchListener(){
        	@Override
        	public boolean onTouch(View v, MotionEvent event){
        		
        		//Determining Which Square the user has clicked and marking it as clicked
        		if(event.getAction() == MotionEvent.ACTION_DOWN){
	
        			float x, y;
	        		if(event.getY() >= (sizeY / 100) * 8 && event.getY() <= (sizeY / 100) * 75){
	        			y = event.getY();
	        			y -= (sizeY / 100) * 8;
	        			
	        			
	        			if(y >= (sizeY / 100) * 90)
		        			y = 9;
		        		else if(y >= (sizeY / 100) * 80)
		        			y = 8;
		        		else if(y >= (sizeY / 100) * 70)
		        			y = 7;
		        		else if(y >= (sizeY / 100) * 60)
		        			y = 6;
		        		else if(y >= (sizeY / 100) * 50)
		        			y = 5;
		        		else if(y >= (sizeY / 100) * 40)
		        			y = 4;
		        		else if(y >= (sizeY / 100) * 30)
		        			y = 3;
		        		else if(y >= (sizeY / 100) * 20)
		        			y = 2;
		        		else if(y >= (sizeY / 100) * 10)
		        			y = 1;
		        		else y = 0; 
	        		}
	        		else if(event.getY() < (sizeY / 100) * 8){ //Clicked above the graph
	        			y = -1;
	        		}
	        		else{										//Clicked below the graph
	        			y = 100;
	        		}
	        		x = event.getX();

	        		if(x >= (sizeX / 100) * 90)
	        			x = 9;
	        		else if(x >= (sizeX / 100) * 80)
	        			x = 8;
	        		else if(x >= (sizeX / 100) * 70)
	        			x = 7;
	        		else if(x >= (sizeX / 100) * 60)
	        			x = 6;
	        		else if(x >= (sizeX / 100) * 50)
	        			x = 5;
	        		else if(x >= (sizeX / 100) * 40)
	        			x = 4;
	        		else if(x >= (sizeX / 100) * 30)
	        			x = 3;
	        		else if(x >= (sizeX / 100) * 20)
	        			x = 2;
	        		else if(x >= (sizeX / 100) * 10)
	        			x = 1;
	        		else x = 0;
	        		
	        		String tempStr = String.valueOf(x) + ", " + String.valueOf(y);
	        		
	        		Log.i("Game", tempStr);
	        			        		
	        		//If the player pushes the button at the bottom of the screen, change view
	        		if(y >= 10){
	        			if(x >= 5){
		        			setContentView(attack);
		        			curView = 3;
		        			attack.draw(canvas);
	        			}
	        			else{
	        				reset();
	        				setContentView(R.layout.activity_main);
	        				curView = 0;
	        			}
	        		}
	        		game.invalidate();
        		}
        		return true;
        	}
        });
        
        
        //Setting up the system that senses touch input for the attack view
        attack.setOnTouchListener(new View.OnTouchListener(){
        	@Override
        	public boolean onTouch(View v, MotionEvent event){
        		
        		//Determining Which Square the user has clicked and marking it as clicked
        		if(event.getAction() == MotionEvent.ACTION_DOWN){
        			
        			float x, y;
	        		if(event.getY() >= (sizeY / 100) * 8.3 && event.getY() <= (sizeY / 100) * 75){
	        			y = event.getY();	        			
	        			
	        			if(y >= (sizeY / 100) * 68)
		        			y = 9;
		        		else if(y >= (sizeY / 100) * 61)
		        			y = 8;
		        		else if(y >= (sizeY / 100) * 55)
		        			y = 7;
		        		else if(y >= (sizeY / 100) * 48)
		        			y = 6;
		        		else if(y >= (sizeY / 100) * 42)
		        			y = 5;
		        		else if(y >= (sizeY / 100) * 35)
		        			y = 4;
		        		else if(y >= (sizeY / 100) * 28)
		        			y = 3;
		        		else if(y >= (sizeY / 100) * 22)
		        			y = 2;
		        		else if(y >= (sizeY / 100) * 14)
		        			y = 1;
		        		else y = 0; 
	        		}
	        		else if(event.getY() < (sizeY / 100) * 8.3){ //Clicked above the graph
	        			y = -1;
	        		}
	        		else{										//Clicked below the graph
	        			y = 100;
	        		}
	        		x = event.getX();

	        		if(x >= (sizeX / 100) * 90)
	        			x = 9;
	        		else if(x >= (sizeX / 100) * 80)
	        			x = 8;
	        		else if(x >= (sizeX / 100) * 70)
	        			x = 7;
	        		else if(x >= (sizeX / 100) * 60)
	        			x = 6;
	        		else if(x >= (sizeX / 100) * 50)
	        			x = 5;
	        		else if(x >= (sizeX / 100) * 40)
	        			x = 4;
	        		else if(x >= (sizeX / 100) * 30)
	        			x = 3;
	        		else if(x >= (sizeX / 100) * 20)
	        			x = 2;
	        		else if(x >= (sizeX / 100) * 10)
	        			x = 1;
	        		else x = 0;
	        		
	        		String tempStr = String.valueOf(x) + ", " + String.valueOf(y);
	        		
	        		Log.i("Attack", tempStr);
	        		
	        		
	        		//Enters new data in the aAttack array to denote that the player has attacked a square 
	        		if(y < 10 && x < 10 && y >= 0 && x >= 0){
	        			if(singlePlayer){
	        				turn++;
	        				if(ai.aiGraph.graph[(int)x][(int)y].state != 3){
	        					//Clearing the temp tags
	        					for(int i = 0; i < 10; i++){
	        						for(int j = 0; j < 10; j++){
	        							if(ai.aiGraph.graph[i][j].state == 3)
	        								ai.aiGraph.graph[i][j].state = 0;
	        						}
	        					}
	        					
	        					ai.aiGraph.graph[(int)x][(int)y].state = 3;
	        				}
	        				else if(ai.aiGraph.graph[(int)x][(int)y].state == 3){
	        					ai.aiGraph.touch((int)x, (int)y);
		        				aAttacks[(int)x][(int)y] = true;
	        					//Clearing the temp tags
	        					for(int i = 0; i < 10; i++){
	        						for(int j = 0; j < 10; j++){
	        							if(ai.aiGraph.graph[i][j].state == 3)
	        								ai.aiGraph.graph[i][j].state = 0;
	        						}
	        					}
	        					
	        					
	        					//Handling the Counter Attack
		        				bAttacks[ai.moves[turn-1].x][ai.moves[turn-1].y] = true;
	        					if(ai.aiGraph.graph[(int)x][(int)y].tag == "boat")
	        						ai.boats_remaining--;
	        					if(aGraph.graph[ai.moves[turn-1].x][ai.moves[turn-1].y].tag == "boat"){
	        						boats_remaining--;
	        						//When the AI actually hits one of the players boats, the AI must recalculate their attacks to improve their chances of hitting the rest of the players ship
	        						//ai.makeNewAttackArray(ai.moves[turn-1].x,ai.moves[turn-1].y);
	        					}
	        					setContentView(game);
	        				}
	        			}
	        			else{//This is what happens when attacking in multiplayer
	        				//Update the turn variable from the server
	        				if(canAttack){
		        				turn++;
		        				if(ai.aiGraph.graph[(int)x][(int)y].state != 3){
		        					//Clearing the temp tags
		        					for(int i = 0; i < 10; i++){
		        						for(int j = 0; j < 10; j++){
		        							if(ai.aiGraph.graph[i][j].state == 3)
		        								ai.aiGraph.graph[i][j].state = 0;
		        						}
		        					}
		        					
		        					ai.aiGraph.graph[(int)x][(int)y].state = 3;
		        				}
		        				else if(ai.aiGraph.graph[(int)x][(int)y].state == 3){
		        					ai.aiGraph.touch((int)x, (int)y);
			        				aAttacks[(int)x][(int)y] = true;
		        					//Clearing the temp tags
		        					for(int i = 0; i < 10; i++){
		        						for(int j = 0; j < 10; j++){
		        							if(ai.aiGraph.graph[i][j].state == 3)
		        								ai.aiGraph.graph[i][j].state = 0;
		        						}
		        					}
		        				}
		        				//Needs to upload turn variable to server
		        				uploadGame();
	        				}
	        			}


	        		}
	        		//If the player pushes the button at the bottom of the screen, change view
	        		else if(y >= 10){
	        			if(x >= 5){
	        				setContentView(game);
	        				curView = 3;
	        				game.draw(canvas);
	        			}
	        			else{
	        				reset();
	        				curView = 0;
	        				setContentView(R.layout.activity_main);
	        			}
	        		}
	    	        attack.invalidate();
	        	}
        		return true;
        	}
        });

        place.setOnTouchListener(new View.OnTouchListener(){
        	@Override
        	public boolean onTouch(View v, MotionEvent event){
        		//Finding the coordinates of the touch event
        		if(event.getAction() == MotionEvent.ACTION_DOWN){
        			
	        		float x, y;
	        		if(event.getY() >= (sizeY / 100) * 8.3 && event.getY() <= (sizeY / 100) * 75){
	        			y = event.getY();	        			
	        			
	        			if(y >= (sizeY / 100) * 68)
		        			y = 9;
		        		else if(y >= (sizeY / 100) * 61)
		        			y = 8;
		        		else if(y >= (sizeY / 100) * 55)
		        			y = 7;
		        		else if(y >= (sizeY / 100) * 48)
		        			y = 6;
		        		else if(y >= (sizeY / 100) * 42)
		        			y = 5;
		        		else if(y >= (sizeY / 100) * 35)
		        			y = 4;
		        		else if(y >= (sizeY / 100) * 28)
		        			y = 3;
		        		else if(y >= (sizeY / 100) * 22)
		        			y = 2;
		        		else if(y >= (sizeY / 100) * 14)
		        			y = 1;
		        		else y = 0; 
	        		}
	        		else if(event.getY() < (sizeY / 100) * 8.3){ //Clicked above the graph
	        			y = -1;
	        		}
	        		else{										//Clicked below the graph
	        			y = 100;
	        		}
	        		x = event.getX();

	        		if(x >= (sizeX / 100) * 90)
	        			x = 9;
	        		else if(x >= (sizeX / 100) * 80)
	        			x = 8;
	        		else if(x >= (sizeX / 100) * 70)
	        			x = 7;
	        		else if(x >= (sizeX / 100) * 60)
	        			x = 6;
	        		else if(x >= (sizeX / 100) * 50)
	        			x = 5;
	        		else if(x >= (sizeX / 100) * 40)
	        			x = 4;
	        		else if(x >= (sizeX / 100) * 30)
	        			x = 3;
	        		else if(x >= (sizeX / 100) * 20)
	        			x = 2;
	        		else if(x >= (sizeX / 100) * 10)
	        			x = 1;
	        		else x = 0;
	        		
	        		String tempStr = String.valueOf(x) + ", " + String.valueOf(y);
	        		
	        		Log.i("Place", tempStr);
	        		
	        		
	        		//Determines which boat is being placed, I wish I could write this as a switch statement... but I can't
		        	if(y < 10 && y >= 0){
		        		if(!boats[0].placed && aGraph.graph[(int)x][(int)y].tag == "water"){			//Placing Battleship
		        			if(aGraph.graph[(int)x][(int)y].state == 3){
		        				//boats[0].placed = aGraph.placeBoat(boats[0], (int)x, (int)y);
		        				int boatCount = 0;
		        				Coord last = new Coord(0,0);
		        				for(int i = 0; i < 10; i++){
		        					for(int j = 0; j < 10; j++){
		        						if(aGraph.graph[i][j].state == 3){
		        							aGraph.graph[i][j].tag = "boat";
		        							if(boatCount == 0){
		        								String tempString = "";
		        								switch(boats[0].direction){
		        								case 0:
		        									tempString = "battleship" + 0 + 0;
		        									break;
		        								case 1:
		        									tempString = "battleship" + 1 + 0;
		        									break;
		        								case 2:
		        									tempString = "battleship" + 2 + 0;
		        									break;
		        								case 3:
		        									tempString = "battleship" + 3 + 0;
		        									break;
		        								default:
		        									break;
		        								}
		        								 
		        								Log.i("Place",tempString);
		        								aGraph.graph[i][j].type = tempString;
		        								last = new Coord(i,j);
		        								boatCount++;
		        							}
		        							else if(boatCount >= 1){
		        								String tempString = "";
		        								switch(boats[0].direction){
		        								case 0:
		        									tempString = "battleship" + 0 + 1;
		        									break;
		        								case 1:
		        									tempString = "battleship" + 1 + 1;
		        									break;
		        								case 2:
		        									tempString = "battleship" + 2 + 1;
		        									break;
		        								case 3:
		        									tempString = "battleship" + 3 + 1;
		        									break;
		        								default:
		        									break;
		        								}
		        								aGraph.graph[i][j].type = tempString;
		        								boatCount++;
		        								last = new Coord(i,j);
		        							}
		        							aGraph.graph[i][j].state = 0;
		        						}
		        					}
		        				}
		        				String tempString = "";
		        				switch(boats[0].direction){
								case 0:
									tempString = "battleship" + 0 + 2;
									break;
								case 1:
									tempString = "battleship" + 1 + 2;
									break;
								case 2:
									tempString = "battleship" + 2 + 2;
									break;
								case 3:
									tempString = "battleship" + 3 + 2;
									break;
								default:
									break;
								}
		        				aGraph.graph[last.x][last.y].type = tempString;
		        				boats[0].placed = true;
		        			}
		        			else{
		        				aGraph.placeBoatTemp(boats[0], (int) x, (int)y);
		        			}
		            		place.invalidate();
		        		}
		        		else if(!boats[1].placed && aGraph.graph[(int)x][(int)y].tag == "water"){		//Placing Submarine
		        			if(aGraph.graph[(int)x][(int)y].state == 3){
		        				//boats[0].placed = aGraph.placeBoat(boats[0], (int)x, (int)y);
		        				int boatCount = 0;
		        				Coord last = new Coord(0,0);
		        				for(int i = 0; i < 10; i++){
		        					for(int j = 0; j < 10; j++){
		        						if(aGraph.graph[i][j].state == 3){
		        							aGraph.graph[i][j].tag = "boat";
		        							if(boatCount == 0){
		        								String tempString = "";
		        								switch(boats[1].direction){
		        								case 0:
		        									tempString = "battleship" + 0 + 0;
		        									break;
		        								case 1:
		        									tempString = "battleship" + 1 + 0;
		        									break;
		        								case 2:
		        									tempString = "battleship" + 2 + 0;
		        									break;
		        								case 3:
		        									tempString = "battleship" + 3 + 0;
		        									break;
		        								default:
		        									break;
		        								}
		        								 
		        								Log.i("Place",tempString);
		        								aGraph.graph[i][j].type = tempString;
		        								last = new Coord(i,j);
		        								boatCount++;
		        							}
		        							else if(boatCount >= 1){
		        								String tempString = "";
		        								switch(boats[1].direction){
		        								case 0:
		        									tempString = "battleship" + 0 + 1;
		        									break;
		        								case 1:
		        									tempString = "battleship" + 1 + 1;
		        									break;
		        								case 2:
		        									tempString = "battleship" + 2 + 1;
		        									break;
		        								case 3:
		        									tempString = "battleship" + 3 + 1;
		        									break;
		        								default:
		        									break;
		        								}
		        								aGraph.graph[i][j].type = tempString;
		        								boatCount++;
		        								last = new Coord(i,j);
		        							}
		        							aGraph.graph[i][j].state = 0;
		        						}
		        					}
		        				}
		        				String tempString = "";
		        				switch(boats[1].direction){
								case 0:
									tempString = "battleship" + 0 + 2;
									break;
								case 1:
									tempString = "battleship" + 1 + 2;
									break;
								case 2:
									tempString = "battleship" + 2 + 2;
									break;
								case 3:
									tempString = "battleship" + 3 + 2;
									break;
								default:
									break;
								}
		        				aGraph.graph[last.x][last.y].type = tempString;
		        				boats[1].placed = true;
		        			}
		        			else{
		        				aGraph.placeBoatTemp(boats[1], (int) x, (int)y);
		        			}
		            		place.invalidate();
		        		}
		        		else if(!boats[2].placed && aGraph.graph[(int)x][(int)y].tag == "water"){		//Placing Air Craft Carrier
		        			if(aGraph.graph[(int)x][(int)y].state == 3){
		        				//boats[0].placed = aGraph.placeBoat(boats[0], (int)x, (int)y);
		        				int boatCount = 0;
		        				Coord last = new Coord(0,0);
		        				for(int i = 0; i < 10; i++){
		        					for(int j = 0; j < 10; j++){
		        						if(aGraph.graph[i][j].state == 3){
		        							aGraph.graph[i][j].tag = "boat";
		        							if(boatCount == 0){
		        								String tempString = "";
		        								switch(boats[2].direction){
		        								case 0:
		        									tempString = "battleship" + 0 + 0;
		        									break;
		        								case 1:
		        									tempString = "battleship" + 1 + 0;
		        									break;
		        								case 2:
		        									tempString = "battleship" + 2 + 0;
		        									break;
		        								case 3:
		        									tempString = "battleship" + 3 + 0;
		        									break;
		        								default:
		        									break;
		        								}
		        								 
		        								Log.i("Place",tempString);
		        								aGraph.graph[i][j].type = tempString;
		        								last = new Coord(i,j);
		        								boatCount++;
		        							}
		        							else if(boatCount >= 1){
		        								String tempString = "";
		        								switch(boats[2].direction){
		        								case 0:
		        									tempString = "battleship" + 0 + 1;
		        									break;
		        								case 1:
		        									tempString = "battleship" + 1 + 1;
		        									break;
		        								case 2:
		        									tempString = "battleship" + 2 + 1;
		        									break;
		        								case 3:
		        									tempString = "battleship" + 3 + 1;
		        									break;
		        								default:
		        									break;
		        								}
		        								aGraph.graph[i][j].type = tempString;
		        								boatCount++;
		        								last = new Coord(i,j);
		        							}
		        							aGraph.graph[i][j].state = 0;
		        						}
		        					}
		        				}
		        				String tempString = "";
		        				switch(boats[2].direction){
								case 0:
									tempString = "battleship" + 0 + 2;
									break;
								case 1:
									tempString = "battleship" + 1 + 2;
									break;
								case 2:
									tempString = "battleship" + 2 + 2;
									break;
								case 3:
									tempString = "battleship" + 3 + 2;
									break;
								default:
									break;
								}
		        				aGraph.graph[last.x][last.y].type = tempString;
		        				boats[2].placed = true;
		        			}
		        			else{
		        				aGraph.placeBoatTemp(boats[2], (int) x, (int)y);
		        			}
		            		place.invalidate();
		        		}
		        		else if(!boats[3].placed && aGraph.graph[(int)x][(int)y].tag == "water"){		//Destroyer
		        			if(aGraph.graph[(int)x][(int)y].state == 3){
		        				//boats[0].placed = aGraph.placeBoat(boats[0], (int)x, (int)y);
		        				int boatCount = 0;
		        				Coord last = new Coord(0,0);
		        				for(int i = 0; i < 10; i++){
		        					for(int j = 0; j < 10; j++){
		        						if(aGraph.graph[i][j].state == 3){
		        							aGraph.graph[i][j].tag = "boat";
		        							if(boatCount == 0){
		        								String tempString = "";
		        								switch(boats[3].direction){
		        								case 0:
		        									tempString = "battleship" + 0 + 0;
		        									break;
		        								case 1:
		        									tempString = "battleship" + 1 + 0;
		        									break;
		        								case 2:
		        									tempString = "battleship" + 2 + 0;
		        									break;
		        								case 3:
		        									tempString = "battleship" + 3 + 0;
		        									break;
		        								default:
		        									break;
		        								}
		        								 
		        								Log.i("Place",tempString);
		        								aGraph.graph[i][j].type = tempString;
		        								last = new Coord(i,j);
		        								boatCount++;
		        							}
		        							else if(boatCount >= 1){
		        								String tempString = "";
		        								switch(boats[3].direction){
		        								case 0:
		        									tempString = "battleship" + 0 + 1;
		        									break;
		        								case 1:
		        									tempString = "battleship" + 1 + 1;
		        									break;
		        								case 2:
		        									tempString = "battleship" + 2 + 1;
		        									break;
		        								case 3:
		        									tempString = "battleship" + 3 + 1;
		        									break;
		        								default:
		        									break;
		        								}
		        								aGraph.graph[i][j].type = tempString;
		        								boatCount++;
		        								last = new Coord(i,j);
		        							}
		        							aGraph.graph[i][j].state = 0;
		        						}
		        					}
		        				}
		        				String tempString = "";
		        				switch(boats[3].direction){
								case 0:
									tempString = "battleship" + 0 + 2;
									break;
								case 1:
									tempString = "battleship" + 1 + 2;
									break;
								case 2:
									tempString = "battleship" + 2 + 2;
									break;
								case 3:
									tempString = "battleship" + 3 + 2;
									break;
								default:
									break;
								}
		        				aGraph.graph[last.x][last.y].type = tempString;
		        				boats[3].placed = true;
		        			}
		        			else{
		        				aGraph.placeBoatTemp(boats[3], (int) x, (int)y);
		        			}
		            		place.invalidate();
		        		}       		
		        		else if(!boats[4].placed && aGraph.graph[(int)x][(int)y].tag == "water"){		//Patrol Boat
		        			if(aGraph.graph[(int)x][(int)y].state == 3){
		        				//boats[0].placed = aGraph.placeBoat(boats[0], (int)x, (int)y);
		        				int boatCount = 0;
		        				Coord last = new Coord(0,0);
		        				for(int i = 0; i < 10; i++){
		        					for(int j = 0; j < 10; j++){
		        						if(aGraph.graph[i][j].state == 3){
		        							aGraph.graph[i][j].tag = "boat";
		        							if(boatCount == 0){
		        								String tempString = "";
		        								switch(boats[4].direction){
		        								case 0:
		        									tempString = "battleship" + 0 + 0;
		        									break;
		        								case 1:
		        									tempString = "battleship" + 1 + 0;
		        									break;
		        								case 2:
		        									tempString = "battleship" + 2 + 0;
		        									break;
		        								case 3:
		        									tempString = "battleship" + 3 + 0;
		        									break;
		        								default:
		        									break;
		        								}
		        								 
		        								Log.i("Place",tempString);
		        								aGraph.graph[i][j].type = tempString;
		        								last = new Coord(i,j);
		        								boatCount++;
		        							}
		        							else if(boatCount >= 1){
		        								String tempString = "";
		        								switch(boats[4].direction){
		        								case 0:
		        									tempString = "battleship" + 0 + 1;
		        									break;
		        								case 1:
		        									tempString = "battleship" + 1 + 1;
		        									break;
		        								case 2:
		        									tempString = "battleship" + 2 + 1;
		        									break;
		        								case 3:
		        									tempString = "battleship" + 3 + 1;
		        									break;
		        								default:
		        									break;
		        								}
		        								aGraph.graph[i][j].type = tempString;
		        								boatCount++;
		        								last = new Coord(i,j);
		        							}
		        							aGraph.graph[i][j].state = 0;
		        						}
		        					}
		        				}
		        				String tempString = "";
		        				switch(boats[4].direction){
								case 0:
									tempString = "battleship" + 0 + 2;
									break;
								case 1:
									tempString = "battleship" + 1 + 2;
									break;
								case 2:
									tempString = "battleship" + 2 + 2;
									break;
								case 3:
									tempString = "battleship" + 3 + 2;
									break;
								default:
									break;
								}
		        				aGraph.graph[last.x][last.y].type = tempString;
		        				boats[4].placed = true;
		        			}
		        			else{
		        				aGraph.placeBoatTemp(boats[4], (int) x, (int)y);
		        			}
		            		place.invalidate();
		        		}
		        		//Once all boats are placed, click anywhere to begin the game
						//NOTE: May trigger approximately when last boat is placed because the input will still be detected
		        		/*else if(boats[0].placed && boats[1].placed && boats[2].placed){
		        			Log.i("GAME", "Launched");
		        			setContentView(game);
		        			String temp = "";
		        	    	for(int i = 0; i < 10; i++){
		        	    		for(int j = 0; j < 10; j++){
		        	    			if(aGraph.graph[i][j].tag == "water") temp += String.valueOf(0);
		        	    			else if(aGraph.graph[i][j].tag == "boat") temp += String.valueOf(1);
		        	    			
		        	    		}
		        	    	}
		        	    	//UPLOAD temp to the server
		        	    	
		        			game.draw(canvas);
		        		}*/
		        	}
		        	else{		
		        		x = event.getX();
		        		if(x >= (sizeX / 2)){					//Rotation Button
		        			Log.i("Rotate","Turning Right");
		        			int i = 0;
		        			while(boats[i].placed && i < 5)
		        				i++;
		        			if(i >= 5)
		        				return false;
		        			boats[i].direction++;
		        			if(boats[i].direction >= 4)
		        				boats[i].direction = 0;		        		
		        		}
		        		else{									//Confirm Button
		        			if(boats[0].placed && boats[1].placed && boats[2].placed){
			        			Log.i("GAME", "Launched");
			        			setContentView(game);
			        			String temp = "";
			        	    	for(int i = 0; i < 10; i++){
			        	    		for(int j = 0; j < 10; j++){
			        	    			if(aGraph.graph[i][j].tag == "water") temp += String.valueOf(0);
			        	    			else if(aGraph.graph[i][j].tag == "boat") temp += String.valueOf(1);
			        	    			
			        	    		}
			        	    	}
			        	    	if(!singlePlayer)
			        	    		downloadGame();
			        	    	
			        			game.draw(canvas);
		        			}
		        		}

		        	}
        		}
        		
        		return true;
        	}
        	
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //Controls the functionality of the back button
    //Controls the functionality of the Back Button
    @Override
    public void onBackPressed() {
		if(curView == 0 || curView == 1){				//This is what occurs when the back button is pressed in one of the menus
			setContentView(R.layout.activity_main);
			if(curView == 1)
				curView = 0;
		}
		else if(curView == 2){							//This is what happens when the back button is pressed from the main game screen
			reset();
			setContentView(R.layout.activity_main);
			curView = 0;
    	}
		else{
			setContentView(game);						//This is what happens when the back button is pressed on the attack screen
			curView = 2;
		}    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void checkWinner(){
    	if(boats_remaining == 0)
    		setContentView(R.layout.lose);
    	if(singlePlayer)	
    		if(ai.boats_remaining == 0)
    			setContentView(R.layout.win);
    	else{
    		if(op_boats_remaining == 0)
    			setContentView(R.layout.win);
    	}
    		
    }
    //This is my view class that I created, it will allow us to dynamically determine our users screen size
    //and load the view accordingly.
    //Additionally this is necessary in order to do work on the canvas (the way that the background and sensor system is
    //currently working)
    public class aView extends View{
	    
    	//Initialization should be done before draw time to save computation
    	LinearLayout layout;
    	Button attack;
    	Paint paint = new Paint();
    	float lx, rx, ty, by;
    	Bitmap[][] ships;
    	Bitmap main_menu, attack_btn;

    	
    	public aView(Context context){
    		super(context);    		
    		this.setBackgroundResource(R.drawable.battleship_main_screen);

    	}
	
    	@Override
    	protected void onDraw(Canvas canvas){
    		super.onDraw(canvas);
    		//If we want to have extra things drawn on the screen, this is where the code should go

    		ships = new Bitmap[10][10];
    		if(!singlePlayer)
    			uploadGame();
    		
    		checkWinner();
    		
    		//This first segment is devoted to drawing the button at the bottom
       		Paint paint = new Paint();
    		paint.setStyle(Paint.Style.FILL);
    		paint.setColor(Color.RED);
    		float lx = 0;
    		float rx = getWidth();
    		float ty = getHeight();
    		ty = (float) (ty * 0.75);
    		float by = getHeight();
    		canvas.drawRect(lx, ty, rx, by, paint);
    		int w = (int)(getHeight() * 0.25);
    		int h = (int)(getWidth() / 2);
    		main_menu = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_main_menu_btn);
    		attack_btn = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_attack_btn);
    		if(h > 0 && w > 0){
    			main_menu = Bitmap.createScaledBitmap(main_menu, h, w, false);
    			attack_btn = Bitmap.createScaledBitmap(attack_btn, h, w, false);
    			canvas.drawBitmap(main_menu, (float)0,(float)(getHeight() * 0.75), null);
    			canvas.drawBitmap(attack_btn, (float)getWidth() / 2,(float)(getHeight() * 0.75), null);
    		}
    		
    		
    		h = (int)(getHeight() * 0.0666);
    		w = (int)(getWidth() / 10);
    		
    		for(int i = 0; i < 10; i++){
    			for(int j = 0; j < 10; j++){
    				float xPos = 0;
    				float yPos = 0;
    				switch(i){
	    				case 0:
	    					xPos = (float)(sizeX * 0.0);
	    					break;
	    				case 1:
	    					xPos = (float)(sizeX * 0.10);
	    					break;
	    				case 2:
	    					xPos = (float)(sizeX * 0.20);
	    					break;
	    				case 3:
	    					xPos = (float)(sizeX * 0.30);
	    					break;
	    				case 4:
	    					xPos = (float)(sizeX * 0.40);
	    					break;
	    				case 5:
	    					xPos = (float)(sizeX * 0.50);
	    					break;
	    				case 6:
	    					xPos = (float)(sizeX * 0.60);
	    					break;
	    				case 7:
	    					xPos = (float)(sizeX * 0.70);
	    					break;
	    				case 8:
	    					xPos = (float)(sizeX * 0.80);
	    					break;
	    				case 9:
	    					xPos = (float)(sizeX * 0.90);
	    					break;
	    				default:
	    					break;
    				}
    				switch(j){
    				case 0:
    					yPos = (float)(sizeY * 0.083);
    					break;
    				case 1:
    					//yPos = (float)(sizeY * 0.15);
    					yPos = (float)(sizeY * 0.13);
    					break;
    				case 2:
    					//yPos = (float)(sizeY * 0.215);
    					yPos = (float)(sizeY * 0.188);
    					break;
    				case 3:
    					//yPos = (float)(sizeY * 0.282);
    					yPos = (float)(sizeY * 0.248);
    					break;
    				case 4:
    					//yPos = (float)(sizeY * 0.35);
    					yPos = (float)(sizeY * 0.305);
    					break;
    				case 5:
    					//yPos = (float)(sizeY * 0.415);
    					yPos = (float)(sizeY * 0.365);
    					break;
    				case 6:
    					//yPos = (float)(sizeY * 0.4825);
    					yPos = (float)(sizeY * 0.425);
    					break;
    				case 7:
    					//yPos = (float)(sizeY * 0.55);
    					yPos = (float)(sizeY * 0.48);
    					break;
    				case 8:
    					//yPos = (float)(sizeY * 0.618);
    					yPos = (float)(sizeY * 0.54);
    					break;
    				case 9:
    					yPos = (float)(sizeY * 0.60);
    					break;
    				default:
    					break;
    				}
    				if(aGraph.graph[i][j].type == "battleship00"){
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_front);
    		    		if(h > 0 && w > 0){
    		    			ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    						canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    		    		}
    				}
    				else if(aGraph.graph[i][j].type == "battleship01"){
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_middle);
    		    		if(h > 0 && w > 0){
    		    			ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    						canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    		    		}
    				}
    				else if(aGraph.graph[i][j].type == "battleship02"){
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_back);
    		    		if(h > 0 && w > 0){
    		    			ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    						canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    		    		}
    				}
    				
    				else if(aGraph.graph[i][j].type == "battleship10"){
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_front3);
    		    		if(h > 0 && w > 0){
    		    			ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    						canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    		    		}
    				}
    				else if(aGraph.graph[i][j].type == "battleship11"){
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_middle1);
    		    		if(h > 0 && w > 0){
    		    			ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    						canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    		    		}
    				}
    				else if(aGraph.graph[i][j].type == "battleship12"){
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_back1);
    		    		if(h > 0 && w > 0){
    		    			ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    						canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    		    		}
    				}
    				
    				else if(aGraph.graph[i][j].type == "battleship20"){
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_front);
    		    		if(h > 0 && w > 0){
    		    			ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    						canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    		    		}
    				}
    				else if(aGraph.graph[i][j].type == "battleship21"){
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_middle);
    		    		if(h > 0 && w > 0){
    		    			ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    						canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    		    		}
    				}
    				else if(aGraph.graph[i][j].type == "battleship22"){
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_back);
    		    		if(h > 0 && w > 0){
    		    			ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    						canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    		    		}
    				}
    				else if(aGraph.graph[i][j].type == "battleship30"){
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_front3);
    		    		if(h > 0 && w > 0){
    		    			ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    						canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    		    		}
    				}
    				else if(aGraph.graph[i][j].type == "battleship31"){
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_middle1);
    		    		if(h > 0 && w > 0){
    		    			ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    						canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    		    		}
    				}
    				else if(aGraph.graph[i][j].type == "battleship32"){
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_back1);
    		    		if(h > 0 && w > 0){
    		    			ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    						canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    		    		}
    				}
    				if(bAttacks[i][j]){						//Rendering Enemy Attacks
    					Bitmap ship;

    					if(aGraph.graph[i][j].tag == "boat"){
    						ship = BitmapFactory.decodeResource(getResources(), R.drawable.hit);
    					}
    					else{ship = BitmapFactory.decodeResource(getResources(), R.drawable.miss);}
    					
    					if(h > 0 && w > 0){
    						ship = Bitmap.createScaledBitmap(ship, h, w, false);
    						canvas.drawBitmap(ship, xPos, yPos, paint);
    					}
    				}
    					
    			}
    		}
    	}
	
	}
    
    //The view class that is responsible for the attack screen
    public class bView extends View{
	    
    	LinearLayout layout;
    	Button attack;
    	Bitmap main_menu, back_btn;
    	Bitmap[][] images;
    	
    	public bView(Context context){
    		super(context);    		
    		this.setBackgroundResource(R.drawable.battleship_main_screen);

    	}
	
    	@Override
    	protected void onDraw(Canvas canvas){
    		super.onDraw(canvas);
    		//If we want to have extra things drawn on the screen, this is where the code should go
    		
    		images = new Bitmap[10][10];
    		checkWinner();
    		
    		if(!singlePlayer)
    			uploadGame();
    		
    		//This first segment is devoted to drawing the button at the bottom 
    		Paint paint = new Paint();
    		paint.setStyle(Paint.Style.FILL);
    		paint.setColor(Color.RED);
    		float lx = 0;
    		float rx = getWidth();
    		float ty = getHeight();
    		ty = (float) (ty * 0.75);
    		float by = getHeight();
    		canvas.drawRect(lx, ty, rx, by, paint);
    		
    		int w = (int)(getHeight() * 0.25);
    		int h = (int)(getWidth() / 2);
    		
    		main_menu = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_main_menu_btn);
    		back_btn = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_back_btn);
    		if(h > 0 && w > 0){
    			main_menu = Bitmap.createScaledBitmap(main_menu, h, w, false);
    			back_btn = Bitmap.createScaledBitmap(back_btn, h, w, false);
    			canvas.drawBitmap(main_menu, (float)0,(float)(getHeight() * 0.75), null);
    			canvas.drawBitmap(back_btn, (float)getWidth() / 2,(float)(getHeight() * 0.75), null);
    		}
    		
       		for(int i = 0; i < 10; i++){
    			for(int j = 0; j < 10; j++){
    				float xPos = 0;
    				float yPos = 0;
    				switch(i){
	    				case 0:
	    					xPos = (float)(sizeX * 0.0);
	    					break;
	    				case 1:
	    					xPos = (float)(sizeX * 0.10);
	    					break;
	    				case 2:
	    					xPos = (float)(sizeX * 0.20);
	    					break;
	    				case 3:
	    					xPos = (float)(sizeX * 0.30);
	    					break;
	    				case 4:
	    					xPos = (float)(sizeX * 0.40);
	    					break;
	    				case 5:
	    					xPos = (float)(sizeX * 0.50);
	    					break;
	    				case 6:
	    					xPos = (float)(sizeX * 0.60);
	    					break;
	    				case 7:
	    					xPos = (float)(sizeX * 0.70);
	    					break;
	    				case 8:
	    					xPos = (float)(sizeX * 0.80);
	    					break;
	    				case 9:
	    					xPos = (float)(sizeX * 0.90);
	    					break;
	    				default:
	    					break;
    				}
    				switch(j){
    				case 0:
    					yPos = (float)(sizeY * 0.083);
    					break;
    				case 1:
    					//yPos = (float)(sizeY * 0.15);
    					yPos = (float)(sizeY * 0.13);
    					break;
    				case 2:
    					//yPos = (float)(sizeY * 0.215);
    					yPos = (float)(sizeY * 0.188);
    					break;
    				case 3:
    					//yPos = (float)(sizeY * 0.282);
    					yPos = (float)(sizeY * 0.248);
    					break;
    				case 4:
    					//yPos = (float)(sizeY * 0.35);
    					yPos = (float)(sizeY * 0.305);
    					break;
    				case 5:
    					//yPos = (float)(sizeY * 0.415);
    					yPos = (float)(sizeY * 0.365);
    					break;
    				case 6:
    					//yPos = (float)(sizeY * 0.4825);
    					yPos = (float)(sizeY * 0.425);
    					break;
    				case 7:
    					//yPos = (float)(sizeY * 0.55);
    					yPos = (float)(sizeY * 0.48);
    					break;
    				case 8:
    					//yPos = (float)(sizeY * 0.618);
    					yPos = (float)(sizeY * 0.54);
    					break;
    				case 9:
    					yPos = (float)(sizeY * 0.60);
    					break;
    				default:
    					break;
    				}
    				if(singlePlayer){
    					
	    				if(ai.aiGraph.graph[i][j].state == 3){			//Rendering Temporary Attacks
	    					lx = i * (sizeX / 10);
	    					rx = lx + (sizeX / 10);
	    					ty = yPos;
	    					by = (float)(yPos + (sizeY * 0.0666));
	    		    		paint.setColor(Color.BLACK);
	    					canvas.drawRect(lx, ty, rx, by, paint);
	    					
	    				}
	    				if(ai.aiGraph.graph[i][j].state == 1){		//Rendering Hits
	    					h = (int)(getHeight() * 0.0666);
	    					w = (int)(getWidth() / 10);

	    					if(ai.aiGraph.graph[i][j].tag == "boat"){
	    						images[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.hit);
	    					}
	    					else{images[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.miss);}
	    					
	    					if(h > 0 && w > 0){
	    						images[i][j] = Bitmap.createScaledBitmap(images[i][j], h, w, false);
	    						canvas.drawBitmap(images[i][j], xPos, yPos, null);
	    					}
	    				}
	    				if(aAttacks[i][j]){
	    					Log.i("Draw","Drawing aAttacks");
    						w = (int)(sizeY * 0.0666);
	    					h = (int)(sizeX / 10);
	    					if(ai.aiGraph.graph[i][j].tag == "boat"){
	    						images[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.hit);
	    					}
	    					else{images[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.miss);}
	    					
	    					if(h > 0 && w > 0){
	    						images[i][j] = Bitmap.createScaledBitmap(images[i][j], h, w, false);
	    						canvas.drawBitmap(images[i][j], xPos, yPos , null);
	    					}
	    				}
	    				
    				}
    				else{	//Multiplayer
        				//Note this needs to be changed to bGraph once bGraph becomes a real thing (i.e. it is being initialized someplace)
    					if(bGraph.graph[i][j].state == 3){			//Rendering Temporary Attacks
	    					lx = i * (sizeX / 10);
	    					rx = lx + (sizeX / 10);
	    					ty = yPos;
	    					by = (float)(yPos + (sizeY * 0.0666));
	    		    		paint.setColor(Color.BLACK);
	    					canvas.drawRect(lx, ty, rx, by, paint);
	    					
	    				}

    					if(bGraph.graph[i][j].state == 1){		//Rendering Hits
	    					h = (int)(getHeight() * 0.0666);
	    					w = (int)(getWidth() / 10);

	    					if(bGraph.graph[i][j].tag == "boat"){
	    						images[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.hit);
	    					}
	    					else{images[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.miss);}
	    					
	    					if(h > 0 && w > 0){
	    						images[i][j] = Bitmap.createScaledBitmap(images[i][j], h, w, false);
	    						canvas.drawBitmap(images[i][j], xPos, yPos, null);
	    					}
	    				}
	    				if(aAttacks[i][j]){
	    					Log.i("Draw","Drawing aAttacks");
    						w = (int)(sizeY * 0.0666);
	    					h = (int)(sizeX / 10);
	    					if(ai.aiGraph.graph[i][j].tag == "boat"){
	    						images[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.hit);
	    					}
	    					else{images[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.miss);}
	    					
	    					if(h > 0 && w > 0){
	    						images[i][j] = Bitmap.createScaledBitmap(images[i][j], h, w, false);
	    						canvas.drawBitmap(images[i][j], xPos, yPos , null);
	    					}
	    				}
    				}
    				if(aAttacks[i][j]){
    					lx = i * (sizeX / 10);
    					rx = lx + (sizeX / 10);
    					by = (float)((j * (sizeY*0.066)) + (sizeY * 0.083));
    					ty = (float)(by - sizeY * 0.066);
    					if(singlePlayer){
    						h = (int)(getHeight() * 0.0666);
	    					w = (int)(getWidth() / 10);
	    					if(ai.aiGraph.graph[i][j].tag == "boat"){
	    						images[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.hit);
	    					}
	    					if(bGraph.graph[i][j].tag == "boat"){
	    						images[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.hit);
	    					}
	    					else{images[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.miss);}
	    					
	    					
	    					
	    					
	    					if(h > 0 && w > 0){
	    						images[i][j] = Bitmap.createScaledBitmap(images[i][j], h, w, false);
	    						canvas.drawBitmap(images[i][j], xPos, yPos , null);
	    					}
    					}
    					else{
    						h = (int)(getHeight() * 0.0666);
	    					w = (int)(getWidth() / 10);
	    					if(bGraph.graph[i][j].tag == "boat"){
	    						images[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.hit);
	    					}
	    					else{images[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.miss);}
	    					
	    					if(h > 0 && w > 0){
	    						images[i][j] = Bitmap.createScaledBitmap(images[i][j], h, w, false);
	    						canvas.drawBitmap(images[i][j], xPos, yPos, null);
	    					}
    					}
    				
    				}
    			}
    		}
    	}
	
	}
    
    
   public class placeView extends View{
	    
    	LinearLayout layout;
    	Button attack;
    	Bitmap[][] ships;
    	Bitmap confirm, rotate, main_menu;
    	
    	public placeView(Context context){
    		super(context);    		
    		this.setBackgroundResource(R.drawable.battleship_main_screen);

    	}
	
    	@Override
    	protected void onDraw(Canvas canvas){
    		ships = new Bitmap[10][10];
    		super.onDraw(canvas);
    		//If we want to have extra things drawn on the screen, this is where the code should go
    		    		
    		//This first segment is devoted to drawing the button at the bottom 
    		Paint paint = new Paint();
    		paint.setStyle(Paint.Style.FILL);
    		paint.setColor(Color.RED);
    		float lx = 0;
    		float rx = getWidth();
    		float ty = getHeight();
    		ty = (float) (ty * 0.75);
    		float by = getHeight();
    		canvas.drawRect(lx, ty, rx, by, paint);
    		
    		int w = (int)(getHeight() * 0.25);
    		int h = (int)(getWidth() / 2);
    		
    		confirm = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_confirm_btn);
    		rotate = BitmapFactory.decodeResource(getResources(), R.drawable.temp_boat);
    		if(h > 0 && w > 0){
    			confirm = Bitmap.createScaledBitmap(confirm, h, w, false);
    			rotate = Bitmap.createScaledBitmap(rotate, h, w, false);
    			canvas.drawBitmap(confirm, (float)0,(float)(getHeight() * 0.75), null);
    			canvas.drawBitmap(rotate, (float)getWidth() / 2,(float)(getHeight() * 0.75), null);
    		}
    		
    		
       		for(int i = 0; i < 10; i++){
    			for(int j = 0; j < 10; j++){
    				float xPos = 0;
    				float yPos = 0;
    				switch(i){
	    				case 0:
	    					xPos = (float)(sizeX * 0.0);
	    					break;
	    				case 1:
	    					xPos = (float)(sizeX * 0.10);
	    					break;
	    				case 2:
	    					xPos = (float)(sizeX * 0.20);
	    					break;
	    				case 3:
	    					xPos = (float)(sizeX * 0.30);
	    					break;
	    				case 4:
	    					xPos = (float)(sizeX * 0.40);
	    					break;
	    				case 5:
	    					xPos = (float)(sizeX * 0.50);
	    					break;
	    				case 6:
	    					xPos = (float)(sizeX * 0.60);
	    					break;
	    				case 7:
	    					xPos = (float)(sizeX * 0.70);
	    					break;
	    				case 8:
	    					xPos = (float)(sizeX * 0.80);
	    					break;
	    				case 9:
	    					xPos = (float)(sizeX * 0.90);
	    					break;
	    				default:
	    					break;
    				}
    				switch(j){
    				case 0:
    					yPos = (float)(sizeY * 0.083);
    					break;
    				case 1:
    					//yPos = (float)(sizeY * 0.15);
    					yPos = (float)(sizeY * 0.13);
    					break;
    				case 2:
    					//yPos = (float)(sizeY * 0.215);
    					yPos = (float)(sizeY * 0.188);
    					break;
    				case 3:
    					//yPos = (float)(sizeY * 0.282);
    					yPos = (float)(sizeY * 0.248);
    					break;
    				case 4:
    					//yPos = (float)(sizeY * 0.35);
    					yPos = (float)(sizeY * 0.305);
    					break;
    				case 5:
    					//yPos = (float)(sizeY * 0.415);
    					yPos = (float)(sizeY * 0.365);
    					break;
    				case 6:
    					//yPos = (float)(sizeY * 0.4825);
    					yPos = (float)(sizeY * 0.425);
    					break;
    				case 7:
    					//yPos = (float)(sizeY * 0.55);
    					yPos = (float)(sizeY * 0.48);
    					break;
    				case 8:
    					//yPos = (float)(sizeY * 0.618);
    					yPos = (float)(sizeY * 0.54);
    					break;
    				case 9:
    					yPos = (float)(sizeY * 0.60);
    					break;
    				default:
    					break;
    				}
    				if(aGraph.graph[i][j].state == 3){
    					lx = i * (sizeX / 10);
    					rx = lx + (sizeX / 10);
    					//by = (float)((j * (sizeY*0.0666)) + (sizeY * 0.083));
    					//ty = (float)(by - sizeY * 0.0666);
    					ty = yPos;
    					by = (float)(yPos + (sizeY * 0.0666));
    		    		paint.setColor(Color.YELLOW);
    					canvas.drawRect(lx, ty, rx, by, paint);
    				}
    				if(aGraph.graph[i][j].type == "battleship00"){
    					Log.i("Draw","Drawing the Boat Image");
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_front);
    					h = (int)(getHeight() * 0.0666);
    					w = (int)(getWidth() / 10);
    					ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    					canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    					//ship.setHeight(h);
    					//ship.setWidth((int)getWidth() / 10);
    				}
    				else if(aGraph.graph[i][j].type == "battleship01"){
    					Log.i("Draw","Drawing the Boat Image");
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_middle);
    					h = (int)(getHeight() * 0.0666);
    					w = (int)(getWidth() / 10);
    					ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    					canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    				}
    				else if(aGraph.graph[i][j].type == "battleship02"){
    					Log.i("Draw","Drawing the Boat Image");
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_back);
    					h = (int)(getHeight() * 0.0666);
    					w = (int)(getWidth() / 10);
    					ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    					canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    				}
    				
    				else if(aGraph.graph[i][j].type == "battleship10"){
    					Log.i("Draw","Drawing the Boat Image");
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_front3);
    					h = (int)(getHeight() * 0.0666);
    					w = (int)(getWidth() / 10);
    					ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    					canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    				}
    				else if(aGraph.graph[i][j].type == "battleship11"){
    					Log.i("Draw","Drawing the Boat Image");
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_middle1);
    					h = (int)(getHeight() * 0.0666);
    					w = (int)(getWidth() / 10);
    					ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    					canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    				}
    				else if(aGraph.graph[i][j].type == "battleship12"){
    					Log.i("Draw","Drawing the Boat Image");
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_back1);
    					h = (int)(getHeight() * 0.0666);
    					w = (int)(getWidth() / 10);
    					ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    					canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    				}
    				
    				else if(aGraph.graph[i][j].type == "battleship20"){
    					Log.i("Draw","Drawing the Boat Image");
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_front);
    					h = (int)(getHeight() * 0.0666);
    					w = (int)(getWidth() / 10);
    					ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    					canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    				}
    				else if(aGraph.graph[i][j].type == "battleship21"){
    					Log.i("Draw","Drawing the Boat Image");
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_middle);
    					h = (int)(getHeight() * 0.0666);
    					w = (int)(getWidth() / 10);
    					ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    					canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    				}
    				else if(aGraph.graph[i][j].type == "battleship22"){
    					Log.i("Draw","Drawing the Boat Image");
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_back);
    					h = (int)(getHeight() * 0.0666);
    					w = (int)(getWidth() / 10);
    					ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    					canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    				}
    				else if(aGraph.graph[i][j].type == "battleship30"){
    					Log.i("Draw","Drawing the Boat Image");
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_front3);
    					h = (int)(getHeight() * 0.0666);
    					w = (int)(getWidth() / 10);
    					ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    					canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    				}
    				else if(aGraph.graph[i][j].type == "battleship31"){
    					Log.i("Draw","Drawing the Boat Image");
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_middle1);
    					h = (int)(getHeight() * 0.0666);
    					w = (int)(getWidth() / 10);
    					ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    					canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    				}
    				else if(aGraph.graph[i][j].type == "battleship32"){
    					Log.i("Draw","Drawing the Boat Image");
    					ships[i][j] = BitmapFactory.decodeResource(getResources(), R.drawable.battleship_back1);
    					h = (int)(getHeight() * 0.0666);
    					w = (int)(getWidth() / 10);
    					ships[i][j] = Bitmap.createScaledBitmap(ships[i][j], h, w, false);
    					canvas.drawBitmap(ships[i][j], xPos, yPos , paint);
    				}
    			}
    		}

    		paint.setColor(Color.BLACK);
    		paint.setTextSize(50);
    		canvas.drawText("Place your Battleships!", (getWidth() / 2) - 50, getHeight() / 2, paint);
    	}
	
	}
    
    
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ///////////Creating Functions that correspond to various buttons////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    public void main_menu_btn(View v){
    	curView = 0;
    	reset();
    	setContentView(R.layout.activity_main);
    }
   public void main_start_single(View v){
	   curView = 2;
	   singlePlayer = true;
	   ai = new AI();
	   setContentView(place);
	   place.draw(canvas);
   }
   public void main_start_multi(View v){
	   curView = 2;
	   singlePlayer = false;
	   setContentView(place);
	   place.draw(canvas);
   }
   
	public void main_start_btn(View v){		
        setContentView(R.layout.setup_game);
        curView = 1;
	}
	
	public void setup_start_btn(View v){
		curView = 2;
		if(singlePlayer){
			ai = new AI();
			setContentView(place);
			place.draw(canvas);
		}
		else{
			setContentView(place);
		}
	}

	public void back_btn(View v){
		if(curView == 0 || curView == 1){
			setContentView(R.layout.activity_main);
			if(curView == 1)
				curView = 0;
		}
		else{
			setContentView(game);
			curView = 2;
		}
	}
	
	public void options_single_player(View v){
		singlePlayer = !singlePlayer;
	}
	
	public void different_btn(View v){
		setContentView(R.layout.options);
	}
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////Resets all the Arrays, useful for starting a new game///////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
	void reset(){
		for(int i = 0; i < 10; i++){
			if(i < 5)
				boats[i].placed = false;
			for(int j = 0; j < 10; j++){
				aGraph.graph[i][j].state = 0;
				aGraph.graph[i][j].tag = "water";
				aGraph.graph[i][j].type = "water";
				bGraph.graph[i][j].state = 0;
				bGraph.graph[i][j].tag = "water";
				bGraph.graph[i][j].type = "water";
				aAttacks[i][j] = false;
				bAttacks[i][j] = false;
			}
		}
	}

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    //////////This is the segment of code dedicated to storing data on the game state///////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    public class Graph{
    	Node[][] graph;
    	public Graph(){
    		graph = new Node[10][10];
    		for(int i =0; i < 10; i++){
    			for(int j = 0; j < 10; j++){
    				graph[i][j] = new Node(i,j);
    			}	
    		}
    	}
    	//Called when the user touches the screen.
    	//Takes coordinate of event and logs it
    	public void touch(int x, int y){
    		graph[x][y].state = 1;
    		Context context = getApplicationContext();
    		String temp = "You pressed " + x + ", " + y;
    		Toast toast = Toast.makeText(context, temp, Toast.LENGTH_SHORT);
    		toast.show();
    	}
    	public boolean placeBoat(Boat boat, int x, int y){
			Log.i("placeBoat","Start");
    		boolean success = true;
    		
    		if(x > 9 || x < 0 || y > 9 || y < 0){
    			return false;
    		}
    		
    		boolean[] valid = new boolean[boat.length];
    		
    		for(int i = 0; i < boat.length; i++){
    			if(graph[x][y].tag == "water"){
    				valid[i] = false;
    				switch(boat.direction){
    					case 0:						//Direction = UP
    						if((y - i) >= 0){
    							if(graph[x][y-i].tag == "water")
    								valid[i] = true;
    						}
    						break;
    					case 1:						//Direction = RIGHT
    						if((x + i) < 10){
    							if(graph[x + i][y].tag == "water")
    								valid[i] = true;
    						}
    						break;
    					case 2:						//Direction = DOWN
    						if((y + i) < 10){
    							if(graph[x][y + i].tag == "water")
    								valid[i] = true;
    						}
    						break;
    					case 3:						//Direction = LEFT
    						if((x - i) >= 0){
    							if(graph[x - i][y].tag == "water")
    								valid[i] = true;
    						}
    						break;
    					default:
    						System.out.println("You've been Trolled");
    						break;
    				}
    			}
    		}
    		for(int i = 0; i < boat.length; i++){
    			if(!valid[i])
    				success = false;
    			else{Log.i("placeBoat","Bad Location");}
    		}
    		//If there is a collision ensure that all the squares are reverted to being water
    		if(success){
	    		for(int i = 0; i < boat.length; i++){
	    			String tempStr = "Boat Piece: " + String.valueOf(i);
	    			Log.i("placeBoat", tempStr);
	    			switch(boat.direction){
	    				case 0:						//Direction = UP
	    					graph[x][y-i].tag = "boat";
	    					tempStr = "Boat Piece: " + String.valueOf(i) + " placed";
	    		    		Log.i("placeBoat", tempStr);
	    					break;
	    				case 1:						//Direction = RIGHT
	    					graph[x + i][y].tag = "boat";
	    					break;
	    				case 2:						//Direction = DOWN
	    					graph[x][y + i].tag = "boat";
	    					break;
	    				case 3:						//Direction = LEFT
	    					graph[x - i][y].tag = "boat";
	    					break;
	    				default:
	    					System.out.println("You've been Trolled");
	    					break;
	    			}
	    		}
    		}
			Log.i("placeBoat","Finish");
    		return success;
    	}
    	public boolean placeBoatTemp(Boat boat, int x, int y){
			Log.i("placeBoat","Start");
    		boolean success = true;
    		
    		//Clearing all temp value from the graph
    		for(int i = 0; i < 10; i++){
    			for(int j = 0; j < 10; j++){
    				//If the square was a temp before, ensure that its type is set to water
    				if(graph[i][j].state == 3)
    					graph[i][j].type = "water";
    				graph[i][j].state = 0;
    				
    			}
    		}
    		
    		if(x > 9 || x < 0 || y > 9 || y < 0){
    			return false;
    		}
    		
    		boolean[] valid = new boolean[boat.length];
    		
    		for(int i = 0; i < boat.length; i++){
    			if(graph[x][y].tag == "water"){
    				valid[i] = false;
    				switch(boat.direction){
    					case 0:						//Direction = UP
    						if((y - i) >= 0){
    							if(graph[x][y-i].tag == "water")
    								valid[i] = true;
    						}
    						break;
    					case 1:						//Direction = RIGHT
    						if((x + i) < 10){
    							if(graph[x][y-i].tag == "water")
    								valid[i] = true;
    						}
    						break;
    					case 2:						//Direction = DOWN
    						if((y + i) < 10){
    							if(graph[x][y-i].tag == "water")
    								valid[i] = true;
    						}
    						break;
    					case 3:						//Direction = LEFT
    						if((x - i) >= 0){
    							if(graph[x][y-i].tag == "water")
    								valid[i] = true;
    						}
    						break;
    					default:
    						System.out.println("You've been Trolled");
    						break;
    				}
    			}
    		}
    		for(int i = 0; i < boat.length; i++){
    			if(!valid[i]){
    				success = false;
    				Log.i("placeBoat","Bad Location");
    			}
    			else{Log.i("placeBoat","Good Location");}
    		}
    		if(success){
	    		for(int i = 0; i < boat.length; i++){
	    			String tempStr = "Boat Piece: " + String.valueOf(i);
	    			Log.i("placeBoat", tempStr);
	    			switch(boat.direction){
	    				case 0:						//Direction = UP
	    					graph[x][y-i].state = 3;
	    					tempStr = boat.type + String.valueOf(i);
	    					Log.i("placeBoat",tempStr);
	    					graph[x][y-i].type = tempStr;
	    					tempStr = "Boat Piece: " + String.valueOf(i) + " placed.State: " + String.valueOf(graph[x][y-i].state);
	    		    		Log.i("placeBoat", tempStr);
	    					break;
	    				case 1:						//Direction = RIGHT
	    					graph[x + i][y].state = 3;
	    					break;
	    				case 2:						//Direction = DOWN
	    					graph[x][y + i].state = 3;
	    					break;
	    				case 3:						//Direction = LEFT
	    					graph[x - i][y].state = 3;
	    					break;
	    				default:
	    					System.out.println("You've been Trolled");
	    					break;
	    			}
	    		}
    		}
			Log.i("placeBoat","Finish");
    		return success;
    	}
    	
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////These functions send and receive information from the server////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    public void parseUp(){
    	String temp = "";
    	for(int i = 0; i < 10; i++){
    		for(int j = 0; j < 10; j++){
    			if(aAttacks[i][j]) temp += String.valueOf(1);
    			else temp += String.valueOf(0);
    		}
    	}
    	
    	//SEND TEMP TO THE SERVER
    	
    }
    
    public void parseDown(){
    	//GET TEMP STRING FROM SERVER
    	String temp = "";
    	
    	//Constructing a string that represents the most recent version of the enemies attack array
    	String temp1 = "";
    	for(int i = 0; i < 10; i++){
    		for(int j = 0; j < 10; j++){
    			if(bAttacks[i][j]) temp1 += String.valueOf(1);
    			else temp1 += String.valueOf(0);
    		}
    	}
    	
    	//If that version is not the same as the version downloaded from the server, update the local version
    	if(temp1 != temp){
    		int tempCounter = 0;
	    	for(int i = 0; i < 10; i++){
	    		for(int j = 0; j < 10; j++){
	    			int tempI = i;
	    			int tempJ = j;
	    			if(tempI == 0 && j > 0) tempI = 10;
	    			if(tempJ == 0) tempJ = 1;
	    			
	    			if(temp.charAt(tempI * tempJ) == '0'){ bAttacks[i][j] = false;}
	    			else if(temp.charAt(tempI * tempJ) == '1'){ bAttacks[i][j] = true;}
	    			
	    			if(bAttacks[i][j] && aGraph.graph[i][j].tag == "boat"){
	    				tempCounter++;
	    			}
	    		}
	    	}
	    	boats_remaining = 17 - tempCounter;
    	}
    }
    
    //Classic CS node data type
    //Sits in a graph, holds information
    public class Node{
    	int x,y;			//Coordinates of the node [0-9][0-9]
    	int state;			//0 = Nothing, 1 = hit   // 3 = temp      I wanted this to be an int in case we get into complex types
    	String tag;			//This is a string representation of the state of a node. Used to denote whether the node is water, boat, or temp
    	String type;		//What type of boat is this node? 
    	public Node(int x, int y){
    		tag = "water";
    		type = "water";
    		this.x = x;
    		this.y = y;
    		state = 0;
    	}	
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////This ADT is representative of an AI Opponent////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    
    //AI begins with a list of all 100 unique moves in a random order. This is intended to be read in one instruction
    //at a time to simulate actual gameplay
    //AI also begins with randomized placements of all ships on a graph aiGraph that can later be transfered to
    //the place of a player graph
    //Note that the AI only currently has 3 boats, this is because I could only think of the names of three of the boats
    //when I wrote this portion of the code, upon seeing this I would appreciate it if somebody remedied this small issue
    public class AI{
    	public Coord[] moves;					//An array that holds all of the AI's attacks
    	public Graph aiGraph;					//The graph that the AI places ships on
    	int boats_remaining;					//The number of boats that the ai has remaining. If this reaches 0, player wins
    	
    	//This portion of the code could be written with an array of boats easily, it is written this way so that it is obvious what is happening in the code.
    	//When I wrote this code I had not yet written the place function, and it was helpful to name all the ships
    	Boat battle_ship;
    	Boat submarine;
    	Boat air_craft_carrier;
    	Boat destroyer;
    	Boat patrol;
    	
    	//Constructor
    	AI(){
    		//This segment is generating coordinates for the random moves
    		Log.i("AI", "Constructing Moves");
    		aiGraph = new Graph();			
    		moves = new Coord[100];			
    		Random r = new Random();
    		int i, tempX, tempY;
    		i = 0;
    		boolean found;
    		
    		//This section of code determines all of the attacks that the AI will make.
    		//Note: The AI will update this array (moves) if they manage to hit a ship
    		Log.i("AI", "Constructing Moves Loop Start");
    		while(i < 100){
    			found = false;
    			tempX = (r.nextInt(10) + 0);
    			tempY = (r.nextInt(10) + 0);

    			
    			//This segment of code is meant to ensure that there are no repeats in generated coordinates
    			//It is commented out at the moment because it is hanging for some reason
    			for(int it = 0; it < i; it++){
    				if(moves[it].x == tempX && moves[it].y == tempY)
    					found = true;
    			}
    			if(!found){
    				//Log.i("AI", "Constructing Moves Loop Placing");
    				moves[i] = new Coord(tempX, tempY);
    				//Log.i("AI", "Constructing Moves Loop Placed");
    				i++;
    			}
    		}
    		//This segment of code is devoted to placing the AIs boats
    		Log.i("AI", "Placing Boats");
    		
    		/////////////////////////////////////////////////////////////////////////////////
    		//NOTE: THIS NEEDS TO BE CHANGED TO 17 WHEN THERE ARE 5 BOATS IN THE GAME////////
    		/////////////////////////////////////////////////////////////////////////////////
    		boats_remaining = 17;			
			
			for(int it = 0; i < 10; i++){
				for(int j = 0; j < 10; j++){
					aiGraph.graph[it][j] = new Node(it, j);
				}
			}
			

   			//Generating a random position for the battle_ship
       		Log.i("AI","Attempting to make a boat");
    		battle_ship = new Boat(4,"battleship", r.nextInt(3) + 0);	
       		while(!battle_ship.placed){
       			tempX = (r.nextInt(9) + 0);
   				tempY = (r.nextInt(9) + 0);
   				battle_ship.placed = aiGraph.placeBoat(battle_ship, tempX, tempY);
       		}

   			//Generating a random position for the submarine
       		
   			submarine = new Boat(3, "submarine", r.nextInt(3) + 0);		
   			while(!submarine.placed){
       			tempX = (r.nextInt(9) + 0);
   				tempY = (r.nextInt(9) + 0);
   				submarine.placed = aiGraph.placeBoat(submarine, tempX, tempY);
   			}
   			
   			//Generating a random position for the Air Craft Carrier
   			air_craft_carrier = new Boat(5, "aircraftcarrier", r.nextInt(3) + 0);
   			while(!air_craft_carrier.placed){
       			tempX = (r.nextInt(9) + 0);
   				tempY = (r.nextInt(9) + 0);
   				air_craft_carrier.placed = aiGraph.placeBoat(air_craft_carrier, tempX, tempY);
   			}
   			//Generating a random position for the Destroyer
   			air_craft_carrier = new Boat(3, "destroyer", r.nextInt(3) + 0);
   			while(!air_craft_carrier.placed){
       			tempX = (r.nextInt(9) + 0);
   				tempY = (r.nextInt(9) + 0);
   				air_craft_carrier.placed = aiGraph.placeBoat(air_craft_carrier, tempX, tempY);
   			}
   			//Generating a random position for the Patrol Boat
   			air_craft_carrier = new Boat(2, "patrol", r.nextInt(3) + 0);
   			while(!air_craft_carrier.placed){
       			tempX = (r.nextInt(9) + 0);
   				tempY = (r.nextInt(9) + 0);
   				air_craft_carrier.placed = aiGraph.placeBoat(air_craft_carrier, tempX, tempY);
   			}
			
			
    		Log.i("AI", "Finished");
    	}
    	
    	//When the AI hits a ship, this function is called to determine where the shots should go from here on out
    	void makeNewAttackArray(int x, int y){
    		Log.i("newMoves","Check 1");
    		
    		Coord[] newMoves = new Coord[100];
    		
    		//Begins by copying all the previous moves to a new array
    		int i;
    		boolean[] found = new boolean[4];
    		for(i = 0; i < turn-1; i++){
    			newMoves[i] = new Coord(moves[i].x, moves[i].y);
    			
    			//Checking which of the surrounding tiles have been hit before
    			if(moves[i].x == x && moves[i].y == (y - 1))			//UP
    				found[0] = true;
    			else if(moves[i].x == (x + 1) && moves[i].y == y)		//RIGHT
    				found[1] = true;
    			else if(moves[i].x == x && moves[i].y == (y + 1))		//DOWN
    				found[2] = true;
    			else if(moves[i].x == (x - 1) && moves[i].y == y)		//LEFT
    				found[3] = true;
    		}

    		Log.i("newMoves","Check 2");
    		
    		i = turn - 1;
    		if(!found[0]){
    			newMoves[i].x = x;
    			newMoves[i].y = (y - 1);
    			i++;
    		}
    		if(!found[1]){
    			newMoves[i].x = (x + 1);
    			newMoves[i].y = y;
    			i++;
    		}
    		if(!found[2]){
    			newMoves[i].x = x;
    			newMoves[i].y = (y + 1);
    			i++;
    		}
    		if(!found[3]){
    			newMoves[i].x = (x - 1);
    			newMoves[i].y = y;
    			i++;
    		}
    		
    		Log.i("newMoves","Check 3");
    		
    		//Declaring Variables here to save processing during the loop below
    		Random r = new Random();
    		int tempX, tempY;
    		boolean temp;
    		
    		//Generating the rest of the moves for the AI
    		while(i < 100){
    			temp = false;
    			tempX = (r.nextInt(10) + 0);
    			tempY = (r.nextInt(10) + 0);

    			
    			//This segment of code is meant to ensure that there are no repeats in generated coordinates
    			//It is commented out at the moment because it is hanging for some reason
    			for(int it = 0; it < i; it++){
    				if(moves[it].x == tempX && moves[it].y == tempY)
    					temp = true;
    			}
    			if(!temp){
    				//Log.i("AI", "Constructing Moves Loop Placing");
    				moves[i] = new Coord(tempX, tempY);
    				//Log.i("AI", "Constructing Moves Loop Placed");
    				i++;
    			}
    		}
    		
    		
    		Log.i("newMoves","Check 4");
    		
    		//Copy the temp array back into "moves"
    		for(i = 0; i < 100; i++){
    			moves[i].x = newMoves[i].x;
    			moves[i].y = newMoves[i].y;
    		}
    	}
    	
    }
    //A simple ADT that holds two integers
    public class Coord{
    	public int x;
    	public int y;
    	Coord(int x, int y){
    		this.x = x;
    		this.y = y;
    	}
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////This ADT is representative of a ship or boat////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    //Represents a boat, basically used to help construct the graph
    //Can be applied to any type of boat/ship found in the game battleship
    public class Boat{
    	int length;										//How many squares the boat occupies
    	String type;									//What type of ship is this? Battleship? Submarine? etc...
    	int direction;									//Which direction is the ship facing 0 = up, 1 = right, 2 = down, 3 = left 
    	int hits_till_sunk;								//The number of shots remaining until the boat sinks
    	Coord pos;
    	boolean placed;
    	Boat(int length, String type, int direction){
    		this.length = length;
    		placed = false;
    		this.type = type;
    		this.direction = direction;
    		this.hits_till_sunk = length;
    	}
    }
    
    //#########################################################################################################################\\
	//#########################################################################################################################\\
	//################################################# Server Functions ######################################################\\
	//#########################################################################################################################\\
	//#########################################################################################################################\\
    
	
    //Server Variables\\
	Gson gson = new Gson();
	private ServerCall downloader = null;				// Background downloader.
	public  String localGameID;							// Randomly generated string for Game ID
	public int localNumPlayers;							// Number of players on a game
	public int playerID;								// 0 = playerA; 1 = playerB
	private static final String URL = "http://ucsc-cmps121-battleship.appspot.com/classexample/default/";
	public  static final String SERVER_URL_PREFIX = "http://ucsc-cmps121-battleship.appspot.com/classexample/default/";
	private static final String DOWNLOADPOST = "downloadGame.json";
	private static final String UPLOADPOST = "uploadGame.json";
	SerialGame remoteGame = new SerialGame();
    ServerIO sIO = new ServerIO(URL, DOWNLOADPOST, UPLOADPOST);
	
	
    
	public void downloadGame(){
		if(localGameID == null){								// we currently are not part of a game
			remoteGame = sIO.downloadJSON();						// look for a game
			Log.i("Download", "Trying to find open game");			
			if(remoteGame.result.equals("no open games")){		// no open games
				Log.i("Download", "Trying to setup game");
				SerialGame sGame = makeGame();						// make game
				sIO.uploadJSON(sGame);								// upload game												
			}else{
				localGameID = remoteGame.gameID;						// store gameID locally
				remoteGame.numPlayers = 2;								// edit numPlayers
				remoteGame.open = false;								// edit open
				playerID = 1;											// save which player we are (playerB)
				sIO.uploadJSON(remoteGame);								//
			}
		}else{
			remoteGame = sIO.downloadJSON(localGameID);						// download game with known gameID
			if(playerID == 0){												// update local graphs depending on which player we are
				parseDownA(remoteGame.playA);
				parseDownA(remoteGame.playB);
			}else{
				parseDownA(remoteGame.playB);
				parseDownB(remoteGame.playA);
			}
		}
	}
	
	public void uploadGame(){
		SerialGame sGame = convertCurrGame();
		sIO.uploadJSON(sGame);
	}
	
	// class that should create a secure random string 
	public final static class randomGameID{
		private static SecureRandom random = new SecureRandom();
		
		public static String nextGameId(){
			return new BigInteger(130, random).toString(32);
		}
	}
	
	public SerialGame makeGame(){
		//make a new game and save relevent variables locally	
		SerialGame sGame = new SerialGame();
		canAttack = true;
		sGame.gameID = randomGameID.nextGameId();
		localGameID = sGame.gameID;								// save locally
		sGame.maxPlayers = 2;
		sGame.numPlayers = 1;
		localNumPlayers = 1;
		sGame.open = true;
		sGame.playA = null;
		sGame.playB = null;
		sGame.turn = 0;
		return sGame;
	}
	
	public SerialGame convertCurrGame(){
		SerialGame sGame = new SerialGame();
		sGame.gameID = localGameID;			
		sGame.maxPlayers = 2;
		sGame.numPlayers = localNumPlayers;
		// set playA and playB
		if(playerID == 0){
			sGame.playA = parseAUp();		//parse our attacks into A
			sGame.playB = parseBUp();		//parse opponent attacks into B
		}else{
			sGame.playB = parseAUp();		//parse our attacks into B
			sGame.playA = parseBUp();		//parse opponent attacks into A
		}
		sGame.turn = turn;					
		return sGame;
	}
	
    public String parseAUp(){
    	String temp = "";
    	for(int i = 0; i < 10; i++){
    		for(int j = 0; j < 10; j++){
    			if(aAttacks[i][j]) temp.concat(String.valueOf(1));
    			else temp.concat(String.valueOf(0));
    		}
    	}
    	return temp;  	
    }
    
    public String parseBUp(){
    	String temp = "";
    	for(int i = 0; i < 10; i++){
    		for(int j = 0; j < 10; j++){
    			if(bAttacks[i][j]) temp.concat(String.valueOf(1));
    			else temp.concat(String.valueOf(0));
    		}
    	}
    	return temp;
    }
    
    public void parseDownA(String server){
    	String temp = server;
    	
    	//Constucnt a current state of our attacks
    	String temp1 = "";
    	for(int i = 0; i < 10; i++){
    		for(int j = 0; j < 10; j++){
    			if(aAttacks[i][j]) temp1 += String.valueOf(1);
    			else temp1 += String.valueOf(0);
    		}
    	}
    	
    	//If that version is not the same as the version downloaded from the server, update the local version
    	if(temp1 != temp){
    		int tempCounter = 0;
	    	for(int i = 0; i < 10; i++){
	    		for(int j = 0; j < 10; j++){
	    			int tempI = i;
	    			int tempJ = j;
	    			if(tempI == 0 && j > 0) tempI = 10;
	    			if(tempJ == 0) tempJ = 1;
	    			
	    			if(temp.charAt(tempI * tempJ) == '0'){ aAttacks[i][j] = false;}
	    			else if(temp.charAt(tempI * tempJ) == '1'){ aAttacks[i][j] = true;}
	    			
	    			if(aAttacks[i][j] && bGraph.graph[i][j].tag == "boat"){
	    				tempCounter++;
	    			}
	    		}
	    	}
	    	boats_remaining = 17 - tempCounter;
    	}
    }
    
    public void parseDownB(String server){
    	String temp = server;
    	
    	//Constructing a string that represents the most recent version of the enemies attack array
    	String temp1 = "";
    	for(int i = 0; i < 10; i++){
    		for(int j = 0; j < 10; j++){
    			if(bAttacks[i][j]) temp1 += String.valueOf(1);
    			else temp1 += String.valueOf(0);
    		}
    	}
    	
    	//If that version is not the same as the version downloaded from the server, update the local version
    	if(temp1 != temp){
    		int tempCounter = 0;
	    	for(int i = 0; i < 10; i++){
	    		for(int j = 0; j < 10; j++){
	    			int tempI = i;
	    			int tempJ = j;
	    			if(tempI == 0 && j > 0) tempI = 10;
	    			if(tempJ == 0) tempJ = 1;
	    			
	    			if(temp.charAt(tempI * tempJ) == '0'){ bAttacks[i][j] = false;}
	    			else if(temp.charAt(tempI * tempJ) == '1'){ bAttacks[i][j] = true;}
	    			
	    			if(bAttacks[i][j] && aGraph.graph[i][j].tag == "boat"){
	    				tempCounter++;
	    			}
	    		}
	    	}
	    	boats_remaining = 17 - tempCounter;
    	}
    }
    
}
