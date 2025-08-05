import { Router } from "express";

import orderController from "../controller/OrderController.js";

const router = Router();

router.get('/api/orders/:id', orderController.findById);
router.get('/api/orders', orderController.findAll);
router.get('/api/orders/product/:productId', orderController.findByProductId);
router.post('/api/orders/', orderController.createOrder);



export default router;