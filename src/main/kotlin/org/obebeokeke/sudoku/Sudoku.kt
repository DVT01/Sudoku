package org.obebeokeke.sudoku

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val EMPTY = 0

class Sudoku {

    private var retrofit: Retrofit
    private var sudokuApi: SudokuApi

    private var originalBoard: MutableList<MutableList<Int>>
    private var board: MutableList<MutableList<Int>>
    private var solvedBoard: MutableList<MutableList<Int>>

    init {
        retrofit = Retrofit.Builder()
            .baseUrl("https://sugoku.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        sudokuApi = retrofit.create(SudokuApi::class.java)

        this.originalBoard = fetchSudokuBoard()
        this.board = this.originalBoard.toMutableList()
        this.solvedBoard = this.originalBoard.toMutableList()
    }

    private fun fetchSudokuBoard(): MutableList<MutableList<Int>> {
        val call = sudokuApi.fetchBoard()
        val response = call.execute()
        return response.body()!!.board
    }

    fun getOriginalBoard(): MutableList<MutableList<Int>> = this.originalBoard

    fun getBoard(): MutableList<MutableList<Int>> = this.board

    fun getSolvedBoard(): MutableList<MutableList<Int>> {

        // If the solved board isn't solved yet then solve it
        if (!isComplete(this.solvedBoard)) {
            solveBoard()
        }

        return this.solvedBoard
    }

    private fun solveBoard(): Boolean {

        // Utilizing a recursive backtracking algorithm
        // Search for the first valid number that I can put in that cell
        for (rowIndex in 0 until this.solvedBoard.count()) {
            for (columnIndex in 0 until this.solvedBoard[rowIndex].count()) {

                // Search for an empty cell
                if (this.solvedBoard[rowIndex][columnIndex] == EMPTY) {

                    // Try all possible numbers
                    for (number in 1..9) {

                        // Make sure the number adheres to the rules of sudoku
                        if (this.cellIsValid(rowIndex, columnIndex, number)) {
                            this.solvedBoard[rowIndex][columnIndex] = number

                            if (solveBoard()) {
                                // Start backtracking recursively
                                return true
                            } else {
                                // If it isn't a solution, empty the cell and continue
                                this.solvedBoard[rowIndex][columnIndex] = EMPTY
                            }
                        }
                    }

                    return false
                }
            }
        }

        return true
    }

    private fun cellIsValid(
        row: Int,
        column: Int,
        number: Int
    ): Boolean {

        // Checks if there are repeating numbers in a column or row
        for (index in 0 until this.solvedBoard.count()) {
            if (this.solvedBoard[row][index] == number) return false
            if (this.solvedBoard[index][column] == number) return false
        }

        // Checks if there are repeating numbers in a 3x3 grid
        lateinit var columnRange: IntRange
        lateinit var rowRange: IntRange

        // Search which 3x3 grid the cell is in
        for (start in 0 until this.solvedBoard.count() step 3) {
            val end = start + 2

            if (row in start..end) {
                rowRange = start..end
            }

            if (column in start..end) {
                columnRange = start..end
            }
        }

        // Search 3x3 grid for number
        for (a in rowRange) {
            for (b in columnRange) {
                if (this.solvedBoard[a][b] == number) return false
            }
        }

        return true
    }

    fun isComplete(): Boolean = isComplete(this.board)

    private fun isComplete(board: MutableList<MutableList<Int>>): Boolean {

        // Go through every cell to see if it's empty
        for (row in board) {
            for (number in row) {
                if (number == EMPTY) return false
            }
        }

        return true
    }

    fun fillCell(rowIndex: Int, columnIndex: Int, number: Int): Boolean {

        // Make sure cell is empty and the number is from 1 to 9
        if (this.board[rowIndex][columnIndex] == EMPTY && number in 1..9) {
            this.board[rowIndex][columnIndex] = number
            return true
        }

        return false
    }
}