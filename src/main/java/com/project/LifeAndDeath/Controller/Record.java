package com.project.LifeAndDeath.Controller;

import java.util.ArrayList;

import com.project.LifeAndDeath.Model.VO;

public class Record {

    public ArrayList<VO> record = new ArrayList();//VO{int x, int y, int color, int moveCount}///// VO:Quiz:Record = 1:n:1 모델

    public ArrayList getRecord() {
        return record;
    }

    public void setRecord(ArrayList record) {
        this.record = record;
    }

    public VO getLastMove(){
        return record.get(record.size()-1);
    }
}
