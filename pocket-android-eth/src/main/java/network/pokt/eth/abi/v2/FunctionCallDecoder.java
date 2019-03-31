package network.pokt.eth.abi.v2;

import java.util.ArrayList;
import java.util.List;

import network.pokt.eth.exceptions.EthContractException;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Function;

public class FunctionCallDecoder {

    public static Object[] decodeCall(Function function, String data) throws EthContractException {
        List<Object> result = new ArrayList<>();
        try {
            List<Type> decodedOutputs = FunctionReturnDecoder.decode(data, function.getOutputParameters());
            for (Type currentOutput: decodedOutputs) {
                result.add(currentOutput.getValue());
            }
        } catch (Exception e) {
            throw new EthContractException(e.getMessage());
        }
        return result.toArray();
    }

}
