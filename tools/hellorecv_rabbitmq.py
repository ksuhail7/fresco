#!/usr/bin/env python3

import pika
import pyckson

def callback(ch, method, properties, body):
    print("[x] Received %r" % body)


connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()
channel.exchange_declare(exchange="fresco", type="fanout")
result = channel.queue_declare(exclusive=True)
queue_name = result.method.queue
channel.queue_bind(exchange="fresco", queue=queue_name)
channel.basic_consume(callback, queue=queue_name, no_ack=True)
print(' [*] Waiting for messages. To exit press CTRL+C')
channel.start_consuming()

