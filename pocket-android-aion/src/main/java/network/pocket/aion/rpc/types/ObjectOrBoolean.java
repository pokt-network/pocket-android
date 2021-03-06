package network.pocket.aion.rpc.types;

import org.json.JSONObject;

/**
 * Object or Boolean Parameter
 */
public class ObjectOrBoolean {

    Boolean booleanValue;
    JSONObject objectValue;

    public ObjectOrBoolean(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public ObjectOrBoolean(JSONObject objectValue) {
        this.objectValue = objectValue;
    }

    public boolean isBoolean() {
        return this.booleanValue != null;
    }

    public boolean isObject() {
        return this.objectValue != null;
    }

}
