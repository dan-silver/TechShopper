package com.silver.dan.deals;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Utils {

    //check if an array contains an item
    public static <T> boolean contains(final T[] array, final T key) {
        return Arrays.asList(array).contains(key);
    }

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    static <K,V extends Comparable<? super V>>
    List<Map.Entry<K, V>> entriesSortedByValues(Map<K,V> map) {

        List<Map.Entry<K,V>> sortedEntries = new ArrayList<>(map.entrySet());

        Collections.sort(sortedEntries,
                new Comparator<Map.Entry<K, V>>() {
                    @Override
                    public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                }
        );

        return sortedEntries;
    }

    // given a JSON object with a JSONArray, dump its elements into an ArrayList
    public static void dumpJSONArrayToArrayList(JSONObject json, String jsonKey, ArrayList<String> arrayList) throws JSONException {
        JSONArray arrayElements = json.getJSONArray(jsonKey);
        for(int i = 0; i < arrayElements.length(); i++) {
            if (arrayList == null)
                arrayList = new ArrayList<>();
            arrayList.add(arrayElements.getString(i));
        }
    }
}
