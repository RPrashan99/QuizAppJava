package com.example.quizserver;

import java.util.Comparator;

public class QuizScoreComparator implements Comparator<Marks> {

    @Override
    public int compare(Marks o1, Marks o2) {
        return Integer.compare(o1.getMarks(), o2.getMarks());
    }
}
