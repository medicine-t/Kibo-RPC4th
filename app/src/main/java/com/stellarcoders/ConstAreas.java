package com.stellarcoders;


public class ConstAreas {
//    public final Area[] KOZs = {
//        new Area(10.783f,-9.8899f, 4.8385f, 11.071f, -9.6929f, 5.0665f),
//        new Area(10.8652f, -9.0734f, 4.3861f, 10.9628f, -8.7314f, 4.6401f),
//        new Area(10.185f,-8.3826f,4.1475f,11.665f, -8.2826f, 4.6725f),
//        new Area(10.7955f,-8.0635f, 5.1055f, 11.3525f, -7.7305f, 5.1305f),
//        new Area(10.563f, -7.1449f, 4.6544f, 10.709f, -6.8099f, 4.8164f)
//    };
    private static final float buffer = 0.10f;
    public static final Area[] KOZs = {
            new Area(10.783f - buffer,-9.8899f - buffer, 4.8385f - buffer, 11.071f + buffer, -9.6929f + buffer, 5.0665f + buffer),
            new Area(10.8652f - buffer, -9.0734f - buffer, 4.3861f - buffer, 10.9628f + buffer, -8.7314f + buffer, 4.6401f + buffer),
            new Area(10.185f - buffer,-8.3826f - buffer,4.1475f - buffer,11.665f + buffer, -8.2826f + buffer, 4.6725f + buffer),
            new Area(10.7955f- buffer,-8.0635f - buffer, 5.1055f - buffer, 11.3525f + buffer, -7.7305f + buffer, 5.1305f + buffer),
            new Area(10.563f- buffer, -7.1449f - buffer, 4.6544f - buffer, 10.709f + buffer, -6.8099f + buffer, 4.8164f + buffer)
    };

    public final Area[] KIZs = {
        new Area(10.3f, -10.2f, 4.32f, 11.55f, -6.0f, 5.57f),
        new Area(9.5f, -10.5f, 4.02f, 10.5f, -9.6f, 4.8f),
    };
}
