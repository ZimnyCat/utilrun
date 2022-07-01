package zimnycat.utilrun.base.settings;

import zimnycat.utilrun.base.SettingBase;

public class SettingBool extends SettingBase {
    public boolean value;

    public SettingBool(String name, boolean value) {
        this.name = name;
        this.value = value;
    }
}
