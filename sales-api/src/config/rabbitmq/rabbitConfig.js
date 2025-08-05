import amqp from 'amqplib/callback_api.js';
import { RABBIT_MQ_URL } from '../constants/secrets.js';
import { PRODUCT_STOCK_UPDATE_QUEUE, PRODUCT_STOCK_UPDATE_ROUTING_KEY, PRODUCT_TOPIC, SALES_CONFIRMATION_QUEUE, SALES_CONFIRMATION_ROUTING_KEY } from './queue.js';
import { listenToSalesConfirmationListener } from '../../modules/sales/rabbitmq/salesConfirmationListener.js';
import { logInfo, logError } from "../../../src/config/logger.js";

const TWO_SECOND = 2000;


export async function connectRabbitMQ() {
  connectRabbitMqAndCreateQueues();
}


async function connectRabbitMqAndCreateQueues() {
  amqp.connect(RABBIT_MQ_URL,{timeout: 180000}, (error, connection) => {
    if (error) {
      logError({ message: 'RabbitMQ connection error', error });
      return;
    }
    logInfo({ message: 'Connected to RabbitMQ successfully' });
    createQueue(connection, PRODUCT_STOCK_UPDATE_QUEUE, PRODUCT_STOCK_UPDATE_ROUTING_KEY, PRODUCT_TOPIC);
    createQueue(connection, SALES_CONFIRMATION_QUEUE, SALES_CONFIRMATION_ROUTING_KEY, PRODUCT_TOPIC);
    logInfo({ message: 'Queues created successfully' });
    setTimeout(() => {
      connection.close()
    }, TWO_SECOND);
  });
  // Chame o listener apenas após as filas serem criadas
  setTimeout(() => {
    listenToSalesConfirmationListener();
  }, TWO_SECOND);
}

async function createQueue(connection, queue, routingKey, topic) {
  connection.createChannel((error, channel) => {
    if (error) {
      logError({ message: `Error creating channel for ${queue}`, error });
      return;
    }
    channel.assertExchange(topic, 'topic', { durable: true });
    channel.assertQueue(queue, { durable: true });
    channel.bindQueue(queue, topic, routingKey);
    logInfo({ message: `Queue ${queue} created and bound to exchange ${topic} with routing key ${routingKey}` });
  });
}
