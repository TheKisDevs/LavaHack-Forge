package com.kisman.cc.util.cosmos;

public enum Raytrace {
        NONE(-1), BASE(0.5), NORMAL(1.5), DOUBLE(2.5), TRIPLE(3.5);

        private final double offset;

        Raytrace(double offset) {
            this.offset = offset;
        }

        public double getOffset() {
            return offset;
        }
}