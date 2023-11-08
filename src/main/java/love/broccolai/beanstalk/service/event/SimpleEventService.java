package love.broccolai.beanstalk.service.event;

import com.seiama.event.EventSubscriber;
import com.seiama.event.EventSubscription;
import com.seiama.event.bus.EventBus;
import com.seiama.event.bus.SimpleEventBus;
import com.seiama.event.registry.EventRegistry;
import com.seiama.event.registry.SimpleEventRegistry;
import love.broccolai.beanstalk.event.Event;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public class SimpleEventService implements EventService {

    private final EventRegistry<Event> registry;
    private final EventBus<Event> bus;

    public SimpleEventService() {
        this.registry = new SimpleEventRegistry<>(Event.class);
        this.bus = new SimpleEventBus<>(
            this.registry,
            this::handleException
        );
    }

    @Override
    public <E extends Event> void register(final Class<E> eventClass, final EventSubscriber<E> subscriber) {
        this.registry.subscribe(eventClass, subscriber);
    }

    @Override
    public void post(final Event event) {
        this.bus.post(event);
    }

    private <E> void handleException(EventSubscription<? super E> eventSubscription, E e, Throwable throwable) {
        throw new RuntimeException(throwable);
    }

}
