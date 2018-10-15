/***************************************************************************************
  CS540 - Section 2
  Homework Assignment 5: Naive Bayes

  NBClassifierImpl.java
  This is the main class that implements functions for Naive Bayes Algorithm!
  ---------
  	*Free to modify anything in this file, except the class name 
  	You are required:
  		- To keep the class name as NBClassifierImpl for testing
  		- Not to import any external libraries
  		- Not to include any packages 
	*Notice: To use this file, you should implement 2 methods below.

	@author: TA 
	@date: April 2017
*****************************************************************************************/

import java.util.ArrayList;
import java.util.List;


public class NBClassifierImpl implements NBClassifier {
	
	private int nFeatures; 		// The number of features including the class 
	private int[] featureSize;	// Size of each features
	private List<List<Double[]>> logPosProbs;	// parameters of Naive Bayes
	
	/**
     * Constructs a new classifier without any trained knowledge.
     */
	public NBClassifierImpl() {

	}

	/**
	 * Construct a new classifier 
	 * 
	 * @param int[] sizes of all attributes
	 */
	public NBClassifierImpl(int[] features) {
		this.nFeatures = features.length;
		
		// initialize feature size
		this.featureSize = features.clone();

		this.logPosProbs = new ArrayList<List<Double[]>>(this.nFeatures);
	}


	/**
	 * Read training data and learn parameters
	 * 
	 * @param int[][] training data
	 */
	@Override
	public void fit(int[][] data) {
		for(int i = 0; i < nFeatures - 1 ;i++){
			ArrayList<Double[]> featureList = new ArrayList<Double[]>();
			for(int j = 0; j < featureSize[i] ; j++){
				int sumBothN = 1;
				int sumBothP = 1;
				int sumPos = 0;
				int sumNeg = 0;
				for(int x = 0;x < data.length ; x++){
					if(data[x][nFeatures -1] == 0){
						sumNeg++;
					}
					if(data[x][nFeatures -1] == 0 && data[x][i] == j){
							sumBothN++;
					}
					if(data[x][nFeatures -1] == 1){
						sumPos++;
					}
					if(data[x][i] == j && data[x][nFeatures -1] == 1){
							sumBothP++;
					}
				}
				Double [] temp = new Double[2];
				temp[0] = Math.log((double)sumBothN / (double)(sumNeg + featureSize[i]));
				temp[1] = Math.log((double)sumBothP / (double)(sumPos + featureSize[i]));
				featureList.add(temp);
			}
			logPosProbs.add(featureList);
		}
		int sumPos = 1;
		int sumNeg = 1;
		for(int i = 0; i < data.length; i++){
			if(data[i][nFeatures - 1] == 0){
				sumNeg++;
			}else{
				sumPos++;
			}
		}
		ArrayList<Double[]> featureList = new ArrayList<Double[]>();
		Double [] temp = new Double[2];
		temp[0] = Math.log((double)sumNeg / (double)(data.length + 2));
		temp[1] = Math.log((double)sumPos / (double)(data.length + 2));
		featureList.add(temp);
		logPosProbs.add(featureList);
		
	}	

	/**
	 * Classify new dataset
	 * 
	 * @param int[][] test data
	 * @return Label[] classified labels
	 */
	@Override
	public Label[] classify(int[][] instances) {
		
		int nrows = instances.length;
		Label[] yPred = new Label[nrows]; // predicted data
		for(int i = 0 ; i < nrows;  i++){
			double sumNeg = 0.0;
			double sumPos = 0.0;
			for(int j = 0 ; j < nFeatures - 1 ; j++){
				sumNeg += logPosProbs.get(j).get(instances[i][j])[0];
				sumPos += logPosProbs.get(j).get(instances[i][j])[1];
			}
			sumNeg += logPosProbs.get(nFeatures - 1).get(0)[0];
			sumPos += logPosProbs.get(nFeatures - 1).get(0)[1];
			if(sumNeg > sumPos){
				yPred[i] = Label.Negative;
			}else{
				yPred[i] = Label.Positive;
			}
		}

		return yPred;
	}
}