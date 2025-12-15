package metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import factory_method.FactoryAcceptCandidate;
import local_search.acceptation_type.AcceptType;
import local_search.acceptation_type.AcceptableCandidate;
import local_search.candidate_type.CandidateType;
import local_search.complement.StrategyType;
import local_search.complement.TabuSolutions;
import metaheuristics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.State;

public class MultiobjectiveTabuSearch extends AbstractTabuSearch {

	private List<Float> listTrace = new ArrayList<Float>();

    public State getStateReferenceTS() {
		return stateReferenceTS;
	}

	public void setStateReferenceTS(State stateReferenceTS) {
		this.stateReferenceTS = stateReferenceTS;
	}

	public MultiobjectiveTabuSearch() {
    	super();
		this.typeAcceptation = AcceptType.AcceptNotDominatedTabu;
		this.strategy = StrategyType.TABU;
		@SuppressWarnings("unused")
		Problem problem = Strategy.getStrategy().getProblem();
		this.typeCandidate = CandidateType.RandomCandidate;
		this.typeGenerator = GeneratorType.MultiobjectiveTabuSearch;
		listTrace.add(weight);
	}

	@Override
	public void updateReference(State stateCandidate, Integer countIterationsCurrent)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ifacceptCandidate = new FactoryAcceptCandidate();
		AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);
		Boolean acept = candidate.acceptCandidate(stateReferenceTS, stateCandidate);
		if(acept.equals(true))
		  stateReferenceTS = stateCandidate;

		if (strategy.equals(StrategyType.TABU) && acept.equals(true)) {
			if (TabuSolutions.listTabu.size() < TabuSolutions.maxelements) {
				Boolean find = false;
				int count = 0;
				while (TabuSolutions.listTabu.size() > count && find.equals(false)) {
					if (TabuSolutions.listTabu.get(count).equals(stateCandidate)) {
						find = true;
					}
					count++;
				}
				if (find.equals(false)) {
					TabuSolutions.listTabu.add(stateCandidate);
				}
			} else {
				TabuSolutions.listTabu.remove(0);
				Boolean find = false;
				int count = 0;
				while (TabuSolutions.listTabu.size() > count && find.equals(false)) {
					if (TabuSolutions.listTabu.get(count).equals(stateCandidate)) {
						find = true;
					}
					count++;
				}
				if (find.equals(false)) {
					TabuSolutions.listTabu.add(stateCandidate);
				}
			}
	}
		getReferenceList();
  }
	
	@Override
	public List<State> getReferenceList() {
		listStateReference.add(stateReferenceTS);
		return listStateReference;
	}

	public void setTypeCandidate(CandidateType typeCandidate){
		this.typeCandidate = typeCandidate;
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

	@Override
	public float[] getTrace() {
		// TODO Auto-generated method stub
		return null;
	}

}


