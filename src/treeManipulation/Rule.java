package treeManipulation;

public class Rule {

	private String rule;
	private TruthTable tt;
	
	public Rule(String rule, TruthTable tt) {
		this.rule = rule;
		this.tt = tt;
	}
	
	public String getRule() {
		return rule;
	}
	
	public TruthTable getTruthTable() {
		return tt;
	}
	
}
