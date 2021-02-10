package org.obebeokeke.sudoku

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Sudoku {

    private var retrofit: Retrofit
    private var sudokuApi: SudokuApi

    init {
        retrofit = Retrofit.Builder()
            .baseUrl("https://sugoku.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        sudokuApi = retrofit.create(SudokuApi::class.java)
    }

    fun getBoard(): List<List<Int>> {
        val call = sudokuApi.fetchBoard()

        val response = call.execute()

        return response.body()!!.board
    }
}