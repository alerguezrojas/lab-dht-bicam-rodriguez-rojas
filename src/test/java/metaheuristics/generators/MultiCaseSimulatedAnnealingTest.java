package metaheuristics.generators;

import local_search.complement.MultiCaseSimulatedAnnealing;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import factory_method.FactoryAcceptCandidate;
import local_search.acceptation_type.AcceptableCandidate;
import local_search.candidate_type.CandidateValue;
import metaheuristics.strategy.Strategy;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;

public class MultiCaseSimulatedAnnealingTest {

    private MultiCaseSimulatedAnnealing mcsa;
    
    @Mock
    private Strategy strategyMock;
    
    @Mock
    private Problem problemMock;
    
    @Mock
    private Operator operatorMock;
    
    @Mock
    private State stateMock;
    
    @Mock
    private CandidateValue candidateValueMock;
    
    @Mock
    private AcceptableCandidate acceptableCandidateMock;

    private MockedStatic<Strategy> strategyStaticMock;
    private AutoCloseable mocks;

    @BeforeEach
    public void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        
        resetSingleton();
        
        strategyStaticMock = mockStatic(Strategy.class);
        strategyStaticMock.when(Strategy::getStrategy).thenReturn(strategyMock);
        
        when(strategyMock.getProblem()).thenReturn(problemMock);
        when(problemMock.getOperator()).thenReturn(operatorMock);
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        
        strategyMock.mapGenerators = new TreeMap<>();
        when(strategyMock.getListKey()).thenReturn(new ArrayList<>());
        
        // Initialize statics
        MultiCaseSimulatedAnnealing.alpha = 0.9;
        MultiCaseSimulatedAnnealing.tinitial = 100.0;
        MultiCaseSimulatedAnnealing.tfinal = 0.1;
        MultiCaseSimulatedAnnealing.countIterationsT = 10;
        
        mcsa = new MultiCaseSimulatedAnnealing();
        
        // Inject mock CandidateValue
        Field cvField = MultiCaseSimulatedAnnealing.class.getDeclaredField("candidatevalue");
        cvField.setAccessible(true);
        cvField.set(mcsa, candidateValueMock);
    }

    @AfterEach
    public void tearDown() throws Exception {
        strategyStaticMock.close();
        mocks.close();
        resetSingleton();
    }

    private void resetSingleton() throws Exception {
        Field instance = Strategy.class.getDeclaredField("strategy");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testGenerate() throws Exception {
        State refState = mock(State.class);
        mcsa.setInitialReference(refState);
        
        List<State> neighborhood = new ArrayList<>();
        when(operatorMock.generatedNewState(any(State.class), anyInt())).thenReturn(neighborhood);
        
        State candidate = mock(State.class);
        when(candidateValueMock.stateCandidate(any(), any(), any(), anyInt(), anyList())).thenReturn(candidate);
        
        State result = mcsa.generate(1);
        
        assertEquals(candidate, result);
        verify(operatorMock).generatedNewState(refState, 1);
        verify(candidateValueMock).stateCandidate(eq(refState), any(), any(), eq(1), eq(neighborhood));
    }
    
    @Test
    public void testUpdateReference() throws Exception {
        State refState = mock(State.class);
        when(refState.clone()).thenReturn(refState);
        mcsa.setInitialReference(refState);
        
        State candidate = mock(State.class);
        when(candidate.clone()).thenReturn(candidate);
        
        try (MockedConstruction<FactoryAcceptCandidate> mockedFactory = mockConstruction(FactoryAcceptCandidate.class,
                (mock, context) -> {
                    when(mock.createAcceptCandidate(any())).thenReturn(acceptableCandidateMock);
                })) {
            
            // Case 1: Accepted
            when(acceptableCandidateMock.acceptCandidate(any(), any())).thenReturn(true);
            
            // Not triggering temperature update yet
            mcsa.updateReference(candidate, 1);
            
            assertEquals(candidate, mcsa.getReference());
            
            // Case 2: Rejected
            State rejectedCandidate = mock(State.class);
            when(acceptableCandidateMock.acceptCandidate(any(), eq(rejectedCandidate))).thenReturn(false);
            
            mcsa.updateReference(rejectedCandidate, 1);
            
            assertEquals(candidate, mcsa.getReference()); // Should remain the previous one
            
            // Case 3: Temperature update
            // countIterationsT is 10. countRept is set to countIterationsT in updateReference.
            // Wait, updateReference says: countRept = countIterationsT;
            // Then if(countIterationsCurrent.equals(countIterationsT)) { update T; countIterationsT += countRept; }
            
            double initialT = MultiCaseSimulatedAnnealing.tinitial;
            int initialCountT = MultiCaseSimulatedAnnealing.countIterationsT;
            
            mcsa.updateReference(candidate, initialCountT);
            
            // Verify T updated
            assertEquals(initialT * MultiCaseSimulatedAnnealing.alpha, MultiCaseSimulatedAnnealing.tinitial, 0.001);
            // Verify countIterationsT updated
            assertEquals(initialCountT + initialCountT, MultiCaseSimulatedAnnealing.countIterationsT);
        }
    }
    
    @Test
    public void testGetType() {
        assertEquals(GeneratorType.MultiCaseSimulatedAnnealing, mcsa.getType());
    }

    @Test
    public void testGetSetSimple() {
        MultiCaseSimulatedAnnealing generator = new MultiCaseSimulatedAnnealing();
        
        generator.setWeight(12.3f);
        assertEquals(12.3f, generator.getWeight());
        
        generator.setTypeGenerator(GeneratorType.MultiCaseSimulatedAnnealing);
        assertEquals(GeneratorType.MultiCaseSimulatedAnnealing, generator.getTypeGenerator());
        
        assertNotNull(generator.getListCountBetterGender());
        assertNotNull(generator.getListCountGender());
        assertNotNull(generator.getTrace());
        // assertNull(generator.getReferenceList());
        assertNull(generator.getSonList());
        
        assertFalse(generator.awardUpdateREF(stateMock));
    }
}
