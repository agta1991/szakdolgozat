package com.github.hiteshsondhi88.libffmpeg;

public class ArmArchHelper {
    static {
        System.loadLibrary("ARM_ARCH");
    }

    public native String cpuArchFromJNI();

    public boolean isARM_v7_CPU(String cpuInfoString) {
        return cpuInfoString.contains("v7");
    }

    public boolean isNeonSupported(String cpuInfoString) {
        // check cpu arch for loading correct ffmpeg lib
        return cpuInfoString.contains("-neon");
    }

}