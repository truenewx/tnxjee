package org.truenewx.tnxjee.model.spec;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ArrayUtil;
import org.truenewx.tnxjee.model.spec.enums.Device;
import org.truenewx.tnxjee.model.spec.enums.OS;
import org.truenewx.tnxjee.model.spec.enums.Program;

/**
 * 终端类型
 *
 * @author liuzhiyi
 */
public class Terminal {

    /**
     * 设备类型
     */
    private Device device;

    /**
     * 程序类型
     */
    private Program program;

    /**
     * 操作系统
     */
    private OS os;

    public Terminal(Device device, Program program, OS os) {
        this.device = device;
        this.program = program;
        this.os = os;
    }

    public static Terminal of(String s) {
        Device device = null;
        Program program = null;
        OS os = null;
        // 允许空的终端表示
        if (StringUtils.isNotBlank(s)) {
            String[] array = s.split(Strings.MINUS);
            device = EnumUtils.getEnum(Device.class, ArrayUtil.get(array, 0));
            program = EnumUtils.getEnum(Program.class, ArrayUtil.get(array, 1));
            os = EnumUtils.getEnum(OS.class, ArrayUtil.get(array, 2));
        }
        return new Terminal(device, program, os);
    }

    /**
     * @return 设备类型
     */
    public Device getDevice() {
        return this.device;
    }

    /**
     * @return 程序类型
     */
    public Program getProgram() {
        return this.program;
    }

    /**
     * @return 操作系统
     */
    public OS getOs() {
        return this.os;
    }

    public boolean supports(Terminal terminal) {
        // 设备属性为null，视为支持所有设备
        if (this.device != null && this.device != terminal.device) {
            return false;
        }
        // 程序类型属性为null，视为支持所有程序类型
        if (this.program != null && this.program != terminal.program) {
            return false;
        }
        // 操作系统属性为null，视为支持所有操作系统
        if (this.os != null && this.os != terminal.os) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (this.device != null) {
            sb.append(this.device.name());
        }
        sb.append(Strings.MINUS);
        if (this.program != null) {
            sb.append(this.program.name());
        }
        sb.append(Strings.MINUS);
        if (this.os != null) {
            sb.append(this.os.name());
        }
        return sb.toString();
    }

}
