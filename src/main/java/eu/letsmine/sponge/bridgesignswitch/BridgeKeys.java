package eu.letsmine.sponge.bridgesignswitch;

import com.google.common.reflect.TypeToken;
import javax.annotation.Generated;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;

@Generated(value = "flavor.pie.generator.data.DataManipulatorGenerator", date = "2017-04-18T10:03:18.208Z")
public class BridgeKeys {

    private BridgeKeys() {}

    public final static Key<Value<Integer>> DISTANCE;
    public final static Key<Value<Boolean>> PRIMARY;
    public final static Key<Value<Boolean>> OPEN;
    static {
        TypeToken<Integer> integerToken = TypeToken.of(Integer.class);
        TypeToken<Value<Integer>> valueIntegerToken = new TypeToken<Value<Integer>>(){};
        TypeToken<Boolean> booleanToken = TypeToken.of(Boolean.class);
        TypeToken<Value<Boolean>> valueBooleanToken = new TypeToken<Value<Boolean>>(){};
        DISTANCE = KeyFactory.makeSingleKey(integerToken, valueIntegerToken, DataQuery.of("Distance"), "bridgesignswitch:distance", "Distance");
        PRIMARY = KeyFactory.makeSingleKey(booleanToken, valueBooleanToken, DataQuery.of("Primary"), "bridgesignswitch:primary", "Primary");
        OPEN = KeyFactory.makeSingleKey(booleanToken, valueBooleanToken, DataQuery.of("Open"), "bridgesignswitch:open", "Open");
    }
}
