package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
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

import evolutionary_algorithms.complement.FatherSelection;
import evolutionary_algorithms.complement.Replace;
import evolutionary_algorithms.complement.Sampling;
import factory_method.FactoryFatherSelection;
import factory_method.FactoryReplace;
import factory_method.FactorySampling;
import metaheuristics.strategy.Strategy;
import problem.definition.Problem;
import problem.definition.State;

public class DistributionEstimationAlgorithmTest {

    private DistributionEstimationAlgorithm dea;
    
    @Mock
    private Strategy strategyMock;
    
    @Mock
    private Problem problemMock;
    
    @Mock
    private State stateMock;
    
    @Mock
    private FatherSelection fatherSelectionMock;
    
    @Mock
    private Sampling samplingMock;
    
    @Mock
    private Replace replaceMock;

    private MockedStatic<Strategy> strategyStaticMock;
    private AutoCloseable mocks;

    @BeforeEach
    public void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        
        resetSingleton();
        
        strategyStaticMock = mockStatic(Strategy.class);
        strategyStaticMock.when(Strategy::getStrategy).thenReturn(strategyMock);
        
        when(strategyMock.getProblem()).thenReturn(problemMock);
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        
        strategyMock.mapGenerators = new TreeMap<>();
        when(strategyMock.getListKey()).thenReturn(new ArrayList<>());
        
        RandomSearch.listStateReference = new ArrayList<>();
        
        dea = new DistributionEstimationAlgorithm();
    }

    @AfterEach
    public void tearDown() throws Exception {
        strategyStaticMock.close();
        mocks.close();
        resetSingleton();
        RandomSearch.listStateReference.clear();
    }

    private void resetSingleton() throws Exception {
        Field instance = Strategy.class.getDeclaredField("strategy");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testGenerate() throws Exception {
        // Setup mocks for factories
        try (MockedConstruction<FactoryFatherSelection> mockedFatherFactory = mockConstruction(FactoryFatherSelection.class,
                (mock, context) -> {
                    when(mock.createSelectFather(any())).thenReturn(fatherSelectionMock);
                });
             MockedConstruction<FactorySampling> mockedSamplingFactory = mockConstruction(FactorySampling.class,
                (mock, context) -> {
                    when(mock.createSampling(any())).thenReturn(samplingMock);
                })) {
            
            // Setup father selection return
            List<State> fathers = new ArrayList<>();
            fathers.add(mock(State.class));
            when(fatherSelectionMock.selection(anyList(), anyInt())).thenReturn(fathers);
            
            // Setup sampling return
            List<State> offspring = new ArrayList<>();
            State child = mock(State.class);
            ArrayList<Double> eval = new ArrayList<>();
            eval.add(10.0);
            when(child.getEvaluation()).thenReturn(eval);
            offspring.add(child);
            
            when(samplingMock.sampling(anyList(), anyInt())).thenReturn(offspring);
            
            // Run generate
            State result = dea.generate(1);
            
            assertNotNull(result);
            assertEquals(child, result);
            
            verify(fatherSelectionMock).selection(anyList(), anyInt());
            verify(samplingMock).sampling(anyList(), anyInt());
        }
    }
    
    @Test
    public void testUpdateReference() throws Exception {
        try (MockedConstruction<FactoryReplace> mockedReplaceFactory = mockConstruction(FactoryReplace.class,
                (mock, context) -> {
                    when(mock.createReplace(any())).thenReturn(replaceMock);
                })) {
            
            State candidate = mock(State.class);
            List<State> updatedList = new ArrayList<>();
            updatedList.add(candidate);
            
            when(replaceMock.replace(any(State.class), anyList())).thenReturn(updatedList);
            
            dea.updateReference(candidate, 1);
            
            assertEquals(1, dea.getReferenceList().size());
            assertEquals(candidate, dea.getReferenceList().get(0));
            
            verify(replaceMock).replace(any(State.class), anyList());
        }
    }
    
    @Test
    public void testGetReference() {
        // Setup reference list
        List<State> refList = new ArrayList<>();
        
        State s1 = mock(State.class);
        ArrayList<Double> e1 = new ArrayList<>();
        e1.add(10.0);
        when(s1.getEvaluation()).thenReturn(e1);
        
        State s2 = mock(State.class);
        ArrayList<Double> e2 = new ArrayList<>();
        e2.add(5.0);
        when(s2.getEvaluation()).thenReturn(e2);
        
        refList.add(s1);
        refList.add(s2);
        
        dea.setListReference(refList);
        
        // Minimization
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        State best = dea.getReference();
        assertEquals(s2, best); // 5.0 < 10.0
        
        // Maximization
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Maximizar);
        best = dea.getReference();
        assertEquals(s1, best); // 10.0 > 5.0
    }
    
    @Test
    public void testGetType() {
        assertEquals(GeneratorType.DistributionEstimationAlgorithm, dea.getType());
    }

    @Test
    public void testMaxValue() {
        List<State> list = new ArrayList<>();
        
        State s1 = mock(State.class);
        ArrayList<Double> e1 = new ArrayList<>();
        e1.add(10.0);
        when(s1.getEvaluation()).thenReturn(e1);
        
        State s2 = mock(State.class);
        ArrayList<Double> e2 = new ArrayList<>();
        e2.add(20.0);
        when(s2.getEvaluation()).thenReturn(e2);
        
        State s3 = mock(State.class);
        ArrayList<Double> e3 = new ArrayList<>();
        e3.add(5.0);
        when(s3.getEvaluation()).thenReturn(e3);
        
        list.add(s1);
        list.add(s2);
        list.add(s3);
        
        State max = dea.MaxValue(list);
        assertNotNull(max);
        assertEquals(20.0, max.getEvaluation().get(0));
    }

    @Test
    public void testGetListStateRef_Empty() {
        RandomSearch.listStateReference = new ArrayList<>();
        List<State> result = dea.getListStateRef();
        assertNotNull(result);
        // Depending on logic, might be empty or not
    }

    @Test
    public void testAwardUpdateREF() {
        List<State> list = new ArrayList<>();
        list.add(stateMock);
        dea.setListReference(list);
        
        assertTrue(dea.awardUpdateREF(stateMock));
        
        State other = mock(State.class);
        assertFalse(dea.awardUpdateREF(other));
    }

    @Test
    public void testGettersAndSetters() {
        dea.setWeight(10.0f);
        assertEquals(10.0f, dea.getWeight());

        dea.setGeneratorType(GeneratorType.GeneticAlgorithm);
        assertEquals(GeneratorType.GeneticAlgorithm, dea.getGeneratorType());

        dea.setDistributionType(evolutionary_algorithms.complement.DistributionType.Univariate);
        assertEquals(evolutionary_algorithms.complement.DistributionType.Univariate, dea.getDistributionType());
        
        assertNotNull(dea.getListCountBetterGender());
        assertNotNull(dea.getListCountGender());
        assertNotNull(dea.getTrace());
        assertNotNull(dea.getSonList());
        
        dea.setInitialReference(stateMock);
        // No getter for initial reference specifically, but used in getReference logic
    }
    
    @Test
    public void testGetListReference() {
        List<State> list = new ArrayList<>();
        list.add(stateMock);
        dea.setListReference(list);
        
        List<State> result = dea.getListReference();
        assertEquals(list, result);
    }
}
