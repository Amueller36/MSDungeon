package com.example.thelegend27.trading.domain

enum class Upgrade {
    STORAGE,
    HEALTH,
    DAMAGE,
    MINING_SPEED,
    MINING,
    MAX_ENERGY,
    ENERGY_REGEN;

    companion object {
        val MAXLEVEL = 5
    }

    fun getValue(level: Int): Int {
        return when (this) {
            STORAGE -> {
                when (level) {
                    0 -> 20
                    1 -> 50
                    2 -> 100
                    3 -> 200
                    4 -> 400
                    5 -> 1000
                    else -> 0
                }
            }

            HEALTH -> {
                when (level) {
                    0 -> 10
                    1 -> 25
                    2 -> 50
                    3 -> 100
                    4 -> 200
                    5 -> 500
                    else -> 0
                }
            }

            DAMAGE -> {
                when (level) {
                    0 -> 1
                    1 -> 2
                    2 -> 5
                    3 -> 10
                    4 -> 20
                    5 -> 50
                    else -> 0
                }
            }

            MINING_SPEED -> {
                when (level) {
                    0 -> 2
                    1 -> 5
                    2 -> 10
                    3 -> 15
                    4 -> 20
                    5 -> 40
                    else -> 0
                }
            }

            MINING -> {
                when (level) {
                    0 -> 2
                    1 -> 3
                    2 -> 4
                    3 -> 5
                    4 -> 6
                    5 -> 7
                    else -> 0
                }
            }

            MAX_ENERGY -> {
                when (level) {
                    0 -> 20
                    1 -> 30
                    2 -> 40
                    3 -> 60
                    4 -> 100
                    5 -> 200
                    else -> 0
                }
            }

            ENERGY_REGEN -> {
                when (level) {
                    0 -> 4
                    1 -> 6
                    2 -> 8
                    3 -> 10
                    4 -> 15
                    5 -> 20
                    else -> 0
                }
            }
        }
    }

    fun getPrice(level: Int): Int {
        return when (level) {
            1 -> 50
            2 -> 300
            3 -> 1500
            4 -> 4000
            5 -> 15000
            else -> throw Exception()
        }
    }
}