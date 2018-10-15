/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 * 
 */

import java.util.*;


public class NNImpl{
	public ArrayList<Node> inputNodes=null;//list of the input layer nodes.
	public ArrayList<Node> hiddenNodes=null;//list of the hidden layer nodes
	public ArrayList<Node> outputNodes=null;// list of the output layer nodes
	
	public ArrayList<Instance> trainingSet=null;//the training set
	
	Double learningRate=1.0; // variable to store the learning rate
	int maxEpoch=1; // variable to store the maximum number of epochs
	
	/**
 	* This constructor creates the nodes necessary for the neural network
 	* Also connects the nodes of different layers
 	* After calling the constructor the last node of both inputNodes and  
 	* hiddenNodes will be bias nodes. 
 	*/
	
	public NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch, Double [][]hiddenWeights, Double[][] outputWeights)
	{
		this.trainingSet=trainingSet;
		this.learningRate=learningRate;
		this.maxEpoch=maxEpoch;
		
		//input layer nodes
		inputNodes=new ArrayList<Node>();
		int inputNodeCount=trainingSet.get(0).attributes.size();
		int outputNodeCount=trainingSet.get(0).classValues.size();
		for(int i=0;i<inputNodeCount;i++)
		{
			Node node=new Node(0);
			inputNodes.add(node);
		}
		
		//bias node from input layer to hidden
		Node biasToHidden=new Node(1);
		inputNodes.add(biasToHidden);
		
		//hidden layer nodes
		hiddenNodes=new ArrayList<Node> ();
		for(int i=0;i<hiddenNodeCount;i++)
		{
			Node node=new Node(2);
			//Connecting hidden layer nodes with input layer nodes
			for(int j=0;j<inputNodes.size();j++)
			{
				NodeWeightPair nwp=new NodeWeightPair(inputNodes.get(j),hiddenWeights[i][j]);
				node.parents.add(nwp);
			}
			hiddenNodes.add(node);
		}
		
		//bias node from hidden layer to output
		Node biasToOutput=new Node(3);
		hiddenNodes.add(biasToOutput);
			
		//Output node layer
		outputNodes=new ArrayList<Node> ();
		for(int i=0;i<outputNodeCount;i++)
		{
			Node node=new Node(4);
			//Connecting output layer nodes with hidden layer nodes
			for(int j=0;j<hiddenNodes.size();j++)
			{
				NodeWeightPair nwp=new NodeWeightPair(hiddenNodes.get(j), outputWeights[i][j]);
				node.parents.add(nwp);
			}	
			outputNodes.add(node);
		}	
	}
	
	/**
	 * Get the output from the neural network for a single instance
	 * Return the idx with highest output values. For example if the outputs
	 * of the outputNodes are [0.1, 0.5, 0.2, 0.1, 0.1], it should return 1. If outputs
	 * of the outputNodes are [0.1, 0.5, 0.1, 0.5, 0.2], it should return 3. 
	 * The parameter is a single instance. 
	 */
	
	public int calculateOutputForInstance(Instance inst)
	{
		for(int index = 0;index < inst.attributes.size();index++){
			inputNodes.get(index).setInput(inst.attributes.get(index));
		}
		for(int index = 0;index < hiddenNodes.size();index++){
			hiddenNodes.get(index).calculateOutput();		
		}
		for(int index = 0;index < outputNodes.size();index++){
			outputNodes.get(index).calculateOutput();		
		}
		int output = 0;
		Double temp = outputNodes.get(0).getOutput();
		for(int i = 1;i < outputNodes.size(); i++){
			if(temp.compareTo(outputNodes.get(i).getOutput()) <= 0){
				output =  i;
				temp = outputNodes.get(i).getOutput();
			}
		}
		return output;
		
	}
	

	
	
	
	/**
	 * Train the neural networks with the given parameters
	 * 
	 * The parameters are stored as attributes of this class
	 */
	
	public void train()
	{
		
		int currEpoch = 0;
		do{
			currEpoch++;
			for(Instance inst : trainingSet){
				for(int index = 0;index < inst.attributes.size();index++){
					inputNodes.get(index).setInput(inst.attributes.get(index));
				}
				for(int index = 0;index < hiddenNodes.size();index++){
					hiddenNodes.get(index).calculateOutput();		
				}
				for(int index = 0;index < outputNodes.size();index++){
					outputNodes.get(index).calculateOutput();		
				}
				ArrayList<Double> deltaErr = new ArrayList<Double>();
				ArrayList<Double> deltaJ = new ArrayList<Double>();
				for(int index = 0;index < outputNodes.size();index++){
					Double out = outputNodes.get(index).getOutput();
					Double gPrime = out - out*out;
					deltaErr.add(gPrime*(inst.classValues.get(index)-out));
				}
				for(int index = 0;index < hiddenNodes.size();index++){
					Double sum = 0.0;
					for(int innerIndex = 0; innerIndex < outputNodes.size(); innerIndex++){
						sum += outputNodes.get(innerIndex).parents.get(index).weight*deltaErr.get(innerIndex);
					}
					Double out = hiddenNodes.get(index).getOutput();
					Double gPrime = out - out*out;
					deltaJ.add(gPrime*sum);
				}
				// Update weights between output and hidden layers
				for(int index = 0; index < outputNodes.size(); index++){
					for(int innerIndex = 0 ; innerIndex < hiddenNodes.size(); innerIndex++){
					outputNodes.get(index).parents.get(innerIndex).weight 
						+= learningRate*hiddenNodes.get(innerIndex).getOutput()
						*deltaErr.get(index);
					}
				}
				// Update weights between the hidden and input layers
				for(int index = 0; index < hiddenNodes.size() - 1; index++){
					for(int innerIndex = 0 ; innerIndex < inputNodes.size(); innerIndex++){
					hiddenNodes.get(index).parents.get(innerIndex).weight 
						+= learningRate*inputNodes.get(innerIndex).getOutput()
						*deltaJ.get(index);
					}
				}				
			}
			
		}while(currEpoch < maxEpoch);		
		
		
	}
	
	
	
	
	
}
