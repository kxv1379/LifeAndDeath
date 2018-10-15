package com.project.LifeAndDeth;

import java.util.ArrayList;

public class Rule {
	Board board;
	Record record;
	
    public int black_Score = 0;//사실 안쓰임
    public int white_Score = 0;//사실 안쓰임22
    public int move_Count = 0;
    
	private ArrayList<Integer> connectedStoneList = new ArrayList<>();
	private ArrayList<Integer> wayOutList = new ArrayList<>();
	
	public enum Color{
		Empty,Black,White
	}
	
	private enum Turn {
        BLACK, WHITE
    }
	
	public Rule(Board board) {
		this.board=board;
	}

	public int killOwnAction(int tileId, int color) {
		board.check(tileId, color);
		if(isKillingMoveAndAction(tileId, color)){
            board.update(tileId, color);
            int count = getFriendCount(tileId, color);
            int count2 = getEmptyCount(tileId);
            if(count == 0 && count2 == 1){//착수지점 사방에 아군이 없고(count = 0), 공배가 하나(count = 1)인 경우 공배TileID를 리턴
                int tmpID;
                int tile[] = getTileID(tileId);
                for (int i = 0 ; i < 4; i++){
                    if (board.isEmpty(tile[i])){
                        tmpID = tile[i];
                        return tmpID;
                    }
                }
                return  -1;
            }
            else return -1;
        } else
        if(ownCheck(tileId, color)){
            board.rollBack();
            return -2;
        }
        else {
            board.update(tileId, color);
            return -1;
        }
	}

	private boolean isKillingMoveAndAction(int tileId, int color){//착수로 인해 상대 돌이 죽을 시 true, 아닐 시 false
        boolean b = false;
        boolean tile[] = getEnemy(tileId, color);//변두리라 타일 자체가 없는 경우false
        int c;
        
        if(color == Color.White.ordinal()) c=Color.Black.ordinal();
        else c = Color.White.ordinal();
        // 그 다음 해당 돌이 죽는 돌인가를 검사.
        
        if (tile[0]){
            connectedStoneList.clear();
            findConnectedStoneByTileID(tileId + 19, c);//주변에 같은 색상의 연결된 돌의 위치를 connectedStoneList에 저장.
            
            if (isDead(connectedStoneList)){
                setScore(connectedStoneList.size(), color);
                board.removeStone(connectedStoneList);
                tile=getEnemy(tileId, color);
                b= true;
            }
        }
        
        if (tile[1]){
            connectedStoneList.clear();
            findConnectedStoneByTileID(tileId - 19, c);
            
            if (isDead(connectedStoneList)){
                setScore(connectedStoneList.size(), color);
                board.removeStone(connectedStoneList);
                tile=getEnemy(tileId, color);
                b=true;
            }
        }
        if (tile[2]){
            connectedStoneList.clear();
            findConnectedStoneByTileID(tileId + 1, c);
            
            if (isDead(connectedStoneList)){
                setScore(connectedStoneList.size(), color);
                board.removeStone(connectedStoneList);
                tile=getEnemy(tileId, color);
                b=true;
            }
        }
        if (tile[3]){
            connectedStoneList.clear();
            findConnectedStoneByTileID(tileId - 1, c);
            
            if (isDead(connectedStoneList)){
                setScore(connectedStoneList.size(), color);
                board.removeStone(connectedStoneList);
                b=true;
            }
        }
        return b;
    }
	
	 public boolean[] getEnemy(int tileId, int color){//사방에 적이 있으면 각 방향에 true값 배열 리턴
	        boolean id[] = getWayOut(tileId);
	        if (id[0])
	            id[0] = isEnemy(tileId+19, color);
	        if (id[1])
	            id[1] = isEnemy(tileId-19, color);
	        if (id[2])
	            id[2] = isEnemy(tileId+1, color);
	        if (id[3])
	            id[3] = isEnemy(tileId-1, color);

	        return id;
	 }
	 
	 public boolean[] getWayOut(int tileId){
	        boolean tile[] = new boolean[4];
	        if (tileId < 342) tile[0] = true;
	        else tile[0] = false;
	        if (tileId >= 19) tile[1] = true;
	        else tile[1] = false;
	        if (tileId%19 != 18) tile[2] = true;
	        else tile[2] = false;
	        if (tileId%19 != 0) tile[3] = true;
	        else tile[3] = false;
	        return tile;
	}
	 
	public boolean isEnemy(int tileId, int color){
	        if(board.isEmpty(tileId) || board.getStoneColor(tileId) == color)
	            return false;
	        else
	            return true;
	}
	
	public boolean[] getFriend(int tileID, int color){
        boolean[] tile = getWayOut(tileID);
        if (tile[0])
            tile[0] = (board.getStoneColor(tileID +19)==color);
        if (tile[1])
            tile[1] = (board.getStoneColor(tileID -19)==color);
        if (tile[2])
            tile[2] = (board.getStoneColor(tileID +1)==color);
        if (tile[3])
            tile[3] = (board.getStoneColor(tileID -1)==color);

        return tile;
    }
	
   private int getFriendCount(int tileID, int color){
        int count = 0 ;
        int tile[] = getTileID(tileID);
        for (int i = 0 ; i<4; i++){
            if (board.getStoneColor(tile[i]) == color)
                count ++;
        }
        return count;
    }
   
