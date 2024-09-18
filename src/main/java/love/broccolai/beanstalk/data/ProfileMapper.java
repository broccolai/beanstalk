package love.broccolai.beanstalk.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;
import love.broccolai.beanstalk.model.profile.FlightStatus;
import love.broccolai.beanstalk.model.profile.Profile;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ProfileMapper implements RowMapper<Profile> {

    @Override
    public Profile map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        ColumnMapper<UUID> uuidMapper = ctx.findColumnMapperFor(UUID.class).orElseThrow(IllegalStateException::new);

        UUID uniqueId = uuidMapper.map(rs, "uuid", ctx);
        long flightRemaining = rs.getLong("flightRemaining");

        return new Profile(uniqueId, Duration.ofSeconds(flightRemaining), FlightStatus.DISABLED);
    }

}
