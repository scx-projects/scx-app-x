package dev.scx.app.x.sql;

import dev.scx.reflect.TypeInfo;
import dev.scx.sql.handler.TypeSQLHandler;
import dev.scx.sql.handler.TypeSQLHandlerFactory;

/// ObjectSQLHandlerFactory
///
/// @author scx567888
final class ObjectSQLHandlerFactory implements TypeSQLHandlerFactory {

    @Override
    public TypeSQLHandler<?> createHandler(TypeInfo typeInfo) {
        return new ObjectSQLHandler(typeInfo);
    }

}
