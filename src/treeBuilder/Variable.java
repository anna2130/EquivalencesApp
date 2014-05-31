package treeBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Variable {
	A("a", 10), B("b", 10), C("c", 10), D("d", 10), E("e", 10), F("f", 10), 
		G("g", 10), H("h", 10), I("i", 10), TRUTH("┬", 3), FALSE("⊥", 3);

	private String value;
	private int weight;

	private Variable(String value, int weight) {
		this.value = value;
		this.weight = weight;
	}
	
	public String getValue() {
		return value;
	}

	private int getWeight() {
		return weight;
	}
	
	private static final List<Variable> VALUES =
			Collections.unmodifiableList(Arrays.asList(values()));

	private static int sumWeights() {
		int sum = 0;
		for(Variable value : VALUES) 
		sum += value.getWeight();
		return sum;
	}

	public static Variable randomVariable()  {
		int size = sumWeights();
		Random rand = new Random();
		
		int randomNum = rand.nextInt(size);
		int currentWeightSum = 0;
		Variable val = null;
		
		for(Variable currentValue : VALUES) {
			val = currentValue;
			if (randomNum > currentWeightSum && 
					randomNum <= (currentWeightSum + currentValue.getWeight())) {
				break;
			}
			currentWeightSum += currentValue.getWeight();
		}
		return val;
	}
}
