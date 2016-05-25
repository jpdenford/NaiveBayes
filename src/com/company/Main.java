package com.company;

public class Main {

    public static void main(String[] args) {

        NaiveBayesClassifier c = NaiveBayesClassifier.classifierFromFile ("part1/spamLabelled.dat");
        c.classifyUnknown("part1/spamUnlabelled.dat");

    }
}
