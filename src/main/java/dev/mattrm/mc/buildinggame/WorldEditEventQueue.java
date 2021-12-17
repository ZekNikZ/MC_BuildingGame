package dev.mattrm.mc.buildinggame;

import dev.mattrm.mc.buildinggame.worldedit.WorldEditEvent;
import dev.mattrm.mc.gametools.Service;
import org.bukkit.Bukkit;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WorldEditEventQueue extends Service {
    private static final WorldEditEventQueue INSTANCE = new WorldEditEventQueue();

    public static WorldEditEventQueue getInstance() {
        return INSTANCE;
    }

    private final Queue<WorldEditEvent> queue = new ConcurrentLinkedQueue<>();

    @Override
    protected void setupService() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, this::applyChange, 0, 5);
    }

    public static void enqueue(WorldEditEvent event) {
        getInstance().enqueueEvent(event);
    }

    private void enqueueEvent(WorldEditEvent event) {
        this.queue.add(event);
    }

    private WorldEditEvent dequeue() {
        return this.queue.remove();
    }

    private void applyChange() {
        if (this.queue.isEmpty()) return;

        WorldEditEvent event = this.dequeue();
        event.apply();
        System.out.println("Applied " + event.getClass().getCanonicalName());
    }
}
