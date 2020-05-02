package com.hoopawolf.mwaw.items;

import java.util.HashMap;
import java.util.Map;

public enum ELEMENT_TYPE
{
    FIRE(0),
    WATER(1),
    LIGHTNING(2),
    EARTH(3),
    LIGHT(4),
    DARK(5),
    NATURE(6),
    AIR(7),
    ICE(8),
    SAND(9),

    TOTAL(10);

    private int value;
    private static Map map = new HashMap<>();

    private ELEMENT_TYPE(int value)
    {
        this.value = value;
    }

    static
    {
        for (ELEMENT_TYPE _elementType : ELEMENT_TYPE.values())
        {
            map.put(_elementType.value, _elementType);
        }
    }

    public static ELEMENT_TYPE valueOf(int elementType)
    {
        return (ELEMENT_TYPE) map.get(elementType);
    }

    public int getValue()
    {
        return value;
    }
}
