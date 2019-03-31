package network.pokt.aion;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import network.pokt.aion.exceptions.AionContractException;
import network.pokt.aion.util.HexStringUtil;
import network.pokt.aion.util.RawFileUtil;
import network.pokt.aion.util.SemaphoreUtil;
import network.pokt.core.errors.PocketError;
import network.pokt.core.model.Wallet;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AionContractTest {

    final String pocketTestContractAddress = "0xA0707404B9BE7a5F630fCed3763d28FA5C988964fDC25Aa621161657a7Bf4b89";
    String testAccountPK = "0x1593800fe636f6fe996a0148c4ee1ecd6ff55a47b351a40f2da9d68815e1c6c958a09d74260842d51592f7be77e02171e4aea295078de50e5695835eee743932";

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("network.pokt.aion.test", appContext.getPackageName());
    }

    @Test
    public void testConstantFunctionCall() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                try {
                    // Get the contract instance
                    AionContract contract = getContractInstance(R.raw.pocket_test_abi);

                    // Prepare parameters
                    List<Object> functionParams = new ArrayList<>();
                    functionParams.add(new BigInteger("2"));
                    functionParams.add(new BigInteger("10"));

                    // Execute function and assert on response
                    contract.executeConstantFunction("multiply", functionParams, null, null, null, null, new Function2<PocketError, Object[], Unit>() {
                        @Override
                        public Unit invoke(PocketError pocketError, Object[] result) {
                            assertNull(pocketError);
                            assertNotNull(result);
                            // Since we know from JSON ABI that the return value is a uint128 we can check if it's type BigInteger
                            assertArrayEquals(new Object[]{"20"}, result);
                            semaphore.release();
                            return null;
                        }
                    });
                } catch (Exception exception) {
                    exception.printStackTrace();
                    semaphore.release();
                }
            }
        });
    }

    @Test
    public void testMultipleReturnsConstantFunction() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                try {
                    // Get the contract instance
                    AionContract contract = getContractInstance(R.raw.pocket_test_abi);

                    List<Object> functionParams = new ArrayList<>();
                    functionParams.add(new BigInteger("100"));
                    functionParams.add(new Boolean(true));
                    functionParams.add(pocketTestContractAddress);
                    functionParams.add("Hello World!");
                    functionParams.add(pocketTestContractAddress);

                    contract.executeConstantFunction("echo", functionParams, null, null, null, null, new Function2<PocketError, Object[], Unit>() {
                        @Override
                        public Unit invoke(PocketError pocketError, Object[] result) {
                            assertNull(pocketError);
                            assertNotNull(result);
                            assertEquals(result[0], "100");
                            assertEquals(result[1], true);
                            assertEquals(result[2], "0xA0707404B9BE7a5F630fCed3763d28FA5C988964fDC25Aa621161657a7Bf4b89");
                            assertEquals(result[3], "Hello World!");
                            assertEquals(result[4], "0xa0707404b9be7a5f630fced3763d28fa5c988964fdc25aa621161657a7bf4b89");
                            semaphore.release();
                            return null;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    semaphore.release();
                }
            }
        });
    }

    @Test
    public void testFunctionCall() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                try {
                    // Get the contract instance
                    final AionContract contract = getContractInstance(R.raw.pocket_test_abi);

                    List<Object> functionParams = new ArrayList<>();
                    functionParams.add(new BigInteger("1"));

                    try {
                        Wallet wallet = contract.getAionNetwork().importWallet(testAccountPK);
                        contract.executeFunction("addToState", wallet, functionParams, null, new BigInteger("100000"), new BigInteger("10000000000"), null, new Function2<PocketError, String, Unit>() {
                            @Override
                            public Unit invoke(PocketError pocketError, String result) {
                                assertNotNull(result);
                                assertNull(pocketError);
                                semaphore.release();
                                return null;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        semaphore.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    semaphore.release();
                }
            }
        });
    }


    // Private helpers
    private AionContract getContractInstance(int abiInterfaceJSON) throws JSONException {
        Context appContext = InstrumentationRegistry.getTargetContext();
        List<String> netIds = new ArrayList<>();
        netIds.add(PocketAion.Networks.MASTERY.getNetID());

        PocketAion pocketAion = new PocketAion(appContext, "DEVID1", netIds, 5, 60000, PocketAion.Networks.MASTERY.getNetID());
        assertNotNull(pocketAion);

        JSONArray abiInterface = new JSONArray(RawFileUtil.readRawTextFile(InstrumentationRegistry.getTargetContext(), abiInterfaceJSON));
        return pocketAion.getMastery().createSmartContractInstance(pocketTestContractAddress, abiInterface);
    }

}
