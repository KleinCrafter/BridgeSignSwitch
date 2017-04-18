package eu.letsmine.sponge.bridgesignswitch;

import java.util.Optional;
import javax.annotation.Generated;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;

@Generated(value = "flavor.pie.generator.data.DataManipulatorGenerator", date = "2017-04-18T10:03:18.173Z")
public class BridgeData extends AbstractData<BridgeData, BridgeData.Immutable> {

    private int distance;
    private boolean primary;
    private boolean open;

    {
        registerGettersAndSetters();
    }

    BridgeData() {
        distance = 0;
        primary = false;
        open = false;
    }

    BridgeData(int distance, boolean primary, boolean open) {
        this.distance = distance;
        this.primary = primary;
        this.open = open;
    }

    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(BridgeKeys.DISTANCE, this::getDistance);
        registerFieldSetter(BridgeKeys.DISTANCE, this::setDistance);
        registerKeyValue(BridgeKeys.DISTANCE, this::distance);
        registerFieldGetter(BridgeKeys.PRIMARY, this::isPrimary);
        registerFieldSetter(BridgeKeys.PRIMARY, this::setPrimary);
        registerKeyValue(BridgeKeys.PRIMARY, this::primary);
        registerFieldGetter(BridgeKeys.OPEN, this::isOpen);
        registerFieldSetter(BridgeKeys.OPEN, this::setOpen);
        registerKeyValue(BridgeKeys.OPEN, this::open);
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Value<Integer> distance() {
        return Sponge.getRegistry().getValueFactory().createValue(BridgeKeys.DISTANCE, distance);
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public Value<Boolean> primary() {
        return Sponge.getRegistry().getValueFactory().createValue(BridgeKeys.PRIMARY, primary);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Value<Boolean> open() {
        return Sponge.getRegistry().getValueFactory().createValue(BridgeKeys.OPEN, open);
    }

    @Override
    public Optional<BridgeData> fill(DataHolder dataHolder, MergeFunction overlap) {
        dataHolder.get(BridgeData.class).ifPresent(that -> {
                BridgeData data = overlap.merge(this, that);
                this.distance = data.distance;
                this.primary = data.primary;
                this.open = data.open;
        });
        return Optional.of(this);
    }

    @Override
    public Optional<BridgeData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<BridgeData> from(DataView container) {
        container.getInt(BridgeKeys.DISTANCE.getQuery()).ifPresent(v -> distance = v);
        container.getBoolean(BridgeKeys.PRIMARY.getQuery()).ifPresent(v -> primary = v);
        container.getBoolean(BridgeKeys.OPEN.getQuery()).ifPresent(v -> open = v);
        return Optional.of(this);
    }

    @Override
    public BridgeData copy() {
        return new BridgeData(distance, primary, open);
    }

    @Override
    public Immutable asImmutable() {
        return new Immutable(distance, primary, open);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(BridgeKeys.DISTANCE.getQuery(), distance)
                .set(BridgeKeys.PRIMARY.getQuery(), primary)
                .set(BridgeKeys.OPEN.getQuery(), open);
    }

    @Generated(value = "flavor.pie.generator.data.DataManipulatorGenerator", date = "2017-04-18T10:03:18.201Z")
    public static class Immutable extends AbstractImmutableData<Immutable, BridgeData> {

        private int distance;
        private boolean primary;
        private boolean open;
        {
            registerGetters();
        }

        Immutable() {
            distance = 0;
            primary = false;
            open = false;
        }

        Immutable(int distance, boolean primary, boolean open) {
            this.distance = distance;
            this.primary = primary;
            this.open = open;
        }

        @Override
        protected void registerGetters() {
            registerFieldGetter(BridgeKeys.DISTANCE, this::getDistance);
            registerKeyValue(BridgeKeys.DISTANCE, this::distance);
            registerFieldGetter(BridgeKeys.PRIMARY, this::isPrimary);
            registerKeyValue(BridgeKeys.PRIMARY, this::primary);
            registerFieldGetter(BridgeKeys.OPEN, this::isOpen);
            registerKeyValue(BridgeKeys.OPEN, this::open);
        }

        public int getDistance() {
            return distance;
        }

        public ImmutableValue<Integer> distance() {
            return Sponge.getRegistry().getValueFactory().createValue(BridgeKeys.DISTANCE, distance).asImmutable();
        }

        public boolean isPrimary() {
            return primary;
        }

        public ImmutableValue<Boolean> primary() {
            return Sponge.getRegistry().getValueFactory().createValue(BridgeKeys.PRIMARY, primary).asImmutable();
        }

        public boolean isOpen() {
            return open;
        }

        public ImmutableValue<Boolean> open() {
            return Sponge.getRegistry().getValueFactory().createValue(BridgeKeys.OPEN, open).asImmutable();
        }

        @Override
        public BridgeData asMutable() {
            return new BridgeData(distance, primary, open);
        }

        @Override
        public int getContentVersion() {
            return 1;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer()
                    .set(BridgeKeys.DISTANCE.getQuery(), distance)
                    .set(BridgeKeys.PRIMARY.getQuery(), primary)
                    .set(BridgeKeys.OPEN.getQuery(), open);
        }

    }

    @Generated(value = "flavor.pie.generator.data.DataManipulatorGenerator", date = "2017-04-18T10:03:18.205Z")
    public static class Builder extends AbstractDataBuilder<BridgeData> implements DataManipulatorBuilder<BridgeData, Immutable> {

        protected Builder() {
            super(BridgeData.class, 1);
        }

        @Override
        public BridgeData create() {
            return new BridgeData();
        }

        @Override
        public Optional<BridgeData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        protected Optional<BridgeData> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }

    }
}
