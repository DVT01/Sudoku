package org.obebeokeke.sudoku

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Sudoku {

    private var retrofit: Retrofit
    private var sudokuApi: SudokuApi

    private var board: MutableList<MutableList<Int>>
    private var solvedBoard: MutableList<MutableList<Int>>

    init {
        retrofit = Retrofit.Builder()
            .baseUrl("https://sugoku.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        sudokuApi = retrofit.create(SudokuApi::class.java)

        this.board = fetchSudokuBoard()
        this.solvedBoard = this.board
    }

    private fun fetchSudokuBoard(): MutableList<MutableList<Int>> {
        val call = sudokuApi.fetchBoard()
        val response = call.execute()
        return response.body()!!.board
    }

    fun getBoard(): MutableList<MutableList<Int>> = this.board

    fun getSolvedBoard(): MutableList<MutableList<Int>> {
        solveBoard()
        return this.solvedBoard
    }

    private fun solveBoard(): Boolean {

        // Utilizing a recursive backtracking algorithm
        // Search for the first valid number that I can put in that cell
        for (rowIndex in 0 until this.solvedBoard.count()) {
            for (columnIndex in 0 until this.solvedBoard[rowIndex].count()) {

                // Search for an empty cell
                if (this.solvedBoard[rowIndex][columnIndex] == 0) {

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
                                this.solvedBoard[rowIndex][columnIndex] = 0
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
        for (index in 0 until this.board.count()) {
            if (this.board[row][index] == number) return false
            if (this.board[index][column] == number) return false
        }

        // Checks if there are repeating numbers in a 3x3 grid
        lateinit var columnRange: IntRange
        lateinit var rowRange: IntRange

        // Search which 3x3 grid the cell is in
        for (start in 0 until this.board.count() step 3) {
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
                if (this.board[a][b] == number) return false
            }
        }

        return true
    }

    fun isComplete(): Boolean {

        // Go through every cell to see if it's empty
        for (row in this.solvedBoard) {
            for (number in row) {
                if (number == 0) return false
            }
        }

        return true
    }
}