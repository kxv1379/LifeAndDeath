package com.project.LifeAndDeath.Model;

import java.util.HashMap;

public class Quiz {//문제 데이터 양식(Model). 편집모드에서 검토모드로 넘어갈 때, 문제를 SQLite에서 불러왔을 때, 쓰이게 됨.(undo 등의 기능 문제 + DB저장용)

    public HashMap hashMap;
    public String quizName;

    public HashMap getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap hashMap) {
        this.hashMap = hashMap;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }
}
