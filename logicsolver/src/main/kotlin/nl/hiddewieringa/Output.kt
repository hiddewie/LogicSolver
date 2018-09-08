package nl.hiddewieringa


data class SudokuOutput(val values: Map<Coordinate, Int>) {
    override fun toString(): String {
        return (1..9).map { i ->
            (1..9).map { j ->
                values.get(Coordinate(i, j))
            }.joinToString()
        }.joinToString("\n")
    }
}
