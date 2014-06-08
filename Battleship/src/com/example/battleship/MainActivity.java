package com.example.battleship;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
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
	
	//################################################## Global Variables ######################################################\\

	//TAGS\\
	private static final String LOG_TAG = "Main";

	//Board Variables\\
	public Graph aGraph;									//A graph of the players ship positions
	public boolean[][] aAttacks;							//A graph of the positions that the player has attacked
	public Graph bGraph;									//A graph of the opponents ship positions
	public boolean[][] bAttacks;							//Keeps track of all of the attacks the opponent has made
	public Boat[] boats;									//An array that stores all of the players boats

	//Game Variables\\
	public int turn;
	public boolean canAttack;								//Determines whether or not it is the players turn
	public int boats_remaining, op_boats_remaining;			//The number of undestroyed ships that the player currently has
	public boolean singlePlayer;							//Determines the source of the opponent (another player == false, AI == true)
	public AI ai;											//Artificial Intelligence that controls opponent during singlePlayer

	//Screen/View Variables\\
	public int sizeX, sizeY;				//Integers that represent the size of the screen the app is being run on
	public View main;						//The main menu screen
	public aView game;						//The main in game view the player sees when they are viewing their half of the board
	public bView attack;					//The view the player sees when they are choosing the location for their attacks
	public placeView place;					//The view that allows the player to place their ships
	public int curView;						//Keeps track of which screen the user is currently on 0 = main menu, 2 = game 
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
	
	        		float y = (event.getY() / (sizeY / 12));
	        		
	        		//If the player pushes the button at the bottom of the screen, change view
	        		if(y >= 10){
	        			setContentView(attack);
	        			curView = 3;
	        			attack.draw(canvas);
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
	        			else{//This is what happens when attacking in muliplayer
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
		        				parseUp();
	        				}
	        			}


	        		}
	        		//If the player pushes the button at the bottom of the screen, change view
	        		else if(y >= 10){
	        			setContentView(game);
	        			curView = 3;
	        			game.draw(canvas);
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
		        			if(aGraph.graph[(int)x][(int)y].state == 3){
		        				//boats[0].placed = aGraph.placeBoat(boats[0], (int)x, (int)y);
		        				for(int i = 0; i < 10; i++){
		        					for(int j = 0; j < 10; j++){
		        						if(aGraph.graph[i][j].state == 3){
		        							aGraph.graph[i][j].tag = "boat";
		        							aGraph.graph[i][j].state = 0;
		        						}
		        					}
		        				}
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
		        				for(int i = 0; i < 10; i++){
		        					for(int j = 0; j < 10; j++){
		        						if(aGraph.graph[i][j].state == 3){
		        							aGraph.graph[i][j].tag = "boat";
		        							aGraph.graph[i][j].state = 0;
		        						}
		        					}
		        				}
		        				boats[1].placed = true;
		        			}
		        			else{
		        				aGraph.placeBoatTemp(boats[1], (int) x, (int)y);
		        			}
		            		place.invalidate();
		        		}
		        		else if(!boats[2].placed && aGraph.graph[(int)x][(int)y].tag == "water"){		//Placing Air Craft Carrier
		        			if(aGraph.graph[(int)x][(int)y].state == 3){
		        				for(int i = 0; i < 10; i++){
		        					for(int j = 0; j < 10; j++){
		        						if(aGraph.graph[i][j].state == 3){
		        							aGraph.graph[i][j].tag = "boat";
		        							aGraph.graph[i][j].state = 0;
		        						}
		        					}
		        				}
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
		        				for(int i = 0; i < 10; i++){
		        					for(int j = 0; j < 10; j++){
		        						if(aGraph.graph[i][j].state == 3){
		        							aGraph.graph[i][j].tag = "boat";
		        							aGraph.graph[i][j].state = 0;
		        						}
		        					}
		        				}
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
		        				for(int i = 0; i < 10; i++){
		        					for(int j = 0; j < 10; j++){
		        						if(aGraph.graph[i][j].state == 3){
		        							aGraph.graph[i][j].tag = "boat";
		        							aGraph.graph[i][j].state = 0;
		        						}
		        					}
		        				}
		        				boats[4].placed = true;
		        			}
		        			else{
		        				aGraph.placeBoatTemp(boats[4], (int) x, (int)y);
		        			}
		            		place.invalidate();
		        		}
		        		//Once all boats are placed, click anywhere to begin the game
						//NOTE: May trigger approximately when last boat is placed because the input will still be detected
		        		else if(boats[0].placed && boats[1].placed && boats[2].placed){
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
    
    
    
    //#########################################################################################################################\\
   	//#########################################################################################################################\\
    //#################################################### Board Views ########################################################\\
  	//#########################################################################################################################\\
   	//#########################################################################################################################\\
    
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
    		this.setBackgroundResource(R.drawable.battleship_main_screen);

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
    				if(aGraph.graph[i][j].tag == "boat"){
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
    		this.setBackgroundResource(R.drawable.battleship_main_screen);

    	}
	
    	@Override
    	protected void onDraw(Canvas canvas){
    		super.onDraw(canvas);
    		//If we want to have extra things drawn on the screen, this is where the code should go
    		
    		checkWinner();
    		parseDown();
    		
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
    					
	    				if(ai.aiGraph.graph[i][j].state == 1){
	    					lx = i * (sizeX / 10);
	    					rx = lx + (sizeX / 10);
	    					by = j * (sizeY / 12);
	    					ty = by + (sizeY / 12);
	    					
	    					if(ai.aiGraph.graph[i][j].tag == "boat"){
	    						paint.setColor(Color.RED);
	    					}
	    					else{paint.setColor(Color.WHITE);}
	    					canvas.drawRect(lx, ty, rx, by, paint);
	    				}
	    				
	    				if(ai.aiGraph.graph[i][j].state == 3){
	    					lx = i * (sizeX / 10);
	    					rx = lx + (sizeX / 10);
	    					by = j * (sizeY / 12);
	    					ty = by + (sizeY / 12);
	    		    		paint.setColor(Color.BLACK);
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
    		this.setBackgroundResource(R.drawable.battleship_main_screen);

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

    		
       		for(int i = 0; i < 10; i++){
    			for(int j = 0; j < 10; j++){
    				if(aGraph.graph[i][j].tag == "boat"){
    					lx = i * (sizeX / 10);
    					rx = lx + (sizeX / 10);
    					by = j * (sizeY / 12);
    					ty = by + (sizeY / 12);
    		    		paint.setColor(Color.GREEN);
    					canvas.drawRect(lx, ty, rx, by, paint);
    				}
    				if(aGraph.graph[i][j].state == 3){
    					lx = i * (sizeX / 10);
    					rx = lx + (sizeX / 10);
    					by = j * (sizeY / 12);
    					ty = by + (sizeY / 12);
    		    		paint.setColor(Color.YELLOW);
    					canvas.drawRect(lx, ty, rx, by, paint);
    				}
    			}
    		}

    		paint.setColor(Color.BLACK);
    		paint.setTextSize(50);
    		canvas.drawText("Place your Battleships!", (getWidth() / 2) - 50, getHeight() / 2, paint);
    	}
	
	}
       
   

   //#########################################################################################################################\\
   //#########################################################################################################################\\
   //############################################### Button Functions ########################################################\\
   //#########################################################################################################################\\
   //#########################################################################################################################\\
    
   
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
    
   
	
	//#########################################################################################################################\\
   	//#########################################################################################################################\\
    //############################################ Storing Data on Game Data ##################################################\\
  	//#########################################################################################################################\\
   	//#########################################################################################################################\\
	
	
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
    
    
    
    
    
    
    
    
    //#########################################################################################################################\\
   	//#########################################################################################################################\\
    //################################################# Ship/Boat: ADT ########################################################\\
  	//#########################################################################################################################\\
   	//#########################################################################################################################\\
    
    
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
 	
 	
 	
 	//#########################################################################################################################\\
   	//#########################################################################################################################\\
    //############################################### AI Opponent: ADT ########################################################\\
  	//#########################################################################################################################\\
   	//#########################################################################################################################\\

    
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
   
    
    //#########################################################################################################################\\
	//#########################################################################################################################\\
	//################################################# Server Functions ######################################################\\
	//#########################################################################################################################\\
	//#########################################################################################################################\\
    
    
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
    
    
    
    //#########################################################################################################################\\
   	//#########################################################################################################################\\
    //################################################ Game Functions #########################################################\\
  	//#########################################################################################################################\\
   	//#########################################################################################################################\\
	
    
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
	
}
