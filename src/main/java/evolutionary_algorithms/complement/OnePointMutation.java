package evolutionary_algorithms.complement;


import metaheurictics.strategy.Strategy;
import problem.definition.State;
import java.security.SecureRandom;

public class OnePointMutation extends Mutation {

	private static final SecureRandom secureRandom = new SecureRandom();

	@Override
	public State mutation(State state, double PM) {
		double probM = secureRandom.nextDouble();
		if(PM >= probM)
		{
			Object key = Strategy.getStrategy().getProblem().getCodification().getAleatoryKey();
			Object value = Strategy.getStrategy().getProblem().getCodification().getVariableAleatoryValue((Integer)key);
			state.getCode().set((Integer) key, value);
		}
		return state;
	}
}
