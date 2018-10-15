import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl{
	private DecTreeNode root;
	//ordered list of attributes
	private List<String> mTrainAttributes; 
	//
	private ArrayList<ArrayList<Double>> mTrainDataSet;
	//Min number of instances per leaf.
	private int minLeafNumber = 10;
	private DataBinderComparator toCompare = new DataBinderComparator();

	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary this is void purposefully
	}

	/**
	 * Build a decision tree given a training set then prune it using a tuning set.
	 * 
	 * @param train: the training set
	 * @param tune: the tuning set
	 */
	DecisionTreeImpl(ArrayList<ArrayList<Double>> trainDataSet, ArrayList<String> trainAttributeNames, int minLeafNumber) {
		this.mTrainAttributes = trainAttributeNames;
		this.mTrainDataSet = trainDataSet;
		this.minLeafNumber = minLeafNumber;
		this.root = buildTree(this.mTrainDataSet);
	}
	
	private DecTreeNode buildTree(ArrayList<ArrayList<Double>> dataSet){
//		return new DecTreeNode(getMajority(mTrainDataSet), "", 0.0);
//	}
		if(dataSet.isEmpty()){
			return new DecTreeNode(getMajority(mTrainDataSet), "", 0.0);
		}
		else if(sameClass(dataSet)){
			return new DecTreeNode(dataSet.get(0).get(dataSet.get(0).size()-1).intValue() , "", 0.0);
		}
		else if(dataSet.size() <= minLeafNumber){
			return new DecTreeNode(getMajority(dataSet), "", 0.0);
		}
		
		Double[] attrInfo = bestAttribute2(dataSet);
		ArrayList<ArrayList<Double>> leftList = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> rightList = new ArrayList<ArrayList<Double>>();
		DecTreeNode tree = new DecTreeNode( -1, mTrainAttributes.get(attrInfo[1].intValue()), attrInfo[0]);
		
		for(int x = 0; x < dataSet.size();x++){
			if(dataSet.get(x).get(attrInfo[1].intValue()) <= attrInfo[0]){
					leftList.add(dataSet.get(x));
			}else{
				rightList.add(dataSet.get(x));
			}
		}
		tree.left =  buildTree(leftList);
		tree.right = buildTree(rightList);
		return tree;
	}
	
	public Double[] bestAttribute2(ArrayList<ArrayList<Double>> dataSet){
		Double[] info = new Double[2];
		info[0] = 0.0;
		info[1] = 0.0;
		Double infoGain = 0.0;
		for(int i = 0; i < mTrainAttributes.size(); i++){
			ArrayList<ArrayList<Double>> sortedSet = sortSet(dataSet, i);
			int classIndex = sortedSet.get(0).size() - 1;
			Double threshold = 0.0;
			for (int k = 0; k < sortedSet.size() - 1; k++) {
				if (!sortedSet.get(k).get(classIndex)
						.equals(sortedSet.get(k + 1).get(classIndex))) {
					threshold = ((sortedSet.get(k).get(i)) + (sortedSet.get(k + 1).get(i))) / 2.0;
					Double newGain = calculateEntropy(sortedSet, threshold, i);
					if(infoGain <= newGain){
						infoGain = newGain;
						info[0] = threshold;
						info[1] = (double)i;
					}
				}
			}
		}
		return info;
	}

	public ArrayList<Double> bestAttribute1(
			ArrayList<ArrayList<Double>> dataSet) {
		ArrayList<Double> bestSplitPointList = new ArrayList<Double>();
		
		for (int i = 0; i < mTrainAttributes.size(); i++) {
			ArrayList<ArrayList<Double>> sortedSet = sortSet(dataSet, i);
			Double threshold = 0.0;
			int classIndex = sortedSet.get(0).size() - 1;
			Double infoGain = 0.0;
			for (int k = 0; k < sortedSet.size() - 1; k++) {
				if (!sortedSet.get(k).get(classIndex).equals(sortedSet.get(k + 1).get(classIndex))) {
					threshold = ((sortedSet.get(k).get(i)) + (sortedSet.get(k + 1).get(i))) / 2.0;
					Double newGain = calculateEntropy(sortedSet,threshold, i);
					if (infoGain <= newGain) {
						infoGain = newGain;
					}
				}
			}
			bestSplitPointList.add(infoGain);
		}
		return bestSplitPointList;
	}

	public void rootInfoGain(ArrayList<ArrayList<Double>> dataSet, ArrayList<String> trainAttributeNames, int minLeafNumber) {
		this.mTrainAttributes = trainAttributeNames;
		this.mTrainDataSet = dataSet;
		this.minLeafNumber = minLeafNumber;
		ArrayList<Double> bestSplitPointList =  bestAttribute1(dataSet);
		for(int i = 0; i<bestSplitPointList.size(); i++){
			System.out.println(this.mTrainAttributes.get(i) + " " + String.format("%.6f", bestSplitPointList.get(i)));
		}	
	}
	
	/**
	 * Print the decision tree in the specified format
	 */
	public void print() {
		printTreeNode("", this.root);
	}

	/**
	 * Recursively prints the tree structure, left subtree first, then right subtree.
	 */
	public void printTreeNode(String prefixStr, DecTreeNode node) {
		String printStr = prefixStr + node.attribute;
			
		System.out.print(printStr + " <= " + String.format("%.6f", node.threshold));
		if(node.left.isLeaf()){
			System.out.println(": " + String.valueOf(node.left.classLabel));
		}else{
			System.out.println();
			printTreeNode(prefixStr + "|\t", node.left);
		}
		System.out.print(printStr + " > " + String.format("%.6f", node.threshold));
		if(node.right.isLeaf()){
			System.out.println(": " + String.valueOf(node.right.classLabel));
		}else{
			System.out.println();
			printTreeNode(prefixStr + "|\t", node.right);
		}
		
		
	}
	
	public double printAccuracy(int numEqual, int numTotal){
		double accuracy = numEqual/(double)numTotal;
		System.out.println(accuracy);
		return accuracy;
	}

	/**
	 * Private class to facilitate instance sorting by argument position since java doesn't like passing variables to comparators through
	 * nested variable scopes.
	 * */
	private class DataBinder{
		
		public ArrayList<Double> mData;
		public int i;
		public DataBinder(int i, ArrayList<Double> mData){
			this.mData = mData;
			this.i = i;
		}
		public Double getArgItem(){
			return mData.get(i);
		}
		public ArrayList<Double> getData(){
			return mData;
		}
	}

	private class DataBinderComparator implements Comparator<DataBinder> {
		public int compare(DataBinder first, DataBinder second) {
			if (first.getArgItem().compareTo(second.getArgItem())!=0) {
				return first.getArgItem().compareTo(second.getArgItem());
			} else {
				return (first.getData().get(first.getData().size() - 1)
						.compareTo(second.getData()
								.get(second.getData().size() - 1)));
			}
		}
	}

	public ArrayList<ArrayList<Double>> sortSet(ArrayList<ArrayList<Double>> dataSet, int i){
		ArrayList<DataBinder> sortable = new ArrayList<DataBinder>();
		if(dataSet.size() == 0)
			return dataSet;
		for(ArrayList<Double> input:dataSet){
			sortable.add(new DataBinder(i, input));
		}
		
		Collections.sort(sortable, toCompare);
		ArrayList<ArrayList<Double>> sortedSet = new ArrayList<ArrayList<Double>>();
		
		for(DataBinder extract : sortable)
			sortedSet.add(extract.getData());
	
		return sortedSet;
	}
	
	public int getMajority(ArrayList<ArrayList<Double>> dataSet){
		int pos = 0;
		int neg = 0;
		for(ArrayList<Double> x : dataSet){
			if(x.get(x.size()-1) == 1){
				pos++;
			}else{
				neg++;
			}
		}
		if(pos >= neg)
			return 1;
		else
			return 0;
	}
	
	public boolean sameClass(ArrayList<ArrayList<Double>> dataSet){
		Double first = dataSet.get(0).get(dataSet.get(0).size() - 1);
		for(int i = 1; i < dataSet.size(); i++){
			if(first.compareTo(dataSet.get(i).get(dataSet.get(0).size()- 1)) != 0){
				return false;
			}
		}
		return true;
	}
	public int classify(List<Double> instance) {
		DecTreeNode node = this.root;
		int attr = -1;
		while(!node.isLeaf()){
			for(int i = 0; i < mTrainAttributes.size(); i++){
				if(mTrainAttributes.get(i).equals(node.attribute)){
					attr = i;
					break;
				}
			}
			if(instance.get(attr) <= node.threshold){
				node = node.left;
			}else{
				node = node.right;
			}
		}		
		return node.classLabel;
	}

	public Double calculateEntropy(ArrayList<ArrayList<Double>> sortedSet,
			Double threshold, int attr) {
		int left = 0;
		int leftpos = 0;
		int right = 0;
		int rightpos = 0;
		int size = sortedSet.size();
		int classIndex = sortedSet.get(0).size() -1;
		for(int i = 0; i < sortedSet.size();i++){
			if(sortedSet.get(i).get(attr).compareTo(threshold) <= 0){
				if(sortedSet.get(i).get(classIndex).compareTo(1.0) == 0){
					leftpos++;
				}
					left++;
			}else{
				if(sortedSet.get(i).get(classIndex).compareTo(1.0) == 0){
					rightpos++;
				}
				right++;
			}
		}
		Double leftPercent =  (double)left / (double)size;
		Double leftEntro = leftPercent * entropyHelper((double)left, (double)leftpos);
		Double rightPercent = (double)right/ (double)size;
		Double rightEntro = rightPercent * entropyHelper((double)right, (double)rightpos);
		Double splitEntro = entropyHelper((double)size, (double)leftpos + (double)rightpos);
		return splitEntro - leftEntro - rightEntro;
	}

	public Double entropyHelper(Double setSize, Double positive){
		if(positive.compareTo(0.0) == 0 || setSize.equals(positive)){
			return 0.0;
		}
		return -(positive/setSize)*log2((positive/setSize)) - 
				((setSize - positive) /setSize)*log2(((setSize - positive)/setSize));
	}
	
	public Double log2(Double num){
		return Math.log(num)/Math.log(2.0);
	}

}
