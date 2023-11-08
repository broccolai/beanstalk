package love.broccolai.beanstalk.service.event;

import com.seiama.event.EventSubscriber;
import love.broccolai.beanstalk.event.Event;
import love.broccolai.beanstalk.service.Service;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface EventService extends Service {

    <E extends Event> void register(Class<E> eventClass, EventSubscriber<E> subscriber);

    void post(Event event);

}
