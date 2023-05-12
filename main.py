import json
import paho.mqtt.client as mqtt
from dataclasses import dataclass
from dataclasses_json import dataclass_json

@dataclass_json
@dataclass
class AssemblyList:
    name: str
    task: list

    def __str__(self):
        return "Name: "+ self.name +  ''.join(["\n--> " + str(i) for i in self.task ])

@dataclass_json
@dataclass
class Task:
    index: int
    name: str #solder, screw, ...
    description: str
    
    def __str__(self):
        return "Index: " + str(self.index) + "\t | Name: "+ self.name + "\t | Description: "+ self.description

class Master:
    def __init__(self):
        self.assemblyLists = self.readAssembylLists()
        self.client = mqtt.Client()
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message
        self.client.connect("localhost", 1883, 60)

        #TODO dont hardcode
        self.currentAssemblyList = 0
        self.currentTask = 0
        print("Publish inital Task")
        #self.client.publish("master/current_task",self.assemblyLists[self.currentAssemblyList].task[self.currentTask].to_json(), retain= True, qos=2)
        self.client.publish("master/current_task",self.assemblyLists[self.currentAssemblyList].task[self.currentTask].to_json())
                            
        pass
    #MQTT methods
    def on_connect(self,client, userdata, flags, rc):

        print("Connected with result code " + str(rc))
        client.subscribe("test/topic")
        client.subscribe("test2/topic")

    def on_message(self,client, userdata, msg):
        if msg.topic == "test/topic":
            print(msg.topic+" "+str(msg.payload))
        elif msg.topic == "test2/topic":
            print("Test2")


    def readAssembylLists(self):
        assemblyLists = []
        with open('assembly-list.json') as f:
            data = json.load(f)

        for ipc in data:
            assembyList = AssemblyList(name= ipc['IPC'],task= [])

            for i,task in enumerate(ipc['Tasks']):
                assembyList.task.append( Task(index = i, name = task['Name'], description=task['Description']))
            
            assemblyLists.append(assembyList)

        return assemblyLists
    
    def printAssemblyLists():
        #TODO
        pass
    
if __name__ == '__main__':
    master = Master()
    master.client.loop_forever()