package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import factory_method.FactoryAcceptCandidate;
import local_search.acceptation_type.AcceptableCandidate;
import local_search.acceptation_type.AcceptType;
import local_search.candidate_type.CandidateType;
import local_search.candidate_type.CandidateValue;
import local_search.complement.StrategyType;
import metaheuristics.strategy.Strategy;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;

public class MultiobjectiveHillClimbingRestartTest {

    private MultiobjectiveHillClimbingRestart generator;
    
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
    
    private MockedStatic<Strategy> strategyStaticMock;
    
    private List<State> listRefPoblacFinal;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        // Reset static fields
        MultiobjectiveHillClimbingRestart.sizeNeighbors = 5;
        
        // Mock Strategy Singleton
        strategyStaticMock = mockStatic(Strategy.class);
        strategyStaticMock.when(Strategy::getStrategy).thenReturn(strategyMock);
        
        // Setup Strategy mock
        when(strategyMock.getProblem()).thenReturn(problemMock);
        when(problemMock.getOperator()).thenReturn(operatorMock);
        
        // Setup listRefPoblacFinal
        listRefPoblacFinal = new ArrayList<>();
        Field listRefField = Strategy.class.getDeclaredField("listRefPoblacFinal");
        listRefField.setAccessible(true);
        listRefField.set(strategyMock, listRefPoblacFinal);
        
        generator = new MultiobjectiveHillClimbingRestart();
        
        // Inject mocks into generator
        Field candidateValueField = AbstractHillClimbing.class.getDeclaredField("candidatevalue");
        candidateValueField.setAccessible(true);
        candidateValueField.set(generator, candidateValueMock);
        
