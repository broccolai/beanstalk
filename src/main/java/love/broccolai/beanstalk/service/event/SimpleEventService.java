package love.broccolai.beanstalk.service.event;

import com.google.inject.Singleton;
import com.sasorio.event.EventSubscriber;
import com.sasorio.event.EventSubscription;
import com.sasorio.event.bus.EventBus;
import com.sasorio.event.bus.SimpleEventBus;
import com.sasorio.event.registry.EventRegistry;
import com.sasorio.event.registry.SimpleEventRegistry;
import love.broccolai.beanstalk.event.Event;
import org.jspecify.annotations.NullMarked;

@Singleton
@NullMarked
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

    private <E> void handleException(
        final EventBus<? super E> bus,
        final EventSubscription<? super E> subscription,
        final E event,
        final Throwable throwable
    ) {
        throw new RuntimeException(throwable);
    }

}