   public int[] getTileID(int tileId){
       boolean b[] = getWayOut(tileId);
       int tile[] = new int[4];
       if (b[0]) tile[0] = tileId +19;
       else tile[0] = -1;
       if (b[1]) tile[1] = tileId -19;
       else tile[1] = -1;
       if (b[2]) tile[2] = tileId +1;
       else tile[2] = -1;
       if (b[3]) tile[3] = tileId -1;
       else tile[3] = -1;
       return  tile;
   }
   
   private int getEmptyCount(int tileID){
       int count = 0;
       int tile[] = getTileID(tileID);
       for (int i = 0 ; i < 4 ; i++){
           if(board.isEmpty(tile[i])) count ++;
       }
       return count;
   }
   
   public boolean ownCheck (int tileId, int color){//착수 금지 체크: 자살 금지 해당 자리가 자살로인한 착수 금지면 true, 아니면 false
       connectedStoneList.clear();
       boolean friend[] = getFriend(tileId, color);
       if (friend[0])
           findConnectedStoneByTileID(tileId + 19, color);//재귀함수형 함수..
       if (friend[1])
           findConnectedStoneByTileID(tileId - 19, color);
       if (friend[2])
           findConnectedStoneByTileID(tileId + 1, color);
       if (friend[3])
           findConnectedStoneByTileID(tileId - 1, color);
       if(overlapCheck(tileId, connectedStoneList) == false) connectedStoneList.add(tileId);
       return isDead(connectedStoneList);
   }
   
   private void findConnectedStoneByTileID(int tileId, int color) {//함수 호출 전, 반드시 list초기화 요망
       if (overlapCheck(tileId, connectedStoneList)) return;//재귀함수 탈출, 이미 등록되어있는 돌.
       connectedStoneList.add(tileId);
       boolean friend[] = getFriend(tileId, color);
       if (friend[0])
           findConnectedStoneByTileID(tileId+19, color);
       if (friend[1])
           findConnectedStoneByTileID(tileId-19, color);
       if (friend[2])
           findConnectedStoneByTileID(tileId+1, color);
       if (friend[3])
           findConnectedStoneByTileID(tileId-1, color);
   }
   
   public boolean overlapCheck(int tileID, ArrayList<Integer> list){//이미 있으면 true
       for (int i = 0 ; i <list.size();i++){
           if ((int)list.get(i) == tileID){
               return true;
           }
       }
       return false;
   }
   
   public boolean isDead(ArrayList<Integer> connectedStoneList) {//공배 0개면 true
       wayOutList.clear();
       for (int i = 0; i < connectedStoneList.size(); i++){
           checkAndAddSafeWayOut((int)connectedStoneList.get(i));
       }
       return wayOutList.isEmpty();
   }
   
   private void checkAndAddSafeWayOut(int tileId) {
       boolean id[] = getWayOut(tileId);
       if (board.isEmpty(tileId + 19) && id[0] && overlapCheck(tileId + 19, wayOutList) == false){
           wayOutList.add(tileId + 19);
       }
       if (board.isEmpty(tileId - 19) && id[1] && overlapCheck(tileId - 19, wayOutList) == false){
           wayOutList.add(tileId - 19);
       }
       if (board.isEmpty(tileId + 1) && id[2] && overlapCheck(tileId + 1, wayOutList) == false){
           wayOutList.add(tileId + 1);
       }
       if (board.isEmpty(tileId - 1) && id[3] && overlapCheck(tileId - 1, wayOutList) == false){
           wayOutList.add(tileId - 1);
       }
   }
   
   private void setScore(int score, int color){
       if(color == Color.White.ordinal()) white_Score += score;
       else black_Score += score;
   }
   
   public boolean goRule(int tileId, int color){
       if (tileId < 0){
           pass(color);
           return true;
       }
       if (paeCheck(tileId)){
           return false;
       }
       if (existenceStoneCheck(tileId))
           return false;
       int i;
       i = killOwnAction(tileId, color);

       if(i == -2){//자살에 한해서..
           return false;
       }
       if(i != -1){
           if(getEmptyCount(i) == 0){
               board.setPae(i);
               record.getRecord().add(move_Count, new VO(tileId%19, tileId/19, color, move_Count));
               move_Count++;
               return true;
           }
       } else {
           board.setPae(-1);
           record.getRecord().add(move_Count, new VO(tileId%19, tileId/19, color, move_Count));
           move_Count++;
           return true;
       }
       return false;
   }
   
   public void pass(int color){
       record.getRecord().add(move_Count, new VO(-1, -1, color, move_Count));
       move_Count++;
       board.setPae(-1);
   }
   public boolean paeCheck(int tileId){//패정보는 board에, 해당 자리가 패로인한 착수 금지면 true, 아니면 false

       if (board.getPaeTile() == tileId) {
           return true;
       }
       else {
           return false;
       }
   }
   
   public boolean existenceStoneCheck(int tileId){//착수금지 체크: 돌 색과는 관계없이 타일에 돌이 이미 존재하여 착수 금지면 true, 아니면 false
       if (board.isEmpty(tileId))
           return false;
       else
           return true;
   }
   public void setRecord(Record record) {
       this.record = record;
   }
   
   public Record getRecord() {
       return record;
   }
   
   public void clear() {
	   move_Count=0;
   }

}
