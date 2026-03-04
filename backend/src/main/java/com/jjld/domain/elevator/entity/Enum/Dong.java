package com.jjld.domain.elevator.entity.Enum;

public enum Dong {
    D101(101),
    D102(102),
    D103(103),
    D104(104);

    private final int dong;

    Dong(int dong) {
        this.dong = dong;
    }

    public int getDong() {
        return dong;
    }

    public static Dong fromDong(int dong) {
        for (Dong d : values()) {
            if (d.getDong() == dong) {
                return d;
            }
        }
        throw new IllegalArgumentException("Invalid dong: " + dong);
    }
}
