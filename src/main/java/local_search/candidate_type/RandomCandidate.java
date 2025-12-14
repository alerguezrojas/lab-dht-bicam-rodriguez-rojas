/**
 * @(#) AleatoryCandidate.java
 */

package local_search.candidate_type;

import java.util.List;
import java.security.SecureRandom;

import problem.definition.State;

public class RandomCandidate extends SearchCandidate {

	private static final SecureRandom secureRandom = new SecureRandom();

	@Override
	public State stateSearch(List<State> listNeighborhood) {
		if (listNeighborhood.isEmpty()) {
			return null;
		}
		int pos = secureRandom.nextInt(listNeighborhood.size());
		State stateAleatory = listNeighborhood.get(pos);
		return stateAleatory;
	}
}
