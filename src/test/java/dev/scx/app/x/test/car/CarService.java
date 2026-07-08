package dev.scx.app.x.test.car;

import dev.scx.app.x.component.Component;
import dev.scx.app.x.test.base.BaseEntityService;
import dev.scx.sql.SQLClient;

@Component
public class CarService extends BaseEntityService<Car> {

    public CarService(SQLClient sqlClient) {
        super(sqlClient);
    }

}
