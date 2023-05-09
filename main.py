import json

with open('assembly-list.json') as f:
    data = json.load(f)

for ipc in data:
    print("IPC: " + ipc['IPC'])
    for i, task in enumerate(ipc['Tasks']):
        print("Task " + str(i) + ": " + task['Name'] + " - " + task['Description'])
    print("--------------------------------------")
