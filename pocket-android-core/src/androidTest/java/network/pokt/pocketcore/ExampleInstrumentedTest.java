package network.pokt.pocketcore;

import android.support.test.runner.AndroidJUnit4;
import kotlin.Unit;
import network.pokt.pocketcore.exceptions.PocketError;
import network.pokt.pocketcore.model.Relay;
import network.pokt.pocketcore.model.Report;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import static org.hamcrest.CoreMatchers.containsString;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {


    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void retrieveNodes() {
        PocketCore pocketCore = new PocketCore("DEVID1", "ETH", "4", 5, 1000);
        pocketCore.retrieveNodes(nodes -> {
            assertNotNull(nodes);
            assertFalse(nodes.isEmpty());

            return Unit.INSTANCE;
        });
    }

    @Test
    public void failRetrievingNodes() {
        PocketCore pocketCoreFail = new PocketCore("DEVID1", "ETH2", "4", 5, 1000);
        pocketCoreFail.retrieveNodes(nodes -> {
            exception.expect(PocketError.class);
            return Unit.INSTANCE;
        });
    }

    @Test
    public void sendRelay() {
        PocketCore pocketCore = new PocketCore("DEVID1", "ETH", "4", 5, 1000);
        String address = "0xf892400Dc3C5a5eeBc96070ccd575D6A720F0F9f";
        String data = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[".concat(address).concat(",\"latest\"],\"id\":67}");
        Relay relay = pocketCore.createRelay("ETH", "4", data, "DEVID1");

        assertTrue(relay.isValid());

        pocketCore.retrieveNodes(nodes -> {
            assertNotNull(nodes);
            assertFalse(nodes.isEmpty());
            pocketCore.send(relay, jsonObject -> {
                assertNotNull(jsonObject);
                assertFalse(jsonObject.has("jsonrcp"));
                return Unit.INSTANCE;
            });

            return Unit.INSTANCE;
        });
    }

    @Test
    public void sendReport() {
        PocketCore pocketCore = new PocketCore("DEVID1", "ETH", "4", 5, 1000);
        pocketCore.retrieveNodes(nodes -> {
            assertNotNull(nodes);
            assertFalse(nodes.isEmpty());
            Report report = pocketCore.createReport(nodes.get(0).getIpPort(), "This is a test, please ignore");

            assertTrue(report.isValid());

            pocketCore.send(report, message -> {
                assertNotNull(message);
                assertThat(message, containsString("Okay"));
                return Unit.INSTANCE;
            });

            return Unit.INSTANCE;
        });
    }
}
