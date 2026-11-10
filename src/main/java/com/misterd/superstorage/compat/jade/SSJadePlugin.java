package com.misterd.superstorage.compat.jade;

import com.misterd.superstorage.block.custom.DrumBlock;
import com.misterd.superstorage.blockentity.custom.DrumBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class SSJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(DrumDataProvider.INSTANCE, DrumBlockEntity.class);
        registration.registerFluidStorage(HiddenFluidStorageProvider.INSTANCE, DrumBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(DrumComponentProvider.INSTANCE, DrumBlock.class);
    }
}