package com.elerna;

public enum FixedSizeThumbnail {

    ICON(24,24),
    SMALL(200, 200),
    BIG(500, 500),
    BIG_RETINA(1000, 1000),

    // keeping the original width and height
    ORIGINAL(-1, -1);

    private int width,height;

    FixedSizeThumbnail(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
