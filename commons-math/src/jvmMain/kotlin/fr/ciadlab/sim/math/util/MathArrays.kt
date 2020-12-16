package fr.ciadlab.sim.math.util

actual object MathArrays {
    /**
     * Compute a linear combination accurately.
     *
     *
     * This method computes a<sub>1</sub>b<sub>1</sub> +
     * a<sub>2</sub>b<sub>2</sub> to high accuracy. It does
     * so by using specific multiplication and addition algorithms to
     * preserve accuracy and reduce cancellation effects. It is based
     * on the 2005 paper [
     * Accurate Sum and Dot Product](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.2.1547) by Takeshi Ogita,
     * Siegfried M. Rump, and Shin'ichi Oishi published in SIAM J. Sci. Comput.
     *
     * @param a1 first factor of the first term
     * @param b1 second factor of the first term
     * @param a2 first factor of the second term
     * @param b2 second factor of the second term
     * @return a<sub>1</sub>b<sub>1</sub> +
     * a<sub>2</sub>b<sub>2</sub>
     * @see .linearCombination
     * @see .linearCombination
     */
    actual fun linearCombination(a1: Double, b1: Double, a2: Double, b2: Double): Double {
        // the code below is split in many additions/subtractions that may
        // appear redundant. However, they should NOT be simplified, as they
        // use IEEE754 floating point arithmetic rounding properties.
        // The variable naming conventions are that xyzHigh contains the most significant
        // bits of xyz and xyzLow contains its least significant bits. So theoretically
        // xyz is the sum xyzHigh + xyzLow, but in many cases below, this sum cannot
        // be represented in only one double precision number so we preserve two numbers
        // to hold it as long as we can, combining the high and low order bits together
        // only at the end, after cancellation may have occurred on high order bits
        // split a1 and b1 as one 26 bits number and one 27 bits number
        val a1High =
            longBitsToDouble(
                doubleToRawLongBits(
                    a1
                ) and (-1L shl 27)
            )
        val a1Low = a1 - a1High
        val b1High =
            longBitsToDouble(
                doubleToRawLongBits(
                    b1
                ) and (-1L shl 27)
            )
        val b1Low = b1 - b1High
        // accurate multiplication a1 * b1
        val prod1High = a1 * b1
        val prod1Low =
            a1Low * b1Low - (prod1High - a1High * b1High - a1Low * b1High - a1High * b1Low)
        // split a2 and b2 as one 26 bits number and one 27 bits number
        val a2High =
            longBitsToDouble(
                doubleToRawLongBits(
                    a2
                ) and (-1L shl 27)
            )
        val a2Low = a2 - a2High
        val b2High =
            longBitsToDouble(
                doubleToRawLongBits(
                    b2
                ) and (-1L shl 27)
            )
        val b2Low = b2 - b2High
        // accurate multiplication a2 * b2
        val prod2High = a2 * b2
        val prod2Low =
            a2Low * b2Low - (prod2High - a2High * b2High - a2Low * b2High - a2High * b2Low)
        // accurate addition a1 * b1 + a2 * b2
        val s12High = prod1High + prod2High
        val s12Prime = s12High - prod2High
        val s12Low = prod2High - (s12High - s12Prime) + (prod1High - s12Prime)
        // final rounding, s12 may have suffered many cancellations, we try
        // to recover some bits from the extra words we have saved up to now
        var result = s12High + (prod1Low + prod2Low + s12Low)
        if (result.isNaN()) {
            // either we have split infinite numbers or some coefficients were NaNs,
            // just rely on the naive implementation and let IEEE754 handle this
            result = a1 * b1 + a2 * b2
        }
        return result
    }

    /**
     * Compute a linear combination accurately.
     *
     *
     * This method computes a<sub>1</sub>b<sub>1</sub> +
     * a<sub>2</sub>b<sub>2</sub> + a<sub>3</sub>b<sub>3</sub>
     * to high accuracy. It does so by using specific multiplication and
     * addition algorithms to preserve accuracy and reduce cancellation effects.
     * It is based on the 2005 paper [
    * Accurate Sum and Dot Product](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.2.1547) by Takeshi Ogita,
     * Siegfried M. Rump, and Shin'ichi Oishi published in SIAM J. Sci. Comput.
     *
     * @param a1 first factor of the first term
     * @param b1 second factor of the first term
     * @param a2 first factor of the second term
     * @param b2 second factor of the second term
     * @param a3 first factor of the third term
     * @param b3 second factor of the third term
     * @return a<sub>1</sub>b<sub>1</sub> +
     * a<sub>2</sub>b<sub>2</sub> + a<sub>3</sub>b<sub>3</sub>
     * @see .linearCombination
     * @see .linearCombination
     */
    actual fun linearCombination(
        a1: Double, b1: Double,
        a2: Double, b2: Double,
        a3: Double, b3: Double): Double {
        // the code below is split in many additions/subtractions that may
        // appear redundant. However, they should NOT be simplified, as they
        // do use IEEE754 floating point arithmetic rounding properties.
        // The variables naming conventions are that xyzHigh contains the most significant
        // bits of xyz and xyzLow contains its least significant bits. So theoretically
        // xyz is the sum xyzHigh + xyzLow, but in many cases below, this sum cannot
        // be represented in only one double precision number so we preserve two numbers
        // to hold it as long as we can, combining the high and low order bits together
        // only at the end, after cancellation may have occurred on high order bits
        // split a1 and b1 as one 26 bits number and one 27 bits number
        val a1High =
            longBitsToDouble(
                doubleToRawLongBits(
                    a1
                ) and (-1L shl 27)
            )
        val a1Low = a1 - a1High
        val b1High =
            longBitsToDouble(
                doubleToRawLongBits(
                    b1
                ) and (-1L shl 27)
            )
        val b1Low = b1 - b1High
        // accurate multiplication a1 * b1
        val prod1High = a1 * b1
        val prod1Low =
            a1Low * b1Low - (prod1High - a1High * b1High - a1Low * b1High - a1High * b1Low)
        // split a2 and b2 as one 26 bits number and one 27 bits number
        val a2High =
            longBitsToDouble(
                doubleToRawLongBits(
                    a2
                ) and (-1L shl 27)
            )
        val a2Low = a2 - a2High
        val b2High =
            longBitsToDouble(
                doubleToRawLongBits(
                    b2
                ) and (-1L shl 27)
            )
        val b2Low = b2 - b2High
        // accurate multiplication a2 * b2
        val prod2High = a2 * b2
        val prod2Low =
            a2Low * b2Low - (prod2High - a2High * b2High - a2Low * b2High - a2High * b2Low)
        // split a3 and b3 as one 26 bits number and one 27 bits number
        val a3High =
            longBitsToDouble(
                doubleToRawLongBits(
                    a3
                ) and (-1L shl 27)
            )
        val a3Low = a3 - a3High
        val b3High =
            longBitsToDouble(
                doubleToRawLongBits(
                    b3
                ) and (-1L shl 27)
            )
        val b3Low = b3 - b3High
        // accurate multiplication a3 * b3
        val prod3High = a3 * b3
        val prod3Low =
            a3Low * b3Low - (prod3High - a3High * b3High - a3Low * b3High - a3High * b3Low)
        // accurate addition a1 * b1 + a2 * b2
        val s12High = prod1High + prod2High
        val s12Prime = s12High - prod2High
        val s12Low = prod2High - (s12High - s12Prime) + (prod1High - s12Prime)
        // accurate addition a1 * b1 + a2 * b2 + a3 * b3
        val s123High = s12High + prod3High
        val s123Prime = s123High - prod3High
        val s123Low = prod3High - (s123High - s123Prime) + (s12High - s123Prime)
        // final rounding, s123 may have suffered many cancellations, we try
        // to recover some bits from the extra words we have saved up to now
        var result = s123High + (prod1Low + prod2Low + prod3Low + s12Low + s123Low)
        if (result.isNaN()) {
            // either we have split infinite numbers or some coefficients were NaNs,
            // just rely on the naive implementation and let IEEE754 handle this
            result = a1 * b1 + a2 * b2 + a3 * b3
        }
        return result
    }
}
