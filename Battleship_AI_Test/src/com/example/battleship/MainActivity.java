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
        
        boats = new Boat[3];
        boats[0] = new Boat(3, "battleship", 0);
        boats[1] = new Boat(3, "submarine", 0);
        boats[2] = new Boat(5, "aircraftcarrier", 0);
        
        singlePlayer = false;
        
		/////////////////////////////////////////////////////////////////////////////////
		//NOTE: THIS NEEDS TO BE CHANGED TO 17 WHEN THERE ARE 5 BOATS IN THE GAME////////
		/////////////////////////////////////////////////////////////////////////////////
        boats_remaining = 11;
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
	
	        		float y = (event.getY() / (sizeY / 12));
	        		
	        		//If the player pushes the button at the bottom of the screen, change view
	        		if(y >= 10){
	        			setContentView(attack);
	        			curView = 3;
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
	        		float x =(event.getX() / (sizeX / 10));
	        		float y = (event.getY() / (sizeY / 12));
	        		
	        		//Enters new data in the aAttack array to denote that the player has attacked a square 
	        		if(y < 10 && x < 10){
	        			if(singlePlayer){
	        				ai.aiGraph.touch((int)x, (int)y);
	        				turn++;
	        				bAttacks[ai.moves[turn-1].x][ai.moves[turn-1].y] = true;
	        				//////////////////////////////////////////////////////////////////////////////////////////////////
	        				//This is a temporary piece of code, it needs to be updated to handle boats of size greater than 1
	        				//////////////////////////////////////////////////////////////////////////////////////////////////
	        				if(ai.aiGraph.graph[(int)x][(int)y].tag == "boat")
	        					ai.boats_remaining--;
	        				if(aGraph.graph[ai.moves[turn-1].x][ai.moves[turn-1].y].tag == "boat")
	        					boats_remaining--;
	        				setContentView(game);
	        			}
	        			else{
	        				bGraph.touch((int)x, (int)y);
	        				turn++;
	        				canAttack = false;
	        			}
	        			//////////////////////////////////////////////////////////////
	        			///We need to add some logic here to handle the turn system///
	        			//////////////////////////////////////////////////////////////
	        			aAttacks[(int)x][(int)y] = true;
	        		}
	        		//If the player pushes the button at the bottom of the screen, change view
	        		else if(y >= 10){
	        			setContentView(game);
	        			curView = 3;
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
	
	        		float x =(event.getX() / (sizeX / 10));
	        		float y = (event.getY() / (sizeY / 12));
	        		
	        		
	        		//Determines which boat is being placed, I wish I could write this as a switch statement... but I can't
		        	if(y < 10){
		        		if(!boats[0].placed && aGraph.graph[(int)x][(int)y].tag == "water"){			//Placing Battleship
		        			boats[0].placed = aGraph.placeBoat(boats[0], (int)x, (int)y);
		            		place.invalidate();
		        		}
		        		else if(!boats[1].placed && aGraph.graph[(int)x][(int)y].tag == "water"){		//Placing Submarine
	
		        			boats[1].placed = aGraph.placeBoat(boats[1], (int)x, (int)y);
		            		place.invalidate();
		        		}
		        		else if(!boats[2].placed && aGraph.graph[(int)x][(int)y].tag == "water"){		//Placing Air Craft Carrier
	
		        			boats[2].placed = aGraph.placeBoat(boats[2], (int)x, (int)y);
		            		place.invalidate();
		
		        		}
		        		//Once all boats are placed, click anywhere to begin the game
						//NOTE: May trigger approximately when last boat is placed because the input will still be detected
		        		else if(boats[0].placed && boats[1].placed && boats[2].placed){
		        			Log.i("GAME", "Launched");
		        			setContentView(game);
		        		}
		        	}
		        	else{
		        		x = event.getX();
		        		if(x >= (sizeX / 2)){
		        			Log.i("Rotate","Turning Right");
		        			int i = 0;
		        			while(boats[i].placed && i < 3)
		        				i++;
		        			if(i >= 3)
		        				return false;
		        			boats[i].direction++;
		        			if(boats[i].direction >= 4)
		        				boats[i].direction = 0;		        		}
		        		else{
		        			Log.i("Rotate","Turning Left");
		        			int i = 0;
		        			while(boats[i].placed && i < 3)
		        				i++;
		        			if(i >= 3)
		        				return false;

		        			boats[i].direction--;
		        			if(boats[i].direction < 0)
		        				boats[i].direction = 3;
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
    	
    	public aView(Context context){
    		super(context);    		
    		this.setBackgroundResource(R.drawable.background1);

    	}
	
    	@Override
    	protected void onDraw(Canvas canvas){
    		super.onDraw(canvas);
    		//If we want to have extra things drawn on the screen, this is where the code should go


    		
    		checkWinner();
    		
    		//This first segment is devoted to drawing the button at the bottom
    		paint.setStyle(Paint.Style.FILL);
    		paint.setColor(Color.RED);
    		lx = 0;
    		rx = getWidth();
    		ty = getHeight();
    		ty -= getHeight() / 12;
    		by = getHeight();
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
    					lx = i * (sizeX / 10);
    					rx = lx + (sizeX / 10);
    					by = j * (sizeY / 12);
    					ty = by + (sizeY / 12);
    					paint.setColor(Color.GREEN);
    					canvas.drawRect(lx, ty, rx, by, paint);
    				}
    				if(aGraph.graph[i][j].state == 1){
    					lx = i * (sizeX / 10);
    					rx = lx + (sizeX / 10);
    					by = j * (sizeY / 12);
    					ty = by + (sizeY / 12);
    					if(aGraph.graph[i][j].tag == "boat"){
    						paint.setColor(Color.RED);
    					}
    					else{paint.setColor(Color.WHITE);}
    					canvas.drawRect(lx, ty, rx, by, paint);
    				}
    				if(bAttacks[i][j]){
    					lx = i * (sizeX / 10);
    					rx = lx + (sizeX / 10);
    					by = j * (sizeY / 12);
    					ty = by + (sizeY / 12);
    					if(aGraph.graph[i][j].tag == "boat"){
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
    		
    		checkWinner();
    		
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
    				if(singlePlayer){
    					if(ai.aiGraph.graph[i][j].tag == "water" || ai.aiGraph.graph[i][j].tag == "boat" ){
    						lx = i * (sizeX / 10);
	    					rx = lx + (sizeX / 10);
	    					by = j * (sizeY / 12);
	    					ty = by + (sizeY / 12);
	    		    		paint.setColor(Color.BLUE);
	    					canvas.drawRect(lx, ty, rx, by, paint);
	    				}
    					
	    				if(ai.aiGraph.graph[i][j].state == 1){
	    					lx = i * (sizeX / 10);
	    					rx = lx + (sizeX / 10);
	    					by = j * (sizeY / 12);
	    					ty = by + (sizeY / 12);
	    					
	    					if(ai.aiGraph.graph[i][j].tag == "ship"){
	    						paint.setColor(Color.RED);
	    					}
	    					else{paint.setColor(Color.WHITE);}
	    					canvas.drawRect(lx, ty, rx, by, paint);
	    				}
	    				
	    				//This needs to be taken out, but it is useful for testing
	    				if(ai.aiGraph.graph[i][j].tag == "boat"){
    						lx = i * (sizeX / 10);
	    					rx = lx + (sizeX / 10);
	    					by = j * (sizeY / 12);
	    					ty = by + (sizeY / 12);
	    		    		paint.setColor(Color.GREEN);
	    					canvas.drawRect(lx, ty, rx, by, paint);
	    				}
    				}
    				else{
        				//Note this needs to be changed to bGraph once bGraph becomes a real thing (i.e. it is being initialized someplace)
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
    				if(aAttacks[i][j]){
    					lx = i * (sizeX / 10);
    					rx = lx + (sizeX / 10);
    					by = j * (sizeY / 12);
    					ty = by + (sizeY / 12);
    					if(singlePlayer){
    						if(ai.aiGraph.graph[i][j].tag == "boat")
    							paint.setColor(Color.RED);
    						else{paint.setColor(Color.WHITE);}
    					}
    					else{
    						if(ai.aiGraph.graph[i][j].tag == "boat")
    							paint.setColor(Color.RED);
    						else{paint.setColor(Color.WHITE);}
    					}
    					canvas.drawRect(lx, ty, rx, by, paint);
    				}
    			}
    		}
    	}
	
	}
    
    
   public class placeView extends View{
	    
    	LinearLayout layout;
    	Button attack;
    	
    	public placeView(Context context){
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
    		paint.setTextSize(100);
    		canvas.drawText("Place your Battleships!", rx/2, getHeight() / 2, paint);
    		
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
    					lx = i * (sizeX / 10);
    					rx = lx + (sizeX / 10);
    					by = j * (sizeY / 12);
    					ty = by + (sizeY / 12);
    		    		paint.setColor(Color.GREEN);
    					canvas.drawRect(lx, ty, rx, by, paint);
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
			setContentView(place);
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
    		
    		/////////////////////////////////////////////////////////////////////////////////
    		//NOTE: THIS NEEDS TO BE CHANGED TO 17 WHEN THERE ARE 5 BOATS IN THE GAME////////
    		/////////////////////////////////////////////////////////////////////////////////
    		boats_remaining = 3;			
			
			for(int it = 0; i < 10; i++){
				for(int j = 0; j < 10; j++){
					aiGraph.graph[it][j] = new Node(it, j);
				}
			}
			

   			//Generating a random position for the battle_ship
       		Log.i("AI","Attempting to make a boat");
    		battle_ship = new Boat(3,"battleship", r.nextInt(3) + 0);	
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
}
