package info.faceland.loot.api.events;

import org.bukkit.event.Cancellable;

public class LootCancellableEvent extends LootEvent implements Cancellable {

    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
