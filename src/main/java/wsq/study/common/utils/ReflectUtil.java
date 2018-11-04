package wsq.study.common.utils;

import org.apache.commons.lang3.StringUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author weisq
 * @date 2018年11月4日
 */
public class ReflectUtil {
    /**
     * 字符串空格处理
     *
     * @param object
     */
    public static void checkBlank(Object object) {
        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            boolean accessFlag = field.isAccessible();
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(object);
                if (field.getType().toString().equals("class java.lang.String")) {
                    if (fieldValue != null) {
                        String trimValue = fieldValue.toString().trim();
                        field.set(object, trimValue);
                        for (int i = 0; i < trimValue.length(); i++) {
                            char c = fieldValue.toString().charAt(i);
                            if (c == ' ') {
                                throw new RuntimeException(field.getName() + "有空格，请重新输入");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            field.setAccessible(accessFlag);
        }
    }

    /**
     * 将对象的属性和值转换为map键值对
     * @param obj
     */
    public static Map<String, String> obj2Map(Object obj) {
        Map<String, String> map = new HashMap<String, String>();
        Field[] fields = obj.getClass().getDeclaredFields(); // 获取对象对应类中的所有属性域
        for (int i = 0; i < fields.length; i++) {
            String varName = fields[i].getName();
            boolean accessFlag = fields[i].isAccessible(); // 获取原来的访问控制权限
            fields[i].setAccessible(true);// 修改访问控制权限
            try {
                Object object = fields[i].get(obj); // 获取在对象中属性fields[i]对应的对象中的变量
                if (object != null && object != "") {
                    map.put(varName, object.toString());
                }
                fields[i].setAccessible(accessFlag);// 恢复访问控制权限
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;

    }

    /**
     * 根据对象获取set方法设置值
     * @param obj 传入的对象
     * @param map 键为对象对应类里的字段，值为字段对应的取值
     * @throws Exception
     */
    public static void setValue(Object obj, Map<String, String> map) throws Exception {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            try {
                if (StringUtils.isNotBlank(entry.getValue())) {
                    Class clazz = obj.getClass();
                    Class[] parameterTypes = new Class[1];
                    Field field = clazz.getDeclaredField(entry.getKey());
                    parameterTypes[0] = field.getType();
                    StringBuffer sb = new StringBuffer();
                    sb.append("set");
                    sb.append(entry.getKey().substring(0, 1).toUpperCase());
                    sb.append(entry.getKey().substring(1));
                    Method method = clazz.getMethod(sb.toString(), parameterTypes);
                    Class[] paramTypes = method.getParameterTypes();
                    for (Class type : paramTypes) {
                        if(type.getSimpleName().equals("String")){
                            method.setAccessible(true);
                            method.invoke(obj, entry.getValue());
                        }else if (type.getSimpleName().equals("Integer")){
                            method.setAccessible(true);
                            method.invoke(obj, Integer.parseInt(entry.getValue()));
                        }
                    }
                }
            } catch (NoSuchFieldException e) {
            }
        }
    }

}

