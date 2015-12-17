
public class ThreeValuedVariable {
	private static String [] threeValues = {"True","False","Undetermined"};//the three possible values for objects
	private String value;//the value of the object
	
	/**
	 * 
	 * @param value-integer that determines value of object
	 * 1 is true, 0 is undetermined, -1 is false
	 */
	public ThreeValuedVariable(int value){
		if(value == 1){
			setValue(threeValues[0]);
		}else if(value == 0){
			setValue(threeValues[2]);
		}else if(value == -1){
			setValue(threeValues[1]);
		}
	}
	
	/**
	 * 
	 * @return return the value of this object which is a string
	 */
	public String getValue(){
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
