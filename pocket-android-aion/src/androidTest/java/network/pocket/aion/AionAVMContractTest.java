package network.pocket.aion;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import network.pocket.aion.util.RawFileUtil;
import network.pocket.aion.util.SemaphoreUtil;
import network.pocket.core.errors.PocketError;
import network.pocket.core.model.Wallet;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AionAVMContractTest {

    final String avmTestContract = "0xa04ea987a8eb8e8e0d2f0386c16b932f2d66fed1313ab636e2b251a2947eff22";
    String testAccountPK = "0x1593800fe636f6fe996a0148c4ee1ecd6ff55a47b351a40f2da9d68815e1c6c958a09d74260842d51592f7be77e02171e4aea295078de50e5695835eee743932";
    AionAVMContract contract;
    List<Object> functionParams;

    @Before
    public void init() {
        // Get the contract instance
        InputStream is = this.getClass().getResourceAsStream("pocket_avm_test_abi");
        Scanner s = new Scanner(is).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        contract = getContractInstance(result);

        functionParams = new ArrayList<>();
        functionParams.add("test_string");
    }

    @Test
    public void testFunctionCall() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                try {
                    Wallet wallet = contract.getAionNetwork().importWallet(testAccountPK);
                    contract.executeFunction("setString", wallet, functionParams, null, new BigInteger("100000"), new BigInteger("10000000000"), null, (pocketError, result1) -> {
                        assertNotNull(result1);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    semaphore.release();
                }
            }
        });
    }


    // Private helpers
    private AionAVMContract getContractInstance(String abidefinition) {
        Context appContext = InstrumentationRegistry.getTargetContext();
        List<String> netIds = new ArrayList<>();
        netIds.add(PocketAion.Networks.MASTERY.getNetID());

        PocketAion pocketAion = new PocketAion(appContext, "DEVID1", netIds, 5, 60000, PocketAion.Networks.MASTERY.getNetID());
        assertNotNull(pocketAion);

        return pocketAion.getMastery().createAVMSmartContractInstance(avmTestContract, abidefinition);
    }

}
