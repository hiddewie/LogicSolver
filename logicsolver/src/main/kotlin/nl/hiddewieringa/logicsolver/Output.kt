package nl.hiddewieringa.logicsolver


data class SudokuOutput(val coordinates: List<Coordinate>, val values: Map<Coordinate, Int>) {

    /**
     * Generates a grid output of the puzzle solution
     */
    override fun toString(): String {
        return coordinates.fold("") { acc, coordinate ->
            acc + spacingForCoordinate(coordinate) + characterForValue(values[coordinate])
        }
    }

    private fun spacingForCoordinate(coordinate: Coordinate): String {
        return if (coordinate.a == 1 && coordinate.b == 1) {
            ""
        } else {
            if (coordinate.b == 1) "\n" else " "
        }
    }

    private fun characterForValue(value: Int?): String {
        return if (value != null) value.toString() else "."
    }
}
