package network.pokt.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import network.pokt.core.errors.WalletPersistenceError;
import network.pokt.core.model.Wallet;
import network.pokt.core.util.SemaphoreUtil;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class WalletTest {

    //private Wallet wallet;
    private String passphrase = "testpassphrase";

    @Test
    public void testSave() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        Wallet wallet = generateWallet("address1");
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(Semaphore semaphore) {

                wallet.save(passphrase, appContext, new Function1<WalletPersistenceError, Unit>() {
                    @Override
                    public Unit invoke(WalletPersistenceError walletPersistenceError) {
                        assertNull(walletPersistenceError);
                        assertTrue(wallet.isSaved(appContext));
                        wallet.delete(appContext);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testWalletRetrieve() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        Wallet wallet = generateWallet("address2");
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(Semaphore semaphore) {
                wallet.save(passphrase, appContext, new Function1<WalletPersistenceError, Unit>() {
                    @Override
                    public Unit invoke(WalletPersistenceError walletPersistenceError) {
                        assertNull(walletPersistenceError);
                        assertTrue(wallet.isSaved(appContext));
                        Wallet.Companion.retrieve(wallet.getNetwork(), wallet.getNetID(), wallet.getAddress(), passphrase, appContext, new Function2<WalletPersistenceError, Wallet, Unit>() {
                            @Override
                            public Unit invoke(WalletPersistenceError walletPersistenceError, Wallet retrievedWallet) {
                                assertNull(walletPersistenceError);
                                assertNotNull(retrievedWallet);
                                assertEquals(wallet.getNetwork(), retrievedWallet.getNetwork());
                                assertEquals(wallet.getNetID(), retrievedWallet.getNetID());
                                assertEquals(wallet.getAddress(), retrievedWallet.getAddress());
                                assertEquals(wallet.getPrivateKey(), retrievedWallet.getPrivateKey());
                                wallet.delete(appContext);
                                semaphore.release();
                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void listAppWallets() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Wallet wallet = generateWallet("address3");
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(Semaphore semaphore) {
                wallet.save(passphrase, appContext, new Function1<WalletPersistenceError, Unit>() {
                    @Override
                    public Unit invoke(WalletPersistenceError walletPersistenceError) {
                        assertNull(walletPersistenceError);
                        assertTrue(wallet.isSaved(appContext));
                        List<String> walletRecordKeys = Wallet.Companion.getWalletsRecordKeys(appContext);
                        assertTrue(walletRecordKeys.contains(wallet.recordKey()));
                        wallet.delete(appContext);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testDelete() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Wallet wallet = generateWallet("address4");
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(Semaphore semaphore) {
                wallet.save(passphrase, appContext, new Function1<WalletPersistenceError, Unit>() {
                    @Override
                    public Unit invoke(WalletPersistenceError walletPersistenceError) {
                        assertNull(walletPersistenceError);
                        assertTrue(wallet.isSaved(appContext));
                        wallet.delete(appContext);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    private Wallet generateWallet(String address) {
        return new Wallet("privateKey", address, "TEST", "1");
    }
}
