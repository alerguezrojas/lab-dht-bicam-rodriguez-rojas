package metaheuristics.generators;

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

public class HillClimbingRestartTest {

    private HillClimbingRestart hcr;
    
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
        
        // Reset statics
        HillClimbingRestart.count = 10;
        HillClimbingRestart.countCurrent = 10;
        
        hcr = new HillClimbingRestart();
        
        // Inject mock CandidateValue
        Field cvField = HillClimbingRestart.class.getDeclaredField("candidatevalue");
        cvField.setAccessible(true);
        cvField.set(hcr, candidateValueMock);
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
    public void testGenerateNormal() throws Exception {
        State refState = mock(State.class);
        hcr.setInitialReference(refState);
        
        // Ensure no restart
        when(strategyMock.getCountCurrent()).thenReturn(5); // count is 10
        
        List<State> neighborhood = new ArrayList<>();
        when(operatorMock.generatedNewState(any(State.class), anyInt())).thenReturn(neighborhood);
        
        State candidate = mock(State.class);
        when(candidateValueMock.stateCandidate(any(), any(), any(), anyInt(), anyList())).thenReturn(candidate);
        
        State result = hcr.generate(1);
        
        assertEquals(candidate, result);
        verify(operatorMock, never()).generateRandomState(anyInt());
    }
    
    @Test
    public void testGenerateRestart() throws Exception {
        State refState = mock(State.class);
        when(refState.getCopy()).thenReturn(refState); // For new State(stateReferenceHC)
        // Wait, new State(state) calls copy constructor which calls getters.
        // I need to mock getters of refState to return non-null lists if needed.
        when(refState.getCode()).thenReturn(new ArrayList<>());
        when(refState.getEvaluation()).thenReturn(new ArrayList<>());
        
        hcr.setInitialReference(refState);
        
        // Trigger restart
        when(strategyMock.getCountCurrent()).thenReturn(10); // count is 10
        
        List<State> randomStates = new ArrayList<>();
        State newState = mock(State.class);
        randomStates.add(newState);
        when(operatorMock.generateRandomState(1)).thenReturn(randomStates);
        
        List<State> neighborhood = new ArrayList<>();
        when(operatorMock.generatedNewState(any(State.class), anyInt())).thenReturn(neighborhood);
        
        State candidate = mock(State.class);
        when(candidateValueMock.stateCandidate(any(), any(), any(), anyInt(), anyList())).thenReturn(candidate);
        
        State result = hcr.generate(1);
        
        assertEquals(candidate, result);
        verify(operatorMock).generateRandomState(1);
        verify(problemMock).Evaluate(newState);
        assertEquals(20, HillClimbingRestart.count); // 10 + 10
    }
    
    @Test
    public void testUpdateReference() throws Exception {
        State refState = mock(State.class);
        hcr.setInitialReference(refState);
        
        State candidate = mock(State.class);
        
        try (MockedConstruction<FactoryAcceptCandidate> mockedFactory = mockConstruction(FactoryAcceptCandidate.class,
                (mock, context) -> {
                    when(mock.createAcceptCandidate(any())).thenReturn(acceptableCandidateMock);
                })) {
            
            // Case 1: Accepted
            when(acceptableCandidateMock.acceptCandidate(any(), any())).thenReturn(true);
            
            hcr.updateReference(candidate, 1);
            
            assertEquals(candidate, hcr.getReference());
            
            // Case 2: Rejected
            State rejectedCandidate = mock(State.class);
            when(acceptableCandidateMock.acceptCandidate(any(), eq(rejectedCandidate))).thenReturn(false);
            
            hcr.updateReference(rejectedCandidate, 1);
            
            assertEquals(candidate, hcr.getReference());
        }
    }
    
    @Test
    public void testGetType() {
        // HillClimbingRestart sets type to HillClimbing in constructor
        assertEquals(GeneratorType.HillClimbing, hcr.getType());
    }

    @Test
    public void testGettersAndSetters() {
        hcr.setWeight(10.0f);
        assertEquals(10.0f, hcr.getWeight());
        
        hcr.setGeneratorType(GeneratorType.GeneticAlgorithm);
        assertEquals(GeneratorType.GeneticAlgorithm, hcr.getGeneratorType());
        
        hcr.setTypeCandidate(local_search.candidate_type.CandidateType.GreaterCandidate);
        
        assertNotNull(hcr.getListCountBetterGender());
        assertNotNull(hcr.getListCountGender());
        assertNotNull(hcr.getTrace());
        assertNull(hcr.getSonList());
        assertFalse(hcr.awardUpdateREF(stateMock));
        
        hcr.setStateRef(stateMock);
        assertEquals(stateMock, hcr.getReference());
    }

    @Test
    public void testGetReferenceList() {
        hcr.setInitialReference(stateMock);
        List<State> list = hcr.getReferenceList();
        assertNotNull(list);
        assertTrue(list.contains(stateMock));
    }
}
