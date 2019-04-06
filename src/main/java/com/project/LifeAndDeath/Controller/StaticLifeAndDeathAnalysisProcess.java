package com.project.LifeAndDeath.Controller;

	
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class StaticLifeAndDeathAnalysisProcess extends Rule {

    public enum LifeAndDeath{
        LIFE, DEATH, PAE, DRAW, I_DONT_KNOW;
    }
    
    public enum Color{
		Empty,Black,White
	}
    
    private ArrayList<StoneGroup>
            stoneGroupsList_BLACK = new ArrayList<>(),
            stoneGroupsList_WHITE = new ArrayList<>();
    private ArrayList groupInfo = new ArrayList();
    private HashMap
            resultMap = new HashMap(),
            LND_InfoMap = new HashMap();
    EyePoint eyePoint[]= new EyePoint[361];

    public StaticLifeAndDeathAnalysisProcess(Board board) {
        super(board);
        for (int i = 0; i < 361 ; i ++) {
            eyePoint[i] = new EyePoint(i);
        }
    }

    private void lifeAndDeathAnalysisProcess(){// CPU 부하 심할듯. 자주쓰지맙시다.

        gatherInfo();//초기정보 수집(단수, 환격 상태, 수상전의 남은 수, 돌의 영향력 세기 등)
        makeStoneGroup(); // 돌무리(StoneGroup)만들기
        findAndProcessDeathGroup();//최소한으로도 살 수 없는 돌 삭제하기
        grouping(); // 돌무리 형태별로 분리하기
        processAndGetAttributeToGroup();//돌무리 주변 그룹과 링크그룹으로 분리, 상대 정보와 그룹 속성 정하기
        processToEffectiveArea(); //집으로 셀수 있는 유효영역과 유효 가능 영역 구분하기 /// 이거 뭔말인지 모르겠고, 사실 필요없을 거 같음
        processLifeAndDeath(); //핵심. 이것만 되도 시연엔 큰 지장 없을듯
        processToCountFight();//수상전 처리.. 수상전手相戰 번역 뭘로 해야하냐??
        processToDrawGroup();//돌 그룹간의 빅처리
        processToExceptionGroup();//양 빅과 옥집활 등 사활 예외 처리.
        whoIsWin();//사실 안쓰임. 개발필요 x
    }
    public int getIndexByValue(ArrayList list, int value){
        for (int i =0; i < list.size(); i++){
            if (list.get(i).equals(value))
                return i;
        }
        return -1;
    }
    public double getLifePointByGroupNo(int groupNo){
        double result = 0;
        if (groupNo < 0){
            for (int i = 0 ; i < stoneGroupsList_WHITE.size(); i++){
                if (stoneGroupsList_WHITE.get(i).groupNo == groupNo){
                    result += stoneGroupsList_WHITE.get(i).getLifePoint();
                }
            }
        } else {
            for (int i = 0; i < stoneGroupsList_BLACK.size(); i++){
                if (stoneGroupsList_BLACK.get(i).groupNo == groupNo){
                    result += stoneGroupsList_BLACK.get(i).getLifePoint();
                }
            }
        }
        return result;
    }
    public LifeAndDeath getLND(int groupNo, int firstColor){
        double value = (double)LND_InfoMap.get(groupNo);
        if (value >= 2){
            return LifeAndDeath.LIFE;
        } else if (value >= 1.5){
            if (groupNo > 0){
                if (firstColor == Color.Black.ordinal()){
                    return LifeAndDeath.LIFE;
                } else {
                    return LifeAndDeath.DEATH;
                }
            } else {
                if (firstColor == Color.Black.ordinal()){
                    return LifeAndDeath.DEATH;
                } else {
                    return LifeAndDeath.LIFE;
                }
            }
        } else {
            return LifeAndDeath.I_DONT_KNOW;
        }
    }
    public void reset(){
        stoneGroupsList_BLACK.clear();
        stoneGroupsList_WHITE.clear();
        groupInfo.clear();
        resultMap.clear();
        LND_InfoMap.clear();
    }
    public void upDateResultValue() {
        reset();
        lifeAndDeathAnalysisProcess();
        //귀찮아서 어쩔 수 없다. 스파게티 만들어야겠음. 맵도 받고, 그룹 정보도 따로 받고, 사활정보도 따로 받아야함.
        //정보들 받기 전에 이 메소드 실행시켜줘야 하는 건 덤.
        //스파게티 풀려면 VO_LND같은 거 만들어서 처리해주면 됨.
        for (StoneGroup stoneGroup : stoneGroupsList_BLACK){
            for (int i = 0; i < stoneGroup.getStoneList().size(); i++){
                resultMap.put(stoneGroup.getStoneList().get(i), stoneGroup.groupNo);
                if(!overlapCheck(stoneGroup.groupNo, groupInfo)){
                    //단지 그룹넘버 접근을 쉽게 하기 위한 리스트..
                    groupInfo.add(stoneGroup.groupNo);
                }
                //그룹 넘버에 따른 사활정보저장,
                //double값을 저장(2이상이면 그냥 삶,
                //1.5이상이면 사활 걸리고, 선후수 여부에 따라 판단)
                //그 외엔 값에 따라 주변 적군과 정보 비교하여서 죽음여부 판단해야함(원래는)
                if (LND_InfoMap.get(stoneGroup.groupNo) != null){
                    double value = (double)LND_InfoMap.get(stoneGroup.groupNo) + stoneGroup.getLifePoint();
                    LND_InfoMap.put(stoneGroup.groupNo, value);
                } else {
                    double value = stoneGroup.getLifePoint();
                    LND_InfoMap.put(stoneGroup.groupNo, value);
                }
            }
        }
        System.out.println("95, stone group BLACK: "+ stoneGroupsList_BLACK.size());
        for (StoneGroup stoneGroup : stoneGroupsList_WHITE){
            for (int i = 0; i < stoneGroup.getStoneList().size(); i++){
                resultMap.put(stoneGroup.getStoneList().get(i), stoneGroup.groupNo);
                if(!overlapCheck(stoneGroup.groupNo, groupInfo)){
                    groupInfo.add(stoneGroup.groupNo);
                }
                if (LND_InfoMap.get(stoneGroup.groupNo) != null){
                    double value = (double)LND_InfoMap.get(stoneGroup.groupNo) + stoneGroup.getLifePoint();
                    LND_InfoMap.put(stoneGroup.groupNo, value);
                } else {
                    double value = stoneGroup.getLifePoint();
                    LND_InfoMap.put(stoneGroup.groupNo, value);
                }
            }
        }
        System.out.println("111, stone group WHITE: "+ stoneGroupsList_WHITE.size());
    }

    private void processToExceptionGroup() {
    }

    private void processToDrawGroup() {
    }
    private void processAndGetAttributeToGroup() {
    }
    private void processLifeAndDeath() {
        for (StoneGroup stoneGroup : stoneGroupsList_WHITE){
            stoneGroup.setLifePoint(calcLifePoint(stoneGroup));
        }
        for (StoneGroup stoneGroup : stoneGroupsList_BLACK){
            stoneGroup.setLifePoint(calcLifePoint(stoneGroup));
        }
    }
    private void processToEffectiveArea() {//집으로 셀수 있는 유효영역과 유효 가능 영역 구분하기 /// 이거 뭔말인지 모르겠고, 사실 필요없을 거 같음
    }

    private void processToCountFight() {
    }

    private void gatherInfo(){

    }

    private boolean findAndProcessDeathGroup(){
        return false;
    }
    private void makeStoneGroup(){
        findAllStoneGroupInMap();
        ArrayList publicWayOutList = new ArrayList();
        ArrayList linkedGroup = new ArrayList();
        ArrayList weakLinkedGroup = new ArrayList();
        for (int i = 0; i < stoneGroupsList_BLACK.size(); i++){
            int number = stoneGroupsList_BLACK.get(i).getGroupNo();
            ArrayList tmp = stoneGroupsList_BLACK.get(i).getWayOutList();
            for (int j = i + 1; j < stoneGroupsList_BLACK.size(); j++){
                for (int k = 0; k < stoneGroupsList_BLACK.get(j).getWayOutList().size();k++){
                    if (overlapCheck((int)stoneGroupsList_BLACK.get(j).getWayOutList().get(k), tmp)){
                        publicWayOutList.add(stoneGroupsList_BLACK.get(j).getWayOutList().get(k));
                    }
                }
                if (publicWayOutList.size()>1){//끊기지 않을 돌. 쌍립 등.
                    stoneGroupsList_BLACK.get(j).setGroupNo(number);
                    linkedGroup.add(stoneGroupsList_BLACK.get(j));
                } else if (publicWayOutList.size() == 1){
                    //사실 이 if else안에 공통 공배 아니어도 장문 등으로 연결된 돌들 처리 해줘야함
                    stoneGroupsList_BLACK.get(j).setGroupNo(number);
                    weakLinkedGroup.add(stoneGroupsList_BLACK.get(j)); // 끊길 수 있는 돌(공배 하나이거나 등등의 이유로)
                } else {
                    //사실 이 if else안에 공통 공배 아니어도 장문 등으로 연결된 돌들 처리 해줘야함
                }
            }
        }
        ////원래 흰색 검은색 나눌 필요는 없는데, 이미 나눈 거 걍 한다...ㅂㄷㅂㄷ
        for (int i = 0; i < stoneGroupsList_WHITE.size(); i++){
            int number = stoneGroupsList_WHITE.get(i).getGroupNo();
            ArrayList tmp = stoneGroupsList_WHITE.get(i).getWayOutList();
            for (int j = i + 1; j < stoneGroupsList_WHITE.size(); j++){
                for (int k = 0; k < stoneGroupsList_WHITE.get(j).getWayOutList().size();k++){
                    if (overlapCheck((int)stoneGroupsList_WHITE.get(j).getWayOutList().get(k), tmp)){
                        publicWayOutList.add(stoneGroupsList_WHITE.get(j).getWayOutList().get(k));
                    }
                }
                if (publicWayOutList.size()>1){//끊기지 않을 돌. 쌍립 등.
                    stoneGroupsList_WHITE.get(j).setGroupNo(number);
                    linkedGroup.add(stoneGroupsList_WHITE.get(j));
                } else if (publicWayOutList.size() == 1){
                    //사실 이 if else안에 공통 공배 아니어도 장문 등으로 연결된 돌들 처리 해줘야함
                    stoneGroupsList_WHITE.get(j).setGroupNo(number);
                    weakLinkedGroup.add(stoneGroupsList_WHITE.get(j)); // 끊길 수 있는 돌(공배 하나이거나 등등의 이유로)
                } else {
                    //사실 이 if else안에 공통 공배 아니어도 장문 등으로 연결된 돌들 처리 해줘야함
                }
            }
        }
    }

    private void grouping() {
    }

    private void whoIsWin(){//사실 안쓰임. 개발필요 x
        //원래 들어갈 내용은 공배 처리 및 최종 집 계산
    }
    private void findAllStoneGroupInMap(){
        ArrayList tmp = new ArrayList();
        StoneGroup stoneGroup;
        for (int i =0; i < 361; i++){
            if (!board.isEmpty(i)){
                if (!overlapCheck(i, tmp)){
                    int color = board.getStoneColor(i);
                    if (color == Color.Black.ordinal()){
                        stoneGroup = new StoneGroup(i);
                        stoneGroup.setGroupNo(i+1);
                        stoneGroupsList_BLACK.add(stoneGroup);
                        tmp.addAll(stoneGroupsList_BLACK);
                    } else if (color == Color.White.ordinal()){
                        stoneGroup = new StoneGroup(i);
                        stoneGroup.setGroupNo((i+1)*-1);
                        stoneGroupsList_BLACK.add(stoneGroup);
                        tmp.addAll(stoneGroupsList_WHITE);
                    } else {//컬러값 이상한 거 나오면 그냥 다음거 찾게됨.(null값)
                        continue;
                    }
                }
            }
        }
    }

    private void EffectPoint(int tileID, int color, ArrayList list){ //isEffectPoint전용 함수. 재귀호출함수임.
        return;
    }

    public ArrayList getGroupInfo() {
        return groupInfo;
    }
    public HashMap getResultMap(){
        return resultMap;
    }
    private ArrayList findEffectArea(int tileID, int color){
        ArrayList wayOutList = new ArrayList();
        EyePoint eyePoint = this.eyePoint[tileID];
        eyePoint.calcEffect();
        wayOutList.add(eyePoint);

        return new ArrayList();///d아직미완성
    }
    public ArrayList findsafetyArea(StoneGroup group){// 궁도파악 isLifePoint에서 미니멀 혼자 살기 판단 불가능시..
        ArrayList result = new ArrayList();
        if (group.getWayOutList().size() == 1){
            if (isKillingMove((int)group.getWayOutList().get(0), group.color)){

            } else {

            }
        }
        return result;//미완성제발 ...끝내자
    }
    public double calcLifePoint(StoneGroup stoneGroup){
        // result의 절대값이 2면 살고, 1.5이상이면 선수 사활 걸림.
        //result = 0, 옥집이 아닌 온존한 착수금지 한집이 확보될 때마다 1씩 커짐
        double result = 0;
        int color = stoneGroup.getColor();
        if (stoneGroup.getWayOutList().size() < 1){
            return result;
        }
        ArrayList<EyePoint> list = new ArrayList<>();
        Integer[] tmp = new Integer[stoneGroup.getWayOutList().size()];
        stoneGroup.getWayOutList().toArray(tmp);

        for (int eye : tmp){
            int maxCount_tmp = eyePoint[eye].maxWayOut*2 - 1;
            int enemyCount = 0;
            //maxCount_tmp는 온존한 집 확보 조건의 최대치(최대치만큼 우군돌이 놓여있으면 무조건 1집.
            //단, 해당 그룹 자체가 단수상태면 안댐. 단수상태는 미리 걸렀으니 ㄱㅊ)
            //저 수치를 만족하는 자리(eye)가 두개 있으면 독립으로 삶.
            //1.사방에 enemyCount가 1이상인 경우, result값은 변하지않는다.

            //2.maxCount가 7이 아니면서 팔방(대각선자리)에 enemyCount가 1이상인 경우, result값은 변하지않는다.
            //3.maxCount가 7이면서 enemyCount가 2이상인 경우, result값은 변하지않는다.
            //END.이 외엔 list에 eyePoint를 등록, 마지막에 eyePoint의 관계를 비교하여 경우에 맞게 조정하여 return result

            boolean[] b = getEnemy(eye, color, 8);
            for (int i = 0; i < 4; i++){
                if (b[i]){
                    enemyCount++;
                }
            }
            if (enemyCount > 0) {
                //각 continue 마다 하드코딩을 해야합니다.
                continue;
            }

            for (int i = 4; i < 8; i++){
                if (b[i]){
                    enemyCount++;
                }
            }
            if (maxCount_tmp < 7){
                if (enemyCount > 0) {
                    //각 continue 마다 하드코딩을 해야합니다.
                    continue;
                }
            } else if (enemyCount > 1) {
                //각 continue 마다 하드코딩을 해야합니다.
                continue;
            }
            list.add(eyePoint[eye]);
        }
        ////여기까진 이 함수의 기본포멧 절반 코딩완료. 계속 테스트하면서 붙여 나가야함.
        // list에 등록된 eyePoint들은 궁도로의 활용이 가능한 것.
        //밑은 삶의 최소 조건을 만족하는지.....미니멀라이프~~ 나의 프로젝트도 미니멀하게~~
        double lifeCount = 0;
        if (list.size() < 2){
            return list.size();
        } else {
            for (int j = 0; j < list.size(); j++){//EyePoint eye : (EyePoint[]) list.toArray()
                EyePoint eye = list.get(j);
                if (isOwn(eye.getID(), getEnemyColor(color))){
                    int cnt = 0;
                    boolean[] friend = getFriend(eye.getID(), color, 8);
                    boolean[] empty = getWayOut(eye.getID(), 8);
                    int[] id = getTileID(eye.getID(), 8);
                    Surround surround;
                    for (int i = 0 ; i < 8; i++){
                        if (friend[i]){
                            cnt++;
                        } else if (empty[i]){
                            //비어있더라도 적의 착수금지 등등의 이유로 나의 권리면 cnt++
                            //자살로 인한 착수 금지만 구현, 호구 패 장문 등의 영향은 추가로 구현 해야함.
                            if (isOwn(id[i], getEnemyColor(color))){//자살로 인한 착수 금지라 ++
                                cnt++;
                            }

                            //여기선 상대에게 옥집화 시킬 수단이 있는지 코딩하면 됩니다.
                        }
                    }

                    if (cnt >= eye.maxWayOut*2-1){
                        lifeCount = lifeCount+1;
                    } else if (cnt-1 == eye.maxWayOut*2-1){
                        lifeCount = lifeCount+0.5;//호구나 패, 장문 등의 영향은 아직 구현 안됐음.
                    }
                } else { // 자살은 아니지만 집인지, 집으로 만들 수 있는지의 판단을 코딩해야함.

                }
            }
            return result;
        }
    }
    public boolean relationCheck_SNEW(int surroundTile, int centerTile){
        switch (getRelationA2B(surroundTile, centerTile)){
            case NORTH: return true;
            case EAST: return true;
            case WEST: return true;
            case SOUTH: return true;
            default: return false;
        }
    }

    public Board getBoard() {
        return board;
    }
    public void setBoard(Board board) {
        this.board = board;
    }
    public int getEnemyColor(int color){
        if (color == Color.White.ordinal()){
            return Color.Black.ordinal();
        } else {
            return Color.White.ordinal();
        }
    }
    private class EyePoint{ //Board클래스의 Tile에 해당. 표준 바둑 용어는 '눈', 서강대 석사논문 "바둑의 정적 사활 분석 프로세스 연구"에서의 번역은 Eye point
        private int x, y, maxWayOut = 0;
        private final int blackMaxRate = 1;
        private final int whiteMaxRate = -1;
        private boolean surround[];//고정 크기 배열 8
        private double effectRate; // 숫자가 클수록 흑의 영향권에 해당, 숫자가 작을수록(음수) 백의 영향권에 해당
        // 아예 백의 착수 금지 지역일 때 1, 아예 흑의 착수 금지 지역일 때 -1 주변 돌의 거리에 따라 영향률 변함.
        // 아예는 아니지만 착수가 떡수인 경우(자충수 등) 1, -1 이상

        private EyePoint(int id) {
            x = id%19;
            y = id/19;
            surround = getWayOut(id, 8);
            setMaxWayOut();
            calcEffect();

        }
        private void calcEffect(){//영향력 계산. 바둑용어로 세력.
            int white = 0, black = 0;
            ArrayList tmpList = new ArrayList();
            if (onStone()){
                //돌이 있을 때와 없을 때 따로 계산해야 할 거 같은데... 나중에 처리하는 게 맞을지도.
                effectRate = 0;
                return;
            }
        }

        public int countSurroundFriend(int color){
            int result =0;
            for (int i : getSurroundEyepointID()) {
                if (board.getStoneColor(i) == color) {
                    result++;
                }
            }
            return result;
        }
        public void setEffectRate(double effectRate) {
            this.effectRate = effectRate;
        }

        public void updateEffectRate(double value){//실수 값을 현재 rate에 더한다.
            effectRate += value;
        }

        public int[] getSurroundEyepointID() {
            int[] surroundEyePointID = new int[maxWayOut];
            int i = 0;
            int j = 0;
            for (boolean b : getWayOut(getID(), 8)){
                if (b){
                    switch (i){
                        case 0: {
                            surroundEyePointID[j] = getID()+19;
                            j++;
                        }
                        case 1: {
                            surroundEyePointID[j] = getID()-19;
                            j++;
                        }
                        case 2: {
                            surroundEyePointID[j] = getID()+1;
                            j++;
                        }
                        case 3: {
                            surroundEyePointID[i] = getID()-1;
                        }
                        case 4: {
                            surroundEyePointID[j] = getID()+20;
                            j++;
                        }
                        case 5: {
                            surroundEyePointID[j] = getID()-20;
                            j++;
                        }
                        case 6: {
                            surroundEyePointID[j] = getID()+18;
                            j++;
                        }
                        case 7: {
                            surroundEyePointID[i] = getID()-18;
                        }
                    }
                }
                i++;
            }
            return surroundEyePointID;
        }

        private EyePoint(int x, int y) {
            this.x = x;
            this.y = y;
            surround = getWayOut(getID(), 8);
            setMaxWayOut();
        }
        public boolean onStone(){
            return !board.isEmpty(getID());
        }
        public int getColor(){
            if (onStone()){
                return board.getStoneColor(getID());
            } else {
                return 0;//돌이 없음. 참고로 enum Color.WHITE가 -1
            }
        }
        public void refresh(){
            calcEffect();
        }
        private int getID(){
            return x+y*19;
        }
        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
        public double getEffectRate() {
            return effectRate;
        }


        public int getMaxWayOut() {
            return maxWayOut;
        }

        public boolean[] getSurround() {
            return surround;
        }

        public void setMaxWayOut() {//인수가 필요없음. 눈의 위치에 따라 활로수가 결정되기 때문에.
            for (boolean b : surround){
                if (b) this.maxWayOut++;
            }
        }
    }
    public EyePoint findEyePointByTileID(int tileID){
        return eyePoint[tileID];
    }
    private class StoneGroup{
        private int color, groupNo;
        private double lifePoint;
        private ArrayList<Integer> stoneList = new ArrayList();
        private ArrayList<Integer> wayOutList;

        public StoneGroup(int tileID){
            color = board.getStoneColor(tileID);
            findConnectedStoneByTileID(tileID, stoneList);
            setWayOutList();
            System.out.println("512 stoneList: " + stoneList.size());
            System.out.println("513 stoneList: " + wayOutList.size());
            Collections.sort(stoneList);//정렬해야 오버랩체크에서 오류가 안 날듯 함.
            Collections.sort(wayOutList);//동일한 이유
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public ArrayList getStoneList() {
            return stoneList;
        }

        public ArrayList getWayOutList() {
            return wayOutList;
        }

        private void setWayOutList() {
            wayOutList = getWayOutListByStoneGroup(stoneList);
        }

        public int getGroupNo() {
            return groupNo;
        }

        public void setGroupNo(int groupNo) {
            this.groupNo = groupNo;
        }

        public double getLifePoint() {
            return lifePoint;
        }

        public void setLifePoint(double lifePoint) {
            this.lifePoint = lifePoint;
        }
    }
    /*    public class LinkedGroup {
        private int color;
        private ArrayList<Integer> linkedWayOutList = new ArrayList(); // stoneGroup들 끼리 "연결시켜주는 공배"리스트(같은 색상의 돌무리가 공통으로 갖는 공배리스트)
        private ArrayList<StoneGroup> stoneGroupList = new ArrayList<>(); // 직렬로 연결된 돌무리의 리스트
        private ArrayList<StoneGroup> non_cutGroup = new ArrayList<>();//사활에서 직접적으로 관여하는 그룹.
        public LinkedGroup(StoneGroup stoneGroup){
            color = stoneGroup.getColor();
            findLinkedGroup(stoneGroup);
            setLinkedWayOutList();
            Collections.sort(linkedWayOutList);
        }
        private void setNon_CutGroup(){
            if (linkedWayOutList.size() == 0){
                return;
            }
            Integer[] tmp = linkedWayOutList.toArray(new Integer[linkedWayOutList.size()]);
            for (int wayOut : tmp){
                for (int i = 0 ; i < stoneGroupList.size(); i++){
                    ArrayList list = stoneGroupList.get(i).getWayOutList();
                    if (StaticLifeAndDeathAnalysisProcess.this.overlapCheck(wayOut, list)){
                        non_cutGroup.add(stoneGroupList.get(i));
                    }
                }
            }
        }
        private void findLinkedGroup(StoneGroup stoneGroup){
            if (overlapCheck(stoneGroup, stoneGroupList)) return;//재귀함수 탈출, 이미 등록되어있는 돌.
            stoneGroupList.add(stoneGroup);
            ArrayList list = stoneGroup.getWayOutList();
            for (int i = 0; i < list.size(); i++){//꼼수.. 별수없다.. 시연 시엔 문제없을 정도의 꼼수.
                boolean friend[] = getFriend((int)list.get(i), color, 8);
                if (friend[0])
                    findLinkedGroup(new StoneGroup((int)list.get(i)+19));
                if (friend[1])
                    findLinkedGroup(new StoneGroup((int)list.get(i)-19));
                if (friend[2])
                    findLinkedGroup(new StoneGroup((int)list.get(i)+1));
                if (friend[3])
                    findLinkedGroup(new StoneGroup((int)list.get(i)-1));
                if (friend[4])
                    findLinkedGroup(new StoneGroup((int)list.get(i)+20));
                if (friend[5])
                    findLinkedGroup(new StoneGroup((int)list.get(i)-20));
                if (friend[6])
                    findLinkedGroup(new StoneGroup((int)list.get(i)+18));
                if (friend[7])
                    findLinkedGroup(new StoneGroup((int)list.get(i)-18));
            }
        }
        private boolean overlapCheck(StoneGroup stoneGroup, ArrayList stoneGroupList) {
            for (int i = 0 ; i <stoneGroupList.size();i++){
                if (stoneGroup.equals(stoneGroupList.get(i))){
                    return true;
                }
            }
            return false;
        }
        public ArrayList getLinkedWayOutList() {
            return linkedWayOutList;
        }

        public void setLinkedWayOutList() {//끝난 거 맞겠지..
            int count = getTotalNumberOfStonegroup();
            ArrayList tmp = new ArrayList();
            ArrayList wayOutList;
            while (count > 1){//1이하면 돌릴 필요가 없음. 그 자체로 전부 연결됐거나 없다는 뜻이니까.
                wayOutList = stoneGroupList.get(count-1).getWayOutList();
                for (int i = 0 ; i < wayOutList.size(); i++){
                    if (StaticLifeAndDeathAnalysisProcess.this.overlapCheck((int)wayOutList.get(i), tmp)){
                        linkedWayOutList.add((int)wayOutList.get(i));
                    } else {
                        tmp.add(wayOutList.get(i));
                    }
                }
                count --;
            }
        }
        public int getTotalNumberOfStonegroup() {
            return stoneGroupList.size();
        }
        public ArrayList getStoneGroupList(){
            return stoneGroupList;
        }
    }*/
}
