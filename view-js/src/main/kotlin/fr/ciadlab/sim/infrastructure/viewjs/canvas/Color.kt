package fr.ciadlab.sim.infrastructure.viewjs.canvas

enum class Color(private val style: String) {

    GRAY("rgb(128,128,128)"),
    DARKGRAY("rgb(169,169,169)"),
    YELLOW("rgb(255,255,0)"),
    WHITE("rgb(255,255,255)"),
    BLACK("rgb(0, 0, 0)"),
    MAGENTA("rgb(255,0,255)"),
    TRANSPARENT("rgba(0, 0, 0, 0)"),
    RED("rgb(255,0,0)");

    override fun toString() = style

    companion object {
        fun rgb(r: Int, g: Int, b: Int) = "rgb($r,$g,$b)"
        fun hsv(h: Int, s: Int, v: Int) = "hsv($h,$s,$v)"
    }
}