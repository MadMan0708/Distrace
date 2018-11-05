import json
import csv


def get_name(span):
    return span["name"].split(" - ")[0]

def is_slave(span):
    return span["name"]

def store_trace_data(trace):
    row = {}
    row['Whole_duration'] = trace[0]["duration"] / 1000.0
    splitting_traces = [ i for i in trace if "splitting" in i["name"]]
    splitting_traces.sort(key=lambda x: x["duration"], reverse=True)
    row["Node1_Full_Duration"] = splitting_traces[0]["duration"] / 1000.0
    row["Node2_Full_Duration"] = splitting_traces[1]["duration"] / 1000.0
    row["Node3_Full_Duration"] = splitting_traces[2]["duration"] / 1000.0
    return row

with open('trace.json') as f:
    data = json.load(f)
    with open('data.csv', 'wb') as csv_file:
        writer = csv.writer(csv_file, delimiter=",")
        writer.writerow(["trace_id", "Whole_duration", "Node1_Full_Duration", "Node2_Full_Duration", "Node3_Full_Duration"])
        for trace in data:
            row = store_trace_data(trace)
            writer.writerow([trace[0]["traceId"], row["Whole_duration"], row["Node1_Full_Duration"], row["Node2_Full_Duration"], row["Node3_Full_Duration"]])








