package com.alibaba.fastjson.parser.deserializer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.util.FieldInfo;

public abstract class FieldDeserializer {

    public final FieldInfo fieldInfo;

    public final Class<?>  clazz;
    
    public int fastMatchToken;

    public FieldDeserializer(Class<?> clazz, FieldInfo fieldInfo, int fastMatchToken){
        this.clazz = clazz;
        this.fieldInfo = fieldInfo;
    }

    public abstract void parseField(DefaultJSONParser parser, Object object, Type objectType,
                                    Map<String, Object> fieldValues);
    
    public void setValue(Object object, int value) {
        try {
            fieldInfo.field.setInt(object, value);
        } catch (Exception e) {
            throw new JSONException("set property error, " + fieldInfo.name, e);
        }
    }
    
    public void setValue(Object object, long value) {
        try {
            fieldInfo.field.setLong(object, value);
        } catch (Exception e) {
            throw new JSONException("set property error, " + fieldInfo.name, e);
        }
    }
    
    public void setValue(Object object, float value) {
        try {
            fieldInfo.field.setFloat(object, value);
        } catch (Exception e) {
            throw new JSONException("set property error, " + fieldInfo.name, e);
        }
    }
    
    public void setValue(Object object, double value) {
        try {
            fieldInfo.field.setDouble(object, value);
        } catch (Exception e) {
            throw new JSONException("set property error, " + fieldInfo.name, e);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setValue(Object object, Object value) {
        final Field field = fieldInfo.field;
        final Method method = fieldInfo.method;
        if (fieldInfo.fieldAccess) {
            try {
                field.set(object, value);
            } catch (Exception e) {
                throw new JSONException("set property error, " + fieldInfo.name, e);
            }
            return;
        }
        
        if (method != null) {
            try {
                if (fieldInfo.isGetOnly()) {
                    if (Map.class.isAssignableFrom(method.getReturnType())) {
                        Map map = (Map) method.invoke(object);
                        if (map != null) {
                            map.putAll((Map) value);
                        }
                    } else {
                        Collection collection = (Collection) method.invoke(object);
                        if (collection != null) {
                            collection.addAll((Collection) value);
                        }
                    }
                } else {
                    if (value == null && fieldInfo.fieldClass.isPrimitive()) {
                        return;
                    }
                    method.invoke(object, value);
                }
            } catch (Exception e) {
                throw new JSONException("set property error, " + fieldInfo.name, e);
            }
            return;
        }
    }
}
