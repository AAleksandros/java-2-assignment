package com.hua.pogsquad;

import java.util.*;
import java.io.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class m3uparser {
    public m3uparser() throws Exception{

    }

    public ArrayList<String> parseFile(File f) throws FileNotFoundException{
        if (f.exists()){
            FileInputStream stream = new FileInputStream(f);
            Scanner sc = new Scanner(stream);
            ArrayList<String> mp3s = new ArrayList<>();
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                if (line.contains(".mp3")){
                    mp3s.add(line);
                }
            }
            sc.close();
            return mp3s;
        }
        return null;
    }
}