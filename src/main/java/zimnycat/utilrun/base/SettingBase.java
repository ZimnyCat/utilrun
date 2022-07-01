package zimnycat.utilrun.base;

import zimnycat.utilrun.base.settings.SettingBool;
import zimnycat.utilrun.base.settings.SettingNum;
import zimnycat.utilrun.base.settings.SettingString;

public class SettingBase {
    public String name;

    public SettingNum num() { return (SettingNum) this; }

    public SettingString string() { return (SettingString) this; }

    public SettingBool bool() { return (SettingBool) this; }
}
