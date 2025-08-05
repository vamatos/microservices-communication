import OrderRepository from "../repository/OrderRepository.js";
import { sendMessageToProductStockUpdateQueue } from "../../product/rabbitmq/productStockUpdateSender.js";
import { BAD_REQUEST, INTERNAL_SERVER_ERROR, OK } from "../../../config/constants/httpStatus.js";
import { REJECTED, PENDING } from "../status/OrderStatus.js";
import OrderException from "../exception/OrderException.js";
import ProductClient from "../../product/client/ProductClient.js";

import { logInfo, logError } from "../../../config/logger.js";


class OrderService {
  async createOrder(req) {
    try {
      let orderData = req.body;
      const { authUser } = req;
      const authorization = req.headers['authorization'];
      const transactionid = req.headers['x-transaction-id'];
      const serviceid = req.headers['x-service-id'];

      this.validateOrderData(orderData);

      let order = this.createInitialOrderData(transactionid, serviceid, orderData, authUser);
      await this.validateProductStock(order, authorization, transactionid);
      const createOrder = await OrderRepository.save(order);
      if (!createOrder) {
        return {
          status: INTERNAL_SERVER_ERROR,
          message: "Error creating order"
        };
      }
      logInfo({ message: `Order created successfully: ${JSON.stringify(createOrder)}`, transactionId: transactionid, serviceId: serviceid });
      this.sendMessage(transactionid, serviceid, createOrder);

      return {
        status: OK,
        createOrder,
      };
    } catch (error) {
      logError({ message: "Error creating order", error: error.message });
      return {
        status: error?.status ? error.status : INTERNAL_SERVER_ERROR,
        message: error.message
      };
    }
  }

  async updateOrder(orderMessage) {
    console.log("Updating order with message:", orderMessage);
    try {
      const order = JSON.parse(orderMessage);
      if (order.salesId && order.status) {
        let existingOrder = await OrderRepository.findById(order.salesId);
        if (existingOrder && order.status !== existingOrder.status) {
          existingOrder.status = order.status;
          existingOrder.updatedAt = new Date();
          await OrderRepository.save(existingOrder);
          logInfo({ message: "Order status updated successfully", data: existingOrder });
        }
      } else {
        logInfo({ message: "Received order message without salesId or status", data: order });
      }

    } catch (err) {
      logError({ message: `Error parsing order message: ${err.message}` });
    }
  }

  async findById(req) {
    try {
      const { id } = req.params;
      const transactionid = req.headers['x-transaction-id'];
      const serviceid = req.headers['x-service-id'];
      this.validateInformedId(id);
      const existingOrder = await OrderRepository.findById(id);
      if (!existingOrder) {
        logInfo({ message: `Order not found for ID: ${id}`, transactionId: transactionid, serviceId: serviceid });
        return {
          status: BAD_REQUEST,
          message: "Order not found"
        };
      }
      logInfo({ message: `Order found for ID: ${id}`, data: existingOrder, transactionId: transactionid, serviceId: serviceid });
      return {
        status: OK,
        existingOrder,
      };
    } catch (error) {
      logError({ message: "Error finding order by ID:", error: error.message });
      return {
        status: error?.status ? error.status : INTERNAL_SERVER_ERROR,
        message: error.message
      };
    }
  }

  async findAll(req) {
    const transactionid = req.headers['x-transaction-id'];
    const serviceid = req.headers['x-service-id'];
    try {
      const orders = await OrderRepository.findAll();
      if (!orders || orders.length === 0) {
        logInfo({ message: "No orders found", transactionId: transactionid, serviceId: serviceid });
        return {
          status: BAD_REQUEST,
          message: "No orders found"
        };
      }
      logInfo({ message: "Orders found", data: orders, transactionId: transactionid, serviceId: serviceid });
      return {
        status: OK,
        orders,
      };
    } catch (error) {
      logError({ message: `Error finding orders: ${error.message}`, transactionId: transactionid, serviceId: serviceid });
      return {
        status: error?.status ? error.status : INTERNAL_SERVER_ERROR,
        message: error.message
      };
    }
  }

  async findByProductId(req) {
    try {
      const { productId } = req.params;
      const transactionid = req.headers['x-transaction-id'];
      const serviceid = req.headers['x-service-id'];

      this.validateInformedProductId(productId);
      const orders = await OrderRepository.findByProductId(productId);
      if (!orders || orders.length === 0) {
        logInfo({ message: `No orders found for productId: ${productId}`, transactionId: transactionid, serviceId: serviceid });
        return {
          status: BAD_REQUEST,
          message: "Order not found"
        };
      }
      logInfo({ message: `Orders found for productId ${productId}`, data: orders, transactionId: transactionid, serviceId: serviceid });
      return {
        status: OK,
        salesId: orders.map(order => order.id),
      };
    } catch (error) {
      logError({ message: `Error finding order by ID: ${error.message}`, transactionId: transactionid, serviceId: serviceid });
      return {
        status: error?.status ? error.status : INTERNAL_SERVER_ERROR,
        message: error.message
      };
    }
  }


  async getAllOrders() {
    return await OrderRepository.findAll();
  }

  validateOrderData(data) {
    if (!data || !data.products) {
      throw new OrderException("The products must be informed", BAD_REQUEST);
    }

  }

  async validateProductStock(order, token, transactionid) {
    let stockIsOk = await ProductClient.checkProductStock(order?.products, token, transactionid);
    if (!stockIsOk) {
      order.status = REJECTED;
      logInfo({ message: "Stock validation failed", data: order.products, transactionId: transactionid });
      throw new OrderException("The stock is out", BAD_REQUEST);
    }
    logInfo({ message: "Stock is ok", order }, { data: order.products, transactionId: transactionid });
  }

  createInitialOrderData(transactionid, serviceid, orderData, authUser) {
    return {
      user: authUser,
      status: PENDING,
      createdAt: new Date(),
      updatedAt: new Date(),
      transactionid: transactionid,
      serviceid: serviceid,
      products: orderData?.products,
    };
  }

  sendMessage(transactionId, serviceId, createOrder) {
    const message = {
      salesId: createOrder?.id,
      products: createOrder?.products
    }
    sendMessageToProductStockUpdateQueue(transactionId, serviceId, message);
  }

  validateInformedId(id) {
    if (!id) {
      throw new OrderException("The order ID must be informed", BAD_REQUEST);
    }
  }
  validateInformedProductId(id) {
    if (!id) {
      throw new OrderException("The product ID must be informed", BAD_REQUEST);
    }
  }
}

export default new OrderService();