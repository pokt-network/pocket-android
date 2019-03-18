package network.pokt.pocketcore;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import network.pokt.pocketcore.model.Blockchain;
import network.pokt.pocketcore.model.Configuration;
import network.pokt.pocketcore.net.API;
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

        API api = new API();
        Blockchain blockchain = new Blockchain("ETH","1","0");
        Blockchain blockchain1 = new Blockchain("ETH", "4", "0");
        ArrayList<Blockchain> array = new ArrayList<Blockchain>();
        array.add(blockchain);
        array.add(blockchain1);
        Configuration configuration = new Configuration("DEVID1", array,5,1000);
        api.getActiveNodes(configuration);

    }
}
