package comp;

public enum HorizontalOrientation {

    Left, Centered, Right;

    public int apply(int placingWidth, int boundingWidth) {
        switch(this) {
            case Left: return 0;
            case Centered: return boundingWidth / 2 - placingWidth / 2;
            case Right: return boundingWidth - placingWidth;
            default: return Integer.MIN_VALUE;
        }
    }

}
