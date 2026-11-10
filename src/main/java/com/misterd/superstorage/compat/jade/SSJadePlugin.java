package com.misterd.superstorage.compat.jade;

import com.misterd.superstorage.block.custom.DrumBlock;
import com.misterd.superstorage.block.custom.SuperChestBlock;
import com.misterd.superstorage.block.custom.SuperTankBlock;
import com.misterd.superstorage.blockentity.custom.DrumBlockEntity;
import com.misterd.superstorage.blockentity.custom.SuperTankBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class SSJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(DrumDataProvider.INSTANCE, DrumBlockEntity.class);
        registration.registerBlockDataProvider(SuperTankDataProvider.INSTANCE, SuperTankBlockEntity.class);
        registration.registerFluidStorage(HiddenFluidStorageProvider.INSTANCE, DrumBlockEntity.class);
        registration.registerFluidStorage(HiddenFluidStorageProvider.INSTANCE, SuperTankBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(DrumComponentProvider.INSTANCE, DrumBlock.class);
        registration.registerBlockComponent(SuperTankComponentProvider.INSTANCE, SuperTankBlock.class);
        registration.registerBlockComponent(SuperConfigComponentProvider.INSTANCE, SuperChestBlock.class);
        registration.registerBlockComponent(SuperConfigComponentProvider.INSTANCE, SuperTankBlock.class);
    }
}