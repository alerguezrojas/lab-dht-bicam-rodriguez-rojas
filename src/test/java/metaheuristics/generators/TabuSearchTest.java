package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import metaheuristics.strategy.Strategy;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;
import local_search.complement.TabuSolutions;
import local_search.candidate_type.CandidateType;

public class TabuSearchTest {

    private TabuSearch tabuSearch;
    
    @Mock
    private Strategy strategyMock;
    
    @Mock
    private Problem problemMock;
    
    @Mock
    private Operator operatorMock;
    
    @Mock
    private State stateMock;
    
    @Mock
    private State newStateMock;

    private MockedStatic<Strategy> strategyStaticMock;
    private AutoCloseable mocks;

    @BeforeEach
    public void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        
        // Reset Singleton
        resetSingleton();
        
        // Clear Tabu list
        TabuSolutions.listTabu.clear();
        TabuSolutions.maxelements = 10;
        
        // Mock Strategy Singleton
        strategyStaticMock = mockStatic(Strategy.class);
        strategyStaticMock.when(Strategy::getStrategy).thenReturn(strategyMock);
        
        // Setup chain
        when(strategyMock.getProblem()).thenReturn(problemMock);
        when(problemMock.getOperator()).thenReturn(operatorMock);
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        
        tabuSearch = new TabuSearch();
    }

    @AfterEach
    public void tearDown() throws Exception {
        strategyStaticMock.close();
        mocks.close();
        resetSingleton();
        TabuSolutions.listTabu.clear();
    }

    private void resetSingleton() throws Exception {
        Field instance = Strategy.class.getDeclaredField("strategy");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testGenerate() throws Exception {
        int operatorNumber = 1;
        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(newStateMock);
        
        tabuSearch.setInitialReference(stateMock);
        
        when(operatorMock.generatedNewState(stateMock, operatorNumber)).thenReturn(neighborhood);
        
        State result = tabuSearch.generate(operatorNumber);
        
        assertNotNull(result);
        assertEquals(newStateMock, result);
        verify(operatorMock).generatedNewState(stateMock, operatorNumber);
    }
    
    @Test
    public void testSetAndGetReference() {
        tabuSearch.setInitialReference(stateMock);
        assertEquals(stateMock, tabuSearch.getReference());
    }
    
    @Test
    public void testGetType() {
        assertEquals(GeneratorType.TabuSearch, tabuSearch.getType());
    }
    
    @Test
    public void testUpdateReference() throws Exception {
        tabuSearch.setInitialReference(stateMock);
        
        ArrayList<Double> evalRef = new ArrayList<>();
        evalRef.add(10.0);
        when(stateMock.getEvaluation()).thenReturn(evalRef);
        
        ArrayList<Double> evalNew = new ArrayList<>();
        evalNew.add(5.0);
        when(newStateMock.getEvaluation()).thenReturn(evalNew);
        
        tabuSearch.updateReference(newStateMock, 1);
        
        assertEquals(newStateMock, tabuSearch.getReference());
        assertEquals(1, TabuSolutions.listTabu.size());
        assertEquals(newStateMock, TabuSolutions.listTabu.get(0));
    }

    @Test
    public void testUpdateReference_TabuLogic_Add() throws Exception {
        tabuSearch.setInitialReference(stateMock);
        
        // Mock acceptance
        // AcceptAnyone always accepts
        
        // Mock Comparator
        when(newStateMock.Comparator(any(State.class))).thenReturn(false);
        
        tabuSearch.updateReference(newStateMock, 1);
        
        assertEquals(1, TabuSolutions.listTabu.size());
        assertTrue(TabuSolutions.listTabu.contains(newStateMock));
    }

    @Test
    public void testUpdateReference_TabuLogic_Full() throws Exception {
        tabuSearch.setInitialReference(stateMock);
        TabuSolutions.maxelements = 1;
        TabuSolutions.listTabu.add(stateMock);
        
        // Mock Comparator
        when(newStateMock.Comparator(any(State.class))).thenReturn(false);
        
        tabuSearch.updateReference(newStateMock, 1);
        
        assertEquals(1, TabuSolutions.listTabu.size());
        assertTrue(TabuSolutions.listTabu.contains(newStateMock));
        assertFalse(TabuSolutions.listTabu.contains(stateMock));
    }

    @Test
    public void testGetReferenceList() {
        tabuSearch.setInitialReference(stateMock);
        List<State> list = tabuSearch.getReferenceList();
        assertNotNull(list);
        assertTrue(list.contains(stateMock));
    }

    @Test
    public void testSetStateRef() {
        tabuSearch.setStateRef(stateMock);
        assertEquals(stateMock, tabuSearch.getReference());
    }

    @Test
    public void testSetGeneratorType() {
        tabuSearch.setTypeGenerator(GeneratorType.GeneticAlgorithm);
        assertEquals(GeneratorType.GeneticAlgorithm, tabuSearch.getTypeGenerator());
    }

    @Test
    public void testSetTypeCandidate() {
        tabuSearch.setTypeCandidate(local_search.candidate_type.CandidateType.GreaterCandidate);
    }

    @Test
    public void testGettersAndSetters() {
        tabuSearch.setWeight(10.0f);
        assertEquals(10.0f, tabuSearch.getWeight());
        
        assertNotNull(tabuSearch.getListCountBetterGender());
        assertNotNull(tabuSearch.getListCountGender());
        assertNotNull(tabuSearch.getTrace());
        assertNull(tabuSearch.getSonList());
        assertFalse(tabuSearch.awardUpdateREF(stateMock));
    }

    @Test
    public void testUpdateReference_TabuLogic_Duplicate() throws Exception {
        tabuSearch.setInitialReference(stateMock);
        TabuSolutions.listTabu.add(stateMock);
        
        // Mock Comparator to return true (duplicate found)
        when(stateMock.Comparator(any(State.class))).thenReturn(true);
        
        tabuSearch.updateReference(newStateMock, 1);
        
        // Should not add duplicate
        assertEquals(1, TabuSolutions.listTabu.size());
        assertEquals(stateMock, TabuSolutions.listTabu.get(0));
    }

    @Test
    public void testUpdateReference_StrategyNotTabu() throws Exception {
        tabuSearch.setInitialReference(stateMock);
        
        // Use reflection to change strategy
        Field strategyField = TabuSearch.class.getDeclaredField("strategy");
        strategyField.setAccessible(true);
        strategyField.set(tabuSearch, local_search.complement.StrategyType.NORMAL);
        
        tabuSearch.updateReference(newStateMock, 1);
        
        // Tabu list should remain empty
        assertEquals(0, TabuSolutions.listTabu.size());
    }

    @Test
    public void testUpdateReference_NotAccepted() throws Exception {
        tabuSearch.setInitialReference(stateMock);
        
        // Use reflection to change typeAcceptation to AcceptBest (which checks evaluation)
        Field typeAcceptationField = TabuSearch.class.getDeclaredField("typeAcceptation");
        typeAcceptationField.setAccessible(true);
        typeAcceptationField.set(tabuSearch, local_search.acceptation_type.AcceptType.AcceptBest);
        
        // Setup evaluations so candidate is worse (Minimization problem)
        ArrayList<Double> evalRef = new ArrayList<>();
        evalRef.add(5.0);
        when(stateMock.getEvaluation()).thenReturn(evalRef);
        
        ArrayList<Double> evalNew = new ArrayList<>();
        evalNew.add(10.0); // Worse
        when(newStateMock.getEvaluation()).thenReturn(evalNew);
        
        tabuSearch.updateReference(newStateMock, 1);
        
        // Should not update reference
        assertEquals(stateMock, tabuSearch.getReference());
        // Should not add to Tabu list (because acept is false)
        assertEquals(0, TabuSolutions.listTabu.size());
    }

    @Test
    public void testUpdateReference_TabuLogic_Full_Duplicate() throws Exception {
        tabuSearch.setInitialReference(stateMock);
        TabuSolutions.maxelements = 2;
        
        State s1 = mock(State.class);
        State s2 = mock(State.class);
        
        TabuSolutions.listTabu.add(s1);
        TabuSolutions.listTabu.add(s2);
        
        // Mock Comparator: s2 is duplicate of candidate
        // Note: s1 is at index 0, s2 at index 1.
        // Code: TabuSolutions.listTabu.remove(0); -> s1 removed. s2 becomes index 0.
        // Loop checks index 0 (s2).
        
        when(s2.Comparator(any(State.class))).thenReturn(true);
        
        tabuSearch.updateReference(newStateMock, 1);
        
        // Should remove s1, check s2 (found duplicate), so not add newStateMock
        assertEquals(1, TabuSolutions.listTabu.size());
        assertEquals(s2, TabuSolutions.listTabu.get(0));
    }
}
