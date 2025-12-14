package problem.definition;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import problem.extension.SolutionMethod;
import problem.extension.TypeSolutionMethod;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


import factory_interface.IFFactorySolutionMethod;
import factory_method.FactorySolutionMethod;


public class Problem {

	public enum ProblemType {Maximizar,Minimizar;}

	private ArrayList<ObjetiveFunction> function;
	private State state;
	private ProblemType typeProblem;
	private Codification codification;
	private Operator operator;
	private int possibleValue;
	private TypeSolutionMethod typeSolutionMethod;
	private IFFactorySolutionMethod factorySolutionMethod;
	
	public Problem() {
		super();
	}

	@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Performance and shared state requirement")
	public ArrayList<ObjetiveFunction> getFunction() {
		return function;
	}

	@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Performance and shared state requirement")
	public void setFunction(ArrayList<ObjetiveFunction> function) {
		this.function = function;
	}

	@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Performance and shared state requirement")
	public State getState() {
		return state;
	}

	@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Performance and shared state requirement")
	public void setState(State state) {
		this.state = state;
	}

	public ProblemType getTypeProblem() {
		return typeProblem;
	}
	public void setTypeProblem(ProblemType typeProblem) {
		this.typeProblem = typeProblem;
	}

	public Codification getCodification() {
		return codification;
	}
	public void setCodification(Codification codification) {
		this.codification = codification;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public int getPossibleValue() {
		return possibleValue;
	}

	public void setPossibleValue(int possibleValue) {
		this.possibleValue = possibleValue;
	}

	public void evaluate(State state) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		double eval = 0;       
		ArrayList<Double> evaluation = new ArrayList<Double>(this.function.size());
		if (typeSolutionMethod == null) {
			eval= function.get(0).evaluation(state);
			evaluation.add(evaluation.size(), eval);
			state.setEvaluation(evaluation);
		}
		else {
			SolutionMethod method = newSolutionMethod(typeSolutionMethod);
			method.evaluationState(state);
		}
	}
	
	public TypeSolutionMethod getTypeSolutionMethod() {
		return typeSolutionMethod;
	}
	public void setTypeSolutionMethod(TypeSolutionMethod typeSolutionMethod) {
		this.typeSolutionMethod = typeSolutionMethod;
	}
	public IFFactorySolutionMethod getFactorySolutionMethod() {
		return factorySolutionMethod;
	}
	public void setFactorySolutionMethod(
			IFFactorySolutionMethod factorySolutionMethod) {
		this.factorySolutionMethod = factorySolutionMethod;
	}
	
	public SolutionMethod newSolutionMethod(TypeSolutionMethod typeSolutionMethod) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (factorySolutionMethod == null) {
			factorySolutionMethod = new FactorySolutionMethod();
		}
		SolutionMethod solutionMethod = factorySolutionMethod.createdSolutionMethod(typeSolutionMethod);
		return solutionMethod;
	}
}

	
	

