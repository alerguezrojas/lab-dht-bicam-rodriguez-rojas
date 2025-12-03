package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import problem.definition.State;

class GenerationalReplaceTest {

    @Test
    void testReplace() throws Exception {
        State s1 = mock(State.class);
        State s2 = mock(State.class);
        State s3 = mock(State.class);
        State candidate = mock(State.class);

        List<State> listState = new ArrayList<>(Arrays.asList(s1, s2, s3));
        GenerationalReplace replace = new GenerationalReplace();

        List<State> result = replace.replace(candidate, listState);

        // Should remove the first element (s1) and add candidate at the end
        assertEquals(3, result.size());
        assertEquals(s2, result.get(0));
        assertEquals(s3, result.get(1));
        assertEquals(candidate, result.get(2));
    }
}
