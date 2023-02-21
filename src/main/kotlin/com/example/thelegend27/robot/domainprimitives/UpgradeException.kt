package com.example.thelegend27.robot.domainprimitives

import java.lang.Exception

class UpgradeException:Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}