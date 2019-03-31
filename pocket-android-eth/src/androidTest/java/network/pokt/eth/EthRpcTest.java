package network.pokt.eth;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import network.pokt.eth.rpc.types.BlockTag;
import network.pokt.eth.rpc.types.ObjectOrBoolean;
import network.pokt.eth.util.SemaphoreUtil;
import network.pokt.eth.util.SemaphoreUtil.SemaphoreCallback;
import network.pokt.core.errors.PocketError;
import network.pokt.core.model.Wallet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class EthRpcTest {

    PocketEth pocketEth;
    String testAccountAddress = "0xE1B33AFb88C77E343ECbB9388829eEf6123a980a";
    String testTxHashHex = "0x5cdcf19fc3934f345fcc9204e48ad9087e5fd2817063d3f6fd0cf4ed9add2a18";
    String blockHashHex = "0x89007206f2c5356505465949e9a2f4f37cee6da9dc04cc86271ab9aeb5ab076d";
    BigInteger blockNumber = new BigInteger("4123837");
    String pocketTestContractAddress = "0x700989575bb2c2cafffdc3c4f583dccf904f90cb";
    //String storageContractAddress = "0xa061d41a9de8b2f317073cc331e616276c7fc37a80b0e05a7d0774c9cf956107";
    String testAccountPK = "b7942b268ade435dfc184a965035c878eb7c1814de09fcc384bf109edbf96108";
    String secondaryTestAddress = "0x79b306dFD6369B3Ce6E1c993891C4503c327B47e";

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
    public void protocolVersion() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().protocolVersion(new Function2<PocketError, String, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, String result) {
                        assertNotNull(result);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void syncing() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().syncing(new Function2<PocketError, ObjectOrBoolean, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, ObjectOrBoolean result) {
                        assertNotNull(result);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void nrgPrice() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().gasPrice(new Function2<PocketError, BigInteger, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, BigInteger result) {
                        assertNotNull(result);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void blockNumber() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().blockNumber(new Function2<PocketError, BigInteger, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, BigInteger result) {
                        assertNotNull(result);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void getBalance() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().getBalance(testAccountAddress, null, new Function2<PocketError, BigInteger, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, BigInteger result) {
                        assertNotNull(result);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void getStorageAt() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().getStorageAt(pocketTestContractAddress, new BigInteger("0"), null, new Function2<PocketError, String, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, String result) {
                        assertNotNull(result);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void getTransactionCount() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().getTransactionCount(testAccountAddress, null, new Function2<PocketError, BigInteger, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, BigInteger result) {
                        assertNotNull(result);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void getBlockTransactionCountByHash() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().getBlockTransactionCountByHash(blockHashHex, new Function2<PocketError, BigInteger, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, BigInteger result) {
                        assertNotNull(result);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void getBlockTransactionCountByNumber() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().getBlockTransactionCountByNumber(new BlockTag(blockNumber), new Function2<PocketError, BigInteger, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, BigInteger result) {
                        assertNotNull(result);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void getCode() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().getCode(pocketTestContractAddress, null, new Function2<PocketError, String, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, String result) {
                        assertNotNull(result);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void call() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                // Test data for calling into the multiply function of the PocketTest contract deployed to mastery
                pocketEth.getRinkeby().getEth().call(pocketTestContractAddress, null, null, null, null, null, "0x22e6d17f00000000000000000000000000000000000000000000000000000000000000640000000000000000000000000000000000000000000000000000000000000001000000000000000000000000700989575bb2c2cafffdc3c4f583dccf904f90cb00000000000000000000000000000000000000000000000000000000000000a00fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff000000000000000000000000000000000000000000000000000000000000000c48656c6c6f20576f726c64210000000000000000000000000000000000000000", new Function2<PocketError, String, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, String result) {
                        assertNotNull(result);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void estimateGas() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().estimateGas(pocketTestContractAddress, null, null, null, null, null, "0x22e6d17f00000000000000000000000000000000000000000000000000000000000000640000000000000000000000000000000000000000000000000000000000000001000000000000000000000000700989575bb2c2cafffdc3c4f583dccf904f90cb00000000000000000000000000000000000000000000000000000000000000a00fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff000000000000000000000000000000000000000000000000000000000000000c48656c6c6f20576f726c64210000000000000000000000000000000000000000", new Function2<PocketError, BigInteger, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, BigInteger result) {
                        assertNotNull(result);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void getBlockByHash() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().getBlockByHash(blockHashHex, false, new Function2<PocketError, JSONObject, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, JSONObject jsonObject) {
                        assertNotNull(jsonObject);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void getBlockByNumber() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().getBlockByNumber(null, false, new Function2<PocketError, JSONObject, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, JSONObject jsonObject) {
                        assertNotNull(jsonObject);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void getTransactionByHash() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().getTransactionByHash(testTxHashHex, new Function2<PocketError, JSONObject, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, JSONObject jsonObject) {
                        assertNotNull(jsonObject);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void getTransactionByBlockHashAndIndex() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().getTransactionByBlockHashAndIndex("0x5b84350472db150a2af8a16e808e9e13edc4d40acdf7847b9407a8188d2298cf", new BigInteger("0"), new Function2<PocketError, JSONObject, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, JSONObject jsonObject) {
                        assertNotNull(jsonObject);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void getTransactionByBlockNumberAndIndex() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().getTransactionByBlockNumberAndIndex(new BlockTag(new BigInteger("1565463")), new BigInteger("0"), new Function2<PocketError, JSONObject, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, JSONObject jsonObject) {
                        assertNotNull(jsonObject);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void getTransactionReceipt() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().getTransactionReceipt(testTxHashHex, new Function2<PocketError, JSONObject, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, JSONObject jsonObject) {
                        assertNotNull(jsonObject);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void getLogs() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketEth.getRinkeby().getEth().getLogs(null, null, new ArrayList<String>(), new ArrayList<String>(), blockHashHex, new Function2<PocketError, JSONArray, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, JSONArray jsonArray) {
                        assertNotNull(jsonArray);
                        assertNull(pocketError);
                        semaphore.release();
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void sendTransaction() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreUtil.SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                final Wallet wallet;
                wallet = pocketEth.importWallet(testAccountPK, null, PocketEth.Companion.getNETWORK(), PocketEth.Networks.RINKEBY.getNetID(), null);
                pocketEth.getRinkeby().getEth().getTransactionCount(wallet.getAddress(), null, new Function2<PocketError, BigInteger, Unit>() {
                    @Override
                    public Unit invoke(PocketError pocketError, BigInteger txCount) {
                        assertNotNull(txCount);
                        assertNull(pocketError);
                        pocketEth.getRinkeby().getEth().sendTransaction(wallet, secondaryTestAddress, new BigInteger("21000"), new BigInteger("10000000000"), new BigInteger("1000000000"), null, txCount, new Function2<PocketError, String, Unit>() {
                            @Override
                            public Unit invoke(PocketError pocketError, String txHash) {
                                // Result is the transaction hash
                                assertNotNull(txHash);
                                assertNull(pocketError);
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
}
