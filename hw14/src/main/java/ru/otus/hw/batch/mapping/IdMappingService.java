package ru.otus.hw.batch.mapping;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdMappingService {

    private final JdbcTemplate jdbcTemplate;

    public void clearAll() {
        jdbcTemplate.update("delete from id_mapping");
    }

    public void save(EntityType type, long postgresId, String mongoId) {
        jdbcTemplate.update(
                "insert into id_mapping(entity_type, postgres_id, mongo_id) values (?, ?, ?)",
                type.name(), postgresId, mongoId
        );
    }

    public Optional<String> findMongoId(EntityType type, long postgresId) {
        return jdbcTemplate.query(
                "select mongo_id from id_mapping where entity_type = ? and postgres_id = ?",
                rs -> rs.next() ? Optional.ofNullable(rs.getString(1)) : Optional.empty(),
                type.name(), postgresId
        );
    }

    public Optional<Long> findPostgresId(EntityType type, String mongoId) {
        return jdbcTemplate.query(
                "select postgres_id from id_mapping where entity_type = ? and mongo_id = ?",
                rs -> rs.next() ? Optional.of(rs.getLong(1)) : Optional.empty(),
                type.name(), mongoId
        );
    }

    public String requireMongoId(EntityType type, long postgresId) {
        return findMongoId(type, postgresId)
                .orElseThrow(() -> new IllegalStateException("No Mongo id mapping for " + type
                        + " postgresId=" + postgresId));
    }

    public long requirePostgresId(EntityType type, String mongoId) {
        return findPostgresId(type, mongoId)
                .orElseThrow(() -> new IllegalStateException("No Postgres id mapping for "
                        + type + " mongoId=" + mongoId));
    }
}
