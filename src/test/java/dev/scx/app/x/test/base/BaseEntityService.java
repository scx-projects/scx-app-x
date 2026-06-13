package dev.scx.app.x.test.base;

import dev.scx.data.Aggregator;
import dev.scx.data.Finder;
import dev.scx.data.aggregation.Aggregation;
import dev.scx.data.field_policy.FieldPolicy;
import dev.scx.data.query.Query;
import dev.scx.data.sql.SQLRepository;
import dev.scx.data.sql.schema_mapping.EntityTable;
import dev.scx.reflect.ClassInfo;
import dev.scx.reflect.ScxReflect;
import dev.scx.sql.SQL;
import dev.scx.sql.SQLClient;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static dev.scx.data.field_policy.FieldPolicyBuilder.includeAll;
import static dev.scx.data.query.QueryBuilder.*;

/// BaseEntityService
///
/// @param <Entity> 继承自 [BaseEntity] 的实体类型
/// @author scx567888
/// @version 0.0.1
public class BaseEntityService<Entity extends BaseEntity> {

    private final SQLRepository<Entity> repository;

    /// 从泛型中获取 entityClass
    public BaseEntityService(SQLClient sqlClient) {
        Class<Entity> entityClass = findEntityClass(this.getClass());
        this.repository = new SQLRepository<>(entityClass, sqlClient);
    }

    /// 手动设置 entityClass
    public BaseEntityService(Class<Entity> entityClass, SQLClient sqlClient) {
        this.repository = new SQLRepository<>(entityClass, sqlClient);
    }

    @SuppressWarnings("unchecked")
    private static <Entity extends BaseEntity> Class<Entity> findEntityClass(Class<?> baseEntityServiceClass) {
        // 这里可以安全强转为 ClassInfo 因为 this.getClass 必然是一个 普通 class.
        var superClass = ((ClassInfo) ScxReflect.typeOf(baseEntityServiceClass)).findSuperType(BaseEntityService.class);
        // 这里 superClass 不可能是 null
        var boundType = superClass.bindings().get(0);
        if (boundType == null) {
            throw new IllegalArgumentException(baseEntityServiceClass.getName() + " : 必须设置泛型参数 !!!");
        }
        return (Class<Entity>) boundType.rawClass();
    }

    /// 处理 updateFilter  使在插入或更新数据时永远过滤 "id", "createdDate", "updatedDate" 三个字段
    private static FieldPolicy updateFilterProcessor(FieldPolicy updateFilter) {
        return updateFilter.exclude("id", "createdDate", "updatedDate");
    }

    public final Entity add(Entity entity) {
        return add(entity, includeAll());
    }

    public final Entity add(FieldPolicy updateFilter) {
        return add((Entity) null, updateFilter);
    }

    public Entity add(Entity entity, FieldPolicy updateFilter) {
        var newID = repository.add(entity, updateFilterProcessor(updateFilter));
        return newID != null ? this.get(newID) : null;
    }

    public final List<Long> add(Collection<Entity> entityList) {
        return add(entityList, includeAll());
    }

    public List<Long> add(Collection<Entity> entityList, FieldPolicy updateFilter) {
        return repository.add(entityList, updateFilterProcessor(updateFilter));
    }

    public final List<Entity> find() {
        return find(query(), includeAll());
    }

    public final List<Entity> find(FieldPolicy selectFilter) {
        return find(query(), selectFilter);
    }

    public final List<Entity> find(Query query) {
        return find(query, includeAll());
    }

    public List<Entity> find(Query query, FieldPolicy selectFilter) {
        return repository.find(query, selectFilter);
    }

    public final List<Entity> find(long... ids) {
        return find(ids.length == 1 ? eq("id", ids[0]) : in("id", ids));
    }

    public final Entity get(long id) {
        return get(id, includeAll());
    }

    public final Entity get(long id, FieldPolicy selectFilter) {
        return get(eq("id", id), selectFilter);
    }

    public final Entity get(Query query) {
        return get(query, includeAll());
    }

    public Entity get(Query query, FieldPolicy selectFilter) {
        return repository.findFirst(query, selectFilter);
    }

    public final Finder<Entity> finder() {
        return finder(query(), includeAll());
    }