        // Set initial state
        generator.stateReferenceHC = stateMock;
    }

    @AfterEach
    void tearDown() {
        strategyStaticMock.close();
    }

    @Test
    void testConstructor() {
        assertNotNull(generator);
        assertEquals(GeneratorType.MultiobjectiveHillClimbingRestart, generator.getType());
        assertEquals(50.0f, generator.getWeight());
    }

    @Test
    void testGenerate() throws Exception {
        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(stateMock);
        
        when(operatorMock.generatedNewState(any(State.class), anyInt())).thenReturn(neighborhood);
        when(candidateValueMock.stateCandidate(any(State.class), any(CandidateType.class), any(StrategyType.class), anyInt(), anyList()))
            .thenReturn(stateMock);
            
        State result = generator.generate(1);
        
        assertNotNull(result);
        verify(operatorMock).generatedNewState(any(State.class), anyInt());
        verify(candidateValueMock).stateCandidate(any(State.class), any(CandidateType.class), any(StrategyType.class), anyInt(), anyList());
    }

    @Test
    void testUpdateReference_FirstSolution() throws Exception {
        State candidateState = mock(State.class);
        when(candidateState.clone()).thenReturn(candidateState);
        when(stateMock.clone()).thenReturn(stateMock);
        
        // Mock FactoryAcceptCandidate
        try (MockedConstruction<FactoryAcceptCandidate> factoryMock = mockConstruction(FactoryAcceptCandidate.class,
                (mock, context) -> {
                    AcceptableCandidate acceptableCandidateMock = mock(AcceptableCandidate.class);
                    when(mock.createAcceptCandidate(any(AcceptType.class))).thenReturn(acceptableCandidateMock);
                    when(acceptableCandidateMock.acceptCandidate(any(State.class), any(State.class))).thenReturn(true);
                })) {
            
            generator.updateReference(candidateState, 1);
            
            // Verify first solution added
            assertEquals(1, listRefPoblacFinal.size());
        }
    }
    
    @Test
    void testUpdateReference_AcceptCandidate() throws Exception {
        State candidateState = mock(State.class);
        when(candidateState.clone()).thenReturn(candidateState);
        when(stateMock.clone()).thenReturn(stateMock);
        
        // Pre-populate listRefPoblacFinal
        listRefPoblacFinal.add(stateMock);
        
        // Mock FactoryAcceptCandidate
        try (MockedConstruction<FactoryAcceptCandidate> factoryMock = mockConstruction(FactoryAcceptCandidate.class,
                (mock, context) -> {
                    AcceptableCandidate acceptableCandidateMock = mock(AcceptableCandidate.class);
                    when(mock.createAcceptCandidate(any(AcceptType.class))).thenReturn(acceptableCandidateMock);
                    when(acceptableCandidateMock.acceptCandidate(any(State.class), any(State.class))).thenReturn(true);
                })) {
            
            generator.updateReference(candidateState, 1);
            
            // Verify stateReferenceHC updated
            verify(candidateState, atLeastOnce()).clone();
        }
    }

    @Test
    void testUpdateReference_RejectThenAcceptNeighbor() throws Exception {
        State candidateState = mock(State.class);
        when(candidateState.clone()).thenReturn(candidateState);
        when(stateMock.clone()).thenReturn(stateMock);
        
        // Pre-populate listRefPoblacFinal
        listRefPoblacFinal.add(stateMock);
        
        // Mock Neighborhood
        List<State> neighborhood = new ArrayList<>();
        State neighbor = mock(State.class);
        when(neighbor.clone()).thenReturn(neighbor);
        neighborhood.add(neighbor);
        
        when(operatorMock.generatedNewState(any(State.class), anyInt())).thenReturn(neighborhood);
        
        // Mock FactoryAcceptCandidate
        try (MockedConstruction<FactoryAcceptCandidate> factoryMock = mockConstruction(FactoryAcceptCandidate.class,
                (mock, context) -> {
                    AcceptableCandidate acceptableCandidateMock = mock(AcceptableCandidate.class);
                    when(mock.createAcceptCandidate(any(AcceptType.class))).thenReturn(acceptableCandidateMock);
                    
                    // First call: reject original candidate
                    // Second call: accept neighbor
                    // Note: acceptCandidate takes (lastState, candidate)
                    // We need to be careful with argument matching
                    when(acceptableCandidateMock.acceptCandidate(any(State.class), any(State.class)))
                        .thenAnswer(invocation -> {
                            State arg1 = invocation.getArgument(1);
                            if (arg1 == candidateState) return false;
                            if (arg1 == neighbor) return true;
                            return false;
                        });
                })) {
            
            generator.updateReference(candidateState, 1);
            
            // Verify neighbor became the reference
            assertEquals(neighbor, generator.getReference());
            verify(problemMock).evaluate(neighbor);
        }
    }

    @Test
    void testUpdateReference_RejectThenRandom() throws Exception {
        State candidateState = mock(State.class);
        when(candidateState.clone()).thenReturn(candidateState);
        when(stateMock.clone()).thenReturn(stateMock);
        
        // Pre-populate listRefPoblacFinal
        listRefPoblacFinal.add(stateMock);
        
        // Mock Neighborhood (empty)
        List<State> neighborhood = new ArrayList<>();
        when(operatorMock.generatedNewState(any(State.class), anyInt())).thenReturn(neighborhood);
        
        // Mock Random State
        List<State> randomStates = new ArrayList<>();
        State randomState = mock(State.class);
        when(randomState.clone()).thenReturn(randomState);
        randomStates.add(randomState);
        when(operatorMock.generateRandomState(1)).thenReturn(randomStates);
        
        // Mock FactoryAcceptCandidate
        try (MockedConstruction<FactoryAcceptCandidate> factoryMock = mockConstruction(FactoryAcceptCandidate.class,
                (mock, context) -> {
                    AcceptableCandidate acceptableCandidateMock = mock(AcceptableCandidate.class);
                    when(mock.createAcceptCandidate(any(AcceptType.class))).thenReturn(acceptableCandidateMock);
                    
                    when(acceptableCandidateMock.acceptCandidate(any(State.class), any(State.class)))
                        .thenAnswer(invocation -> {
                            State arg1 = invocation.getArgument(1);
                            if (arg1 == candidateState) return false;
                            if (arg1 == randomState) return true;
                            return false;
                        });
                })) {
            
            generator.updateReference(candidateState, 1);
            
            // Verify random state became the reference
            assertEquals(randomState, generator.getReference());
            verify(problemMock).evaluate(randomState);
        }
    }
}
