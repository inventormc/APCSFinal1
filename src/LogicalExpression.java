import java.util.HashMap;

public interface LogicalExpression {
	
	/**
	 * @return whether or not sentence is valid
	 * valid: all possible assignments satisfy the sentence
	 */
	boolean valid();
	
	/**
	 * @return whether or not sentence is satisfiable
	 * satisfiable: at least one assignment that satisfies the sentence
	 */
	boolean satisfiable();
	
	/**
	 * @return whether or not sentence is contingent
	 * contingent: at least one assignment that satisfies the sentence, and one that falsifies it
	 */
	boolean contingent();
	
	/**
	 * Equivalent: all assignments that satisfy first sentence satisfy the second and vise versa
	 * @return ThreeValuedVariable (either true,false, or undetermined) for equivalent sentences
	 * @param le - logical sentence to compare sentence with
	 */
	ThreeValuedVariable equivalent(LogicalExpression le); 
	
	/**
	 * Entail: Assignments that satisfy first sentence also satisfy thes second
	 * @return ThreeValuedVariable (either true,false, or undetermined) for one sentence entailing the other
	 * @param le - logical sentence to compare sentence with
	 */
	ThreeValuedVariable entails(LogicalExpression le);
	
	/**
	 * @return number of variables in the sentence
	 */
	int getNumOfVariablesInSentence();
	
	/**
	 * @return returns all possible outcomes for a sentence
	 */
	boolean[] allPossibleOutcomes();
	
	/**
	 * @return result of evaluation of the sentence
	 * @param values - values to evaluate on the sentence
	 */
	boolean evaluate(HashMap<String,Integer>  values);
	
	/**
	 * @return returns 2d array with values that can satisfy the sentence
	 */
	HashMap<String,Integer>[]  getSatisfyingValues();
	
	/**
	 * @return returns variables of a sentence after shunting yard is applied
	 */
	String[] getVariablesInOrder();
	
	/**
	 * eliminates spaces from the sentence array
	 */
	void cleanUp();
	
	/**
	 * 
	 * @return the number of distinct variables in a sentence
	 */
	int getNumOfDistinctVariablesInSentence();
	
	/**
	 * 
	 * @return string[] with the unique variables of a sentence
	 */
	String[] getDistinctVariables();
}
