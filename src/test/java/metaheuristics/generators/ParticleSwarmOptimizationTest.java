package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import metaheurictics.strategy.Strategy;
import problem.definition.Codification;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;

public class ParticleSwarmOptimizationTest {

    private ParticleSwarmOptimization pso;
    
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

    @Mock
    private Codification codificationMock;

    private MockedStatic<Strategy> strategyStaticMock;
    private AutoCloseable mocks;

    @BeforeEach
    public void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        
        // Reset Singleton
        resetSingleton();
        
        // Mock Strategy Singleton
        strategyStaticMock = mockStatic(Strategy.class);
        strategyStaticMock.when(Strategy::getStrategy).thenReturn(strategyMock);
        
        // Setup chain
        when(strategyMock.getProblem()).thenReturn(problemMock);
        when(problemMock.getOperator()).thenReturn(operatorMock);
        when(problemMock.getState()).thenReturn(stateMock);
        
        // Fix: Ensure stateMock has code so UpdateVelocity loop runs
        ArrayList<Object> stateCode = new ArrayList<>();
        stateCode.add(0.0); // Size 1
        when(stateMock.getCode()).thenReturn(stateCode);
        
        when(stateMock.getCopy()).thenReturn(newStateMock);
        when(newStateMock.getCode()).thenReturn(new ArrayList<>(stateCode));
        when(newStateMock.getEvaluation()).thenReturn(new ArrayList<>());
        when(newStateMock.getCopy()).thenReturn(newStateMock);
        
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        when(problemMock.getCodification()).thenReturn(codificationMock);
        when(codificationMock.getVariableCount()).thenReturn(1);
        
        // Setup Strategy fields
        strategyMock.mapGenerators = new TreeMap<>();
        when(strategyMock.getListKey()).thenReturn(new ArrayList<>());
        when(strategyMock.getCountMax()).thenReturn(100);
        
        // Setup static fields
        ParticleSwarmOptimization.coutSwarm = 1;
        ParticleSwarmOptimization.countParticleBySwarm = 2;
        ParticleSwarmOptimization.countRef = 2;
        ParticleSwarmOptimization.countParticle = 0;
        ParticleSwarmOptimization.wmax = 0.9;
        ParticleSwarmOptimization.wmin = 0.2;
        ParticleSwarmOptimization.learning1 = 2;
        ParticleSwarmOptimization.learning2 = 2;
        ParticleSwarmOptimization.binary = false;
        
        // Setup RandomSearch static list
        RandomSearch.listStateReference = new ArrayList<>();
        
        // Create instance
        pso = new ParticleSwarmOptimization();
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
        // Setup particles
        List<Particle> particles = new ArrayList<>();
        
        // Particle 1
        State s1 = mock(State.class);
        ArrayList<Object> code1 = new ArrayList<>();
        code1.add(1.0);
        when(s1.getCode()).thenReturn(code1);
        ArrayList<Double> eval1 = new ArrayList<>();
        eval1.add(10.0);
        when(s1.getEvaluation()).thenReturn(eval1);
        when(s1.getCopy()).thenReturn(s1);
        
        State pBest1 = mock(State.class);
        when(pBest1.getCode()).thenReturn(code1);
        when(pBest1.getEvaluation()).thenReturn(eval1);
        when(pBest1.getCopy()).thenReturn(pBest1);
        
        Particle p1 = new Particle(pBest1, s1, new ArrayList<>());
        particles.add(p1);
        
        // Particle 2
        State s2 = mock(State.class);
        ArrayList<Object> code2 = new ArrayList<>();
        code2.add(2.0);
        when(s2.getCode()).thenReturn(code2);
        ArrayList<Double> eval2 = new ArrayList<>();
        eval2.add(5.0);
        when(s2.getEvaluation()).thenReturn(eval2);
        when(s2.getCopy()).thenReturn(s2);
        
        State pBest2 = mock(State.class);
        when(pBest2.getCode()).thenReturn(code2);
        when(pBest2.getEvaluation()).thenReturn(eval2);
        when(pBest2.getCopy()).thenReturn(pBest2);
        
        Particle p2 = new Particle(pBest2, s2, new ArrayList<>());
        particles.add(p2);
        
        pso.setListParticle(particles);
        
        // Initialize lBest and gBest
        ParticleSwarmOptimization.lBest = new State[1];
        ParticleSwarmOptimization.lBest[0] = pBest2; // Best is 5.0
        ParticleSwarmOptimization.gBest = pBest2;
        
        // Mock Codification for UpdateCode
        when(codificationMock.getVariableCount()).thenReturn(1);
        
        // Run generate
        State result = pso.generate(1);
        assertNotNull(result);
        
        // Run generate again for next particle
        State result2 = pso.generate(1);
        assertNotNull(result2);
    }
    
    @Test
    public void testGetType() {
        assertEquals(GeneratorType.ParticleSwarmOptimization, pso.getType());
    }
    
    @Test
    public void testUpdateReference() throws Exception {
        // Setup particles
        List<Particle> particles = new ArrayList<>();
        
        // Particle 1
        State s1 = mock(State.class);
        ArrayList<Object> code1 = new ArrayList<>();
        code1.add(1.0);
        when(s1.getCode()).thenReturn(code1);
        ArrayList<Double> eval1 = new ArrayList<>();
        eval1.add(10.0);
        when(s1.getEvaluation()).thenReturn(eval1);
        when(s1.getCopy()).thenReturn(s1);
        
        State pBest1 = mock(State.class);
        when(pBest1.getCode()).thenReturn(code1);
        when(pBest1.getEvaluation()).thenReturn(eval1);
        when(pBest1.getCopy()).thenReturn(pBest1);
        
        Particle p1 = new Particle(pBest1, s1, new ArrayList<>());
        particles.add(p1);
        
        pso.setListParticle(particles);
        
        // Initialize lBest and gBest
        ParticleSwarmOptimization.lBest = new State[1];
        ParticleSwarmOptimization.lBest[0] = pBest1;
        ParticleSwarmOptimization.gBest = pBest1;
        
        // Mock getReferenceList for gBest update check
        List<State> refList = new ArrayList<>();
        State refState = mock(State.class);
        ArrayList<Double> refEval = new ArrayList<>();
        refEval.add(20.0); // Worse than 10.0
        when(refState.getEvaluation()).thenReturn(refEval);
        refList.add(refState);
        pso.setListStateReference(refList);
        
        ParticleSwarmOptimization.countParticle = 0;
        
        // Mock Strategy.getStrategy().getProblem().getTypeProblem()
        when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.Minimizar);
        
        // Create a valid stateCandidate
        State stateCandidate = mock(State.class);
        ArrayList<Double> candEval = new ArrayList<>();
        candEval.add(8.0); // Better than 10.0
        when(stateCandidate.getEvaluation()).thenReturn(candEval);
        ArrayList<Object> candCode = new ArrayList<>();
        candCode.add(1.5);
        when(stateCandidate.getCode()).thenReturn(candCode);
        
        // Run updateReference
        pso.updateReference(stateCandidate, 1);
        
        // Verify pBest updated
        State updatedPBest = pso.getListParticle().get(0).getStatePBest();
        assertEquals(8.0, updatedPBest.getEvaluation().get(0));
        assertEquals(1.5, updatedPBest.getCode().get(0));
    }
}
