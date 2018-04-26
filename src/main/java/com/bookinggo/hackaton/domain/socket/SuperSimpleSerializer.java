package com.bookinggo.hackaton.domain.socket;

import com.bookinggo.hackaton.ffstp.FriendlyTemplate.Serializer;

public class SuperSimpleSerializer implements Serializer {
    @Override
    public <T> String serialize(T t) {
        return String.valueOf(t);
    }

    @Override
    public <T> T deserialize(String s, Class<T> aClass) {
        return aClass.cast(s);
    }
}
