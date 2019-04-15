package network.pokt.aion;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import network.pokt.aion.util.SemaphoreUtil;
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

    PocketAion pocketAion;

    @Test
    @Before
    public void setup() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        List<String> netIds = new ArrayList<>();
        netIds.add(PocketAion.Networks.MASTERY.getNetID());

        this.pocketAion = new PocketAion(appContext, "DEVID1", netIds, 5, 60000, PocketAion.Networks.MASTERY.getNetID());
        assertNotNull(this.pocketAion);
    }

    @Test
    public void version() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketAion.getMastery().getNet().version(new Function2<PocketError, String, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, String result) {
                        assertEquals(result, PocketAion.Networks.MASTERY.getNetID());
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
                pocketAion.getMastery().getNet().listening(new Function2<PocketError, Boolean, Unit>() {
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
                pocketAion.getMastery().getNet().peerCount(new Function2<PocketError, BigInteger, Unit>() {
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
