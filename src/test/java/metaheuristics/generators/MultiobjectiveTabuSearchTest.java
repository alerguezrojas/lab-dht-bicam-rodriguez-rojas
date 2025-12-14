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
import local_search.complement.TabuSolutions;
import metaheuristics.strategy.Strategy;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;

public class MultiobjectiveTabuSearchTest {

    private MultiobjectiveTabuSearch generator;
    
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
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        
        // Reset TabuSolutions
        TabuSolutions.listTabu = new ArrayList<>();
        TabuSolutions.maxelements = 5;
        
        // Mock Strategy Singleton
        strategyStaticMock = mockStatic(Strategy.class);
        strategyStaticMock.when(Strategy::getStrategy).thenReturn(strategyMock);
        
        // Setup Strategy mock
        when(strategyMock.getProblem()).thenReturn(problemMock);
        when(problemMock.getOperator()).thenReturn(operatorMock);
        
        generator = new MultiobjectiveTabuSearch();
        
        // Inject mocks into generator
        Field candidateValueField = MultiobjectiveTabuSearch.class.getDeclaredField("candidatevalue");
        candidateValueField.setAccessible(true);
        candidateValueField.set(generator, candidateValueMock);
        
        // Set initial state
        generator.setStateReferenceTS(stateMock);
    }

    @AfterEach
    void tearDown() throws Exception {
        strategyStaticMock.close();
        mocks.close();
        TabuSolutions.listTabu.clear();
    }

    @Test
    void testConstructor() {
        assertNotNull(generator);
        assertEquals(GeneratorType.MultiobjectiveTabuSearch, generator.getType());
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
    void testUpdateReference_Accepted_AddToTabu() throws Exception {
        State candidateState = mock(State.class);
        
        // Mock FactoryAcceptCandidate
        try (MockedConstruction<FactoryAcceptCandidate> factoryMock = mockConstruction(FactoryAcceptCandidate.class,
                (mock, context) -> {
                    AcceptableCandidate acceptableCandidateMock = mock(AcceptableCandidate.class);
                    when(mock.createAcceptCandidate(any(AcceptType.class))).thenReturn(acceptableCandidateMock);
                    when(acceptableCandidateMock.acceptCandidate(any(State.class), any(State.class))).thenReturn(true);
                })) {
            
            generator.updateReference(candidateState, 1);
            
            // Verify stateReferenceTS updated
            assertEquals(candidateState, generator.getReference());
            
            // Verify added to Tabu list
            assertEquals(1, TabuSolutions.listTabu.size());
            assertTrue(TabuSolutions.listTabu.contains(candidateState));
        }
    }
    
    @Test
    void testUpdateReference_Accepted_TabuFull() throws Exception {
        TabuSolutions.maxelements = 1;
        State existingState = mock(State.class);
        TabuSolutions.listTabu.add(existingState);
        
        State candidateState = mock(State.class);
        
        // Mock FactoryAcceptCandidate
        try (MockedConstruction<FactoryAcceptCandidate> factoryMock = mockConstruction(FactoryAcceptCandidate.class,
                (mock, context) -> {
                    AcceptableCandidate acceptableCandidateMock = mock(AcceptableCandidate.class);
                    when(mock.createAcceptCandidate(any(AcceptType.class))).thenReturn(acceptableCandidateMock);
                    when(acceptableCandidateMock.acceptCandidate(any(State.class), any(State.class))).thenReturn(true);
                })) {
            
            generator.updateReference(candidateState, 1);
            
            // Verify stateReferenceTS updated
            assertEquals(candidateState, generator.getReference());
            
            // Verify Tabu list updated (old removed, new added)
            assertEquals(1, TabuSolutions.listTabu.size());
            assertTrue(TabuSolutions.listTabu.contains(candidateState));
            assertFalse(TabuSolutions.listTabu.contains(existingState));
        }
    }
    
    @Test
    void testUpdateReference_Accepted_TabuDuplicate() throws Exception {
        State candidateState = mock(State.class);
        TabuSolutions.listTabu.add(candidateState);
        
        // Mock FactoryAcceptCandidate
        try (MockedConstruction<FactoryAcceptCandidate> factoryMock = mockConstruction(FactoryAcceptCandidate.class,
                (mock, context) -> {
                    AcceptableCandidate acceptableCandidateMock = mock(AcceptableCandidate.class);
                    when(mock.createAcceptCandidate(any(AcceptType.class))).thenReturn(acceptableCandidateMock);
                    when(acceptableCandidateMock.acceptCandidate(any(State.class), any(State.class))).thenReturn(true);
                })) {
            
            generator.updateReference(candidateState, 1);
            
            // Verify stateReferenceTS updated
            assertEquals(candidateState, generator.getReference());
            
            // Verify Tabu list size unchanged (duplicate not added)
            assertEquals(1, TabuSolutions.listTabu.size());
        }
    }

    @Test
    void testUpdateReference_Rejected() throws Exception {
        State candidateState = mock(State.class);
        
        // Mock FactoryAcceptCandidate
        try (MockedConstruction<FactoryAcceptCandidate> factoryMock = mockConstruction(FactoryAcceptCandidate.class,
                (mock, context) -> {
                    AcceptableCandidate acceptableCandidateMock = mock(AcceptableCandidate.class);
                    when(mock.createAcceptCandidate(any(AcceptType.class))).thenReturn(acceptableCandidateMock);
                    when(acceptableCandidateMock.acceptCandidate(any(State.class), any(State.class))).thenReturn(false);
                })) {
            
            generator.updateReference(candidateState, 1);
            
            // Verify stateReferenceTS NOT updated
            assertEquals(stateMock, generator.getReference());
            
            // Verify NOT added to Tabu list
            assertEquals(0, TabuSolutions.listTabu.size());
        }
    }
    
    @Test
    void testGettersSetters() {
        generator.setInitialReference(stateMock);
        assertEquals(stateMock, generator.getReference());
        
        generator.setTypeGenerator(GeneratorType.HillClimbing);
        assertEquals(GeneratorType.HillClimbing, generator.getTypeGenerator());
        
        assertNotNull(generator.getReferenceList());
        
        assertNull(generator.getSonList());
        assertNull(generator.getListCountBetterGender());
        assertNull(generator.getListCountGender());
        assertNull(generator.getTrace());
        
        generator.setWeight(100.0f);
        assertEquals(100.0f, generator.getWeight());
        
        assertFalse(generator.awardUpdateREF(stateMock));
        
        generator.setTypeCandidate(CandidateType.GreaterCandidate);
    }
}
