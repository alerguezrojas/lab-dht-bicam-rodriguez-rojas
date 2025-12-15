package metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import factory_method.FactoryAcceptCandidate;
import local_search.acceptation_type.AcceptType;
import local_search.acceptation_type.AcceptableCandidate;
import local_search.candidate_type.CandidateType;
import local_search.complement.StrategyType;
import problem.definition.State;

public class MultiobjectiveStochasticHillClimbing extends AbstractHillClimbing {

	protected List<Float> listTrace = new ArrayList<Float>();
	
	public MultiobjectiveStochasticHillClimbing() {
		super();
		this.typeAcceptation = AcceptType.AcceptNotDominated;
		this.strategy = StrategyType.NORMAL;
		this.typeCandidate = CandidateType.NotDominatedCandidate;
		this.Generatortype = GeneratorType.MultiobjectiveStochasticHillClimbing;
		listTrace.add(weight);
	}

	@Override
	public void updateReference(State stateCandidate, Integer countIterationsCurrent) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ifacceptCandidate = new FactoryAcceptCandidate();
		AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);
		Boolean accept = candidate.acceptCandidate(stateReferenceHC, stateCandidate);
		if(accept.equals(true))
		  stateReferenceHC = stateCandidate.clone();
		getReferenceList();
	}
	
	@Override
	public List<State> getReferenceList() {
		listStateReference.add( stateReferenceHC.clone());
		return listStateReference;
	}

	@Override
	public float[] getTrace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getListCountBetterGender() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getListCountGender() {
		// TODO Auto-generated method stub
		return null;
	}
}
