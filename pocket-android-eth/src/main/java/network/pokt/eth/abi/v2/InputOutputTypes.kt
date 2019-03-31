package network.pokt.eth.abi.v2

import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.*
import org.web3j.abi.datatypes.Int

class UintType : TypeReference<Uint>()
class IntType : TypeReference<Int>()
class AddressType : TypeReference<Address>()
class BoolType : TypeReference<Bool>()
class BytesType : TypeReference<Bytes>()
class DynamicBytesType : TypeReference<DynamicBytes>()
class UfixedType : TypeReference<Ufixed>()
class FixedType : TypeReference<Fixed>()
class StringType : TypeReference<Utf8String>()