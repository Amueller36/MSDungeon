package com.example.thelegend27.robot.domain.strategies.farming

import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.commands.CommandFactory
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import com.example.thelegend27.trading.domain.Resource
import com.example.thelegend27.trading.domain.Restoration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory

class MineOrBuyRegenerateIfTooLowEnergy(private var robot: Robot, val farmStrategy: FarmStrategy) : RobotStrategy {
    private val logger = LoggerFactory.getLogger(MineOrBuyRegenerateIfTooLowEnergy::class.java)
    private val highestMinableResource = Resource.getHighestMinableResourceByLevel(robot.levels.miningLevel)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val robotFlow = RobotRepository.asFlow().map { it[robot.id] }
    override suspend fun getCommand(): Command {
        robot = robotFlow.first() ?: return CommandFactory.createRegenerateCommand(robot.id.toString())
        if (robot.currentStatus.energy < highestMinableResource.getEnergyMiningCost()) {
            if (highestMinableResource == Resource.PLATIN && robot.levels.energyLevel > 1) {
                farmStrategy.monetenMade -= Restoration.ENERGY_RESTORE.getPrice()
                CommandFactory.createBuyEnergyRestoreCommand(robot.id.toString())
            }
            CommandFactory.createRegenerateCommand(robot.id.toString())
        }
        logger.info(
            "Robot ${robot.id} is gonna mine, inventory : Max ${robot.inventory.maxStorage} Used: ${robot.inventory.usedStorage}: Current planet ${robot.currentPlanet.id}"
        )
        return CommandFactory.createMineCommand(robot.id.toString(), robot.currentPlanet.id.toString())
    }

}