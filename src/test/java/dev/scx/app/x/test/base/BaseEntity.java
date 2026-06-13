package dev.scx.app.x.test.base;

import dev.scx.data.sql.annotation.Column;

import java.time.LocalDateTime;

/// BaseEntity
///
/// @author scx567888
/// @version 0.0.1
public abstract class BaseEntity {

    /// id
    @Column(primary = true, autoIncrement = true)
    public Long id;

    /// 创建时间
    @Column(notNull = true, defaultValue = "(NOW())", index = true)
    public LocalDateTime createdDate;

    /// 最后修改时间
    @Column(notNull = true, defaultValue = "(NOW())", onUpdate = "CURRENT_TIMESTAMP", index = true)
    public LocalDateTime updatedDate;

}
