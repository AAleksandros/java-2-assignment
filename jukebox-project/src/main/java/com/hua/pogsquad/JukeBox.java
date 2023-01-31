package com.hua.pogsquad;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import gr.hua.dit.oop2.musicplayer.Player;
import gr.hua.dit.oop2.musicplayer.PlayerException;
import gr.hua.dit.oop2.musicplayer.PlayerFactory;
import gr.hua.dit.oop2.musicplayer.*;

public class JukeBox {
   private JFrame frame;
   private JLabel headerLabel;
   private JLabel statusLabel;
   private JPanel controlPanel;
   private static volatile Thread thread;

   public JukeBox(){
        prepareGUI();
   }
   public static void main(String[] args){
        Player p = PlayerFactory.getPlayer();

        p.addPlayerListener(new PlayerListener() {
            public void statusUpdated(PlayerEvent e) {
                System.out.println("Status changed to " + e.getStatus());
            }
        });

        p.addPlayerListener(new PlayerListener() {
            public void statusUpdated(PlayerEvent e) {
                if (e.getStatus() == Player.Status.IDLE) {
                    System.out.println(e.getSource());
                    e.getSource().stop();
                } 
            }
        });

        JukeBox swingControlDemo = new JukeBox();
        swingControlDemo.playButton(p);
        swingControlDemo.pauseButton(p);
        swingControlDemo.resumeButton(p);
        swingControlDemo.createM3UButton();
        swingControlDemo.PlayM3UNormal(p);
   }
   private void prepareGUI(){
      frame = new JFrame("Java Swing");
      frame.setSize(500,500);
      frame.setLayout(new GridLayout(3, 1));
      frame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }
      });
      headerLabel = new JLabel("", JLabel.CENTER);
      statusLabel = new JLabel("",JLabel.CENTER);
      statusLabel.setSize(350,100);
      controlPanel = new JPanel();
      controlPanel.setLayout(new FlowLayout());
      frame.add(headerLabel);
      frame.add(controlPanel);
      frame.add(statusLabel);
      frame.setVisible(true);
   }
   private void playButton(Player p){
      headerLabel.setText("Music Player");
      JButton okButton = new JButton("Play a single mp3 file");
      okButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent actevent) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "MP3 Only", "mp3");
            fileChooser.setFileFilter(filter);
            int result = fileChooser.showOpenDialog(new JFrame());
            if (result == JFileChooser.APPROVE_OPTION) {
                File filemp3 = fileChooser.getSelectedFile();
                String filename = filemp3.getAbsolutePath();
                try{
                    p.stop();
                }finally{
                    try {
                        InputStream song = new FileInputStream(filename);
                        p.startPlaying(song);
                    }catch (FileNotFoundException e) {
                        System.err.println("File papakia.mp3 not found");
                    }catch (PlayerException e) {
                        System.err.println("Something's wrong with the player: " + e.getMessage());
                    }
                }
        }
      }
    });
      controlPanel.add(okButton);
      frame.setVisible(true);
   }


private void pauseButton(Player p){
    JButton okButton = new JButton("Pause");
    okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent actevent) {
            p.pause();
        }
    });
    controlPanel.add(okButton);
    frame.setVisible(true);
    }

private void resumeButton(Player p){
    JButton okButton = new JButton("Resume");
    okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent actevent) {
            p.resume();
        }
    });
    controlPanel.add(okButton);
    frame.setVisible(true);
    }

private void createM3UButton(){
    JButton okButton = new JButton("Create M3U Playlist");
    okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent actevent) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(new JFrame());
            if (result == JFileChooser.APPROVE_OPTION) {
                File directory = fileChooser.getSelectedFile();
                String pathname = directory.getAbsolutePath();
                File[] files = directory.listFiles((dir, name) -> name.endsWith(".mp3"));
                String[] fileNames = new String[files.length];
                for (int i = 0; i < files.length; i++) {
                    fileNames[i] = files[i].getName();
                }
    
                try {
                    FileWriter myWriter = new FileWriter(pathname+"/playlist.m3u", false);
                    for (int i = 0; i < files.length; i++) {
                        myWriter.write(pathname+"/"+fileNames[i]+"\n");
                    }
                    myWriter.close();
                    }catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
            }
        }
    });
    controlPanel.add(okButton);
    frame.setVisible(true);
    }

