package comp;

public enum VerticalOrientation {

    Top, Centered, Bottom;

    public int apply(int placingHeight, int boundingHeight) {
        switch(this) {
            case Top: return 0;
            case Centered: return boundingHeight / 2 - placingHeight - 2;
            case Bottom: return boundingHeight - placingHeight;
            default: return Integer.MIN_VALUE;
        }
    }

}
