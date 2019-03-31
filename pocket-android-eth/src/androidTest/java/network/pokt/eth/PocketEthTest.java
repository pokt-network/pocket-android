package network.pokt.eth;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import network.pokt.core.model.Wallet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PocketEthTest {

    PocketEth pocketEth;

    @Before
    @Test
    public void testPocketAionInitialization() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        List<String> netIds = new ArrayList<>();
        netIds.add(PocketEth.Networks.RINKEBY.getNetID());

        this.pocketEth = new PocketEth(appContext, "DEVID1", netIds, 5, 60000, PocketEth.Networks.RINKEBY.getNetID());
        assertNotNull(this.pocketEth);
    }

    @Test
    public void testCreateWallet() {
        Wallet wallet = this.pocketEth.createWallet(PocketEth.Companion.getNETWORK(), PocketEth.Networks.RINKEBY.getNetID(), null);
        assertNotNull(wallet);
    }

    @Test
    public void testImportWallet() {
        Wallet wallet = this.pocketEth.createWallet(PocketEth.Companion.getNETWORK(), PocketEth.Networks.RINKEBY.getNetID(), null);
        assertNotNull(wallet);
        Wallet importedWallet = this.pocketEth.importWallet(wallet.getPrivateKey(),  wallet.getAddress(), wallet.getNetwork(), wallet.getNetID(), null);
        assertNotNull(importedWallet);
    }
}
