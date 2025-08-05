import Order from "../model/Order.js";

class OrderRepository {
  async save(order) {
    try {
      return await Order.create(order);
    } catch (error) {
      console.error("Error saving order:", error.message);
      return null;
    }
  }

  async findById(orderId) {
    try {
      return await Order.findById(orderId);
    } catch (error) {
      console.error("Error finding order:", error.message);
      return null;
    }
  }

  async findAll() {
    try {
      return await Order.find();
    } catch (error) {
      console.error("Error finding orders:", error.message);
      return null;
    }
  }
  async findByProductId(productId) {
    try {
      return await Order.find({ "products.productId": Number(productId) });
    } catch (error) {
      console.error("Error finding orders by product ID:", error.message);
      return null;
    }
  }
  async findAll() {
    try {
      return await Order.find();
    } catch (error) {
      console.error("Error finding orders:", error.message);
      return null;
    }
  }
}

export default new OrderRepository();