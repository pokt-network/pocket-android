package network.pokt.pocketcore;

import android.support.test.runner.AndroidJUnit4;
import kotlin.Unit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    PocketCore pocketCore;

    @Test
    public void retrieveFailedNodes() {
        pocketCore.retrieveNodes(nodes -> {
            assertNull("expected crash",nodes);
            return Unit.INSTANCE;
        });

    }

    @Test
    public void retrieveNodes() {
        pocketCore.retrieveNodes(nodes -> {
            assertNotNull(nodes);
            return Unit.INSTANCE;
        });

    }

    @Before
    public void setUp() throws Exception {
        this.pocketCore = new PocketCore("DEVID1", "ETH", "4", "0", 5, 1000);
    }

    @After
    public void tearDown() throws Exception {
        this.pocketCore = null;
    }
}
