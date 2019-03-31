package network.pokt.eth;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import network.pokt.eth.util.RawFileUtil;
import network.pokt.eth.util.SemaphoreUtil;
import network.pokt.core.errors.PocketError;
import network.pokt.core.model.Wallet;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class EthContractTest {

    final String pocketTestContractAddress = "0x700989575bb2c2cafffdc3c4f583dccf904f90cb";
    String testAccountPK = "d97131a82ffa10142a277c49cb847c041f15eaff0fd0594d3152ca1ded2e1cb7";

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("network.pokt.eth.test", appContext.getPackageName());
    }

    @Test
    public void testConstantFunctionCall() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                try {
                    // Get the contract instance
                    EthContract contract = getContractInstance(R.raw.pocket_test_abi);

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
                            assertArrayEquals(new Object[]{new BigInteger("20")}, result);
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
                    EthContract contract = getContractInstance(R.raw.pocket_test_abi);

                    List<Object> functionParams = new ArrayList<>();
                    functionParams.add(new BigInteger("100"));
                    functionParams.add(new Boolean(true));
                    functionParams.add(pocketTestContractAddress);
                    functionParams.add("Hello World!");
                    functionParams.add("0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");

                    contract.executeConstantFunction("echo", functionParams, null, null, null, null, new Function2<PocketError, Object[], Unit>() {
                        @Override
                        public Unit invoke(PocketError pocketError, Object[] result) {
                            assertNull(pocketError);
                            assertNotNull(result);
                            assertEquals(result[0], new BigInteger("100"));
                            assertEquals(result[1], true);
                            assertEquals(result[2], "0x700989575bb2c2cafffdc3c4f583dccf904f90cb");
                            assertEquals(result[3], "Hello World!");
                            assertTrue(Arrays.equals((byte[]) result[4], Numeric.hexStringToByteArray("0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")));
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
                    final EthContract contract = getContractInstance(R.raw.pocket_test_abi);

                    List<Object> functionParams = new ArrayList<>();
                    functionParams.add(new BigInteger("1"));

                    try {
                        Wallet wallet = contract.getEthNetwork().importWallet(testAccountPK);
                        contract.executeFunction("addToState", wallet, functionParams, null, new BigInteger("100000"), new BigInteger("10000000000"), new BigInteger("0"), new Function2<PocketError, String, Unit>() {
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
    private EthContract getContractInstance(int abiInterfaceJSON) throws JSONException {
        Context appContext = InstrumentationRegistry.getTargetContext();
        List<String> netIds = new ArrayList<>();
        netIds.add(PocketEth.Networks.RINKEBY.getNetID());

        PocketEth pocketEth = new PocketEth(appContext, "DEVID1", netIds, 5, 60000, PocketEth.Networks.RINKEBY.getNetID());
        assertNotNull(pocketEth);

        JSONArray abiInterface = new JSONArray(RawFileUtil.readRawTextFile(InstrumentationRegistry.getTargetContext(), abiInterfaceJSON));
        return pocketEth.getRinkeby().createSmartContractInstance(pocketTestContractAddress, abiInterface);
    }

}
