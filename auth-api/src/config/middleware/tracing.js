import { v4 as uuid4 } from 'uuid';
import { BAD_REQUEST } from '../constants/httpStatus.js';

export default (req, res, next) => {
  let transactionId = req.headers['x-transaction-id'];

  if (!transactionId) {
    return res.status(BAD_REQUEST).json({ message: "The x-transaction-id header is required" });
  }
  req.headers['x-service-id'] = uuid4();
  return next();
}