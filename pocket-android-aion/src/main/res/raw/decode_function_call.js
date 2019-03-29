var returnValue = "%s";
var outputs = %s;

var decodedValue = new aionInstance.eth.Contract([])._decodeMethodReturn(outputs, returnValue);
if (typeof decodedValue === "object" && !Array.isArray(decodedValue)) {
    decodedValue = Object.keys(decodedValue).map(key => decodedValue[key]);
}