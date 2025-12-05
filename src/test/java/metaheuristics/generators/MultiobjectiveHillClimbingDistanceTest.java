package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
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

public class MultiobjectiveHillClimbingDistanceTest {

    private MultiobjectiveHillClimbingDistance generator;
    
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
        MultiobjectiveHillClimbingDistance.distanceSolution = new ArrayList<>();
        MultiobjectiveHillClimbingDistance.sizeNeighbors = 5;
        
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
        
        generator = new MultiobjectiveHillClimbingDistance();
        
        // Inject mocks into generator
        Field candidateValueField = MultiobjectiveHillClimbingDistance.class.getDeclaredField("candidatevalue");
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
        assertEquals(GeneratorType.MultiobjectiveHillClimbingDistance, generator.getType());
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
            assertEquals(1, MultiobjectiveHillClimbingDistance.distanceSolution.size());
            assertEquals(0.0, MultiobjectiveHillClimbingDistance.distanceSolution.get(0));
        }
    }
    
    @Test
    void testUpdateReference_AcceptCandidate() throws Exception {
        State candidateState = mock(State.class);
        when(candidateState.clone()).thenReturn(candidateState);
        when(stateMock.clone()).thenReturn(stateMock);
        
        // Pre-populate listRefPoblacFinal
        listRefPoblacFinal.add(stateMock);
        MultiobjectiveHillClimbingDistance.distanceSolution.add(0.0);
        
        // Mock FactoryAcceptCandidate
        try (MockedConstruction<FactoryAcceptCandidate> factoryMock = mockConstruction(FactoryAcceptCandidate.class,
                (mock, context) -> {
                    AcceptableCandidate acceptableCandidateMock = mock(AcceptableCandidate.class);
                    when(mock.createAcceptCandidate(any(AcceptType.class))).thenReturn(acceptableCandidateMock);
                    when(acceptableCandidateMock.acceptCandidate(any(State.class), any(State.class))).thenReturn(true);
                })) {
            
            generator.updateReference(candidateState, 1);
            
            // Verify stateReferenceHC updated
            // Since stateReferenceHC is protected, we can check if it was updated by checking if clone was called on candidateState
            verify(candidateState, atLeastOnce()).clone();
        }
    }

    @Test
    void testUpdateReference_RejectCandidate_FindInNeighborhood() throws Exception {
        State candidateState = mock(State.class);
        when(candidateState.clone()).thenReturn(candidateState);
        when(stateMock.clone()).thenReturn(stateMock);
        
        // Pre-populate listRefPoblacFinal
        listRefPoblacFinal.add(stateMock);
        MultiobjectiveHillClimbingDistance.distanceSolution.add(10.0);
        
        // Mock neighborhood
        State neighborState = mock(State.class);
        when(neighborState.clone()).thenReturn(neighborState);
        // Ensure neighbor is NOT contained (Contain returns false)
        // Contain uses Comparator. We need to mock Comparator on visitedState elements.
        // visitedState is initially empty or populated? 
        // In updateReference: visitedState = new ArrayList<State>(); if accept is true.
        // But here accept is false.
        // visitedState is an instance field.
        
        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(neighborState);
        when(operatorMock.generatedNewState(any(State.class), anyInt())).thenReturn(neighborhood);
        
        // Mock FactoryAcceptCandidate to return false
        try (MockedConstruction<FactoryAcceptCandidate> factoryMock = mockConstruction(FactoryAcceptCandidate.class,
                (mock, context) -> {
                    AcceptableCandidate acceptableCandidateMock = mock(AcceptableCandidate.class);
                    when(mock.createAcceptCandidate(any(AcceptType.class))).thenReturn(acceptableCandidateMock);
                    when(acceptableCandidateMock.acceptCandidate(any(State.class), any(State.class))).thenReturn(false);
                })) {
            
            generator.updateReference(candidateState, 1);
            
            // Verify SolutionMoreDistance logic was executed
            // It should pick the solution with max distance from listRefPoblacFinal
            // We only have one state in listRefPoblacFinal with distance 10.0
            // So stateReferenceHC should become that state (stateMock)
            // But wait, SolutionMoreDistance returns a state from listRefPoblacFinal.
            // stateReferenceHC = SolutionMoreDistance(...)
            
            // We can verify that stateReferenceHC is set to stateMock (which is in listRefPoblacFinal)
            assertEquals(stateMock, generator.getReference());
        }
    }

    @Test
    void testDistanceCalculateAdd() {
        State s1 = mock(State.class);
        State s2 = mock(State.class);
        State s3 = mock(State.class); // The new one
        
        List<State> solutions = new ArrayList<>();
        solutions.add(s1);
        solutions.add(s2);
        solutions.add(s3);
        
        // Setup existing distances
        MultiobjectiveHillClimbingDistance.distanceSolution = new ArrayList<>();
        MultiobjectiveHillClimbingDistance.distanceSolution.add(10.0);
        MultiobjectiveHillClimbingDistance.distanceSolution.add(20.0);
        
        // Mock distances
        // s1 <-> s3
        when(s1.Distance(s3)).thenReturn(5.0);
        // s2 <-> s3
        when(s2.Distance(s3)).thenReturn(5.0);
        // s3 <-> s1
        when(s3.Distance(s1)).thenReturn(5.0);
        // s3 <-> s2
        when(s3.Distance(s2)).thenReturn(5.0);
        
        List<Double> result = MultiobjectiveHillClimbingDistance.DistanceCalculateAdd(solutions);
        
        assertEquals(3, result.size());
        // s1 new distance: 10 + 5 = 15
        assertEquals(15.0, result.get(0));
        // s2 new distance: 20 + 5 = 25
        assertEquals(25.0, result.get(1));
        // s3 distance: dist(s3, s1) + dist(s3, s2) = 5 + 5 = 10
        assertEquals(10.0, result.get(2));
    }
    
    @Test
    void testGettersSetters() {
        generator.setInitialReference(stateMock);
        assertEquals(stateMock, generator.getReference());
        
        generator.setGeneratorType(GeneratorType.HillClimbing);
        assertEquals(GeneratorType.HillClimbing, generator.getGeneratorType());
        
        assertNotNull(generator.getReferenceList());
        
        assertNull(generator.getSonList());
        assertNull(generator.getListCountBetterGender());
        assertNull(generator.getListCountGender());
        assertNull(generator.getTrace());
        
        generator.setWeight(100.0f);
        assertEquals(100.0f, generator.getWeight());
        
        assertFalse(generator.awardUpdateREF(stateMock));
    }
}

