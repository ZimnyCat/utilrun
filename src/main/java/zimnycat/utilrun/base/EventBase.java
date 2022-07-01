package zimnycat.utilrun.base;

public class EventBase {
    private boolean cancelled = false;

    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    public boolean isCancelled() { return cancelled; }
}
