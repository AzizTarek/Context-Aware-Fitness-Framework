package com.example.myapplication.components.other

/**
 * Identify user type
 */
var userType: Int = 1
fun identifyUserType(totalSteps:Int, totalProgress:Float) : Int
{
    if (totalProgress<=40 && totalSteps>= 7500) //Spark [low Motivation - high Ability]
        userType =2
    else if (totalProgress>=40 && totalSteps<=7500) //Facilitator [high Motivation - low Ability]
        userType =1
    else if (totalProgress>=75 && totalSteps>=7500) //Signal [high Motivation - high Ability]
        userType =3
    return userType
}

/**
 * Return a personalised message to encourage a user to walk more in three different levels
 */
fun userPersonalisedMessage():String{
    var message = ""
    if(userType==2)
        message = " Boost your mood and energy! Take a brisk walk now and feel the benefits!"
    else if(userType==1)
        message=" Make walking easy! Plan your route, wear comfy shoes, and set achievable goals."
    else if(userType==3)
        message=" Listen to your body! Walk when you feel the urge for natural movement."
    return message
}