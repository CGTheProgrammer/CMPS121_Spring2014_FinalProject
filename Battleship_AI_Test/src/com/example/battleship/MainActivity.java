package com.example.battleship;

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

public class MainActivity extends ActionBarActivity {
	//The graph of the players area
	public Graph aGraph;
	public boolean[][] aAttacks;
	//The graph of the opponents area
	public Graph bGraph;
	public boolean[][] bAttacks;
	public boolean canAttack;
	
	
	public int sizeX, sizeY;
	//The main menu
	public View main;
	//The main game area
	public aView game;
	//The attack view
	public bView attack;
	public int curView;
	public boolean singlePlayer;
	public AI ai;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //This is where we are creating our view
        singlePlayer = false;
        game = new aView(getApplicationContext());
        attack = new bView(getApplicationContext());
        setContentView(R.layout.activity_main);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        sizeX = size.x;
        sizeY = size.y;
        //Setting up the system that senses 
        game.setOnTouchListener(new View.OnTouchListener(){
        	@Override
        	public boolean onTouch(View v, MotionEvent event){
        		
        		//Determining Which Square the user has clicked and marking it as clicked
        		float x =(event.getX() / (sizeX / 10));
        		float y = (event.getY() / (sizeY / 12));
        		
        		if(y >= 10){
        			setContentView(attack);
        			curView = 3;
        		}
        		return true;
        	}
        });
        attack.setOnTouchListener(new View.OnTouchListener(){
        	@Override
        	public boolean onTouch(View v, MotionEvent event){
        		
        		//Determining Which Square the user has clicked and marking it as clicked
        		float x =(event.getX() / (sizeX / 10));
        		float y = (event.getY() / (sizeY / 12));
        		
        		if(y < 10 && x < 10){
        			aGraph.touch((int)x,(int)y);
        			aAttacks[(int)x][(int)y] = true;
        	        attack.invalidate();
        		}
        		else if(y >= 10){
        			setContentView(game);
        			curView = 3;
        		}
        		return true;
        	}
        });
        //Creating the graph that tracks the back of the screen
        aGraph = new Graph();
        
        aAttacks = new boolean[10][10];
        bAttacks = new boolean[10][10];
        
        for(int i = 0; i < 10; i++){
        	for(int j = 0; j < 10; j++){
        		aAttacks[i][j] = false;
        		bAttacks[i][j] = false;
        	}
        }
        
