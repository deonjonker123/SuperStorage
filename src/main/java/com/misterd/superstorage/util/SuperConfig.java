package com.misterd.superstorage.util;

import com.misterd.superstorage.component.SuperSettings;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Arrays;
import java.util.List;

public class SuperConfig {
    private static final String SETTINGS_KEY = "settings";

    private boolean locked;
    private boolean voidOverflow;
    private boolean capabilitiesEnabled = true;
    private final SideMode[] sides = new SideMode[RelativeSide.values().length];

    public SuperConfig() {
        Arrays.fill(sides, SideMode.NONE);
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isVoidOverflow() {
        return voidOverflow;
    }

    public void setVoidOverflow(boolean voidOverflow) {
        this.voidOverflow = voidOverflow;
    }

    public boolean isCapabilitiesEnabled() {
        return capabilitiesEnabled;
    }

    public void setCapabilitiesEnabled(boolean capabilitiesEnabled) {
        this.capabilitiesEnabled = capabilitiesEnabled;
    }

    public SideMode getSide(RelativeSide side) {
        return sides[side.ordinal()];
    }

    public void setSide(RelativeSide side, SideMode mode) {
        sides[side.ordinal()] = mode;
    }

    public SuperSettings toSettings() {
        return new SuperSettings(locked, voidOverflow, capabilitiesEnabled, List.of(sides));
    }

    public void fromSettings(SuperSettings settings) {
        this.locked = settings.locked();
        this.voidOverflow = settings.voidOverflow();
        this.capabilitiesEnabled = settings.capabilitiesEnabled();
        for (int i = 0; i < sides.length && i < settings.sides().size(); i++) {
            sides[i] = settings.sides().get(i);
        }
    }

    public boolean isDefault() {
        return toSettings().equals(SuperSettings.DEFAULT);
    }

    public void save(ValueOutput output) {
        output.store(SETTINGS_KEY, SuperSettings.CODEC, toSettings());
    }

    public void load(ValueInput input) {
        fromSettings(input.read(SETTINGS_KEY, SuperSettings.CODEC).orElse(SuperSettings.DEFAULT));
    }
}