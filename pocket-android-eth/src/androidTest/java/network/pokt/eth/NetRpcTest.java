package network.pokt.eth;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import network.pokt.eth.util.SemaphoreUtil;
import network.pokt.core.errors.PocketError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class NetRpcTest {

    PocketEth pocketEth;

    @Test
    @Before
    public void setup() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        List<String> netIds = new ArrayList<>();
        netIds.add(PocketEth.Networks.RINKEBY.getNetID());

        this.pocketEth = new PocketEth(appContext, "DEVID1", netIds, 5, 60000, PocketEth.Networks.RINKEBY.getNetID());
        assertNotNull(this.pocketEth);
    }

    @Test
    public void version() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getNet().version(new Function2<PocketError, String, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, String result) {
                        assertEquals(result, PocketEth.Networks.RINKEBY.getNetID());
                        assertNull(pocketError);
                        semaphore.release();
                        return Unit.INSTANCE;
                    }
                });
            }
        });
    }

    @Test
    public void listening() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getNet().listening(new Function2<PocketError, Boolean, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, Boolean result) {
                        assertEquals(result, true);
                        assertNull(pocketError);
                        semaphore.release();
                        return Unit.INSTANCE;
                    }
                });
            }
        });
    }

    @Test
    public void peerCount() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getNet().peerCount(new Function2<PocketError, BigInteger, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, BigInteger result) {
                        assertNotNull(result);
                        assertNull(pocketError);
                        semaphore.release();
                        return Unit.INSTANCE;
                    }
                });
            }
        });
    }
}
