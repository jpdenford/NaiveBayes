package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by denforjohn on 16/05/16.
 */
public class Classifier {

    int[][][] frequencies;
    double[][][] probablitiies;

    private Classifier(String filename) throws IOException {
        ArrayList<Instance> instances = null;
        File file = new File(filename);
        if(file.exists()){
            instances = createInstances(file);
        }
        else throw new IllegalArgumentException("File doesn't exist");

        frequencies = createFrequencyTable(instances);
        probablitiies = createProbablityTable(frequencies);
    }

    private int[][][] createFrequencyTable(ArrayList<Instance> instances){
        int attributeCount = instances.get(0).getValues().length;
        int[][][] frequencies = new int[2][attributeCount][2];

        for (Instance i : instances) {
            boolean[] vals = i.getValues();
            for (int j = 0; j < vals.length; j++) {
                frequencies[toInt(i.getClassLabel())][j][toInt(vals[j])] += 1;
            }
        }

//        for (int i = 0; i < frequencies.length; i++) {
//            for (int j = 0; j < frequencies[i].length; j++) {
//                for (int k = 0; k < frequencies[i][j].length; k++) {
//                    frequencies[i][j][k] += 1;
//                    //System.out.print(frequencies[i][j][k]);
//                }
//            }
//            System.out.println("\n");
//        }

        return frequencies;
    }

    private double[][][] createProbablityTable(int[][][] frequencies){
        double[][][] probabilities = new double[frequencies.length][frequencies[0].length][frequencies[0][0].length];
        //outcome true / false
        for (int i = 0; i < frequencies.length; i++) {
            //attribute
            for (int j = 0; j < frequencies[i].length; j++) {
                //attribute true / false
                for (int k = 0; k < frequencies[i][j].length; k++) {
                    frequencies[i][j][k] += 1;
                }
            }
            System.out.println("\n");
        }

        return probabilities;
    }

    private int toInt(Boolean b){
        return b? 1 : 0;
    }

    private ArrayList<Instance> createInstances(File fin) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fin));

        ArrayList<Instance> instances = new ArrayList();
        String line = null;
        while ((line = br.readLine()) != null) {
            instances.add(new Instance(line));
            System.out.println(instances.get(instances.size()-1).toString());
        }
        br.close();
        return instances;
    }

    public static Classifier classifierFromFile(String filename){
        Classifier c = null;
        try{
            c = new Classifier(filename);
        }catch (IOException e) {
            System.err.println("Couldnt read file: " + filename);
        }
        return c;
    }

    private class Instance{
        private boolean [] values;
        private boolean classLabel;

        public Instance(String row){
            if(row == null)
                throw new IllegalArgumentException("Row shouldn't be null");

            String [] lineVals = row.trim().split("\\s+");

            //Should at least have an attribute and a label
            if(lineVals.length < 2)
                throw new IllegalArgumentException();

            boolean [] values = new boolean[lineVals.length-1];

            for (int i = 0; i < values.length; i++) {
                values[i] = toBool(lineVals[i]);
            }

            this.classLabel = toBool(lineVals[lineVals.length-1]);
            this.values = values;
        }

        //"0" (trimmed) to true else false
        private boolean toBool(String val){
            if(val == null) return false;
            return !val.trim().equals("0");
        }


        public boolean[] getValues() {
            return values;
        }

        public void setValues(boolean[] values) {
            this.values = values;
        }

        public Boolean getClassLabel() {
            return classLabel;
        }

        public void setClassLabel(Boolean classLabel) {
            this.classLabel = classLabel;
        }

        @Override
        public String toString() {
            return "Instance{" +
                    "values=" + Arrays.toString(values) +
                    ", classLabel=" + classLabel +
                    '}';
        }
    }

}
