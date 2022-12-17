package com.hua.pogsquad;

import java.io.*;
import java.util.*;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import gr.hua.dit.oop2.musicplayer.Player;
import gr.hua.dit.oop2.musicplayer.PlayerException;
import gr.hua.dit.oop2.musicplayer.PlayerFactory;

//TODO Vale sthn arxh ena if na kanei check an to prwto arg einai .m3u or .mp3, an einai m3u kane call ton parser
//TODO Vale kai to random don't forget
//TODO Also don't forget, an den einai oute random oute loop na ta kanei me thn seira kai telos
//TODO chop chop

public class JukeBox {
 public static void main(String[] args) {
   Player p = PlayerFactory.getPlayer();
   
   try {
     InputStream song = new FileInputStream("/home/chris/Downloads/Bruh - Sound Effect (HD).mp3");
     p.play(song); /*
     for(;;){ 
      p.play(song);
      try{
        song.close();
      }catch (IOException e){
        System.err.println("wrong");
      }
      song = new FileInputStream("/home/chris/Downloads/Bruh - Sound Effect (HD).mp3");
     } */
   }catch (FileNotFoundException e) {
     System.err.println("File papakia.mp3 not found");
   } catch (PlayerException e) {
     System.err.println("Something's wrong with the player: " + e.getMessage());
   } finally {
     if (p != null)
       p.close();
  }


  m3uparser parser;
  try {
    parser = new m3uparser();
    ArrayList<String> playlist = parser.parseFile(new File(args[0]));
    for (int counter = 0; counter < playlist.size(); counter++){
      System.out.println(playlist.get(counter));
    }
  } catch (Exception e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
  }
 }
}