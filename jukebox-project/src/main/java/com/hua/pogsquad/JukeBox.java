package com.hua.pogsquad;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import gr.hua.dit.oop2.musicplayer.Player;
import gr.hua.dit.oop2.musicplayer.PlayerException;
import gr.hua.dit.oop2.musicplayer.PlayerFactory;

public class JukeBox {
 public static void main(String[] args) {
   Player p = PlayerFactory.getPlayer();
   
   try {
     InputStream song = new FileInputStream("/home/bidaze/Desktop/enemy.mp3");
     p.play(song);
   }catch (FileNotFoundException e) {
     System.err.println("File papakia.mp3 not found");
   } catch (PlayerException e) {
     System.err.println("Something's wrong with the player: " + e.getMessage());
   } finally {
     if (p != null)
       p.close();
  }
 }
}