package zimnycat.utilrun.events;

import zimnycat.utilrun.base.EventBase;

public class KeyPressEvent extends EventBase {
    private final int key;

    public KeyPressEvent(int key) { this.key = key; }

    public int getKey() { return key; }
}
