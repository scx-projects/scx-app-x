package dev.scx.app.x.test.person;

import dev.scx.app.x.component.Component;
import dev.scx.app.x.test.base.BaseEntityService;
import dev.scx.sql.SQLClient;

@Component
public class PersonService extends BaseEntityService<Person> {

    public PersonService(SQLClient sqlClient) {
        super(sqlClient);
    }

}
