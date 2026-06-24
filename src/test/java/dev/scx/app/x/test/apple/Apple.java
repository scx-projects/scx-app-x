package dev.scx.app.x.test.apple;

import dev.scx.app.x.test.base.BaseEntity;
import dev.scx.data.sql.annotation.Table;

@Table("apple")
public class Apple extends BaseEntity {

    /// 名称, 例如: 红富士
    public String name;

    /// 颜色, 例如: 红色 / 绿色 / 黄色
    public String color;

    /// 产地
    public String origin;

    /// 单价
    public Double price;

    /// 库存
    public Integer stock;

}
