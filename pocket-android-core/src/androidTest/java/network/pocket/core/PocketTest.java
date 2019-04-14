package network.pocket.core;

import android.support.test.runner.AndroidJUnit4;
import kotlin.Unit;
import network.pocket.core.model.Relay;
import network.pocket.core.util.PocketTestPlugin;
import network.pocket.core.util.SemaphoreUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PocketTest {


    PocketTestPlugin plugin;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        this.plugin = new PocketTestPlugin("DEVID1", "ETH", new String[]{"4"}, 5, 60000);
        assertNotNull(this.plugin);
    }

    @After
    public void tearDown() {
        this.plugin = null;
    }

    @Test
    public void testNoNodesAvailable() {
        // Note the invalid networkName on the constructor
        PocketTestPlugin plugin = new PocketTestPlugin("DEVID1", "TEST", new String[]{"4"}, 5, 60000);
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(Semaphore semaphore) {
                String address = "0xf892400Dc3C5a5eeBc96070ccd575D6A720F0F9f";
                String data = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\"".concat(address).concat("\",\"latest\"],\"id\":67}");
                Relay relay = new Relay("TEST", "4", "DEVID1", data);
                plugin.send(relay, (pocketError, jsonObject) -> {
                    assertNotNull(pocketError);
                    assertNull(jsonObject);
                    semaphore.release();
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @Test
    public void testSendRelay() {
        PocketTestPlugin plugin = this.plugin;

        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(Semaphore semaphore) {
                String address = "0xf892400Dc3C5a5eeBc96070ccd575D6A720F0F9f";
                String data = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\"".concat(address).concat("\",\"latest\"],\"id\":67}");
                Relay relay = new Relay("ETH", "4", "DEVID1", data);
                plugin.send(relay, (pocketError, jsonObject) -> {
                    assertNull(pocketError);
                    assertNotNull(jsonObject);
                    semaphore.release();
                    return Unit.INSTANCE;
                });
            }
        });

    }

    @Test
    public void testSendRelayWithParams() {
        PocketTestPlugin plugin = this.plugin;

        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(Semaphore semaphore) {
                String address = "0xf892400Dc3C5a5eeBc96070ccd575D6A720F0F9f";
                String data = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\"".concat(address).concat("\",\"latest\"],\"id\":67}");
                plugin.send("ETH", "4", data, (pocketError, jsonObject) -> {
                     assertNull(pocketError);
                    assertNotNull(jsonObject);
                    semaphore.release();
                    return Unit.INSTANCE;
                });
            }
        });

    }
}
