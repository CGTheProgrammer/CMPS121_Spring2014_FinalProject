package com.example.battleship;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import com.example.battleship.ServerCall;
import com.example.battleship.ServerCallSpec;
import com.example.battleship.SerialGame;
import com.google.gson.Gson;

import android.util.Log;




public class ServerIO {
	static final private String LOG_TAG = "ServerIO";
	private static String URL = "http://ucsc-cmps121-battleship.appspot.com/classexample/default/";
	private static String DOWNLOADPOSTFIX = "downloadGame.json";
	private static String UPLOADPOSTFIX = "uploadGame.json";
	private ServerCall downloader = null;
	
	ServerIO(String url, String downloadPostFix, String uploadPostFix)
	{
		URL = url;
		DOWNLOADPOSTFIX = downloadPostFix;
		UPLOADPOSTFIX = uploadPostFix;
	}
	
	
	//Tries to download the database entry that matches the gameID
	//If no ID given (empty string), it picks an open game
	//If no open game found, returns null
	public SerialGame downloadJSON(String gameID)
	{
		Gson gson = new Gson();
		SerialGame sGame = new SerialGame();
		
		//Returns a specific closed game, or finds an open one and returns the gameID
		HashMap<String, String> m = new HashMap<String, String>();
		ServerCallSpec downloadSpec = new ServerCallSpec();
		downloadSpec.url = URL + DOWNLOADPOSTFIX;
		m = new HashMap<String,String>();
		
		m.put("gameID", gameID);
		
		downloadSpec.setParams(m);

		// Initiates server call.
		downloader = new ServerCall();
		downloader.execute(downloadSpec);
		
		
		
		try {
			//Grab the result of the download
			PostProcessPair test = downloader.get();
			//Log.d(LOG_TAG, test.result);
			//Convert download to game object
			sGame = gson.fromJson(test.result, SerialGame.class);
			//Log.d(LOG_TAG, sGame.gameID);
			return sGame;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e)
		{
			
		}
		return null;
	}
	
	//Download without ID
	public SerialGame downloadJSON()
	{
		return downloadJSON("find");
	}
	
	public boolean uploadJSON(SerialGame inputGame)
	{
		Gson gson = new Gson();
		SerialGame outputGame = new SerialGame();
		HashMap<String, String> m = new HashMap<String, String>();
		
        //SendSpec handles the server transaction, don't use the same one for uploads and downloads!
        ServerCallSpec uploadSpec = new ServerCallSpec();
        //Configure URL: Uploads use uploadGame, downloads use downloadGame
		uploadSpec.url = URL + UPLOADPOSTFIX;
		
		m.putAll(inputGame.toHash());
//		m.put("gameID", "test");
//		m.put("open", String.valueOf(sGame.open));
//		m.put("numPlayers", String.valueOf(sGame.numPlayers));
//		m.put("maxPlayers", String.valueOf(sGame.maxPlayers));
//		m.put("turn", String.valueOf(sGame.turn));
//		m.put("playA", String.valueOf(sGame.playA));
//		m.put("playB", String.valueOf(sGame.playB));
		
		uploadSpec.setParams(m);
		
		// Initiates and executes server call.
		downloader = new ServerCall();
		downloader.execute(uploadSpec);
		
		try {
			//Grab the result of the download
			PostProcessPair test = downloader.get();
			Log.d(LOG_TAG, test.result);
			//Convert download to game object
			outputGame = gson.fromJson(test.result, SerialGame.class);
			Log.d(LOG_TAG, outputGame.gameID);
			if (outputGame.result.equals("ok"))
			{
				return true;
			}
			else return false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e)
		{
			
		}
		
		return false;
	}
}