/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.common.mybatis;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;

/**
 *
 * @author cdd
 */
public abstract class ArrayTypeHandler<T> extends BaseTypeHandler<T[]> {

    @Override
    public T[] getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return this.getArray(rs.getObject(columnName));
    }

    @Override
    public T[] getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return this.getArray(rs.getObject(columnIndex));
    }

    @Override
    public T[] getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return this.getArray(cs.getObject(columnIndex));
    }

    abstract protected T[] getArray(Object columnValue) throws SQLException ;
}