    public final Finder<Entity> finder(FieldPolicy selectFilter) {
        return finder(query(), selectFilter);
    }

    public final Finder<Entity> finder(Query query) {
        return finder(query, includeAll());
    }

    public Finder<Entity> finder(Query query, FieldPolicy selectFilter) {
        return repository.finder(query, selectFilter);
    }

    public final Entity update(Entity entity) {
        return update(entity, includeAll());
    }

    public final Entity update(Entity entity, FieldPolicy updateFilter) {
        if (entity.id == null) {
            throw new RuntimeException("根据 id 更新时 id 不能为空");
        }
        this.update(entity, updateFilter, eq("id", entity.id));
        return this.get(entity.id);
    }

    public final long update(Entity entity, Query query) {
        return update(entity, includeAll(), query);
    }

    public final long update(FieldPolicy updateFilter, Query query) {
        return update(null, updateFilter, query);
    }

    public long update(Entity entity, FieldPolicy updateFilter, Query query) {
        return repository.update(entity, updateFilterProcessor(updateFilter), query);
    }

    public final long delete(long... ids) {
        if (ids.length == 0) {
            throw new IllegalArgumentException("待删除的 ids 数量至少为 1 个");
        }
        return delete(ids.length == 1 ? eq("id", ids[0]) : in("id", ids));
    }

    public long delete(Query query) {
        return repository.delete(query);
    }

    public final long count() {
        return repository.count();
    }

    public final long count(Query query) {
        return repository.count(query);
    }

    public final Aggregator aggregator(Query beforeAggregateQuery, Aggregation aggregation, Query afterAggregateQuery) {
        return repository.aggregator(beforeAggregateQuery, aggregation, afterAggregateQuery);
    }

    public final Aggregator aggregator(Aggregation aggregation, Query afterAggregateQuery) {
        return aggregator(query(), aggregation, afterAggregateQuery);
    }

    public final Aggregator aggregator(Query beforeAggregateQuery, Aggregation aggregation) {
        return aggregator(beforeAggregateQuery, aggregation, query());
    }

    public final Aggregator aggregator(Aggregation aggregation) {
        return aggregator(query(), aggregation, query());
    }

    public final List<Map<String, Object>> aggregate(Query beforeAggregateQuery, Aggregation aggregation, Query afterAggregateQuery) {
        return aggregator(beforeAggregateQuery, aggregation, afterAggregateQuery).list();
    }

    public final List<Map<String, Object>> aggregate(Aggregation aggregation, Query afterAggregateQuery) {
        return aggregator(aggregation, afterAggregateQuery).list();
    }

    public final List<Map<String, Object>> aggregate(Query beforeAggregateQuery, Aggregation aggregation) {
        return aggregator(beforeAggregateQuery, aggregation).list();
    }

    public final List<Map<String, Object>> aggregate(Aggregation aggregation) {
        return aggregator(aggregation).list();
    }

    public final Map<String, Object> aggregateFirst(Query beforeAggregateQuery, Aggregation aggregation, Query afterAggregateQuery) {
        return aggregator(beforeAggregateQuery, aggregation, afterAggregateQuery).first();
    }

    public final Map<String, Object> aggregateFirst(Aggregation aggregation, Query afterAggregateQuery) {
        return aggregator(aggregation, afterAggregateQuery).first();
    }

    public final Map<String, Object> aggregateFirst(Query beforeAggregateQuery, Aggregation aggregation) {
        return aggregator(beforeAggregateQuery, aggregation).first();
    }

    public final Map<String, Object> aggregateFirst(Aggregation aggregation) {
        return aggregator(aggregation).first();
    }

    // *********************** 便捷代理方法 *************************

    public final SQLRepository<Entity> repository() {
        return repository;
    }

    public final Class<Entity> entityClass() {
        return repository.entityClass();
    }

    public final EntityTable<Entity> table() {
        return repository.table();
    }

    public final SQLClient sqlClient() {
        return repository.sqlClient();
    }

    public final SQL buildListSQL(Query query, FieldPolicy selectFilter) {
        return repository.buildSelectSQL(query, selectFilter);
    }

    public final SQL buildGetSQL(Query query, FieldPolicy selectFilter) {
        return repository.buildSelectFirstSQL(query, selectFilter);
    }

}
