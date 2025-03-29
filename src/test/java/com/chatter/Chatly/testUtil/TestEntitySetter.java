package com.chatter.Chatly.testUtil;
import java.lang.reflect.Field;

public class TestEntitySetter {

    public static <T, K> void setEntityId(T entity, String field, K id) throws Exception {
        Field idField = entity.getClass().getDeclaredField(field); // "id" 필드 찾기
        idField.setAccessible(true); // private 접근 허용
        idField.set(entity, id); // ID 값 설정
    }

}
