package network.pokt.aion;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import network.pokt.aion.rpc.types.BlockTag;
import network.pokt.aion.rpc.types.ObjectOrBoolean;
import network.pokt.aion.util.SemaphoreUtil;
import network.pokt.aion.util.SemaphoreUtil.SemaphoreCallback;
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

    PocketAion pocketAion;
    String testAccountAddress = "0xa05b88ac239f20ba0a4d2f0edac8c44293e9b36fa937fb55bf7a1cd61a60f036";
    String testTxHashHex = "0xab24681fc474b4a6cbc9489a7595634abbfcd1ef205e1807df53fc70619496bd";
    String blockHashHex = "0x1ab636692ebfaf9a181d4671e0f1f3d3bc8bd9a9ec91c8a19dcbcc06a9975390";
    BigInteger blockNumber = new BigInteger("1565463");
    String pocketTestContractAddress = "0xA0707404B9BE7a5F630fCed3763d28FA5C988964fDC25Aa621161657a7Bf4b89";
    String storageContractAddress = "0xa061d41a9de8b2f317073cc331e616276c7fc37a80b0e05a7d0774c9cf956107";
    String testAccountPK = "0x2b5d6fd899ccc148b5f85b4ea20961678c04d70055b09dac7857ea430757e6badb4cfe129e670e2fef1b632ed0eab9572954feebbea9cb32134b284763acd34e";
    String secondaryTestAddress = "0xa07743f4170ded07da3ccd2ad926f9e684a5f61e90d018a3c5d8ea60a8b3406a";

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
    public void protocolVersion() {
        SemaphoreUtil.executeSemaphoreCallback(new SemaphoreCallback() {
            @Override
            public void execute(final Semaphore semaphore) {
                pocketAion.getMastery().getEth().protocolVersion(new Function2<PocketError, String, Unit>() {
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
                pocketAion.getMastery().getEth().syncing(new Function2<PocketError, ObjectOrBoolean, Unit>() {
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
                pocketAion.getMastery().getEth().nrgPrice(new Function2<PocketError, BigInteger, Unit>() {
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
                pocketAion.getMastery().getEth().blockNumber(new Function2<PocketError, BigInteger, Unit>() {
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
                pocketAion.getMastery().getEth().getBalance(testAccountAddress, null, new Function2<PocketError, BigInteger, Unit>() {
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
                pocketAion.getMastery().getEth().getStorageAt(storageContractAddress, new BigInteger("0"), null, new Function2<PocketError, String, Unit>() {
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
                pocketAion.getMastery().getEth().getTransactionCount(testAccountAddress, null, new Function2<PocketError, BigInteger, Unit>() {
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
                pocketAion.getMastery().getEth().getBlockTransactionCountByHash(blockHashHex, new Function2<PocketError, BigInteger, Unit>() {
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
                pocketAion.getMastery().getEth().getBlockTransactionCountByNumber(new BlockTag(blockNumber), new Function2<PocketError, BigInteger, Unit>() {
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
                pocketAion.getMastery().getEth().getCode(pocketTestContractAddress, null, new Function2<PocketError, String, Unit>() {
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
                pocketAion.getMastery().getEth().call(pocketTestContractAddress, null, null, null, null, null, "0xbbaa0820000000000000000000000000000000020000000000000000000000000000000a", new Function2<PocketError, String, Unit>() {
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
                pocketAion.getMastery().getEth().estimateGas(pocketTestContractAddress, null, null, null, null, null, "0xbbaa0820000000000000000000000000000000020000000000000000000000000000000a", new Function2<PocketError, BigInteger, Unit>() {
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
                pocketAion.getMastery().getEth().getBlockByHash(blockHashHex, false, new Function2<PocketError, JSONObject, Unit>() {
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
                pocketAion.getMastery().getEth().getBlockByNumber(null, false, new Function2<PocketError, JSONObject, Unit>() {
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
                pocketAion.getMastery().getEth().getTransactionByHash(testTxHashHex, new Function2<PocketError, JSONObject, Unit>() {
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
                pocketAion.getMastery().getEth().getTransactionByBlockHashAndIndex("0x1ab636692ebfaf9a181d4671e0f1f3d3bc8bd9a9ec91c8a19dcbcc06a9975390", new BigInteger("0"), new Function2<PocketError, JSONObject, Unit>() {
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
                pocketAion.getMastery().getEth().getTransactionByBlockNumberAndIndex(new BlockTag(new BigInteger("1565463")), new BigInteger("0"), new Function2<PocketError, JSONObject, Unit>() {
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
                pocketAion.getMastery().getEth().getTransactionReceipt(testTxHashHex, new Function2<PocketError, JSONObject, Unit>() {
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
                pocketAion.getMastery().getEth().getLogs(null, null, new ArrayList<String>(), new ArrayList<String>(), blockHashHex, new Function2<PocketError, JSONArray, Unit>() {
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
                try {

                    wallet = pocketAion.importWallet(testAccountPK, null, PocketAion.Companion.getNETWORK(), PocketAion.Networks.MASTERY.getNetID(), null);

                    pocketAion.getMastery().getEth().getTransactionCount(wallet.getAddress(), null, new Function2<PocketError, BigInteger, Unit>() {
                        @Override
                        public Unit invoke(PocketError pocketError, BigInteger txCount) {
                            assertNotNull(txCount);
                            assertNull(pocketError);

                            try {
                                pocketAion.getMastery().getEth().sendTransaction(wallet, secondaryTestAddress, new BigInteger("21000"), new BigInteger("10000000000"), new BigInteger("1000000000"), null, txCount, new Function2<PocketError, String, Unit>() {
                                    @Override
                                    public Unit invoke(PocketError pocketError, String txHash) {
                                        // Result is the transaction hash
                                        assertNotNull(txHash);
                                        assertNull(pocketError);
                                        semaphore.release();
                                        return null;
                                    }
                                });
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                semaphore.release();
                            }
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
}
