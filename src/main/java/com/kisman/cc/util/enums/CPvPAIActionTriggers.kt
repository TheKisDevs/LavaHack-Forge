package com.kisman.cc.util.enums

enum class CPvPAIActionTriggers {
    TargetGotOutFromPlaceRange,
    PlacePosGotOutFromPlaceRange,
    PlacePosIsLethalForTarget,
    PlacePosIsLethalForSelf,
    TargetInHole,
    SurroundGotBreak,
    SurroundGotTryToBreak,
    SelfGotTrap,
    TargetGotTrap,
    TargetSurroundGotTryToBreak,
    TargetSurroundGotBreak,
    TargetSurroundIsBreaking,
    SurroundIsBreaking,
    PlacePosDontExist,
    HolesDontExist,
    PlacePosAndHolesDontExist,
    FinishMovingToHole,
    MovingToHole,
    TargetIsMovingToYourHole
}