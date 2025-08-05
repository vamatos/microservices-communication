import amqp from 'amqplib/callback_api.js';
import { RABBIT_MQ_URL } from '../../../config/constants/secrets.js';
import { PRODUCT_TOPIC, PRODUCT_STOCK_UPDATE_QUEUE,PRODUCT_STOCK_UPDATE_ROUTING_KEY } from '../../../config/rabbitmq/queue.js';

import { logInfo, logError } from "../../../config/logger.js";


export function sendMessageToProductStockUpdateQueue(transactionId, serviceId, message) {
  amqp.connect(RABBIT_MQ_URL, (error, connection) => {
    if (error) {
      logError({ message: `RabbitMQ connection error: ${error.message}` });
      return;
    }
    logInfo({ message: 'Connected to RabbitMQ successfully for sending product stock update' });

    connection.createChannel((error, channel) => {
      if (error) {
        logError({ message: 'Error creating channel:', error });
        return;
      }
      
      channel.assertExchange(PRODUCT_TOPIC, 'topic', { durable: true });
      channel.assertQueue(PRODUCT_STOCK_UPDATE_QUEUE, { durable: true });
      channel.bindQueue(PRODUCT_STOCK_UPDATE_QUEUE, PRODUCT_TOPIC, PRODUCT_STOCK_UPDATE_ROUTING_KEY);
      
      const jsonStringMessage = JSON.stringify(message);
      channel.publish(PRODUCT_TOPIC, PRODUCT_STOCK_UPDATE_ROUTING_KEY, Buffer.from(jsonStringMessage));

      logInfo({ message: 'Product stock update sent:', data: message, transactionId: transactionId, serviceId: serviceId });
    });
  });
}