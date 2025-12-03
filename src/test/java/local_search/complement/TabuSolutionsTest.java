package local_search.complement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import problem.definition.State;

class TabuSolutionsTest {

    @BeforeEach
    void setUp() {
        TabuSolutions.listTabu = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        TabuSolutions.listTabu = new ArrayList<>();
    }

    @Test
    void testFilterNeighborhoodEmptyTabu() throws Exception {
        TabuSolutions tabuSolutions = new TabuSolutions();
        List<State> neighborhood = new ArrayList<>();
        State s1 = mock(State.class);
        neighborhood.add(s1);
        
        List<State> result = tabuSolutions.filterNeighborhood(neighborhood);
        
        assertEquals(1, result.size());
        assertEquals(s1, result.get(0));
    }
    
    @Test
    void testFilterNeighborhoodWithTabu() throws Exception {
        TabuSolutions tabuSolutions = new TabuSolutions();
        List<State> neighborhood = new ArrayList<>();
        State s1 = mock(State.class);
        State s2 = mock(State.class);
        
        neighborhood.add(s1);
        neighborhood.add(s2);
        
        TabuSolutions.listTabu.add(s1);
        
        List<State> result = tabuSolutions.filterNeighborhood(neighborhood);
        
        assertEquals(1, result.size());
        assertEquals(s2, result.get(0));
    }
    
    @Test
    void testFilterNeighborhoodAllTabu() {
        TabuSolutions tabuSolutions = new TabuSolutions();
        List<State> neighborhood = new ArrayList<>();
        State s1 = mock(State.class);
        
        neighborhood.add(s1);
        
        TabuSolutions.listTabu.add(s1);
        
        assertThrows(Exception.class, () -> {
            tabuSolutions.filterNeighborhood(neighborhood);
        });
    }
}
