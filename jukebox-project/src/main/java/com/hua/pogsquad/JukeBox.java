package com.hua.pogsquad;

import java.io.*;
import java.util.*;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import gr.hua.dit.oop2.musicplayer.Player;
import gr.hua.dit.oop2.musicplayer.PlayerException;
import gr.hua.dit.oop2.musicplayer.PlayerFactory;
import java.util.Random;

public class JukeBox {
  public static void main(String[] args) {
    Player p = PlayerFactory.getPlayer();
   
    if (args[0].contains(".m3u")){
      m3uparser parser;
      try {
        parser = new m3uparser();
        ArrayList<String> playlist = parser.parseFile(new File(args[0]));
        if (args[1].equals("loop")){ //plays all songs in the m3u playlist and then restarts... a loop if you would...
          int n = 0;
          for(;;){
            InputStream song = new FileInputStream(playlist.get(n));
            p.play(song);
            try{
              song.close();
            }catch (IOException e){
              System.err.println("wrong");
            }
            if (n+1 == playlist.size()){
              n = 0;
            }else{
              n = n + 1;
            }
          }
        }else if (args[1].equals("random")){
          ArrayList<String> nonplayedsongs = playlist;
          int n = playlist.size()-1;
          Random rand = new Random();
          while (n > -1){
            InputStream song = new FileInputStream(nonplayedsongs.get(rand.nextInt(n)));
            p.play(song);
            nonplayedsongs.remove(n);
            n = n-1;
          }
        }else{ //plays songs in their proper order
          for (int counter = 0; counter < playlist.size(); counter++){
            InputStream song = new FileInputStream(playlist.get(counter));
            p.play(song);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
   }else{
    if (args[1].equals("loop")){
      try {
        InputStream song = new FileInputStream(args[0]);
        for(;;){ 
          p.play(song);
          try{
            song.close();
          }catch (IOException e){
            System.err.println("wrong");
          }
          song = new FileInputStream(args[0]);
        }
      }catch (FileNotFoundException e) {
        System.err.println("File papakia.mp3 not found");
      } catch (PlayerException e) {
        System.err.println("Something's wrong with the player: " + e.getMessage());
      } finally {
        if (p != null)
          p.close();
      }
    }else{
      try {
        InputStream song = new FileInputStream(args[0]);
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
 }
}