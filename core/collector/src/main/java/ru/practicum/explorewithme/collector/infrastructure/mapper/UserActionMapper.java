package ru.practicum.explorewithme.collector.infrastructure.mapper;

import com.google.protobuf.Timestamp;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.ewm.stats.grpc.ActionTypeProto;
import ru.practicum.ewm.stats.grpc.UserActionProto;
import ru.practicum.ewm.stats.kafka.ActionTypeAvro;
import ru.practicum.ewm.stats.kafka.UserActionAvro;

@Mapper(componentModel = "spring")
public interface UserActionMapper {

    @Mapping(source = "timestamp", target = "timestamp", qualifiedByName = "toInstant")
    @Mapping(source = "actionType", target = "actionType", qualifiedByName = "toActionType")
    UserActionAvro toAvro(UserActionProto userActionProto);

    @Named("toActionType")
    default ActionTypeAvro toAvroActionType(ActionTypeProto actionTypeProto) {
        if (actionTypeProto == null) {
            throw new IllegalArgumentException("ActionTypeProto cannot be null.");
        }

        return switch (actionTypeProto) {
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            case UNRECOGNIZED ->
                throw new IllegalArgumentException("Cannot map unknown or unrecognized ActionTypeProto: " + actionTypeProto);
        };
    }

    @Named("toInstant")
    default Instant toInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}

