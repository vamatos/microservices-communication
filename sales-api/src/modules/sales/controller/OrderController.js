import OrderService from "../service/OrderService.js";

class OrderController {
   async createOrder(req, res) {
    let order = await OrderService.createOrder(req);
    return res.status(order.status).json(order);
  }

  async findById(req, res) {
    let order = await OrderService.findById(req);
    return res.status(order.status).json(order);
  }

  async findAll(req, res) {
    let orders = await OrderService.findAll(req);
    return res.status(200).json(orders);
  }

  async findByProductId(req, res) {
    let orders = await OrderService.findByProductId(req);
    return res.status(200).json(orders);
  }

}

export default new OrderController();