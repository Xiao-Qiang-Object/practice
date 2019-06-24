package wsq.study.common.utils;


import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author ex-weisq001
 * @ClassName ReflectionUtils
 * @description: 反射工具类: 提供了一系列的获取某一个类的信息的方法<br />
 * 包括获取全类名，实现的接口，接口的泛型等<br />
 * 并且提供了根据Class类型获取对应的实例对象的方法，以及修改属性和调用对象的方法等
 * @date 2019/6/24 14:15
 */
public class ReflectionUtils {

  /**
   * 获取包名
   *
   * @return 包名【String类型】,相对路径，根路径开始，如src.main.java是根路径，则从下一层路径开始
   */
  public static String getPackage(Class<?> clazz) {
    Package pck = clazz.getPackage();
    if (null != pck) {
      return pck.getName();
    } else {
      return "没有包！";
    }
  }

  /**
   * 获取继承的父类的全类名
   *
   * @return 继承的父类的全类名【String类型】
   */
  public static String getSuperClassName(Class<?> clazz) {
    Class<?> superClass = clazz.getSuperclass();
    if (null != superClass) {
      return superClass.getName();
    } else {
      return "没有父类！";
    }
  }

  /**
   * 获取全类名
   *
   * @return 全类名【String类型】
   */
  public static String getClassName(Class<?> clazz) {
    return clazz.getName();
  }

  /**
   * 获取实现的接口名
   *
   * @return 所有的接口名【每一个接口名的类型为String，最后保存到一个List集合中】
   */
  public static List<String> getInterfaces(Class<?> clazz) {
    Class<?>[] interfaces = clazz.getInterfaces();
    int len = interfaces.length;

    List<String> list = new ArrayList<String>();
    for (int i = 0; i < len; i++) {
      Class<?> itfc = interfaces[i];

      // 接口名
      String interfaceName = itfc.getSimpleName();

      list.add(interfaceName);
    }

    return list;
  }

  /**
   * 获取所有属性
   *
   * @return 所有的属性【每一个属性添加到StringBuilder中，最后保存到一个List集合中】,这里将每个属性、修饰符、类型拼接成实体类里缩写的样式，包含";"
   */
  public static List<StringBuilder> getFields(Class<?> clazz) {
    Field[] fields = clazz.getDeclaredFields();
    int len = fields.length;

    List<StringBuilder> list = new ArrayList<StringBuilder>();
    StringBuilder sb = null;
    for (int i = 0; i < len; i++) {
      Field field = fields[i];
      sb = new StringBuilder();

      // 修饰符
      String modifier = Modifier.toString(field.getModifiers());
      sb.append(modifier + " ");

      // 数据类型
      Class<?> type = field.getType();
      String typeName = type.getSimpleName();
      sb.append(typeName + " ");

      // 属性名
      String fieldName = field.getName();
      sb.append(fieldName + ";");

      list.add(sb);
    }

    return list;
  }

  /**
   * 获取所有公共的属性
   *
   * @return 所有公共的属性【每一个属性添加到StringBuilder中，最后保存到一个List集合中】,这里将每个属性、修饰符、类型拼接成实体类里缩写的样式，包含";"
   */
  public static List<StringBuilder> getPublicFields(Class<?> clazz) {
    Field[] fields = clazz.getFields();
    int len = fields.length;

    List<StringBuilder> list = new ArrayList<StringBuilder>();
    StringBuilder sb = null;
    for (int i = 0; i < len; i++) {
      Field field = fields[i];
      sb = new StringBuilder();

      // 修饰符
      String modifier = Modifier.toString(field.getModifiers());
      sb.append(modifier + " ");

      // 数据类型
      Class<?> type = field.getType();
      String typeName = type.getSimpleName();
      sb.append(typeName + " ");

      // 属性名
      String fieldName = field.getName();
      sb.append(fieldName + ";");

      list.add(sb);
    }

    return list;
  }

  /**
   * 获取所有构造方法
   *
   * @return 所有的构造方法【每一个构造方法添加到StringBuilder中，最后保存到一个List集合中】
   */
  public static List<StringBuilder> getConstructors(Class<?> clazz) {
    Constructor<?>[] constructors = clazz.getDeclaredConstructors();
    int len = constructors.length;

    List<StringBuilder> list = new ArrayList<StringBuilder>();
    StringBuilder sb = null;
    for (int i = 0; i < len; i++) {
      Constructor<?> constructor = constructors[i];
      sb = new StringBuilder();

      // 修饰符
      String modifier = Modifier.toString(constructor.getModifiers());
      sb.append(modifier + " ");

      // 方法名（类名）
      String constructorName = clazz.getSimpleName();
      sb.append(constructorName + " (");

      // 形参列表
      Class<?>[] parameterTypes = constructor.getParameterTypes();
      int length = parameterTypes.length;
      for (int j = 0; j < length; j++) {
        Class<?> parameterType = parameterTypes[j];

        String parameterTypeName = parameterType.getSimpleName();

        if (j < length - 1) {
          sb.append(parameterTypeName + ", ");
        } else {
          sb.append(parameterTypeName);
        }

      }

      sb.append(") {}");

      list.add(sb);
    }

    return list;
  }

