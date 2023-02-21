package com.example.thelegend27.trading.domain



enum class Resource {
    COAL,
    IRON,
    GEM,
    GOLD,
    PLATIN;

    companion object {
        fun fromString(string: String): Resource {
            return when (string.uppercase()) {
                "COAL" -> COAL
                "IRON" -> IRON
                "GEM" -> GEM
                "GOLD" -> GOLD
                "PLATIN" -> PLATIN
                else -> throw IllegalArgumentException("Unknown resource: $string")
            }
        }

        fun getHighestMinableResourceByLevel(miningLevel: Int) : Resource {
            return when (miningLevel) {
                0 -> COAL
                1 -> IRON
                2 -> GEM
                3 -> GOLD
                4 -> PLATIN
                else -> throw IllegalArgumentException("Mining level cannot be greater than 5")
            }
        }
    }
    fun getEnergyMiningCost() : Int {
        return when (this) {
            COAL -> 1
            IRON -> 2
            GEM -> 3
            GOLD -> 4
            PLATIN -> 5
        }
    }

    override fun toString(): String {
        return super.toString().lowercase()
    }

    fun getRequiredLevel(): Int {
        return when (this) {
            COAL -> 0
            IRON -> 1
            GEM -> 2
            GOLD -> 3
            PLATIN -> 4
        }
    }

    fun value(): Int {
        return when (this) {
            COAL -> 5
            IRON -> 15
            GEM -> 30
            GOLD -> 50
            PLATIN -> 60
        }
    }




}