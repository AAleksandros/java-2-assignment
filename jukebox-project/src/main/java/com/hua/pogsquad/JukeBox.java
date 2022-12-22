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
import com.hua.pogsquad.m3uparser;

public class JukeBox {
  public static void main(String[] args) {
    Player p = PlayerFactory.getPlayer();
   
    if (args[0].contains(".m3u") && (args[0].contains(".mp3") == false)){
      try {
        m3uparser parser = new m3uparser();
        ArrayList<String> playlist = parser.parseFile(new File(args[0]));
        if (args.length == 2 && args[1].equals("loop")){ 
          int n = 0;
          for(;;){
            InputStream song = new FileInputStream(playlist.get(n));
            System.out.println("Now playing:" + playlist.get(n));
            p.play(song);
            try{
              song.close();
            }catch (IOException e){
              System.err.println("wrong");
            }
            if (n + 1 == playlist.size()){
              n = 0;
            }else{
              n = n + 1;
            }
          }
        }else if (args[1].equals("random")){
          ArrayList<String> nonplayedsongs = playlist;
          int n = playlist.size();
          Random rand = new Random();
          while (n > 0){
            int randnum = rand.nextInt(n);
            InputStream song = new FileInputStream(nonplayedsongs.get(randnum));
            System.out.println("Now playing:" + nonplayedsongs.get(randnum));
            p.play(song);
            nonplayedsongs.remove(randnum);
            n = n-1;
          }
        }else{ 
          for (int counter = 0; counter < playlist.size(); counter++){
            InputStream song = new FileInputStream(playlist.get(counter));
            p.play(song);
          }
        }
      } catch (Exception e) {
        System.err.println("Playlist file not found.");
      }
   }else{
    if (args[1].equals("loop") && (args[0].contains(".mp3"))){
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
        System.err.println("File" + args[0] +"Does not exist. Exiting program..." );
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
        System.err.println("File not found.");
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