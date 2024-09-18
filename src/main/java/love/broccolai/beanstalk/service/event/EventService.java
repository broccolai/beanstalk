package love.broccolai.beanstalk.service.event;

import com.seiama.event.EventSubscriber;
import love.broccolai.beanstalk.event.Event;
import love.broccolai.beanstalk.service.Service;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface EventService extends Service {

    <E extends Event> void register(Class<E> eventClass, EventSubscriber<E> subscriber);

    void post(Event event);

}
