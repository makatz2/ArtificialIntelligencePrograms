/***************************************************************************************
  CS540 - Section 2
  Homework Assignment 2: Game Playing

  TakeStones.java
  This is the main class that implements functions for Take Stones playing!
  ---------
  	*Free to modify anything in this file, except the class name 
  	You are required:
  		- To keep the class name as TakeStones for testing
  		- Not to import any external libraries
  		- Not to include any packages 
	*Notice: To use this file, you should implement 4 methods below.

	@author: TA 
	@date: Feb 2017
*****************************************************************************************/

import java.util.ArrayList;


public class TakeStones {

	final int WIN_SCORE = 100;	// score of max winning game
	final int LOSE_SCORE = -100;// score of max losing game
	final int INFINITY = 1000;	// infinity constant

	/** 
    * Class constructor.
    */
	public TakeStones () {};


	/**
	* This method is used to generate a list of successors 
	* @param state This is the current game state
	* @return ArrayList<Integer> This is the list of state's successors
	*/
	public ArrayList<Integer> generate_successors(GameState state) {
		int lastMove = state.get_last_move();	// the last move
		int size = state.get_size();			// game size
		ArrayList<Integer> successors = new ArrayList<Integer>();	// list of successors
		// If this is the first move follow special rules for first move
		if(lastMove == -1){
			for(int x = 1; x < (double)size / 2;x += 2){
			successors.add(x);
			}
			return successors;
		// follow normal rules for picking successors.
		}else{
			// add all factors still available
			for(int x = 1;x < lastMove; x++){
				if(lastMove % x == 0 && state.get_stone(x)){
					successors.add(x);
				}
			}
			// add all multiples still available
			int temp = lastMove * 2;
			int mult = 2;
			while(temp <= size){
				if(state.get_stone(temp)){
					successors.add(temp);
				}
				mult++;
				temp = lastMove*mult;
			}
		return successors;
		}
	}


	/**
	* This method is used to evaluate a game state based on 
	* the given heuristic function 
	* @param state This is the current game state
	* @return int This is the static score of given state
	*/
	public int evaluate_state(GameState state) {
		// if stone 1 is still available, score is 0
		if (state.get_stone(1)) 
			return 0;
		int lastMove = state.get_last_move();
		ArrayList<Integer> successors = generate_successors(state);
		// If last move is 1 and number of succesors is even return -5
		// otherwise return 5
		if (1 == lastMove) {
			int successorCount = successors.size();
			if(successorCount % 2 == 0)
				return -5;
			else
				return 5;
		// if last move is a prime count the number of multiples. If even
		// return -7 otherwise return 7
		} else if (Helper.is_prime(lastMove)){
			int count = 0;
			for(Integer x:successors){
				if(x % lastMove == 0){
					count++;
				}
			}
			if(count % 2 == 0)
				return -7;
			else 
				return 7;
		// else get largest prime factor and count number of multiples. If even
		// return -6 otherwise return 6
		} else {
			int largestPrime = Helper.get_largest_prime_factor(lastMove);
			int count = 0;
			for(Integer x:successors){
				if(x % largestPrime == 0){
					count++;
				}
			}
			if(count % 2 == 0)
				return -6;
			else 
				return 6;
		}
	}


	/**
	* This method is used to get the best next move from the current state
	* @param state This is the current game state
	* @param depth Current depth of search
	* @param maxPlayer True if player is Max Player; Otherwise, false
	* @return int This is the number indicating chosen stone
	*/
	public int get_next_move(GameState state, int depth, boolean maxPlayer) {
		int move = -1;			// the best next move 
		int alpha = -INFINITY;	// initial value of alpha
		int beta = INFINITY;	// initial value of beta

		// Getting successors of the given state 
		ArrayList<Integer> successors = generate_successors(state);
		// Check if depth is 0 or it is terminal state 
		if (0 == depth || 0 == successors.size()) {
			state.log();
			Helper.log_alphabeta(alpha, beta);
			return move;
		}
		// call recursive alphabeta
		alphabeta(state, depth, alpha, beta, maxPlayer);
		return state.get_last_move();
	}

	

	/**
	* This method is used to implement alpha-beta pruning for both 2 players
	* @param state This is the current game state
	* @param depth Current depth of search
	* @param alpha Current Alpha value
	* @param beta Current Beta value
	* @param maxPlayer True if player is Max Player; Otherwise, false
	* @return int This is the number indicating score of the best next move
	*/
	public int alphabeta(GameState state, int depth, int alpha, int beta,
			boolean maxPlayer) {
		ArrayList<Integer> successors = generate_successors(state);
		int v;
		int lastMove = state.get_last_move();
		int nextMove = state.get_size();
		if (successors.isEmpty()) {
			if(maxPlayer)
				v = -100;
			else
				v = 100;
		}else if (depth == 0) {
			 v = evaluate_state(state);
		// max algorithm 
		}else if (maxPlayer) {
			v = -INFINITY; // score of the best next move
			for (Integer x : successors) {
				state.remove_stone(x);
				v = Math.max(v,alphabeta(state, depth - 1, alpha, beta, !maxPlayer));
				state.set_stone(x);
				state.set_last_move(lastMove);
				if (v >= beta)
					break;
				if(alpha == v && nextMove >= x){
					// store nextMove to return to get_next_move
					nextMove = x;
				}
				else if(v > alpha){
					alpha = v;
					// store nextMove to return to get_next_move
					nextMove = x;
				}
				
			}
		// min algorithm
		} else {
			v = INFINITY; // score of the best next move
			for (Integer x : successors) {
				state.remove_stone(x);
				v = Math.min(v,
						alphabeta(state, depth - 1, alpha, beta, !maxPlayer));
				state.set_stone(x);
				state.set_last_move(lastMove);
				if (v <= alpha)
					break;
				if(beta == v && nextMove >= x){
					// store nextMove to return to get_next_move
					nextMove = x;
				}
				else if(v < beta){
					beta = v;
					// store nextMove to return to get_next_move
					nextMove = x;
				}
			}
		}
		
		state.log();
		Helper.log_alphabeta(alpha, beta);
		state.set_last_move(nextMove);
		return v;
	}


	/**
	* This is the main method which makes use of addNum method.
	* @param args A sequence of integer numbers, including the number of stones,
	* the number of taken stones, a list of taken stone and search depth
	* @return Nothing.
	* @exception IOException On input error.
	* @see IOException
	*/
	public static void main (String[] args) {
		try {
			// Read input from command line
			int n = Integer.parseInt(args[0]);		// the number of stones
			int nTaken = Integer.parseInt(args[1]);	// the number of taken stones
			
			// Initialize the game state
			GameState state = new GameState(n);		// game state
			int stone;
			for (int i = 0; i < nTaken; i++) {
				stone = Integer.parseInt(args[i + 2]);
				state.remove_stone(stone);
			}

			int depth = Integer.parseInt(args[nTaken + 2]);	// search depth
			// Process for depth being 0
			if (0 == depth)
				depth = n + 1;

			TakeStones player = new TakeStones();	// TakeStones Object
			boolean maxPlayer = (0 == (nTaken % 2));// Detect current player

			// Get next move
			int move = player.get_next_move(state, depth, maxPlayer);	
			// Remove the chosen stone out of the board
			state.remove_stone(move);

			// Print Solution 
			System.out.println("NEXT MOVE");
			state.log();

		} catch (Exception e) {
			System.out.println("Invalid input");
		}
	}
}