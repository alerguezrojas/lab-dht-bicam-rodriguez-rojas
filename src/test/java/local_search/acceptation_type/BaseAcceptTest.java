package local_search.acceptation_type;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;

import metaheuristics.strategy.Strategy;
import problem.definition.Problem;

public class BaseAcceptTest {

    protected MockedStatic<Strategy> strategyMockedStatic;
    protected Strategy strategyMock;
    protected Problem problemMock;

    @BeforeEach
    public void setUp() {
        strategyMock = mock(Strategy.class);
        problemMock = mock(Problem.class);
        strategyMockedStatic = mockStatic(Strategy.class);
        strategyMockedStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
        when(strategyMock.getProblem()).thenReturn(problemMock);
    }

    @AfterEach
    public void tearDown() {
        strategyMockedStatic.close();
    }
}
