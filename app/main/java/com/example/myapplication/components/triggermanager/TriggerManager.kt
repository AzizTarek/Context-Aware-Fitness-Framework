package com.example.myapplication.components.triggermanager

import com.example.myapplication.components.other.API_TYPE_SYSTEM

object TriggerManager {

    private var triggerList = ArrayList<ContextTrigger>()
    var defaultContext = ContextTrigger(API_TYPE_SYSTEM,"Enjoy your day",APIPriority.CALENDAR)
    fun getTrigger() : ContextTrigger {
        println("Size of Trigger List == ${triggerList.size}")
        return if (triggerList.size == 1){
            this.triggerList[0]
        } else if(triggerList.size > 1){
            val sortedTriggers = triggerList.sortedBy { it.priority }
            sortedTriggers[0]
        } else defaultContext
    }

    fun addTrigger(contextTrigger: ContextTrigger){
        triggerList.add(contextTrigger)
    }
    fun removeTrigger(contextTrigger: ContextTrigger){
        triggerList.remove(contextTrigger)
    }

    fun getTriggerListSize():Int{
        return triggerList.size
    }

    fun clearTriggers(){
        triggerList.clear()
    }
}