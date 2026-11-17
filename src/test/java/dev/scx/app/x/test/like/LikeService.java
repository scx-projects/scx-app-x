package dev.scx.app.x.test.like;

import dev.scx.app.x.component.Component;
import dev.scx.app.x.test.base.BaseEntityService;
import dev.scx.sql.SQLClient;

@Component
public class LikeService extends BaseEntityService<Like> {

    public LikeService(SQLClient sqlClient) {
        super(sqlClient);
    }

}
