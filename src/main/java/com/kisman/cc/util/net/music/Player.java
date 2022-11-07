package com.kisman.cc.util.net.music;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.kisman.cc.util.net.music.player.MP3Player;


public class Player {

	public static MP3Player player;
	public static boolean isPlaying = false;
	public static boolean paused;
	public static boolean playerStopped = true;
	public static boolean playerPlaying;

	public static String currentSong = null;
	
	public static void resume() {
		if (player != null) {
			player.play();
			isPlaying = true;
			paused = false;
		}
	}
	
	public static void play(String url) {
		stop();
		currentSong = url;
		
					URL u;
					try {
						u = new URL(url);
						player = new MP3Player(u);
					} catch (MalformedURLException ignored) {}

					player.setRepeat(false);



		new Thread(() -> {
			try {
		player.play();
		isPlaying = true;
		paused = false;
} catch (Exception ignored) {}
}).start();
	}
	
	
	public static void playFile(File f) {
		stop();
					player = new MP3Player(f);
					player.setRepeat(false);
					new Thread() {
						@Override
						public void run() {
							try {
					player.play();
					isPlaying = true;
					paused = false;
				} catch (Exception e) {
				}
			}
		}.start();
	}
	
	public static void pause() {
		if (player != null) {
			player.pause();
			isPlaying = false;
			paused = true;
		}
	}
	
	public static boolean isPlaying(){
		return playerPlaying;
	}
	
	public static boolean isStopped(){
		return playerStopped;
	}
	
	public static void stop() {
		if (player != null) {
			player.stop();
			player = null;
			currentSong = null;
			isPlaying = false;
			paused = false;
		}
	}
	
	public static void setVolume(float volume) {
		if (player != null) {
			player.setVolume((int)volume);
		}
	}
	
	
}
