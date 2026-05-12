package com.AAZl3l4.common.configuration;

import com.AAZl3l4.common.utils.AesEncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis AES加密TypeHandler
 * 用于自动对数据库字段进行AES加解密
 * 适用于String类型的敏感字段
 */
@Slf4j
@MappedTypes(String.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class AesEncryptTypeHandler extends BaseTypeHandler<String> {

    /**
     * 插入数据时加密
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        try {
            String encrypted = AesEncryptUtil.encrypt(parameter);
            ps.setString(i, encrypted);
        } catch (Exception e) {
            log.error("字段加密失败，使用原始值", e);
            ps.setString(i, parameter);
        }
    }

    /**
     * 根据列名获取数据时解密
     */
    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return decryptValue(value);
    }

    /**
     * 根据列索引获取数据时解密
     */
    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return decryptValue(value);
    }

    /**
     * 存储过程获取数据时解密
     */
    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return decryptValue(value);
    }

    /**
     * 解密值
     *
     * @param value 数据库中的值
     * @return 解密后的明文
     */
    private String decryptValue(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        try {
            // 判断是否为加密数据，是则解密
            if (AesEncryptUtil.isEncrypted(value)) {
                return AesEncryptUtil.decrypt(value);
            }
            return value;
        } catch (Exception e) {
            log.error("字段解密失败，返回原始值", e);
            return value;
        }
    }
}
