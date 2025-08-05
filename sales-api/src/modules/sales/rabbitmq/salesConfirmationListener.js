import amqp from 'amqplib/callback_api.js';
import { RABBIT_MQ_URL } from '../../../config/constants/secrets.js';
import { PRODUCT_TOPIC, SALES_CONFIRMATION_QUEUE, SALES_CONFIRMATION_ROUTING_KEY } from '../../../config/rabbitmq/queue.js';
import { logInfo, logError } from "../../../config/logger.js";

import OrderService from '../service/OrderService.js';

export function listenToSalesConfirmationListener() {
  amqp.connect(RABBIT_MQ_URL, (error, connection) => {
    if (error) {
      logError({ message: 'RabbitMQ connection error:', error });
      return;
    }
    logInfo({ message: 'Listening to RabbitMQ for sales confirmation...' });
    connection.createChannel((error, channel) => {
      if (error) {
        logError({ message: 'Error creating channel:', error });
        return;
      }

      channel.consume(SALES_CONFIRMATION_QUEUE, (message) => {
        if (message !== null) {
          logInfo({ message: 'Received sales confirmation', data: message.content.toString() });

          OrderService.updateOrder(message.content.toString());
        }
      }, { noAck: true });

      logInfo({ message: `Listening to queue ${SALES_CONFIRMATION_QUEUE} for sales confirmations` });
    });
  })
}