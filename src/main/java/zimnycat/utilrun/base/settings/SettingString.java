package zimnycat.utilrun.base.settings;

import zimnycat.utilrun.base.SettingBase;

public class SettingString extends SettingBase {
    public String value;

    public SettingString(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
