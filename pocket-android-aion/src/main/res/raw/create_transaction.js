var txObj = {
    nonce: "%s",
    to: "%s",
    value: "%s",
    data: "%s",
    gas: "%s",
    gasPrice: "%s",
    type: 1
};

var txPromise = aionInstance.eth.accounts.signTransaction(txObj, "%s");