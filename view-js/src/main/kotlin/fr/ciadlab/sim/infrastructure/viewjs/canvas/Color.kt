package fr.ciadlab.sim.infrastructure.viewjs.canvas

class Color(private val style: String) {
    val alpha: Double by lazy {
        if(style.startsWith("rgba")) {
            (style.substringAfterLast(",").substringBeforeLast(")").toInt()) / 255.0
        } else {
            1.0
        }
    }

    override fun toString() = style

    companion object {
        fun rgb(r: Int, g: Int, b: Int) = Color("rgb($r,$g,$b)")
        fun rgb(r: Double, g: Double, b: Double) = rgb((255 * r).toInt(), (255 * g).toInt(), (255 * b).toInt())
        fun hsv(h: Int, s: Int, v: Int) = Color("hsv($h,$s,$v)")
        fun rgba(r: Int, g: Int, b: Int, a: Int) = Color("rgba($r,$g,$b,$a)")
        fun rgba(r: Double, g: Double, b: Double, a: Double) =
            rgba((255 * r).toInt(), (255 * g).toInt(), (255 * b).toInt(), (255 * a).toInt())

        val GRAY = Color("rgb(128,128,128)")
        val DARKGRAY = Color("rgb(169,169,169)")
        val YELLOW = Color("rgb(255,255,0)")
        val WHITE = Color("rgb(255,255,255)")
        val BLACK = Color("rgb(0, 0, 0)")
        val MAGENTA = Color("rgb(255,0,255)")
        val TRANSPARENT = Color("rgba(0, 0, 0, 0)")
        val RED = Color("rgb(255,0,0)")
        val GREEN = Color("rgb(0,255,0)")
        val BLUE = Color("rgb(0,0,255)")
    }
}
