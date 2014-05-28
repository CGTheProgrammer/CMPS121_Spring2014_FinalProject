package com.example.battleship;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	public Graph aGraph;
	public int sizeX, sizeY;
	public View main;
	public aView game;
	public int curView;
	public boolean singlePlayer;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //This is where we are creating our view
        singlePlayer = false;
        game = new aView(getApplicationContext());
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
        		int x =(int)(event.getX() / (sizeX / 10));
        		int y = (int)(event.getY() / (sizeY / 10));
        		aGraph.touch(x,y);
        		return true;
        	}
        });
        //Creating the graph that tracks the back of the screen
        aGraph = new Graph();
        
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
    	
    }
    
    //Classic CS node data type
    //Sits in a graph, holds information
    public class Node{
    	int x,y;			//Coordinates of the node [0-9][0-9]
    	int state;			//Water = 0, Ship = 1, Hit = 2
    	public Node(int x, int y){
    		this.x = x;
    		this.y = y;
    		state = 0;
    	}	
    }
}
