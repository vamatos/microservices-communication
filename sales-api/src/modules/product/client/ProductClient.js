import axios from 'axios';

import { PRODUCT_API_URL } from '../../../config/constants/secrets.js';
import { logInfo, logError } from "../../../config/logger.js";
class ProductClient {

  async checkProductStock(products, token, transactionid) {
    try {
      const headers = {
        Authorization: token,
        'x-transaction-id': transactionid
      };
      let response = false;
      logInfo({ message: 'Checking product stock', data: products, transactionId: transactionid });
      await axios.post(`${PRODUCT_API_URL}/check-stock`, {
        products
      }, {
        headers
      })
      .then((res) => {
        logInfo({ message: `Success response from Product API`, transactionId: transactionid });
        response = true;
      })
      .catch((error) => {
        logError({ message: `Error checking product stock: ${error.message}` }, { transactionId: transactionid });
        response = false;
      });
      return response;
    } catch (error) {
      return false;
    }
  }
}

export default new ProductClient();