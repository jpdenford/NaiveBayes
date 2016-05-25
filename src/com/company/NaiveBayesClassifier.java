package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by JP Denford on 16/05/16.
 */
public class NaiveBayesClassifier {

    private double[] classProbabilities;
    private double[][][] probablities;


    private NaiveBayesClassifier(String filename) throws IOException {
        ArrayList<Instance> instances;
        File file = new File(filename);
        instances = createInstances(file,true);

        int[][][] frequencies = createFrequencyTable(instances);
        probablities = createProbablityTable(frequencies);

        //set overall class probabilities
        int totalFalse = 0;
        for (Instance i : instances) {
            totalFalse += !i.getClassLabel()? 1 : 0;
        }
        classProbabilities = new double[2];

        classProbabilities[0] = (double) totalFalse / instances.size();
        classProbabilities[1] = 1.0 - classProbabilities[0];
    }

    public void classifyUnknown(String filename) {
        ArrayList<Instance> instances;
        File file = new File(filename);
        try{
            instances = createInstances(file,false);
        } catch (IOException e){
            System.out.println("An error occured while reading file");
            return;
        }

        for (Instance i : instances) {
            System.out.println("\n" + i.toString());
            classifyInstance(i);
        }
    }

    private void classifyInstance(Instance unknown){
        double probabilityFalse = classProbabilities[0];
        double probabilityTrue = classProbabilities[1];


        //calculate probablity of false or true
        for (int i = 0; i < probablities[0].length; i++) {
            boolean instanceAttr = unknown.getValues()[i];
            probabilityFalse *= probablities[0][i][toInt(instanceAttr)];
            probabilityTrue *= probablities[1][i][toInt(instanceAttr)];
        }

        System.out.println("probability\nfalse: \t" + probabilityFalse + " true:\t"+probabilityTrue);
        boolean finalVal =  probabilityFalse < probabilityTrue;
        System.out.println(finalVal? "TRUE" : "FALSE");
        unknown.setClassLabel(finalVal);
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
        System.out.println("num instances: " + instances.size());
        return frequencies;
    }

    private double[][][] createProbablityTable(int[][][] frequencies){
        double[][][] probabilities = new double[frequencies.length][frequencies[0].length][frequencies[0][0].length];
        //outcome true / false
        for (int i = 0; i < frequencies.length; i++) {
            //attribute
            for (int j = 0; j < frequencies[i].length; j++) {
                //attribute true / false
                int numAttrFalse = frequencies[i][j][0];
                int numAttrTrue = frequencies[i][j][1];
                double total = numAttrFalse + numAttrTrue;

                probabilities[i][j][0] = numAttrFalse / total;
                probabilities[i][j][1] = numAttrTrue / total;
                System.out.println("Attribte:\ttrue: " + probabilities[i][j][0] + "\t numFalse: " + probabilities[i][j][1]);
            }
        }
        return probabilities;
    }

    private int toInt(Boolean b){
        return b? 1 : 0;
    }

    private ArrayList<Instance> createInstances(File fin,boolean isClassified) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fin));

        ArrayList<Instance> instances = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            instances.add(new Instance(line,isClassified));
            System.out.println(instances.get(instances.size()-1).toString());
        }
        br.close();
        return instances;
    }

    public static NaiveBayesClassifier classifierFromFile(String filename){
        NaiveBayesClassifier c = null;
        try{
            c = new NaiveBayesClassifier(filename);
        }catch (IOException e) {
            System.err.println("Couldnt read file: " + filename);
        }
        return c;
    }

    private class Instance {
        private boolean [] values;
        private Boolean classLabel;

        public Instance(String row,boolean isClassified){
            if(row == null || row.length() == 0)
                throw new IllegalArgumentException("Row shouldn't be null");

            String [] lineVals = row.trim().split("\\s+");

            //Should at least have an attribute and a label
            if(lineVals.length < 2 && isClassified)
                throw new IllegalArgumentException();

            boolean [] values = new boolean[(isClassified)?lineVals.length-1 : lineVals.length];

            for (int i = 0; i < values.length; i++) {
                values[i] = toBool(lineVals[i]);
            }

            this.classLabel = (isClassified)?toBool(lineVals[lineVals.length-1]) : null;
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
