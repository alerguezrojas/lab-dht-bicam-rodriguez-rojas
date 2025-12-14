package problem.definition;



import java.util.ArrayList;

import metaheuristics.generators.GeneratorType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class State implements Cloneable {
	
	protected GeneratorType typeGenerator;
	protected ArrayList<Double> evaluation;
	protected int number;
	protected ArrayList<Object> code;
	
	public State(State ps) {
		typeGenerator = ps.getTypeGenerator();
		evaluation = ps.getEvaluation() == null ? null : new ArrayList<Double>(ps.getEvaluation());
		number = ps.getNumber();
		code = ps.getCode() == null ? null : new ArrayList<Object>(ps.getCode());
	}
	
	public State(ArrayList<Object> code) {
		super();
		this.code = code;
	}
	
	public State() {
		code=new ArrayList<Object>();
	}	
	
	@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Performance and shared state requirement")
	public ArrayList<Object> getCode() {
		return code;
	}

	@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Performance and shared state requirement")
	public void setCode(ArrayList<Object> listCode) {
		this.code = listCode;
	}

	public GeneratorType getTypeGenerator() {
		return typeGenerator;
	}
	public void setTypeGenerator(GeneratorType typeGenerator) {
		this.typeGenerator = typeGenerator;
	}

	
	@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Performance and shared state requirement")
	public ArrayList<Double> getEvaluation() {
		return evaluation;
	}

	@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Performance and shared state requirement")
	public void setEvaluation(ArrayList<Double> evaluation) {
		this.evaluation = evaluation;
	}

	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	
	@Override
	public State clone() {
		try {
			State cloned = (State) super.clone();
			cloned.typeGenerator = this.typeGenerator;
			cloned.evaluation = this.evaluation != null ? new ArrayList<Double>(this.evaluation) : null;
			cloned.number = this.number;
			cloned.code = this.code != null ? new ArrayList<Object>(this.code) : null;
			return cloned;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
	
	public Object getCopy(){
		return new State(this);
	}
	
	public boolean comparator(State state){

		boolean result=false;
		if(state.getCode().equals(getCode())){
			result=true;
		}
		return result;
	}
	public double distance(State state){
		double distancia = 0;
		for (int i = 0; i < state.getCode().size(); i++) {
			if (!(state.getCode().get(i).equals(this.getCode().get(i)))) {
				distancia++;
			}
		}
	return distancia;
	}
}
