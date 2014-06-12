package treeBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Variable {
	U("u", 100), V("v", 100), W("w", 100), X("x", 100), Y("y", 100), Z("z", 100), 
	TRUTH("┬", 50), FALSE("⊥", 50);

	private String value;
	private int weight;
	private static int truthProbability;

	private Variable(String value, int weight) {
		this.value = value;
		this.weight = weight;
	}

	public String getValue() {
		return value;
	}

	private int getWeight() {
		if (value.equals("┬") || value.equals("⊥"))
			return truthProbability;
		else
			return weight;
	}

	private final static List<Variable> VALUES =
			Collections.unmodifiableList(Arrays.asList(values()));

	private static int sumWeights() {
		int sum = 0;
		for(Variable value : VALUES)
			sum += value.getWeight();
		return sum;
	}

	public static Variable randomVariable(int probability)  {
		truthProbability = probability;
		
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
