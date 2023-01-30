package com.hua.pogsquad;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import gr.hua.dit.oop2.musicplayer.Player;
import gr.hua.dit.oop2.musicplayer.PlayerException;
import gr.hua.dit.oop2.musicplayer.PlayerFactory;
import gr.hua.dit.oop2.musicplayer.*;

public class SwingDemo {
   private JFrame frame;
   private JLabel headerLabel;
   private JLabel statusLabel;
   private JPanel controlPanel;
   public SwingDemo(){
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
                    e.getSource().stop();
                } 
            }
        });

        SwingDemo swingControlDemo = new SwingDemo();
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
      JButton okButton = new JButton("Play");
      okButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent actevent) {
            try{
                p.stop();
            }finally{
                try {
                    System.out.println("Hello");
                    InputStream song = new FileInputStream("/home/chris/Downloads/1.mp3");
                    p.startPlaying(song);
                }catch (FileNotFoundException e) {
                    System.err.println("File papakia.mp3 not found");
                }catch (PlayerException e) {
                    System.err.println("Something's wrong with the player: " + e.getMessage());
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
    JButton okButton = new JButton("Play M3U once");
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
                list.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                      String selectedFile = list.getSelectedValue();
                      if (selectedFile != null && selectedFile.endsWith(".mp3")) {
                        try{
                            p.stop();
                        }finally{
                            try {
                                System.out.println("Hello");
                                for (int i = playlist.indexOf(selectedFile); i < playlist.size(); i++){
                                    InputStream song = new FileInputStream(fileNames[i]);
                                    p.play(song);
                                }
                            }catch (FileNotFoundException ex) {
                                System.err.println("File papakia.mp3 not found");
                            }catch (PlayerException ex) {
                                System.err.println("Something's wrong with the player: " + ex.getMessage());
                            }
                        }
                      }
                    }
                  });
                  JToggleButton toggleButton = new JToggleButton("Loop");
                  ItemListener itemListener = new ItemListener() {
                      public void itemStateChanged(ItemEvent itemEvent)
                      {
           
                          int state = itemEvent.getStateChange();
           
                          if (state == ItemEvent.SELECTED) {
                              System.out.println("Selected");
                          }
                          else {
                              System.out.println("Deselected");
                          }
                      }
                  };
                  toggleButton.addItemListener(itemListener);

                  JFrame frame = new JFrame("MP3 Player");
                  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                  frame.setSize(400, 400);
              
                  JPanel panel = new JPanel();
                  panel.setLayout(new BorderLayout());
                  JScrollPane scrollPane = new JScrollPane(list);
                  panel.add(scrollPane, BorderLayout.CENTER);
                  panel.add(toggleButton, BorderLayout.SOUTH);
              
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