        curView = 0;
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public void onBackPressed() {
		if(curView == 0 || curView == 1){
			setContentView(R.layout.activity_main);
			if(curView == 1)
				curView = 0;
		}
		else if(curView == 2){
			setContentView(R.layout.activity_main);
			curView = 0;
    	}
		else{
			setContentView(game);
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
    
    
    //This is my view class that I created, it will allow us to dynamically determine our users screen size
    //and load the view accordingly.
    //Additionally this is necessary in order to do work on the canvas (the way that the background and sensor system is
    //currently working)
    public class aView extends View{
	    
    	LinearLayout layout;
    	Button attack;
    	
    	public aView(Context context){
    		super(context);    		
    		this.setBackgroundResource(R.drawable.background1);

    	}
	
    	@Override
    	protected void onDraw(Canvas canvas){
    		super.onDraw(canvas);
    		//If we want to have extra things drawn on the screen, this is where the code should go

    		//This first segment is devoted to drawing the button at the bottom
    		Paint paint = new Paint();
    		paint.setStyle(Paint.Style.FILL);
    		paint.setColor(Color.RED);
    		float lx = 0;
    		float rx = getWidth();
    		float ty = getHeight();
    		ty -= getHeight() / 12;
    		float by = getHeight();
    		canvas.drawRect(lx, ty, rx, by, paint);
    		paint.setColor(Color.BLACK);
    		paint.setTextSize(20);
    		canvas.drawText("Go To ATTACK!", rx/2, ((ty + by) / 2), paint);
    		
    		
    		for(int i = 0; i < 10; i++){
    			for(int j = 0; j < 10; j++){
    				if(aGraph.graph[i][j].tag == "water"){
    					lx = i * (sizeX / 10);
    					rx = lx + (sizeX / 10);
    					by = j * (sizeY / 12);
    					ty = by + (sizeY / 12);
    		    		paint.setColor(Color.BLUE);
    					canvas.drawRect(lx, ty, rx, by, paint);
    				}
    				else if(aGraph.graph[i][j].tag == "boat"){
    					Bitmap boat = BitmapFactory.decodeResource(getResources(), R.drawable.temp_boat);
    					canvas.drawBitmap(boat, 0, 0, paint);
    				}
    				if(aGraph.graph[i][j].state == 1){
    					lx = i * (sizeX / 10);
    					rx = lx + (sizeX / 10);
    					by = j * (sizeY / 12);
    					ty = by + (sizeY / 12);
    					if(aGraph.graph[i][j].tag == "ship"){
    						paint.setColor(Color.RED);
    					}
    					else{paint.setColor(Color.WHITE);}
    					canvas.drawRect(lx, ty, rx, by, paint);
    				}
    			}
    		}
    	}
	
	}
    
    //The view class that is responsible for the attack screen
    public class bView extends View{
	    
    	LinearLayout layout;
    	Button attack;
    	
    	public bView(Context context){
    		super(context);    		
    		this.setBackgroundResource(R.drawable.background1);

    	}
	
    	@Override
    	protected void onDraw(Canvas canvas){
    		super.onDraw(canvas);
    		//If we want to have extra things drawn on the screen, this is where the code should go
    		
    		//This first segment is devoted to drawing the button at the bottom 
    		Paint paint = new Paint();
    		paint.setStyle(Paint.Style.FILL);
    		paint.setColor(Color.RED);
    		float lx = 0;
    		float rx = getWidth();
    		float ty = getHeight();
    		ty -= getHeight() / 12;
    		float by = getHeight();
    		canvas.drawRect(lx, ty, rx, by, paint);
    		paint.setColor(Color.BLACK);
    		paint.setTextSize(20);
    		canvas.drawText("Go To Main", rx/2, ((ty + by) / 2), paint);
    		
       		for(int i = 0; i < 10; i++){
    			for(int j = 0; j < 10; j++){
    				//Note this needs to be changed to bGraph once bGraph becomes a real thing (i.e. it is being initialized someplace)
    				if(singlePlayer){
    					if(ai.aiGraph.graph[i][j].tag == "water" || ai.aiGraph.graph[i][j].tag == "boat" ){
    						lx = i * (sizeX / 10);
	    					rx = lx + (sizeX / 10);
	    					by = j * (sizeY / 12);
	    					ty = by + (sizeY / 12);
	    		    		paint.setColor(Color.BLUE);
	    					canvas.drawRect(lx, ty, rx, by, paint);
	    				}

	    				//Note this needs to be changed to bGraph once bGraph becomes a real thing (i.e. it is being initialized someplace)
	    				if(ai.aiGraph.graph[i][j].state == 1){
	    					lx = i * (sizeX / 10);
	    					rx = lx + (sizeX / 10);
	    					by = j * (sizeY / 12);
	    					ty = by + (sizeY / 12);
	        				//Note this needs to be changed to bGraph once bGraph becomes a real thing (i.e. it is being initialized someplace)
	    					if(ai.aiGraph.graph[i][j].tag == "ship"){
	    						paint.setColor(Color.RED);
	    					}
	    					else{paint.setColor(Color.WHITE);}
	    					canvas.drawRect(lx, ty, rx, by, paint);
	    				}
    				}
    				else{
    					if(aGraph.graph[i][j].tag == "water" || aGraph.graph[i][j].tag == "boat" ){
    						lx = i * (sizeX / 10);
	    					rx = lx + (sizeX / 10);
	    					by = j * (sizeY / 12);
	    					ty = by + (sizeY / 12);
	    		    		paint.setColor(Color.BLUE);
	    					canvas.drawRect(lx, ty, rx, by, paint);
	    				}

	    				//Note this needs to be changed to bGraph once bGraph becomes a real thing (i.e. it is being initialized someplace)
	    				if(aGraph.graph[i][j].state == 1){
	    					lx = i * (sizeX / 10);
	    					rx = lx + (sizeX / 10);
	    					by = j * (sizeY / 12);
	    					ty = by + (sizeY / 12);
	        				//Note this needs to be changed to bGraph once bGraph becomes a real thing (i.e. it is being initialized someplace)
	    					if(aGraph.graph[i][j].tag == "ship"){
	    						paint.setColor(Color.RED);
	    					}
	    					else{paint.setColor(Color.WHITE);}
	    					canvas.drawRect(lx, ty, rx, by, paint);
	    				}
    				}
    			}
    		}
    	}
	
	}
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ///////////Creating Functions that correspond to various buttons////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    
	public void main_start_btn(View v){		
        setContentView(R.layout.setup_game);
        curView = 1;
	}
	
	public void setup_start_btn(View v){
		curView = 2;
		if(singlePlayer){
			ai = new AI();
			setContentView(game);
		}
		else{
			setContentView(game);
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
    		int dist_to_collision = 0;
    		for(int i = 0; i < boat.length; i++){
    			if(graph[x][y].tag == "water"){
    				switch(boat.direction){
    					case 0:						//Direction = UP
    						if((y - i) > 0){
    							if(graph[x][y-i].tag == "water")
    								graph[x][y-i].tag = "boat";
    							else{
    								success = false;
    								dist_to_collision = i;
    							}
    						}
    						else{
								success = false;
								dist_to_collision = i;
							}
    						break;
    					case 1:						//Direction = RIGHT
    						if((x + i) < 10){
    							if(graph[x + i][y].tag == "water")
    								graph[x + i][y].tag = "boat";
    							else{
    								success = false;
    								dist_to_collision = i;
    							}
    						}
    						else{
								success = false;
								dist_to_collision = i;
							}
    						break;
    					case 2:						//Direction = DOWN
    						if((y + i) < 10){
    							if(graph[x][y + i].tag == "water")
    								graph[x][y + i].tag = "boat";
    							else{
    								success = false;
    								dist_to_collision = i;
    							}
    						}
    						else{
								success = false;
								dist_to_collision = i;
							}
    						break;
    					case 3:						//Direction = LEFT
    						if((x - i) > 0){
    							if(graph[x - i][y].tag == "water")
    								graph[x - i][y].tag = "boat";
    							else{
    								success = false;
    								dist_to_collision = i;
    							}
    						}
    						else{
								success = false;
								dist_to_collision = i;
							}
    						break;
    					default:
    						System.out.println("You've been Trolled");
    						break;
    				}
    			}
    		}
    		//If there is a collision ensure that all the squares are reverted to being water
    		if(!success){
    			Log.i("placeBoat","Bad Location");
	    		for(int i = 0; i < dist_to_collision; i++){
	    			if(graph[x][y].tag == "water"){
	    				switch(boat.direction){
	    					case 0:						//Direction = UP
	    						if((y - i) > 0)
	    							graph[x][y-i].tag = "water";
	    						break;
	    					case 1:						//Direction = RIGHT
	    						if((x + i) < 10)
	    							graph[x + i][y].tag = "water";
	    						break;
	    					case 2:						//Direction = DOWN
	    						if((y + i) < 10)
	    							graph[x][y + i].tag = "water";
	    						break;
	    					case 3:						//Direction = LEFT
	    						if((x - i) > 0)
	    							graph[x - i][y].tag = "water";
	    						break;
	    					default:
	    						System.out.println("You've been Trolled");
	    						break;
	    				}
	    			}
	    		}
    		}
			Log.i("placeBoat","Finish");
    		return success;
    	}
    	
    }
    
    //Classic CS node data type
    //Sits in a graph, holds information
    public class Node{
    	int x,y;			//Coordinates of the node [0-9][0-9]
    	int state;			//0 = Nothing, 1 = hit         I wanted this to be an int in case we get into complex types
    	String tag;			//
    	public Node(int x, int y){
    		tag = "water";
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
    	public Coord[] moves;
    	public Graph aiGraph;
    	int boats_remaining;
    	Boat battle_ship;
    	Boat submarine;
    	Boat air_craft_carrier;
    	AI(){
    		//This segment is generating coordinates for the random moves
    		Log.i("AI", "Constructing Moves");
    		moves = new Coord[100];
    		Random r = new Random();
    		int i, tempX, tempY;
    		i = 0;
    		boolean found;
    		Log.i("AI", "Constructing Moves Loop Start");
    		while(i < 100){
    			found = false;
    			tempX = (r.nextInt(10) + 0);
    			//Log.i("AI",String.valueOf(tempX));
    			tempY = (r.nextInt(10) + 0);
    			//Log.i("AI",String.valueOf(tempY));

    			
    			//This segment of code is meant to ensure that there are no repeats in generated coordinates
    			//It is commented out at the moment because it is hanging for some reason
    			/*for(int it = 0; it < i; it++){
    				if(moves[it].x == tempX && moves[it].y == tempY)
    					found = true;
    			}*/
    			if(!found){
    				//Log.i("AI", "Constructing Moves Loop Placing");
    				moves[i] = new Coord(tempX, tempY);
    				//Log.i("AI", "Constructing Moves Loop Placed");
    				i++;
    			}
    		}
    		//This segment of code is devoted to placing the AIs boats
    		Log.i("AI", "Placing Boats");
    		boats_remaining = 5;
    		found = true;
			//This array should have a length equal to the number of boats
			boolean[] tempBools = new boolean[3];
			for(int iterator = 0; iterator< 3; iterator++)
				tempBools[iterator] = false;
			Log.i("AI","Boat loop start");
    		while(found){
    			if(!tempBools[0]){
        			Log.i("AI","Attempting to make a boat");
    				battle_ship = new Boat(3,"battleship", r.nextInt(3) + 0);
    				tempX = (r.nextInt(3) + 0);
    				tempY = (r.nextInt(3) + 0);
        			Log.i("AI","Now Placing Boat");
    				tempBools[0] = aiGraph.placeBoat(battle_ship, tempX, tempY);
    				if(tempBools[0])
    					Log.i("AI", "placeBoat Works");
    				else{Log.i("AI","placeBoat kinda works");}
    			}
    			if(!tempBools[1]){
    				tempX = (r.nextInt(3) + 0);
    				tempY = (r.nextInt(3) + 0);
    				submarine = new Boat(3, "submarine", 0);
    				tempBools[1] = aiGraph.placeBoat(submarine, tempX, tempY);
    			}
    			if(!tempBools[2]){
    				tempX = (r.nextInt(3) + 0);
    				tempY = (r.nextInt(3) + 0);
    				air_craft_carrier = new Boat(5, "aircraftcarrier",0);
    				tempBools[2] = aiGraph.placeBoat(air_craft_carrier, tempX, tempY);
    			}
    			
    			
    			if(tempBools[0] && tempBools[1] && tempBools[2])
    				found = false;
    		}
    		Log.i("AI", "Finished");
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
    	Boat(int length, String type, int direction){
    		this.length = length;
    		this.type = type;
    		this.direction = direction;
    	}
    }
}
