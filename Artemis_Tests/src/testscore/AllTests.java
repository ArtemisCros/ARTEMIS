package testscore;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({TestMachine.class, TestNetwork.class, TestLogger.class, TestLink.class, TestMessage.class,
	TestSchedulingPolicies.class, TestNetworkLinker.class})

public class AllTests {

}
