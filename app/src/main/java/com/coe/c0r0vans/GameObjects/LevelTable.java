package com.coe.c0r0vans.GameObjects;

import java.util.HashMap;

/**
 * Simple container of level table
 */
public class LevelTable {
    private static HashMap<Integer,LevelTable.Level>  instance;
    public static void init(){
        instance=new HashMap<>();
        instance.put(1,new Level(0,25000));
        instance.put(2,new Level(25000,69000));
        instance.put(3,new Level(69000,165000));
        instance.put(4,new Level(165000,373000));
        instance.put(5,new Level(373000,778000));
        instance.put(6,new Level(778000,1533000));
        instance.put(7,new Level(1533000,2856000));
        instance.put(8,new Level(2856000,5202000));
        instance.put(9,new Level(5202000,9045000));
        instance.put(10,new Level(9045000,15403000));
        instance.put(11,new Level(15403000,25475000));
        instance.put(12,new Level(25475000,40679000));
        instance.put(13,new Level(40679000,64193000));
        instance.put(14,new Level(64193000,98498000));
        instance.put(15,new Level(98498000,145431000));
        instance.put(16,new Level(145431000,212233000));
        instance.put(17,new Level(212233000,306606000));
        instance.put(18,new Level(306606000,441774000));
        instance.put(19,new Level(441774000,626545000));
        instance.put(20,new Level(626545000,626545000));
    }

    public static int getStart(int level){
        if (instance==null) init();
        return instance.get(level).start;
    }
    public static int getEnd(int level){
        if (instance==null) init();
        return instance.get(level).end;
    }
    private static class Level{
        private int start;
        private int end;
        public Level(int start,int end){
            this.start=start;
            this.end=end;
        }
        public int getStart(){
            return start;
        }
        public int getEnd(){
            return end;
        }
    }
}
