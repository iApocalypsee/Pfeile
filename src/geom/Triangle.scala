package geom

class Triangle(p1: Point, p2: Point, p3: Point) {
    def getPoint1 = p1
    def getPoint2 = p2
    def getPoint3 = p3

    def area = (p2 - p1).parallelogramArea(p3 - p1) / 2.0

//x = p1 + s (p2 - p1) + t (p3 - p1);   u := p2 - p1, v := p3 - p1, w := x - p1
//wx = s ux + t vx;   wy = s uy + t vy
// s = (wx - t vx) / ux = (wy - t vy) / uy
// uy wx - t uy vx = ux wy - t ux vy
// uy wx - ux wy = t (uy vx - ux vy)
// t = (uy wx - ux wy) / (uy vx - ux vy) <-- denominator != 0 because u and v
// are already shown to be linearly independent
// t = (wx - s ux) / vx = (wy - s uy) / vy
// vy wx - s ux vy = vx wy - s uy vx
// vy wx - vx wy = s (ux vy - uy vx)
// s = (vy wx - vx wy) / (ux vy - uy vx)
    def contains(x: Point) = {
        val u = p2 - p1
        val v = p3 - p1
        val w = x - p1
        if (u isParallel v) {
            //if u is parallel to v, x is only in the triangle if w is parallel to u and v, too
            u isParallel w
        } else {
            //represent w in terms of u and v:  w = s u + t v
            val t = (u.getY * w.getX - u.getX * w.getY) / (u.getY * v.getX - u.getX * v.getY)
            val s = (v.getY * w.getX - v.getX * w.getY) / (u.getX * v.getY - u.getY * v.getX)
            //point is in triangle iff 0 <= s <= 1, 0 <= t <= 1 and 0 <= s + t <= 1
            (t >= 0 && t <= 1) && (s >= 0 && s <= 1) && (s + t >= 0 && s + t <= 1)
        }
    }
}
