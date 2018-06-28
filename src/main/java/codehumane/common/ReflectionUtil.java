package codehumane.common;

import org.springframework.classify.util.MethodInvoker;
import org.springframework.classify.util.MethodInvokerUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * reflection 관련된 유틸리티성 코드 모음
 */
public class ReflectionUtil {

    /**
     * 클래스의 모든 필드를 부모를 포함하여 모두 가져온다.
     *
     * @param clazz 클래스 타입
     * @return 모든 필드
     */
    public static List<Field> getAllFields(Class clazz) {
        return getAllFields(clazz, new ArrayList<>());
    }

    // 부모의 모든 필드를 가져오기 위한 재귀연산 (재귀보다 좋은게 있으려나?)
    private static List<Field> getAllFields(Class clazz, List<Field> fields) {
        Class superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            getAllFields(superClazz, fields);
        }

        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return fields;
    }

    /**
     * 인자가 하나인 메소드를 호출한다.
     *
     * @param object     메소드를 소유한 객체
     * @param methodName 메소드명
     * @param paramValue 파라미터 값
     */
    public static void invokeMethodForSingleArgument(
            Object object, String methodName, Object paramValue) {

        final MethodInvoker methodInvoker = MethodInvokerUtils
                .getMethodInvokerByName(object, methodName, true, paramValue.getClass());
        methodInvoker.invokeMethod(paramValue);
    }

    /**
     * 리플렉션에서 특정 값의 타입(valueType)이 특정 대상(target)에 할당될 수 있는지 검사한다.<br/>
     * 예를 들어, primitive type의 wrapper class는 primitive type에 할당될 수 있다.
     *
     * @param target    할당 대상
     * @param valueType 대상에 할당하려는 값의 타입
     * @return 할당 가능 여부
     */
    public static boolean isAssignable(Class<?> target, Class<?> valueType) {
        return ClassUtils.isAssignable(target, valueType);
    }

    /**
     * 디폴트 생성자를 활용하여 인스턴스화
     *
     * @param destinationType 클래스
     * @param <D>             타입
     * @return 인스턴스
     * @throws NoSuchMethodException     기본 생성자 없는 경우
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static <D> D constructByDefault(Class<D> destinationType)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {

        final Constructor<D> constructor = destinationType.getDeclaredConstructor(); // 기본 생성자 사용
        constructor.setAccessible(true); // 접근자가 public이 아닌 경우 대응
        return constructor.newInstance();
    }

    /**
     * 필드에 값 할당
     *
     * @param object 필드를 소유한 객체
     * @param field  필드
     * @param value  값
     * @throws IllegalAccessException
     */
    public static void setField(Object object, Field field, Object value)
            throws IllegalAccessException {
        field.setAccessible(true);
        field.set(object, value);
    }

    /**
     * 필드 값 반환
     *
     * @param object 필드를 소유한 객체
     * @param field  필드
     * @return
     * @throws IllegalAccessException
     */
    public static Object getField(Object object, Field field)
            throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(object);
    }
}
