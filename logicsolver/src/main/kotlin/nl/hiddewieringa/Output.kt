package nl.hiddewieringa


data class SudokuOutput(val values: Map<Coordinate, Int>) {

    /**
     * Generates a grid output of the puzzle solution
     */
    override fun toString(): String {
        return (1..9).map { i ->
            (1..9).map { j ->
                values[Coordinate(i, j)] ?: '.'
            }.joinToString(" ")
        }.joinToString("\n")
    }
}
