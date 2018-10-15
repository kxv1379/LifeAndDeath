package com.project.LifeAndDeth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Board {
	Map<Integer,Integer > hashMap;
	ArrayList<Integer> tileList;
	ArrayList<Integer[]> updateList;
	private int tmpTile, paeTile;
	
	public enum Color{
		Empty,Black,White
	}
	
	public Board () {
	       hashMap = new HashMap<>();
	   }
    public Board (ArrayList<Integer> tileList) {
        hashMap = new HashMap<>();
        this.tileList = tileList;
    }
    public Board(ArrayList<Integer[]> updateList,int color) {
    	hashMap = new HashMap<>();
    	this.updateList=updateList;
    }
    
    protected void check(int tileID, int color){
        hashMap.put(tileID, color);
        tmpTile = tileID;
    }
    
	public void update(int num) {
		updateList.add(new Integer[] {num,0});
		hashMap.remove(num);
		
	}
	public void update(int num, int color) {
		updateList.add(new Integer[] {num,color});
		hashMap.put(num, color);
	}
	
	public boolean isEmpty(int tileId) {
        if (tileId == -1) return  false;
        else if (hashMap.get(tileId) == null) return true;
        else return false;
    }
	
	public int getStoneColor(int tileID){//타일에 스톤이 없거나 키값이 없으면 0리턴
        if(hashMap.get(tileID) == null) return 0;
        else if ((int) hashMap.get(tileID) == Color.Black.ordinal()) return Color.Black.ordinal();
        else if ((int) hashMap.get(tileID) == Color.White.ordinal()) return Color.White.ordinal();
        else return 0;

    }
	
	protected void rollBack(){
        hashMap.remove(tmpTile);
    }
	
	protected void removeStone(ArrayList<Integer> list){
        for (int i = 0 ; i < list.size(); i++){
            update((int)list.get(i));
        }
    }
	
	public void setPae(int tileId){//패일 때
        if (paeTile != -1){
        	updateList.add(new Integer[] {tileId,0});
        }
        if (tileId == -1) {
            paeTile = tileId;
            return;
        }
        else {
            paeTile = tileId;
//            tile = (FrameLayout) tileList.get(paeTile);
//            tile.setBackground(drawer.drawSquare("패")); //스크린에 띄울 부분
        }
    }
	public int getPaeTile() {
        return paeTile;
    }
	
	public void setTileList(ArrayList tileList) {
	    this.tileList = tileList;
	}
	
	public void clear() {
		hashMap.clear();
		tmpTile=-1;
		paeTile=-1;
	}
}
