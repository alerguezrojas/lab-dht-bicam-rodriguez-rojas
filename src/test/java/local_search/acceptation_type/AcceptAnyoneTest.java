package local_search.acceptation_type;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import problem.definition.State;
import static org.mockito.Mockito.*;

class AcceptAnyoneTest {

    @Test
    void testAcceptCandidate() {
        AcceptAnyone acceptAnyone = new AcceptAnyone();
        State current = mock(State.class);
        State candidate = mock(State.class);
        
        assertTrue(acceptAnyone.acceptCandidate(current, candidate));
    }
}