  /**
   * 获取所有自身的方法
   *
   * @return 所有自身的方法【每一个方法添加到StringBuilder中，最后保存到一个List集合中】
   */
  public static List<StringBuilder> getMethods(Class<?> clazz) {
    Method[] methods = clazz.getDeclaredMethods();
    int len = methods.length;

    List<StringBuilder> list = new ArrayList<StringBuilder>();
    StringBuilder sb = null;
    for (int i = 0; i < len; i++) {
      Method method = methods[i];
      sb = new StringBuilder();

      // 修饰符
      String modifier = Modifier.toString(method.getModifiers());
      sb.append(modifier + " ");

      // 返回值类型
      Class<?> returnClass = method.getReturnType();
      String returnType = returnClass.getSimpleName();
      sb.append(returnType + " ");

      // 方法名
      String methodName = method.getName();
      sb.append(methodName + " (");

      // 形参列表
      Class<?>[] parameterTypes = method.getParameterTypes();
      int length = parameterTypes.length;

      for (int j = 0; j < length; j++) {
        Class<?> parameterType = parameterTypes[j];

        // 形参类型
        String parameterTypeName = parameterType.getSimpleName();

        if (j < length - 1) {
          sb.append(parameterTypeName + ", ");
        } else {
          sb.append(parameterTypeName);
        }

      }

      sb.append(") {}");

      list.add(sb);
    }

    return list;
  }

  /**
   * 获取所有公共的方法
   *
   * @return 所有公共的方法【每一个方法添加到StringBuilder中，最后保存到一个List集合中】
   */
  public static List<StringBuilder> getPublicMethods(Class<?> clazz) {
    Method[] methods = clazz.getMethods();
    int len = methods.length;

    List<StringBuilder> list = new ArrayList<StringBuilder>();
    StringBuilder sb = null;
    for (int i = 0; i < len; i++) {
      Method method = methods[i];
      sb = new StringBuilder();

      // 修饰符
      String modifier = Modifier.toString(method.getModifiers());
      sb.append(modifier + " ");

      // 返回值类型
      Class<?> returnClass = method.getReturnType();
      String returnType = returnClass.getSimpleName();
      sb.append(returnType + " ");

      // 方法名
      String methodName = method.getName();
      sb.append(methodName + " (");

      // 形参列表
      Class<?>[] parameterTypes = method.getParameterTypes();
      int length = parameterTypes.length;

      for (int j = 0; j < length; j++) {
        Class<?> parameterType = parameterTypes[j];

        // 形参类型
        String parameterTypeName = parameterType.getSimpleName();

        if (j < length - 1) {
          sb.append(parameterTypeName + ", ");
        } else {
          sb.append(parameterTypeName);
        }

      }

      sb.append(") {}");

      list.add(sb);
    }

    return list;
  }

  /**
   * 获取所有的注解名
   *
   * @return 所有的注解名【每一个注解名的类型为String，最后保存到一个List集合中】
   */
  public static List<String> getAnnotations(Class<?> clazz) {
    Annotation[] annotations = clazz.getAnnotations();
    int len = annotations.length;

    List<String> list = new ArrayList<String>();
    for (int i = 0; i < len; i++) {
      Annotation annotation = annotations[i];

      String annotationName = annotation.annotationType().getSimpleName();
      list.add(annotationName);
    }

    return list;
  }

  /**
   * 获取父类的泛型
   *
   * @return 父类的泛型【Class类型】
   */
  public static Class<?> getSuperClassGenericParameterizedType(Class<?> clazz) {
    Type genericSuperClass = clazz.getGenericSuperclass();

    Class<?> superClassGenericParameterizedType = null;

    // 判断父类是否有泛型
    if (genericSuperClass instanceof ParameterizedType) {
      // 向下转型，以便调用方法
      ParameterizedType pt = (ParameterizedType) genericSuperClass;
      // 只取第一个，因为一个类只能继承一个父类
      Type superClazz = pt.getActualTypeArguments()[0];
      // 转换为Class类型
      superClassGenericParameterizedType = (Class<?>) superClazz;
    }

    return superClassGenericParameterizedType;
  }

