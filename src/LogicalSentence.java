import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class LogicalSentence implements LogicalExpression {
	
	String[] sentence;//the logical sentence
	private static final String[] OPERATORS = { "~", "&", "|"};//operators
	private static final String[] ASSOCIATIVITIES = {"r","l","l"};//associativities of the operators (right or left)
	
	/**
	 * constructor that takes a string s, and puts sentence 
	 * in RPN form - inside an array
	 * @param s - a logical sentence as a string
	 */
	public LogicalSentence(String s) {
		sentence = s.split("");//split string into an array
		cleanUp();//take out the blank spaces 
		shuntingYard();//put sentence in reverse polish notation
	}
	
	/**
	 * 
	 * @return sentence that this LogicalExpression holds
	 */
	public String[] getSentence() {
		return sentence;
	}
	
	/**
	 * 
	 * @param sentence - sentence that this LogicalExpression object will hold
	 */
	public void setSentence(String[] sentence) {
		this.sentence = sentence;
	}
	
	/**
	 * @return whether or not sentence is valid
	 * valid: all possible assignments satisfy the sentence
	 */
	@Override
	public boolean valid() {
		boolean[] outcomes = allPossibleOutcomes();//gets all possible outcomes
		for(int i = 0;i < outcomes.length;i++){
			if(!outcomes[i]){
				return false;//if there is a value in the outcomes that is false, sentence is not valid
			}
		}
		return true;//if all values inside possible outcomes are true, the sentence is valid
	}
	
	/**
	 * @return whether or not sentence is satisfiable
	 * satisfiable: at least one assignment that satisfies the sentence
	 */
	@Override
	public boolean satisfiable() {
		boolean[] outcomes = allPossibleOutcomes();//gets all possible outcomes
		for(int i = 0;i < outcomes.length;i++){
			if(outcomes[i]){
				return true;//if there is at least on 'true' n the possible outcomes, the sentence is satisfiable
			}
		}
		return false;//if there are no true values in the outcome, sentence is not satisfiable
	}
	
	/**
	 * @return whether or not sentence is contingent
	 * contingent: at least one assignment that satisfies the sentence, and one that falsifies it
	 */
	@Override
	public boolean contingent() {
		if(!valid() && satisfiable()){//if the sentence is not valid but satisfiable, it is contingent
			return true;
		}else{//otherwise it is not contingent
			return false;
		}
	}
	
	/**
	 * @return returns a hashmap array with true/false values that can satisfy the sentence
	 */
	@Override
	public HashMap<String,Integer>[] getSatisfyingValues(){
		HashMap<String,Integer>[] allTruthAssignments = allTruthAssignments();//all possible truth assignments for the sentence
		boolean [] outcomes = allPossibleOutcomes();//all possible outcomes for the sentence
		int numOfTrue = 0;
		//get the number of outcomes that are true (of all the possible outcomes)
		for(int i = 0;i < outcomes.length;i++){
			if(outcomes[i]){
				numOfTrue++;
			}
		}
		HashMap<String,Integer>[] satisfyingValues = new HashMap[numOfTrue];//HashMap array to hold assignments that satisfy the sentence
		int index = 0;
		for(int i = 0;i < outcomes.length;i++){
			if(outcomes[i]){//if an assignment satisfies the sentence, store the truth assignment that made the sentence true
				satisfyingValues[index] = allTruthAssignments[i];
				index++;
			}
		}
		return satisfyingValues;//return the hashmap array with assignments that satisfy this sentence
	}

	/**
	 * @return returns the order of the variables in the sentence after shunting yard is applied
	 */
	@Override
	public String[] getVariablesInOrder(){
		String[] variablesInOrder = new String[getNumOfVariablesInSentence()];
		int index = 0;
		for(int i = 0;i < sentence.length;i++){
			if(isVariable(sentence[i])){//when the token is a variable, add it to the array
				variablesInOrder[index] = sentence[i];
				index++;
			}
		}
		return variablesInOrder;
	}
	
	/**
	 * 
	 * @return a string[] with distinct variables
	 */
	public String[] getDistinctVariables(){
		String[] variablesInOrder = getVariablesInOrder();//get variables with duplicates (e.g inside a&a|b)
		for(int i = 0;i < variablesInOrder.length;i++){
			for(int j = 0;j < variablesInOrder.length;j++){
				if(i != j){
					if(variablesInOrder[i].equals(variablesInOrder[j])){
						variablesInOrder[j] = " ";//if there is a duplicate, get rid of it by turning it into a space
					}
				}
			}
		}
		variablesInOrder = cleanUp(variablesInOrder);//get rid of the blank spaces
		return variablesInOrder;
	}
	/**
	 * Equivalent: all assignments that satisfy first sentence satisfy the second and vise versa
	 * @return ThreeValuedVariable (either true,false, or undetermined) for equivalent sentences
	 * @param le - logical sentence to compare sentence with
	 */
	@Override
	public ThreeValuedVariable equivalent(LogicalExpression le) {
		if(entails(le).getValue().equalsIgnoreCase("True") 
				&& le.entails(this).getValue().equalsIgnoreCase("True")){//if sentences entail one another, they are equivalent
			return new ThreeValuedVariable(1);//true
		}else if(entails(le).getValue().equalsIgnoreCase("Undetermined")  
				|| le.entails(this).getValue().equalsIgnoreCase("Undetermined")){//if entail is undetermined, equivalent cannot be determined either
			return new ThreeValuedVariable(0);//undetermined
		}else{//else means not equivalent
			return new ThreeValuedVariable(-1);//false
		}
	}
	
	/**
	 * Entail: Assignments that satisfy first sentence also satisfy the second
	 * @return ThreeValuedVariable (either true,false, or undetermined) for one sentence entailing the other
	 * @param le - logical sentence to compare sentence with
	 */
	@Override
	public ThreeValuedVariable entails(LogicalExpression le) {
		if(getNumOfDistinctVariablesInSentence() == le.getNumOfDistinctVariablesInSentence()){
			HashMap<String,Integer>[] satisfyingFirst = getSatisfyingValues();//assignments that satisfy the first
			
			boolean [] outcomesOfSecond = new boolean[satisfyingFirst.length];//array to store outcomes of testing on the second sentence
			for(int i = 0;i < satisfyingFirst.length;i++){
				outcomesOfSecond[i] = le.evaluate(satisfyingFirst[i]);//test assignment that satisfy the first sentence, on the second sentence
			}
			for(int i = 0;i < outcomesOfSecond.length;i++){
				if(!outcomesOfSecond[i]){//if there is an assignment that results in 'false', no entail
					return new ThreeValuedVariable(-1);//false
				}
			}
			return new ThreeValuedVariable(1);//if all outcomes of testing on second sentence are true, the first entails the second
		}else if(getNumOfDistinctVariablesInSentence() < le.getNumOfDistinctVariablesInSentence()){
			HashMap<String,Integer>[] satisfyingFirst = getSatisfyingValues();//assignments that satisfy the first
			
			int difference = le.getNumOfDistinctVariablesInSentence() - getNumOfDistinctVariablesInSentence();
			String[] temp = new String[difference];//array to store variables not in the first sentence
			String[] first = getDistinctVariables();//first variables
			String[] second = le.getDistinctVariables();//second varibilaes
			int index = 0;
			for(int i = 0;i < second.length;i++){
				if(!contains(first,second[i])){//if the first sentence doesn't contain the element, store it
					temp[index] = second[i];
					index++;
				}
			}
			String fakeSentence = "";
			index = 0;
			for(int i = 0;i < (temp.length * 2) - 1;i++){
				if(i % 2 == 0){
					fakeSentence += temp[index];
					index++;
				}else{
					fakeSentence += "&";
				}
			}//making a "fake sentence" using variables in the second sentence but not the first
			HashMap<String,Integer>[] secondPart = new LogicalSentence(fakeSentence).allTruthAssignments();//all possible truth values for second sentence
			HashMap<String,Integer>[] testOnSecond = new HashMap[satisfyingFirst.length * secondPart.length];//values to test on second sentence
			for(int i = 0;i < testOnSecond.length;i++){
				testOnSecond[i] = new HashMap<String,Integer>();
			}
			index = 0;
			for(int i = 0;i < satisfyingFirst.length;i++){
				for(int j = 0;j < secondPart.length;j++){
					testOnSecond[index].putAll(satisfyingFirst[i]);
					testOnSecond[index].putAll(secondPart[j]);//merge hashmap satisfyingfirst and secondpart, so we have values
															//to assign to all variables in the second sentence
					index++;
				}
			}
			boolean [] outcomesOfSecond = new boolean[testOnSecond.length];//array to store outcomes of testing on the second sentence
			for(int i = 0;i < testOnSecond.length;i++){
				outcomesOfSecond[i] = le.evaluate(testOnSecond[i]);//test values on second sentence
			}
			for(int i = 0;i < outcomesOfSecond.length;i++){
				if(!outcomesOfSecond[i]){
					return new ThreeValuedVariable(-1);//does not entail if there is any false outcome
				}
			}
			return new ThreeValuedVariable(1);//true
		}else if(getNumOfDistinctVariablesInSentence() > le.getNumOfDistinctVariablesInSentence()){
			HashMap<String,Integer>[] satisfyingFirst = getSatisfyingValues();//assignments that satisfy the first
			
			String[] first = getDistinctVariables();//first variables
			String[] second = le.getDistinctVariables();//second variables
			String[] temp = new String[second.length];//array to store variables in both sentences

			int index = 0;
			for(int i = 0;i < first.length;i++){
				if(contains(second,first[i])){//if the first sentence contains the element, store it
					temp[index] = first[i];
					index++;
				}
			}
			HashMap<String,Integer>[] testOnSecond = new HashMap[satisfyingFirst.length];//values to test on second sentence
			for(int i = 0;i < testOnSecond.length;i++){
				testOnSecond[i] = new HashMap<String,Integer>();
				for(int j = 0;j < temp.length;j++){
					testOnSecond[i].put(temp[j], satisfyingFirst[i].get(temp[j]));//take truth assignements from satisfyingfirst hashmap
																				//which satisfy the first sentence
				}
			}
			boolean [] outcomesOfSecond = new boolean[testOnSecond.length];//array to store outcomes of testing on the second sentence
			for(int i = 0;i < testOnSecond.length;i++){
				outcomesOfSecond[i] = le.evaluate(testOnSecond[i]);//test on second sentence
			}
			for(int i = 0;i < outcomesOfSecond.length;i++){
				if(!outcomesOfSecond[i]){//if there are any false outcomes, does not entail
					return new ThreeValuedVariable(-1);
				}
			}
			return new ThreeValuedVariable(1);//true
		}else{
			return new ThreeValuedVariable(0);//else, entails cannot be undetermined
		}
	}
	
	/**
	 * 
	 * @param array check this array for the item
	 * @param item item to check for
	 * @return if the array contains the item
	 * 
	 */
	public static boolean contains(String[] array,String item){
		for(int i = 0;i < array.length;i++){
			if(array[i].equals(item)){
				return true;
			}
		}
		return false;
	}
	/**
	 * puts sentence in Reverse Polish Notation for easy evaluation
	 */
	private void shuntingYard() {
		cleanUp();
		ArrayList<String> output = new ArrayList<>();//final output (sentence in reverse polish notation form)
		Stack<String> operatorStack = new Stack<>();//temporary stack for operators
		for (int i = 0; i < sentence.length; i++) {
			if (isVariable(sentence[i])) {
				output.add(sentence[i]);//if token is a variable,add it to the output
			} else if (sentence[i].equals("(")) {
				operatorStack.push(sentence[i]);//push left parenthesis to operator stack
			} else if (sentence[i].equals(")")) {
				while (!operatorStack.peek().equals("(")) {
					output.add(operatorStack.pop());//if token is ")" , pop tokens onto the output until we find left parenthesis
					if (operatorStack.isEmpty()) {
						System.out.println("Mismatched parenthesis");//if we don't find left parenthesis, mismatched
						System.exit(0);
					}
				}
				operatorStack.pop();//pop the left parenthesis away too
			} else {//for operators
				if (!operatorStack.isEmpty()) {
					while (!operatorStack.isEmpty()
							&& ((ASSOCIATIVITIES[getPrecedence(sentence[i])].equals("r") && getPrecedence(sentence[i]) > getPrecedence(operatorStack.peek())
									)||(ASSOCIATIVITIES[getPrecedence(sentence[i])].equals("l") && getPrecedence(sentence[i]) >= getPrecedence(operatorStack.peek())))
							&& !operatorStack.peek().equals("(")) {
							output.add(operatorStack.pop());
							//while there is an operator token, o2, at the top of the operator stack, and either
							//o1 is left-associative and its precedence is less than or equal to that of o2, or
							//o1 is right associative, and has precedence less than that of o2,
							//then pop o2 off the operator stack, onto the output queue;
					}
					operatorStack.push(sentence[i]);//once we're done popping, push the operator onto the stack
				} else {
					operatorStack.push(sentence[i]);//if the stack is empty, push 
				}

			}
		}
		while (!operatorStack.isEmpty()) {
			if (operatorStack.peek().equals("(") || operatorStack.peek().equals(")")) {
				System.out.println("Mismatched parenthesis");
				System.exit(0);
			} else {
				output.add(operatorStack.pop());//add elements remaining in operator stack to output
			}
		}
		sentence = new String[output.size()];
		for (int i = 0; i < output.size(); i++) {
			sentence[i] = output.get(i);//transfer new sentence into logicalsentence object
		}
	}
	
	/**
	 * 
	 * @param token - token to check if variable or not
	 * @return if token is a variable or not
	 */
	private static boolean isVariable(String token) {
		for (int i = 0; i < OPERATORS.length; i++) {
			if (token.equals(OPERATORS[i]) || token.equals("(") || token.equals(")")) {//if token is an operator or parenthesis, its not a variable
				return false;//compare token to operator array and also make sure its not a parenthesis
			}
		}
		return true;//otherwise, it is a variable
	}
	
	/**
	 * 
	 * @param operator - operator to get precedence of
	 * @return return the precedence of an operator (just the position of operator in the array)
	 * note: lower index means higher precedence
	 */
	private static int getPrecedence(String operator) {
		for (int i = 0; i < OPERATORS.length; i++) {
			if (operator.equals(OPERATORS[i])) {
				return i;
			}
		}
		return -1;// if operator isn't passed into this function,
					// an error in the program will occur with negative index
	}
	
	/**
	 * eliminates spaces from the sentence array
	 */
	@Override
	public void cleanUp() {
		ArrayList<String> temp = new ArrayList<>();
		for (int i = 0; i < sentence.length; i++) {
			if (!sentence[i].equals(" ")) {
				temp.add(sentence[i]);//get rid of spaces by adding elements as long as they're not " "
			}
		}
		sentence = new String[temp.size()];
		for (int i = 0; i < temp.size(); i++) {
			sentence[i] = temp.get(i);//transfer cleaned up elements back into sentence
		}
	}
	
	/**
	 * 
	 * @param array - string[] to clean up
	 * @return- cleaned up array
	 */
	public static String[] cleanUp(String[] array) {
		ArrayList<String> temp = new ArrayList<>();
		for (int i = 0; i < array.length; i++) {
			if (!array[i].equals(" ")) {
				temp.add(array[i]);//get rid of spaces by adding elements as long as they're not " "
			}
		}
		array = new String[temp.size()];
		for (int i = 0; i < temp.size(); i++) {
			array[i] = temp.get(i);//transfer cleaned up elements 
		}
		return array;//return cleaned up array
	} 
	
	/**
	 * @return number of variables in the sentence
	 */
	@Override
	public int getNumOfVariablesInSentence(){
		int numOfVariables = 0;
		for(int i = 0;i < sentence.length;i++){
			if(isVariable(sentence[i])){
				numOfVariables++;//count variables
			}
		}
		return numOfVariables;
	}
	
	/**
	 * @return number of distinct variables in a sentence
	 */
	public int getNumOfDistinctVariablesInSentence(){
		int numOfVariables = 0;
		ArrayList<String> values = new ArrayList<>();
		for(int i = 0;i < sentence.length;i++){
			if(isVariable(sentence[i])){
				if(!values.contains(sentence[i])){
					numOfVariables++;//count variable if the variable isn't already existing
				}
				values.add(sentence[i]);
			}
		}
		return numOfVariables;
	}
	
	/**
	 * 
	 * @return a HashMap array with all possible truth assignments for a sentence
	 * this is done by printing binary representation of the number of variables inside
	 * the sentence, and filling any empty spaces with 0's
	 */
	private HashMap<String,Integer>[] allTruthAssignments(){
		int numOfVariables = getNumOfDistinctVariablesInSentence();//get number of variables
		HashMap<String,Integer>[] allTruthAssignments = new HashMap[(int)Math.pow(2, numOfVariables)];//hashmap to hold all possible values
		String[] distinctVariables = getDistinctVariables();
		for(int i = 0;i < allTruthAssignments.length;i++){//numbers to have binary representations of
			String binary = Integer.toBinaryString(i);//get the binary representation of the number
			String[] temp = binary.split("");//split the number into an array (0 is false, 1 is true)
			int numOfPlaceholders = 0;
			if(temp.length < numOfVariables){
				numOfPlaceholders = numOfVariables - temp.length;//count the number of place holders needed 
																//e.g binary # may be 10, but we need 0010 so two placeholders
			}
			int [] values = new int[numOfVariables];
			for(int j = 0;j < numOfPlaceholders;j++){
				values[j] = 0;//put 0 in for placeholders
			}
			for(int j = numOfPlaceholders;j < numOfVariables;j++){
				values[j] = Integer.parseInt(temp[j - numOfPlaceholders]);//add the digits from the binary # in
			}
			allTruthAssignments[i] = new HashMap<String,Integer>();
			for(int j = 0;j < distinctVariables.length;j++){
				allTruthAssignments[i].put(distinctVariables[j], values[j]);//assign truefalse to variables
			}

		}
		return allTruthAssignments;
	}
	
	/**
	 * @return result of evaluation of the sentence
	 * @param values - values to evaluate on the sentence
	 */
	@Override
	public boolean evaluate(HashMap<String,Integer> values){
		boolean[] bool = {false,true};//to convert 0,1 to false,true
		String[] temp = Arrays.copyOf(sentence, sentence.length);//operate on copy of sentence so we don't mess up the sentence
		Stack<Integer> answerStack = new Stack<>();//answer stack that will help with evaluation
		for(int j = 0;j < temp.length;j++){
			if(isVariable(temp[j])){
				temp[j] = Integer.toString(values.get(temp[j]));//replace variables with true/false assignments
			}
		}
		for(int j = 0;j < temp.length;j++){
			if(isVariable(temp[j])){
				answerStack.push(Integer.parseInt(temp[j]));//if token is variable (true/false) then push it onto the stack
			}else{
				switch (temp[j]){
				case "~"://if ~ operator, pop a value from the answer stack, switch its value, and push it back onto the stack
					if(answerStack.peek() == 0){
						answerStack.pop();
						answerStack.push(1);
					}else{
						answerStack.pop();
						answerStack.push(0);
					}
					break;
				case "&"://if & operator, pop two values, evaluate the two values with &, and push result back onto the stack
					boolean first = bool[answerStack.pop()];
					boolean second = bool[answerStack.pop()];
					boolean answer = first && second;
					for(int k = 0;k < bool.length;k++){
						if(answer == bool[k]){
							answerStack.push(k);
						}
					}
					break;
				case "|"://if | operator, pop two values, evaluate the two values with |, and push the result back onto the stack
					boolean firstBool = bool[answerStack.pop()];
					boolean secondBool = bool[answerStack.pop()];
					boolean answerBool = firstBool || secondBool;
					for(int k = 0;k < bool.length;k++){
						if(answerBool == bool[k]){
							answerStack.push(k);
						}
					}
				}
			}
		}
		if(answerStack.size() == 1){
			return bool[answerStack.pop()];//size should be 1, and this 1 value is the answer to evaluating the sentence
		}else{
			System.out.println("User input invalid");
			return false;
		}
	}
	
	/**
	 * @return returns all possible outcomes for a sentence
	 */
	@Override
	public boolean[] allPossibleOutcomes() {
		int numOfVariables = getNumOfDistinctVariablesInSentence();//get number of variables
		boolean[] outcomes = new boolean[(int)Math.pow(2, numOfVariables)];//boolean array to hold all possible outcomes
		HashMap<String,Integer>[] allTruthAssignments = allTruthAssignments();//get all possible truth assignments
		for(int i = 0;i < (int)Math.pow(2, numOfVariables);i++){
			outcomes[i] = evaluate(allTruthAssignments[i]);//evaluate each of the possible truth assignments, and store the result
		}
		return outcomes;
	}

	public static void main(String args[]) {
		Scanner s = new Scanner(System.in);
		System.out.println("Enter first logical sentence: ");
		LogicalSentence ls = new LogicalSentence(s.nextLine());
		System.out.println("Enter second logical sentence: ");
		LogicalSentence ls2 = new LogicalSentence(s.nextLine());
		s.close();
		
		System.out.println("All possible outcomes of first sentence: ");
		System.out.println(Arrays.toString(ls.allPossibleOutcomes()));
		System.out.println("All possible outcomes of second sentence: ");
		System.out.println(Arrays.toString(ls2.allPossibleOutcomes()));
		
		System.out.println("------------------------");
		
		System.out.println("FIRST SENTENCE DATA: ");
		System.out.println("Valid: " + ls.valid());
		System.out.println("Satisfiable: " + ls.satisfiable());
		System.out.println("Contingent: " + ls.contingent());
		
		System.out.println("------------------------");
		
		System.out.println("SECOND SENTENCE DATA: ");
		System.out.println("Valid: " + ls2.valid());
		System.out.println("Satisfiable: " + ls2.satisfiable());
		System.out.println("Contingent: " + ls2.contingent());
		
		System.out.println("------------------------");
		
		System.out.println("COMBINED DATA: ");
		System.out.println("First entails second: " + ls.entails(ls2).getValue());
		System.out.println("Second entails first: " + ls2.entails(ls).getValue());
		System.out.println("Equivalent: " + ls.equivalent(ls2).getValue());
		
	}
}
