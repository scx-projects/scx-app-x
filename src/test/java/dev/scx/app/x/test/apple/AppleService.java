package dev.scx.app.x.test.apple;

import dev.scx.app.x.component.Component;
import dev.scx.app.x.test.base.BaseEntityService;
import dev.scx.sql.SQLClient;

@Component
public class AppleService extends BaseEntityService<Apple> {

    public AppleService(SQLClient sqlClient) {
        super(sqlClient);
    }

}
