package love.broccolai.beanstalk.commands.arguments;

import love.broccolai.beanstalk.commands.cloud.commander.Commander;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

@DefaultQualifier(NonNull.class)
public interface DescribedArgumentParser<T> extends
    ParserDescriptor<Commander, T>,
    ArgumentParser<Commander, T>,
    BlockingSuggestionProvider.Strings<Commander> {

    @Override
    default ArgumentParser<Commander, T> parser() {
        return this;
    }

}
