package com.github.hiteshsondhi88.libffmpeg;

import android.os.Build;

import com.github.hiteshsondhi88.libffmpeg.cpuhelper.CpuArch;
import com.github.hiteshsondhi88.libffmpeg.cpuhelper.CpuArchHelper;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

public class CpuArchHelperTest extends TestCase {

    public void testGetCpuArch() throws Exception {
        CpuArch cpuArch = CpuArchHelper.getCpuArch();
        assertNotNull(cpuArch);
        if (Build.CPU_ABI.equals(CpuArchHelper.getx86CpuAbi())) {
            assertEquals(cpuArch, CpuArch.x86);
        } else if (Build.CPU_ABI.equals(CpuArchHelper.getArmeabiv7CpuAbi())) {
            assertThat(cpuArch == CpuArch.ARMv7 || cpuArch == CpuArch.ARMv7_NEON).isTrue();
        } else {
            assertEquals(cpuArch, CpuArch.NONE);
        }
    }

}