  /**
   * 获取接口的所有泛型
   *
   * @return 所有的泛型接口【每一个泛型接口的类型为Class，最后保存到一个List集合中】
   */
  public static List<Class<?>> getInterfaceGenericParameterizedTypes(Class<?> clazz) {
    Type[] genericInterfaces = clazz.getGenericInterfaces();
    int len = genericInterfaces.length;

    List<Class<?>> list = new ArrayList<Class<?>>();
    for (int i = 0; i < len; i++) {
      Type genericInterface = genericInterfaces[i];

      // 判断接口是否有泛型
      if (genericInterface instanceof ParameterizedType) {
        ParameterizedType pt = (ParameterizedType) genericInterface;

        // 得到所有的泛型【Type类型的数组】
        Type[] interfaceTypes = pt.getActualTypeArguments();

        int length = interfaceTypes.length;

        for (int j = 0; j < length; j++) {
          // 获取对应的泛型【Type类型】
          Type interfaceType = interfaceTypes[j];
          // 转换为Class类型
          Class<?> interfaceClass = (Class<?>) interfaceType;
          list.add(interfaceClass);
        }

      }

    }

    return list;
  }

  /**
   * 根据Class类型，获取对应的实例【要求必须有无参的构造器】
   *
   * @return 对应的实例【Object类型】
   */
  public static Object getNewInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException {
    return clazz.newInstance();
  }

  /**
   * 根据传入的类的Class对象，以及构造方法的形参的Class对象，获取对应的构造方法对象
   *
   * @param clazz          类的Class对象
   * @param parameterTypes 构造方法的形参的Class对象【可以不写】
   * @return 构造方法对象【Constructor类型】
   */
  public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes)
    throws NoSuchMethodException, SecurityException {
    return clazz.getDeclaredConstructor(parameterTypes);
  }

  /**
   * 根据传入的构造方法对象，以及，获取对应的实例
   *
   * @param constructor 构造方法对象
   * @param initargs    传入构造方法的实参【可以不写】
   * @return 对应的实例【Object类型】
   */
  public static Object getNewInstance(Constructor<?> constructor, Object... initargs)
    throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    constructor.setAccessible(true);
    return constructor.newInstance(initargs);
  }

  /**
   * 根据传入的属性名字符串，修改对应的属性值
   *
   * @param clazz 类的Class对象
   * @param name  属性名
   * @param obj   要修改的实例对象
   * @param value 修改后的新值
   */
  public static void setField(Class<?> clazz, String name, Object obj, Object value)
    throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    Field field = clazz.getDeclaredField(name);
    field.setAccessible(true);
    field.set(obj, value);
    field.setAccessible(false);
  }

  /**
   * 根据传入的方法名字符串，获取对应的方法
   *
   * @param clazz          类的Class对象
   * @param name           方法名
   * @param parameterTypes 方法的形参对应的Class类型【可以不写,建议给上】
   * @return 方法对象【Method类型】
   */
  public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes)
    throws NoSuchMethodException, SecurityException {
    return clazz.getDeclaredMethod(name, parameterTypes);
  }

  /**
   * 根据传入的方法对象，调用对应的方法
   *
   * @param method 方法对象
   * @param obj    要调用的实例对象【如果是调用静态方法，则可以传入null】
   * @param args   传入方法的实参【可以不写】
   * @return 方法的返回值【没有返回值，则返回null】
   */
  public static Object invokeMethod(Method method, Object obj, Object... args)
    throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    method.setAccessible(true);
    return method.invoke(obj, args);
  }

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
   *
   * @param obj
   */
  public static Map<String, String> obj2Map(Object obj) {
    Map<String, String> map = new HashMap<String, String>();
    // 获取对象对应类中的所有属性域
    Field[] fields = obj.getClass().getDeclaredFields();
    for (int i = 0; i < fields.length; i++) {
      String varName = fields[i].getName();
      // 获取原来的访问控制权限
      boolean accessFlag = fields[i].isAccessible();
      // 修改访问控制权限
      fields[i].setAccessible(true);
      try {
        // 获取在对象中属性fields[i]对应的对象中的变量
        Object object = fields[i].get(obj);
        if (object != null && object != "") {
          map.put(varName, object.toString());
        }
        // 恢复访问控制权限
        fields[i].setAccessible(accessFlag);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return map;

  }

  /**
   * 根据对象获取set方法设置值
   *
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
            if (type.getSimpleName().equals("String")) {
              method.setAccessible(true);
              method.invoke(obj, entry.getValue());
            } else if (type.getSimpleName().equals("Integer")) {
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
