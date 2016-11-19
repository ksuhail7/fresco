#!/usr/bin/env python3

import pika

from pika import spec


def confirm_handler(frame, args):
    if type(frame.method) == spec.Confirm.SelectOk:
        print("channel in 'confirm' mode")
    elif type(frame.method) == spec.Basic.Nack:
        if frame.method.delivery_tag in msg_ids:
            print("message lost")
    elif type(frame.method) == spec.Basic.Ack:
        if frame.method.delivery_tag in msg_ids:
            print("confirm received")
            msg_ids.remove(frame.method.delivery_tag)


msg = "hello"
msg_props = pika.BasicProperties()
msg_props.content_type = "text/plain"
msg_ids = []
connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()
channel.confirm_delivery(confirm_handler)
channel.queue_declare(queue='hello')
channel.basic_publish(body=msg, exchange='', properties=msg_props, routing_key='hello')
msg_ids.append(len(msg_ids) + 1)
channel.close()
