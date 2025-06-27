package com.mehmetozanguven.zad_demo_case.core.entity;

import java.time.OffsetDateTime;

public interface Auditable {
    Long getCreateTimestampMilli();

    void setCreateTimestampMilli(long timestamp);

    OffsetDateTime getCreateUTCTime();

    void setCreateUTCTime(OffsetDateTime createTime);

    OffsetDateTime getLastModifiedUTCTime();

    void setLastModifiedUTCTime(OffsetDateTime lastModifiedTime);

    Long getLastModifiedTimestampMilli();

    void setLastModifiedTimestampMilli(long timestamp);
}
