package socialnet.utils;


import org.springframework.stereotype.Component;
import socialnet.exception.InvokeException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Character.isDigit;

@Component
public class Reflection {
    public <T> List<String> getAllFields(T cls) {
        var result = new ArrayList<String>();
        for (Field field : cls.getClass().getDeclaredFields()) {
            //System.out.println(getMethodName("get", field.getName()));
            result.add(getSqlName(field.getName()));
        }
        return result;
    }

    private String getMethodName(String prefix, String fieldName) {
        return prefix +
                String.valueOf(fieldName.charAt(0)).toUpperCase().concat(fieldName.substring(1));
    }

    public <T> Object methodInvoke(T cls, String methodName) {
        try {
            Method method = cls.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(cls);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new InvokeException(e.getMessage());
        }
    }

    public String getFieldNames(Object object) {
        String result = "";
        String delemitter = "";

        for (Field field : object.getClass().getDeclaredFields()) {
            result = result.concat(delemitter).concat(getSqlName(field.getName()));
            delemitter = ", ";
        }
        return result;
    }

    public String getFieldNames(Object object, String[] excludeFields) {
        String result = "";
        String delemitter = "";

        List<String> exludeList = Arrays.asList(excludeFields);
        exludeList = exludeList.stream().map(String::toLowerCase).collect(Collectors.toList());

        for (Field field : object.getClass().getDeclaredFields()) {
            if (!exludeList.contains(field.getName().toLowerCase())) {
                result = result.concat(delemitter)
                        .concat(getSqlName(field.getName()));
                delemitter = ", ";
            }
        }
        return result;
    }

    public String getInsertStatement(Object object, String excludeField) {
        String result = "(";
        String delemitter = "";
        String values = "";

        for (Field field : object.getClass().getDeclaredFields()) {
            String fieldName = getSqlName(field.getName());
            if (!field.getName().equalsIgnoreCase(excludeField)) {
                result = result.concat(delemitter).concat(fieldName);
                values += delemitter.concat(" ?");
                delemitter = ", ";
            }
        }
        return result.concat(") values (" + values).concat(")");
    }


    public String getFieldNamesWithQuestionMark(Object object) {
        String result = "";
        String delemitter = "";
        for (Field field : object.getClass().getDeclaredFields()) {
            result = result.concat(delemitter)
                    .concat(getSqlName(field.getName()))
                    .concat(" = ? ");
            delemitter = ", ";
        }
        return result;
    }


    public Map<String, Object> getFieldsAndValues(Object object) {

        Map<String, Object> result = new HashMap<>();

        for (Field field : object.getClass().getDeclaredFields())
            result.put(getSqlName(field.getName()), methodInvoke(object, getMethodName("get", field.getName())));

        return result;
    }
    public Map<String, Object> getFieldsAndValuesQuery(Object object, Object[] addValues) {

        StringBuilder fieldNames = new StringBuilder();
        Map<String, Object> result = new HashMap<>();
        String delemitter = "";

        List<Object> values = new ArrayList<>();

        for (Field field : object.getClass().getDeclaredFields()) {
            Object fieldValue = methodInvoke(object, getMethodName("get", field.getName()));

            if (fieldValue != null) {
                fieldNames.append(String.format("%s%s%s", delemitter, getSqlName(field.getName()), " = ? "));
                delemitter = ", ";
                values.add(fieldValue);
            }
        }

        if (addValues != null)
            Collections.addAll(values, addValues);

        result.put("fieldNames", fieldNames.toString());
        result.put("values", values.toArray());
        return result;
    }

    public String getSqlName(String fieldName) {
        String result = "";
        String capsName = fieldName.toUpperCase();

        int charCount = fieldName.length();
        int i = 0;
        while (i < charCount) {
            if ((fieldName.charAt(i) == capsName.charAt(i)) && (!isDigit(fieldName.charAt(i)))) {
                result += "_" + fieldName.charAt(i);
                i++;
            } else {
                result += fieldName.charAt(i);
                i++;
            }
        }
        return result;
    }

    public Object[] getValues(Object object) {
        var declaredFields = object.getClass().getDeclaredFields();
        Object[] objects = new Object[declaredFields.length];
        int i = 0;
        for (Field field : declaredFields) {
            var value = methodInvoke(object, getMethodName("get", field.getName()));
            objects[i] = value;
            i++;
        }
        return objects;
    }

