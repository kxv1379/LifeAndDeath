package com.project.LifeAndDeath.Controller;

import java.util.ArrayList;
import java.util.HashMap;

import com.project.LifeAndDeath.Model.Quiz;
import com.project.LifeAndDeath.Model.VO;

public class Rule {
	
	public enum Color{
		Empty,Black,White
	}
	
	private enum Turn {
        BLACK, WHITE
    }
	
	 public enum Surround{
	        SOUTH, NORTH, EAST, WEST, SE, NW, SW, NE, NOTHING
	}
	 
	public Board board;
	public Record record;
	
    public int black_Score = 0;//사실 안쓰임
    public int white_Score = 0;//사실 안쓰임22
    public int move_Count = 0;
    
	private ArrayList<Integer> connectedStoneList = new ArrayList<>();
	private ArrayList<Integer> wayOutList = new ArrayList<>();
	
	private Turn turn = Turn.BLACK;
	
	public Rule(Board board) {
		this.board=board;
	}
	
	public Rule (Board board, int color_FirstMove){//editMode 보통 에디터에서 편집 후 받아오는 경우
        this.board = board;
        if (color_FirstMove == Color.White.ordinal())
            turn = Turn.WHITE;
        else turn = Turn.BLACK;
    }
    public Rule (Board board, int color_FirstMove, int black_Score, int white_Score){
        this.board = board;
        if (color_FirstMove == Color.White.ordinal())
            turn = Turn.WHITE;
        else turn = Turn.BLACK;
        this.black_Score = black_Score;
        this.white_Score = white_Score;
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
	
	  public boolean isKillingMove(int tileID, int color){//스파게티 아님 외부서 써도 됨.
	        board.check(tileID, color);
	        boolean b = false;
	        boolean tile[] = getEnemy(tileID, color);//변두리라 타일 자체가 없는 경우false
	        int c;
	        if(color == Color.White.ordinal()) c=Color.Black.ordinal();
	        else c = Color.White.ordinal();
	        // 그 다음 해당 돌이 죽는 돌인가를 검사.
	        if (tile[0]){
	            connectedStoneList.clear();
	            findConnectedStoneByTileID(tileID + 19, c);
	            if (isDead(connectedStoneList)){
	                b= true;
	            }
	        }
	        if (tile[1]){
	            connectedStoneList.clear();
	            findConnectedStoneByTileID(tileID - 19, c);
	            if (isDead(connectedStoneList)){
	                b= true;
	            }
	        }
	        if (tile[2]){
	            connectedStoneList.clear();
	            findConnectedStoneByTileID(tileID + 1, c);
	            if (isDead(connectedStoneList)){
	                b= true;
	            }
	        }
	        if (tile[3]){
	            connectedStoneList.clear();
	            findConnectedStoneByTileID(tileID - 1, c);
	            if (isDead(connectedStoneList)){
	                b= true;
	            }
	        }
	        board.rollBack();
	        return b;
	    }
	    public int howManyKillingThisMove(int tileID, int color){//스파게티 아님 외부서 써도 됨.
	        board.check(tileID, color);
	        int result = 0;
	        boolean tile[] = getEnemy(tileID, color);//변두리라 타일 자체가 없는 경우false
	        int c;
	        if(color == Color.White.ordinal()) c=Color.Black.ordinal();
	        else c = Color.White.ordinal();
	        // 그 다음 해당 돌이 죽는 돌인가를 검사.
	        if (tile[0]){
	            connectedStoneList.clear();
	            findConnectedStoneByTileID(tileID + 19, c);
	            if (isDead(connectedStoneList)){
	                result += connectedStoneList.size();
	            }
	        }
	        if (tile[1]){
	            connectedStoneList.clear();
	            findConnectedStoneByTileID(tileID - 19, c);
	            if (isDead(connectedStoneList)){
	                result += connectedStoneList.size();
	            }
	        }
	        if (tile[2]){
	            connectedStoneList.clear();
	            findConnectedStoneByTileID(tileID + 1, c);
	            if (isDead(connectedStoneList)){
	                result += connectedStoneList.size();
	            }
	        }
	        if (tile[3]){
	            connectedStoneList.clear();
	            findConnectedStoneByTileID(tileID - 1, c);
	            if (isDead(connectedStoneList)){
	                result += connectedStoneList.size();
	            }
	        }
	        board.rollBack();
	        return result;
	    }
	    public Surround getRelationA2B(int surroundTileID, int centerTileID){//B가 Center, A가 Surround
	        int value = surroundTileID - centerTileID;
	        switch (value){
	            case 19: return Surround.SOUTH;
	            case -19: return Surround.NORTH;
	            case +1: return Surround.EAST;
	            case -1: return Surround.WEST;
	            case 20: return Surround.SE;
	            case -20: return Surround.NW;
	            case 18: return Surround.SW;
	            case -18: return Surround.NE;
	            default: return Surround.NOTHING;
	        }
	    }
	    /*public int getTileIDBySurround (Surround surround) {//이거 아직 쓰면 안됨. 수정해야함.
	        switch (surround){
	            case SOUTH: return 19;
	            case NORTH: return -19;
	            case EAST: return 1;
	            case WEST: return -1;
	            case SE: return 20;
	            case NW: return -20;
	            case SW: return 18;
	            case NE: return -18;
	            default: return 999;
	        }
	    }*/
	    public boolean[] getEnemy(int tileId, int color, int option){//사방에 적이 있으면 각 방향에 true값 배열 리턴
	        boolean[] id;
	        if (option == 4){
	            return getEnemy(tileId, color);
	        } else {
	            id = getWayOut(tileId, 8);
	            if (id[0])
	                id[0] = isEnemy(tileId + 19, color);
	            if (id[1])
	                id[1] = isEnemy(tileId - 19, color);
	            if (id[2])
	                id[2] = isEnemy(tileId + 1, color);
	            if (id[3])
	                id[3] = isEnemy(tileId - 1, color);
	            if (id[4])
	                id[4] = isEnemy(tileId + 20, color);
	            if (id[5])
	                id[5] = isEnemy(tileId - 20, color);
	            if (id[6])
	                id[6] = isEnemy(tileId + 18, color);
	            if (id[7])
	                id[7] = isEnemy(tileId - 18, color);
	            return id;
	        }
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
		
		 public boolean[] getWayOut(int tileId, int option){//옵션은 방향을 나타냄. 4, 혹은 8.
		        boolean tile[] = new boolean[option];
		        if (option == 4){
		            if (tileId < 342) tile[0] = true;
		            else tile[0] = false;
		            if (tileId >= 19) tile[1] = true;
		            else tile[1] = false;
		            if (tileId%19 != 18) tile[2] = true;
		            else tile[2] = false;
		            if (tileId%19 != 0) tile[3] = true;
		            else tile[3] = false;
		        }
		        else if (option == 8){
		            if (tileId < 342) tile[0] = true;
		            else tile[0] = false;
		            if (tileId >= 19) tile[1] = true;
		            else tile[1] = false;
		            if (tileId%19 != 18) tile[2] = true;
		            else tile[2] = false;
		            if (tileId%19 != 0) tile[3] = true;
		            else tile[3] = false;
		            tile[4] = true;
		            tile[5] = true;
		            tile[6] = true;
		            tile[7] = true;
		            if (!tile[0]){
		                tile[4] = false;
		                tile[6] = false;
		            }
		            if (!tile[1]){
		                tile[5] = false;
		                tile[7] = false;
		            }
		            if (!tile[2]){
		                tile[4] = false;
		                tile[7] = false;
		            }
		            if (!tile[3]){
		                tile[6] = false;
		                tile[5] = false;
		            }
		        }
		        return tile;
		    }
		 
		public boolean isEnemy(int tileId, int color){
		        if(board.isEmpty(tileId) || board.getStoneColor(tileId) == color)
		            return false;
		        else
		            return true;
		}
		
		 public boolean[] getFriend(int tileID, int color, int option){//옵션은 4, 8만 구현했음. 4는 옵션 안준 것과 동일, 8은 8방
		        boolean[] tile = getWayOut(tileID, option);
		        if (option == 4){
		            if (tile[0])
		                tile[0] = (board.getStoneColor(tileID +19)==color);
		            if (tile[1])
		                tile[1] = (board.getStoneColor(tileID -19)==color);
		            if (tile[2])
		                tile[2] = (board.getStoneColor(tileID +1)==color);
		            if (tile[3])
		                tile[3] = (board.getStoneColor(tileID -1)==color);

		        } else if (option == 8){
		            if (tile[0])
		                tile[0] = (board.getStoneColor(tileID +19)==color);
		            if (tile[1])
		                tile[1] = (board.getStoneColor(tileID -19)==color);
		            if (tile[2])
		                tile[2] = (board.getStoneColor(tileID +1)==color);
		            if (tile[3])
		                tile[3] = (board.getStoneColor(tileID -1)==color);
		            if (tile[4])
		                tile[4] = (board.getStoneColor(tileID +20)==color);
		            if (tile[5])
		                tile[5] = (board.getStoneColor(tileID -20)==color);
		            if (tile[6])
		                tile[6] = (board.getStoneColor(tileID +18)==color);
		            if (tile[7])
		                tile[7] = (board.getStoneColor(tileID -18)==color);
		        }
		        return tile;
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
	   public int[] getTileID(int tileId, int option){
	        if (option != 8){
	            return getTileID(tileId);
	        } else {
	            boolean b[] = getWayOut(tileId, 8);
	            int tile[] = new int[8];
	            if (b[0]) tile[0] = tileId + 19;
	            else tile[0] = -1;
	            if (b[1]) tile[1] = tileId - 19;
	            else tile[1] = -1;
	            if (b[2]) tile[2] = tileId + 1;
	            else tile[2] = -1;
	            if (b[3]) tile[3] = tileId - 1;
	            else tile[3] = -1;
	            if (b[4]) tile[4] = tileId + 20;
	            else tile[4] = -1;
	            if (b[5]) tile[5] = tileId - 20;
	            else tile[5] = -1;
	            if (b[6]) tile[6] = tileId + 18;
	            else tile[6] = -1;
	            if (b[7]) tile[7] = tileId - 18;
	            else tile[7] = -1;
	            return tile;
	        }
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
	   
	   public void findConnectedStoneByTileID(int id, ArrayList list){
	        connectedStoneList.clear();
	        if (board.isEmpty(id)){
	            System.out.println("갑분띠???");
	            return;
	        }
	        findConnectedStoneByTileID(id, board.getStoneColor(id));//private method, 재귀호출메소드
	        list.clear();
	        list.addAll(connectedStoneList);
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
	   
	   public boolean isOwn(int tileID, int color){
	        boolean result = isKillingMove(tileID, color);
	        if (result == true){
	            return false;//죽이는 수니까 자살 아님.
	        } else {//죽이지 못하고, 뒀는데, 공배 없으면 자살이라 true, 공배있으면 false
	            board.check(tileID, color);
	            result = ownCheck(tileID, color);
	            board.rollBack();
	            return result;
	        }
	    }
	   
	   public boolean isDead(ArrayList<Integer> connectedStoneList) {//공배 0개면 true
	       wayOutList.clear();
	       for (int i = 0; i < connectedStoneList.size(); i++){
	           checkAndAddSafeWayOut((int)connectedStoneList.get(i));
	       }
	       return wayOutList.isEmpty();
	   }
	   
	   public boolean isAtari(ArrayList connectedStoneList){
	        wayOutList.clear();
	        for (int i = 0; i < connectedStoneList.size(); i++){
	            checkAndAddSafeWayOut((int)connectedStoneList.get(i));
	        }
	        if (wayOutList.size() == 1)
	            return true;
	        else return false;
	    }
	    public int countWayOut(ArrayList connectedStoneList){
	        wayOutList.clear();
	        for (int i = 0; i < connectedStoneList.size(); i++){
	            checkAndAddSafeWayOut((int)connectedStoneList.get(i));
	        }
	        return wayOutList.size();
	    }
	    public ArrayList getWayOutListByStoneGroup(ArrayList connectedStoneList){
	        wayOutList.clear();
	        for (int i = 0; i < connectedStoneList.size(); i++){
	            checkAndAddSafeWayOut((int)connectedStoneList.get(i));
	        }
	        return (ArrayList)wayOutList.clone();
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
		   move_Count = 0;
	       record.getRecord().clear();
	       black_Score = 0;
	       white_Score = 0;
		   
	   }
	   
	   public int getMove_Count() {
	       return move_Count;
	   }
	   
	   public void undo(int i, Quiz quiz) {
	       while (i > 0) {
	           record.getRecord().remove(record.getRecord().size() - 1);
	           i--;
	       }
	
	       ArrayList<VO> tmpList = (ArrayList<VO>) record.getRecord().clone();
	       clear();
	       board.setHashMap((HashMap) quiz.getHashMap().clone());
	       for (int j = 0; j < tmpList.size(); j++) {
	           VO vo = (VO) tmpList.get(j);
	           goRule(vo.getX() + vo.getY() * 19, vo.getColor());
	       }
	
	   }
	   
	   public void reset(){
	        move_Count = 0;
	        record.getRecord().clear();
	        black_Score = 0;
	        white_Score = 0;
	    }
	}