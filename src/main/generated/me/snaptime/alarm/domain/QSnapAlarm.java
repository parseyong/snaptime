package me.snaptime.alarm.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSnapAlarm is a Querydsl query type for SnapAlarm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSnapAlarm extends EntityPathBase<SnapAlarm> {

    private static final long serialVersionUID = -1738323013L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSnapAlarm snapAlarm = new QSnapAlarm("snapAlarm");

    public final me.snaptime.common.QBaseTimeEntity _super = new me.snaptime.common.QBaseTimeEntity(this);

    public final EnumPath<me.snaptime.alarm.enums.AlarmType> alarmType = createEnum("alarmType", me.snaptime.alarm.enums.AlarmType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final BooleanPath isRead = createBoolean("isRead");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final me.snaptime.user.domain.QUser receiver;

    public final me.snaptime.user.domain.QUser sender;

    public final me.snaptime.snap.domain.QSnap snap;

    public final NumberPath<Long> snapAlarmId = createNumber("snapAlarmId", Long.class);

    public QSnapAlarm(String variable) {
        this(SnapAlarm.class, forVariable(variable), INITS);
    }

    public QSnapAlarm(Path<? extends SnapAlarm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSnapAlarm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSnapAlarm(PathMetadata metadata, PathInits inits) {
        this(SnapAlarm.class, metadata, inits);
    }

    public QSnapAlarm(Class<? extends SnapAlarm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.receiver = inits.isInitialized("receiver") ? new me.snaptime.user.domain.QUser(forProperty("receiver")) : null;
        this.sender = inits.isInitialized("sender") ? new me.snaptime.user.domain.QUser(forProperty("sender")) : null;
        this.snap = inits.isInitialized("snap") ? new me.snaptime.snap.domain.QSnap(forProperty("snap"), inits.get("snap")) : null;
    }

}