    public Object[] getValues(Object object, String excludeField) {
        var declaredFields = object.getClass().getDeclaredFields();
        Object[] objects = new Object[declaredFields.length - 1];
        int i = 0;
        for (Field field : declaredFields) {
            if (!excludeField.equalsIgnoreCase(field.getName())) {
                var value = methodInvoke(object, getMethodName("get", field.getName()));
                objects[i] = value;
                i++;
            }
        }
        return objects;
    }


    public String getUpdateSql(Object object, String excludeField) {
        var declaredFields = object.getClass().getDeclaredFields();
        int i = 0;
        String delimiter = "";
        StringBuilder stringBuilder = new StringBuilder();
        for (Field field : declaredFields) {
            if (!excludeField.equalsIgnoreCase(field.getName())) {
                var fieldName = getSqlName(field.getName());
                var value = methodInvoke(object, getMethodName("get", field.getName()));
                stringBuilder.append(delimiter + fieldName + " = " + getSqlValues(value));
                i++;
                delimiter = ", ";
            }
        }
        return stringBuilder.toString();
    }

    public String getSqlValues(Object value) {
        if ((value.getClass().equals(String.class)) || (value.getClass().equals(Timestamp.class))) {
            return "'".concat(value.toString().replace("'", "`")).concat("'");
        } else {
            return value.toString();
        }
    }

    public Object[] getValuesArray(Object object) {
        var declaredFields = object.getClass().getDeclaredFields();

        Object[] objects = new Object[declaredFields.length];
        int i = 0;
        for (Field field : declaredFields) {
            var value = methodInvoke(object, getMethodName("get", field.getName()));
            objects[i] = value;
            i++;
        }
        return objects;
    }

    public Object[] getValuesArray(Object object, Object[] addParams) {
        var declaredFields = object.getClass().getDeclaredFields();

        Object[] objects = new Object[declaredFields.length + addParams.length];
        int i = 0;
        for (Field field : declaredFields) {
            var value = methodInvoke(object, getMethodName("get", field.getName()));
            objects[i] = value;
            i++;
        }
        int j = 0;
        while (i < declaredFields.length + addParams.length) {
            objects[i] = addParams[j];
            i++;
            j++;
        }
        return objects;
    }


    public String getStringValues(Object object) {

        String delemiter = "";
        StringBuilder stringBuilder = new StringBuilder();

        for (Object value : getValues(object)) {

            if ((value.getClass().equals(String.class)) || (value.getClass().equals(Timestamp.class))) {
                stringBuilder.append(delemiter.concat("'").concat(value.toString().replace("'", "`")).concat("'"));
            } else {
                stringBuilder.append(delemiter.concat(value.toString()));
            }

            delemiter = ", ";
        }
        return stringBuilder.toString();
    }

    public String getStringValues(Object object, String excludeField) {

        String delemiter = "";
        StringBuilder stringBuilder = new StringBuilder();

        for (Object value : getValues(object, excludeField)) {
            if ((value.getClass().equals(String.class)) || (value.getClass().equals(Timestamp.class))) {
                stringBuilder.append(delemiter.concat("'").concat(value.toString().replace("'", "`")).concat("'"));
            } else {
                stringBuilder.append(delemiter.concat(value.toString()));
            }

            delemiter = ", ";
        }
        return stringBuilder.toString();
    }

    public int[] getTypesArray(Object object) {
        var declaredFields = object.getClass().getDeclaredFields();
        int[] result = new int[declaredFields.length];
        int i = 0;
        for (Field field : declaredFields) {
            var type = field.getType();
            // TODO: 31.03.2023 добавить недостающие классы для преобразования
            if (type == String.class)
                result[i] = Types.VARCHAR;
            else if (type == Long.class)
                result[i] = Types.BIGINT; //-5;
            else if (type == Timestamp.class)
                result[i] = Types.TIMESTAMP;
            i++;
        }
        return result;
    }

    public void objectsAdapt(Object[] objects, int[] types) {
        int i = 0;
        for (int type : types) {
            if ((type == 12) && (objects[i] != null)) {
                objects[i] = "'".concat((String) objects[i]).concat("'");
            }
            i++;
        }
    }

}
