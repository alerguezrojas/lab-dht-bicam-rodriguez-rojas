package metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import factory_method.FactoryAcceptCandidate;
import local_search.acceptation_type.AcceptType;
import local_search.acceptation_type.AcceptableCandidate;
import local_search.candidate_type.CandidateType;
import local_search.complement.StrategyType;
import metaheuristics.strategy.Strategy;
import problem.definition.State;

public class MultiobjectiveHillClimbingRestart extends AbstractHillClimbing {

	protected List<Float> listTrace = new ArrayList<Float>();
	private List<State> visitedState = new ArrayList<State>();
	public static int sizeNeighbors;


	public MultiobjectiveHillClimbingRestart() {
		super();
		this.typeAcceptation = AcceptType.AcceptNotDominated;
		this.strategy = StrategyType.NORMAL;
		this.typeCandidate = CandidateType.NotDominatedCandidate;
		this.Generatortype = GeneratorType.MultiobjectiveHillClimbingRestart;
		listTrace.add(weight);
	}

	@Override
	public void updateReference(State stateCandidate, Integer countIterationsCurrent) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		//Agregando la primera solucin a la lista de soluciones no dominadas

		if(Strategy.getStrategy().listRefPoblacFinal.size() == 0){
			Strategy.getStrategy().listRefPoblacFinal.add(stateReferenceHC.clone());
		}

		ifacceptCandidate = new FactoryAcceptCandidate();
		AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);
		State lastState = Strategy.getStrategy().listRefPoblacFinal.get(Strategy.getStrategy().listRefPoblacFinal.size()-1);
		List<State> neighborhood = new ArrayList<State>();
		neighborhood = Strategy.getStrategy().getProblem().getOperator().generatedNewState(stateReferenceHC, sizeNeighbors);
		int i= 0;

		Boolean accept = candidate.acceptCandidate(lastState, stateCandidate.clone());

		if(accept.equals(true)){
			stateReferenceHC = stateCandidate.clone();
			visitedState = new ArrayList<State>();
			lastState = stateCandidate.clone();
			//tomar xc q pertenesca a la vecindad de xa
		}
		else{
			boolean stop = false;
			while (i < neighborhood.size()&& stop==false) {
				if (Contain(neighborhood.get(i))==false) {
					stateCandidate = neighborhood.get(i);
					Strategy.getStrategy().getProblem().evaluate(stateCandidate);  
					visitedState.add(stateCandidate);
					accept = candidate.acceptCandidate(lastState, stateCandidate.clone());
					stop=true;
				}
				i++;
			}
			while (stop == false) {
				stateCandidate = Strategy.getStrategy().getProblem().getOperator().generateRandomState(1).get(0);
				if (Contain(stateCandidate)==false) {
					Strategy.getStrategy().getProblem().evaluate(stateCandidate);  
					stop=true;
					accept = candidate.acceptCandidate(lastState, stateCandidate.clone());
				}
			}
			if(accept.equals(true)){
				stateReferenceHC = stateCandidate.clone();
				visitedState = new ArrayList<State>();
				lastState = stateCandidate.clone();
				//tomar xc q pertenesca a la vecindad de xa
			}
		}


		getReferenceList();
	}

	@Override
	public List<State> getReferenceList() {
		listStateReference.add(stateReferenceHC.clone());
		return listStateReference;
	}

	private boolean Contain(State state){
		boolean found = false;
		for (Iterator<State> iter = visitedState.iterator(); iter.hasNext();) {
			State element = (State) iter.next();
			if(element.comparator(state)==true){
				found = true;
			}
		}
		return found;
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
