package dev.scx.app.x.sql;

import dev.scx.reflect.TypeInfo;
import dev.scx.serialize.ScxSerialize;
import dev.scx.sql.handler.TypeSQLHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/// ObjectSQLHandler
///
/// @author scx567888
final class ObjectSQLHandler implements TypeSQLHandler<Object> {

    private final TypeInfo typeInfo;

    public ObjectSQLHandler(TypeInfo typeInfo) {
        this.typeInfo = typeInfo;
    }

    @Override
    public TypeInfo valueType() {
        return typeInfo;
    }

    @Override
    public void bind(PreparedStatement ps, int i, Object value) throws SQLException {
        var json = ScxSerialize.toJson(value);
        ps.setString(i, json);
    }

    @Override
    public Object read(ResultSet rs, int i) throws SQLException {
        var json = rs.getString(i);
        if (json == null) {
            return json;
        }
        return ScxSerialize.fromJson(json, typeInfo);
    }

}
