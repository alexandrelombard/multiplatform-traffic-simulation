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
        return a1 * b1 + a2 * b2
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
        return a1 * b1 + a2 * b2 + a3 * b3
    }
}
