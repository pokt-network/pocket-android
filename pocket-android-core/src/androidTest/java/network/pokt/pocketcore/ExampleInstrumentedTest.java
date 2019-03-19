package network.pokt.pocketcore;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import kotlin.Unit;
import network.pokt.pocketcore.model.Blockchain;
import network.pokt.pocketcore.model.Configuration;
import network.pokt.pocketcore.model.Node;
import network.pokt.pocketcore.net.PocketAPI;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("network.pokt.pocketcore.test", appContext.getPackageName());

        PocketCore pocketCore = new PocketCore("DEVID1", "ETH", "4", "0", 5, 1000);
        pocketCore.retrieveNodes(nodes -> {
            for(Node node : nodes){

            }

            return Unit.INSTANCE;
        });

    }
}
