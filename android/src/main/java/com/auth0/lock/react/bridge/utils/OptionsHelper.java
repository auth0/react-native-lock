package com.auth0.lock.react.bridge.utils;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class OptionsHelper {

    private static final String TAG = OptionsHelper.class.getName();

    private OptionsHelper() {}

    public static  Map<String, Object> convertReadableMapToMap(ReadableMap reactMap) {
        Map<String, Object> map = new HashMap<>();
        ReadableMapKeySetIterator keySet = reactMap.keySetIterator();
        while (keySet.hasNextKey()) {
            String key = keySet.nextKey();
            Object object = null;
            switch (reactMap.getType(key)) {
                case Array:
                    object = convertReadableArrayToArray(reactMap.getArray(key));
                    break;
                case Boolean:
                    object = reactMap.getBoolean(key);
                    break;
                case Map:
                    object = convertReadableMapToMap(reactMap.getMap(key));
                    break;
                case Null:
                    object = null;
                    break;
                case Number:
                    try {
                        object = reactMap.getDouble(key);
                    } catch (java.lang.ClassCastException e) {
                        object = reactMap.getInt(key);
                    }
                    break;
                case String:
                    object = reactMap.getString(key);
                    break;
                default:
                    Log.e(TAG, "Unknown type: " + reactMap.getType(key) + " for key: " + key);
            }
            map.put(key, object);
        }
        return map;
    }

    public static ArrayList convertReadableArrayToArray(ReadableArray reactArray) {
        ArrayList<Object> array = new ArrayList<>();
        for (int i=0, size = reactArray.size(); i<size; ++i) {
            Object object = null;
            switch (reactArray.getType(i)) {
                case Array:
                    object = convertReadableArrayToArray(reactArray.getArray(i));
                    break;
                case Boolean:
                    object = reactArray.getBoolean(i);
                    break;
                case Map:
                    object = convertReadableMapToMap(reactArray.getMap(i));
                    break;
                case Null:
                    object = null;
                    break;
                case Number:
                    try {
                        object = reactArray.getDouble(i);
                    } catch (java.lang.ClassCastException e) {
                        object = reactArray.getInt(i);
                    }
                    break;
                case String:
                    object = reactArray.getString(i);
                    break;
                default:
                    Log.e(TAG, "Unknown type: " + reactArray.getType(i) + " for index: " + i);
            }
            array.add(object);
        }
        return array;
    }
}