private void PlayM3UNormal(Player p){
    JButton okButton = new JButton("Play M3U");
    okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent actevent) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "M3U Only", "m3u");
            fileChooser.setFileFilter(filter);
            int result = fileChooser.showOpenDialog(new JFrame());
            if (result == JFileChooser.APPROVE_OPTION) {
                File filem3u = fileChooser.getSelectedFile();
                m3uparser parser;
                try {
                parser = new m3uparser();
                ArrayList<String> playlist = parser.parseFile(filem3u);
                String[] fileNames = new String[playlist.size()];
                DefaultListModel<String> listModel = new DefaultListModel<>();
                for (int i = 0; i < playlist.size(); i++) {
                  fileNames[i] = playlist.get(i);
                  listModel.addElement(playlist.get(i));
                }
                JList<String> list = new JList<>(listModel);
                ArrayList<String> tmpLoopList = new ArrayList<String>();
                tmpLoopList.add("no");
                ArrayList<String> tmpShuffleList = new ArrayList<String>();
                tmpShuffleList.add("no");
                ArrayList<String> startingsong = new ArrayList<String>();
                startingsong.add(fileNames[0]);
                ArrayList<String> currentsong = new ArrayList<String>();
                currentsong.add(fileNames[0]);
                list.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                      String selectedFile = list.getSelectedValue();
                      if (selectedFile != null && selectedFile.endsWith(".mp3")) {
                        startingsong.set(0, selectedFile);
                        currentsong.set(0, selectedFile);
                      }
                      list.clearSelection();
                    }
                  });
                  JToggleButton toggleloopButton = new JToggleButton("Loop");
                  ItemListener itemListenerloop = new ItemListener() {
                      public void itemStateChanged(ItemEvent itemEvent)
                      {
           
                          int state = itemEvent.getStateChange();
           
                          if (state == ItemEvent.SELECTED) {
                              System.out.println("Selected");
                              tmpLoopList.set(0, "yes");
                          }
                          else {
                              System.out.println("Deselected");
                              tmpLoopList.set(0, "no");
                          }
                      }
                  };
                  toggleloopButton.addItemListener(itemListenerloop);

                  JToggleButton toggleshuffleButton = new JToggleButton("Shuffle");
                  ItemListener itemListenershuffle = new ItemListener() {
                      public void itemStateChanged(ItemEvent itemEvent)
                      {
           
                          int state = itemEvent.getStateChange();
           
                          if (state == ItemEvent.SELECTED) {
                              tmpShuffleList.set(0, "yes");
                          }
                          else {
                              tmpShuffleList.set(0, "no");
                          }
                      }
                  };
                  toggleshuffleButton.addItemListener(itemListenershuffle);

                  JButton playskipbutton = new JButton("Play / Skip");
                  playskipbutton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e)
                      {
                        if (thread != null && thread.isAlive()) {
                            thread.interrupt();
                            thread = null;
                          } else {
                            thread = new Thread(() -> {
                                try {

                                    if (tmpLoopList.get(0) == "no" && tmpShuffleList.get(0) == "no"){
                                        if (p.getStatus() == Player.Status.PLAYING){
                                            p.stop();
                                            for (int i = (playlist.indexOf(currentsong.get(0))+1); i < playlist.size(); i++){
                                                InputStream song = new FileInputStream(playlist.get(i));
                                                p.play(song);
                                                currentsong.set(0, playlist.get(i));
                                                startingsong.set(0, playlist.get(i));
                                            }
                                        }else{
                                                for (int i = playlist.indexOf(startingsong.get(0)); i < playlist.size(); i++){
                                                    InputStream song = new FileInputStream(playlist.get(i));
                                                    p.play(song);
                                                    currentsong.set(0, playlist.get(i));
                                                    startingsong.set(0, playlist.get(i));
                                                }
                                            }
                                    }else if (tmpLoopList.get(0) == "yes" && tmpShuffleList.get(0) == "no"){
                                        if (p.getStatus() == Player.Status.PLAYING){
                                            p.stop();
                                            int i = playlist.indexOf(currentsong.get(0))+1;
                                            for(;;){ 
                                                InputStream song = new FileInputStream(playlist.get(i));
                                                p.play(song);
                                                currentsong.set(0, playlist.get(i));
                                                startingsong.set(0, playlist.get(i));
                                                if (i + 1 == playlist.size()){
                                                    i = 0;
                                                    }else{
                                                    i = i + 1;
                                                    }
                                                }
                                        }else{
                                            int i = playlist.indexOf(startingsong.get(0));
                                            for(;;){ 
                                                InputStream song = new FileInputStream(playlist.get(i));
                                                p.play(song);
                                                currentsong.set(0, playlist.get(i));
                                                startingsong.set(0, playlist.get(i));
                                                if (i + 1 == playlist.size()){
                                                    i = 0;
                                                    }else{
                                                    i = i + 1;
                                                    }
                                                }
                                        }
                                    }else if (tmpLoopList.get(0) == "no" && tmpShuffleList.get(0) == "yes"){
                                        if (p.getStatus() == Player.Status.PLAYING){
                                            p.stop();
                                            ArrayList<String> nonplayedsongs = playlist;
                                            nonplayedsongs.remove(nonplayedsongs.indexOf(currentsong.get(0)));
                                            int n = nonplayedsongs.size();
                                            Random rand = new Random();
                                            while (n > 0){
                                                int randnum = rand.nextInt(n);
                                                currentsong.set(0, playlist.get(n));
                                                startingsong.set(0, playlist.get(n));
                                                InputStream song = new FileInputStream(nonplayedsongs.get(randnum));
                                                System.out.println("Now playing:" + nonplayedsongs.get(randnum));
                                                p.play(song);
                                                nonplayedsongs.remove(randnum);
                                                n = n-1;
                                            }
                                    }else {
                                        ArrayList<String> nonplayedsongs = playlist;
                                        int n = playlist.size();
                                        Random rand = new Random();
                                        while (n > 0){
                                            int randnum = rand.nextInt(n);
                                            currentsong.set(0, playlist.get(n));
                                            startingsong.set(0, playlist.get(n));
                                            InputStream song = new FileInputStream(nonplayedsongs.get(randnum));
                                            System.out.println("Now playing:" + nonplayedsongs.get(randnum));
                                            p.play(song);
                                            nonplayedsongs.remove(randnum);
                                            n = n-1;
                                        }
                                    }
                                }
                                                            
                            } catch (Exception ex) {
                                // TODO: handle exception
                            }
                            });
                            thread.start();
                          }
                    }
                });

                toggleloopButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (toggleshuffleButton.isSelected()==true){
                        toggleshuffleButton.setSelected(!toggleshuffleButton.isSelected());
                    }
                }
                });
            
                toggleshuffleButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (toggleloopButton.isSelected()==true){
                        toggleloopButton.setSelected(!toggleloopButton.isSelected());
                    }
                }
                });

                JFrame frame = new JFrame("MP3 Player");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 400);
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                JScrollPane scrollPane = new JScrollPane(list);
                panel.add(scrollPane, BorderLayout.CENTER);
                panel.add(toggleloopButton, BorderLayout.EAST);
                panel.add(toggleshuffleButton, BorderLayout.WEST);
                panel.add(playskipbutton, BorderLayout.SOUTH);
            
                frame.add(panel);
                frame.setVisible(true);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    });
    controlPanel.add(okButton);
    frame.setVisible(true);
    }
    
}