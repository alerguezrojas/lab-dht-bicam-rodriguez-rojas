package evolutionary_algorithms.complement;

public class Probability {
    private Object key;
    private Object value;
	private float probability;
	
	public Probability() {}

	public Probability(Probability other) {
		this.key = other.key;
		this.value = other.value;
		this.probability = other.probability;
	}
	
	public float getProbability() {
		return probability;
	}
	public void setProbability(float probability) {
		this.probability = probability;
	}
	public Object getKey() {
		return key;
	}
	public void setKey(Object key) {
		this.key = key;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}
