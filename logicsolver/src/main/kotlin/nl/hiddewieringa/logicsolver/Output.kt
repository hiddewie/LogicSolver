package nl.hiddewieringa.logicsolver


data class SudokuOutput(val coordinates: List<Coordinate>, val values: Map<Coordinate, Int>) {

    /**
     * Generates a grid output of the puzzle solution
     */
    override fun toString(): String {
        val max = coordinates.max() ?: throw Exception("Cannot determine max coordinate from list")

        return (1..(max.a)).fold("") { acc, i ->
            acc + (1..(max.b)).fold("") { subAcc, j ->
                val coordinate = Coordinate(i, j)
                subAcc + spacingForCoordinate(coordinate) + (if (values.contains(coordinate)) characterForValue(values[coordinate]) else " ")
            } + "\n"
        }
    }

    private fun spacingForCoordinate(coordinate: Coordinate): String {
        return if (coordinate.b == 1) "" else " "
    }

    private fun characterForValue(value: Int?): String {
        return if (value != null) value.toString() else "."
    }
}
