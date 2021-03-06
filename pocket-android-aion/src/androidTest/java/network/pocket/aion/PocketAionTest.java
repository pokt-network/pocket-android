package network.pocket.aion;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import network.pocket.core.model.Wallet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.*;

import static org.junit.Assert.assertNotNull;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PocketAionTest {

    PocketAion pocketAion;

    @Before
    @Test
    public void testPocketAionInitialization() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        List<String> netIds = new ArrayList<>();
        netIds.add(PocketAion.Networks.MASTERY.getNetID());

        this.pocketAion = new PocketAion(appContext, "DEVID1", netIds, 5, 60000, PocketAion.Networks.MASTERY.getNetID());
        assertNotNull(this.pocketAion);
    }

    @Test
    public void testCreateWallet() {
        Wallet wallet = this.pocketAion.getMastery().createWallet();
        assertNotNull(wallet);
    }

    @Test
    public void testImportWallet() {
        Wallet wallet = this.pocketAion.getMastery().createWallet();
        assertNotNull(wallet);
        Wallet importedWallet = this.pocketAion.getMastery().importWallet(wallet.getPrivateKey());
        assertNotNull(importedWallet);
    }
}
