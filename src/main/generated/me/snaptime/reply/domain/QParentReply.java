package me.snaptime.reply.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QParentReply is a Querydsl query type for ParentReply
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QParentReply extends EntityPathBase<ParentReply> {

    private static final long serialVersionUID = -938448581L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QParentReply parentReply = new QParentReply("parentReply");

    public final me.snaptime.common.QBaseTimeEntity _super = new me.snaptime.common.QBaseTimeEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Long> parentReplyId = createNumber("parentReplyId", Long.class);

    public final me.snaptime.snap.domain.QSnap snap;

    public final me.snaptime.user.domain.QUser writer;

    public QParentReply(String variable) {
        this(ParentReply.class, forVariable(variable), INITS);
    }

    public QParentReply(Path<? extends ParentReply> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QParentReply(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QParentReply(PathMetadata metadata, PathInits inits) {
        this(ParentReply.class, metadata, inits);
    }

    public QParentReply(Class<? extends ParentReply> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.snap = inits.isInitialized("snap") ? new me.snaptime.snap.domain.QSnap(forProperty("snap"), inits.get("snap")) : null;
        this.writer = inits.isInitialized("writer") ? new me.snaptime.user.domain.QUser(forProperty("writer")) : null;
    }

}

