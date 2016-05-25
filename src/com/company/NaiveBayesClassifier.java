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

    /*Naive Bayes classifier for boolean data*/
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

    /*Classify Unknown Instances From a file*/
    public void classifyUnknown(String filename) {
        ArrayList<Instance> instances;
        File file = new File(filename);
        try{
            instances = createInstances(file,false);
        } catch (IOException e){
            System.out.println("An error occured while reading file");
            return;
        }
        System.out.println("Number of instances: " + instances.size());
        
        for (Instance i : instances) {
            classifyInstance(i);
        }
    }

    /*Classify an unknown instance based on the existing classifier*/
    public void classifyInstance(Instance unknown){
        double probabilityFalse = classProbabilities[0];
        double probabilityTrue = classProbabilities[1];


        //calculate probablity of false or true
        for (int i = 0; i < probablities[0].length; i++) {
            boolean instanceAttr = unknown.getValues()[i];
            probabilityFalse *= probablities[0][i][toInt(instanceAttr)];
            probabilityTrue *= probablities[1][i][toInt(instanceAttr)];
        }
        System.out.println("\n"+unknown.toString());
        System.out.println("P(!S|D): " + probabilityFalse + "\tP(S|D): "+probabilityTrue);
        boolean finalVal =  probabilityFalse < probabilityTrue;
        System.out.println("Predicted class: " + (finalVal? "TRUE" : "FALSE"));
        unknown.setClassLabel(finalVal);
    }

    /*Given a list of instances, construct the frequency table
    needed for the probability table*/
    private int[][][] createFrequencyTable(ArrayList<Instance> instances){
        int attributeCount = instances.get(0).getValues().length;
        int[][][] frequencies = new int[2][attributeCount][2];

        for (Instance i : instances) {
            boolean[] vals = i.getValues();
            for (int j = 0; j < vals.length; j++) {
                frequencies[toInt(i.getClassLabel())][j][toInt(vals[j])] += 1;
            }
        }
        System.out.println("Number of instances: " + instances.size());
        return frequencies;
    }

    /*Construct the probability table which will be used to classify unknown instances*/
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
            }
        }
        return probabilities;
    }

    private int toInt(Boolean b){
        return b? 1 : 0;
    }

    /*Given a file of instances, construct classifiers
    * File lines should be in the following form
    * where the values represent boolean features and the
    * righmost value is the classification if known (pass in flag accordingly)
    *   1   0   0   1   1
    *   0   1   1   1   0
    * */
    private ArrayList<Instance> createInstances(File fin, boolean isClassified) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fin));

        String classified = (isClassified)?" classified":" unclassified";
        System.out.println("\nReading" + classified + " Instances.");

        ArrayList<Instance> instances = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            instances.add(new Instance(line,isClassified));
            System.out.println(instances.get(instances.size()-1).toString());
        }
        br.close();
        return instances;
    }

    /*Returns a classifier if the file can be read correctly else null*/
    public static NaiveBayesClassifier classifierFromFile(String filename){
        NaiveBayesClassifier c = null;
        try{
            c = new NaiveBayesClassifier(filename);
        }catch (IOException e) {
            System.err.println("Couldnt read file: " + filename);
        }
        return c;
    }

    /*Represents a line in the file*/
    public class Instance {
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

        //"0" (trimmed) to false else true
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
            String label = ((classLabel == null)? "unclassified" : classLabel.toString());
            return "Instance{" +
                    "values= " + Arrays.toString(values) +
                    ", classLabel= " + label +
                    '}';
        }
    }

}
