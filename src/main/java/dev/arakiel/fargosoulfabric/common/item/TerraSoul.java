package dev.arakiel.fargosoulfabric.common.item;

import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;

import java.util.Set;

import static dev.arakiel.fargosoulfabric.register.FargoSoulItemRegister.*;

public class TerraSoul extends SoulItem {

    public TerraSoul(Properties properties) {
        super(properties);
    }

    @Override
    public Set<SoulItem> getSoulItemList() {
        return Set.of(CosmicPowerItem, DeathPowerItem, EarthPowerItem, ForestPowerItem, LifePowerItem, NaturePowerItem, SpiritPowerItem, TerraPowerItem, WillPowerItem);
    }
}