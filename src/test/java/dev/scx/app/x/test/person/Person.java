package dev.scx.app.x.test.person;

import dev.scx.app.x.test.base.BaseEntity;
import dev.scx.data.sql.annotation.Table;

@Table("person")
public class Person extends BaseEntity {

    /// 关联的 汽车 ID
    public Long carID;

    /// 年龄
    public Integer age;

    /// 钱
    public Long money;

    public Person setMoney(long money) {
        this.money = money;
        return this;
    }

}
