package local_search.complement;

import metaheuristics.generators.Generator;
import metaheuristics.generators.GeneratorType;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import factory_interface.IFFactoryAcceptCandidate;
import factory_method.FactoryAcceptCandidate;
import local_search.acceptation_type.AcceptType;
import local_search.acceptation_type.AcceptableCandidate;
import local_search.candidate_type.CandidateType;
import local_search.candidate_type.CandidateValue;
import metaheuristics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.State;


public class MultiCaseSimulatedAnnealing extends Generator {

	private CandidateValue candidatevalue;
	private AcceptType typeAcceptation;
	private StrategyType strategy;
	private CandidateType typeCandidate;
	private State stateReferenceSA;
    private IFFactoryAcceptCandidate ifacceptCandidate;
    private static Double alpha;
    private static Double tinitial;
    private static Double tfinal;
    private static int countIterationsT;
    private GeneratorType typeGenerator;
    private List<State> listStateReference = new ArrayList<>();
    private float weight;
	private List<Float> listTrace = new ArrayList<>();

    public int simpleTest() {
        return 1;
    }

    public static Double getAlpha() {
        return alpha;
    }

    public static void setAlpha(Double alpha) {
        MultiCaseSimulatedAnnealing.alpha = alpha;
    }

    public static Double getTinitial() {
        return tinitial;
    }

    public static void setTinitial(Double tinitial) {
        MultiCaseSimulatedAnnealing.tinitial = tinitial;
    }

    public static Double getTfinal() {
        return tfinal;
    }

    public static void setTfinal(Double tfinal) {
        MultiCaseSimulatedAnnealing.tfinal = tfinal;
    }

    public static int getCountIterationsT() {
        return countIterationsT;
    }

    public static void setCountIterationsT(int countIterationsT) {
        MultiCaseSimulatedAnnealing.countIterationsT = countIterationsT;
    }

    public GeneratorType getTypeGenerator() {
		return typeGenerator;
	}

	public void setTypeGenerator(GeneratorType typeGenerator) {
		this.typeGenerator = typeGenerator;
	}

	public MultiCaseSimulatedAnnealing(){
    	super();
    	this.typeAcceptation = AcceptType.AcceptMulticase;
		this.strategy = StrategyType.NORMAL;
		this.typeCandidate = CandidateType.RandomCandidate;
		this.candidatevalue = new CandidateValue();
		this.typeGenerator = GeneratorType.MultiCaseSimulatedAnnealing;
		this.weight = 50;
		listTrace.add(weight);
    }

	@Override
	public State generate(Integer operatornumber) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Problem problem = Strategy.getStrategy().getProblem();
		List<State> neighborhood = problem.getOperator().generatedNewState(stateReferenceSA, operatornumber);
	    return candidatevalue.stateCandidate(stateReferenceSA, typeCandidate, strategy, operatornumber, neighborhood);
	}

	@Override
	public State getReference() {
		return stateReferenceSA;
	}

	public void setStateRef(State stateRef) {
		this.stateReferenceSA = stateRef;
	}

	@Override
	public void setInitialReference(State stateInitialRef) {
		this.stateReferenceSA = stateInitialRef;
	}

	@Override
	public void updateReference(State stateCandidate, Integer countIterationsCurrent)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		int countRept = countIterationsT;
		ifacceptCandidate = new FactoryAcceptCandidate();
		AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);
		Boolean accept = candidate.acceptCandidate(stateReferenceSA, stateCandidate);
		if(accept.equals(true))
		  stateReferenceSA = stateCandidate.clone();
		if(countIterationsCurrent.equals(countIterationsT)){
			tinitial = tinitial * alpha;
			countIterationsT = countIterationsT + countRept;
		}
	}

	@Override
	public GeneratorType getType() {
		return this.typeGenerator;
	}

	@Override
	public List<State> getReferenceList() {
		listStateReference.add(stateReferenceSA.clone());
		return listStateReference;
	}

	@Override
	public List<State> getSonList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean awardUpdateREF(State stateCandidate) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public float getWeight() {
		return weight;
	}

	@Override
	public void setWeight(float weight) {
		this.weight = weight;
	}

	@Override
	public int[] getListCountBetterGender() {
		if (listCountBetterGender == null) {
			listCountBetterGender = new int[0];
		}
		return listCountBetterGender;
	}

	@Override
	public int[] getListCountGender() {
		return new int[0];
	}

	@Override
	public float[] getTrace() {
		float[] trace = new float[listTrace.size()];
		for (int i = 0; i < listTrace.size(); i++) {
			trace[i] = listTrace.get(i);
		}
		return trace;
	}
	
}
