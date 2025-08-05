import UserService from "../service/UserService.js";
import { logInfo, logError } from "../../../config/logger.js";

class UserController {

  async getAccessToken(req, res) {
    let accessToken = await UserService.getAccessToken(req);
    return res.status(accessToken.status).json(accessToken);
  }


  async findByEmail(req, res) {
    try {
      const transactionid = req.headers['x-transaction-id'];
      const serviceid = req.headers['x-service-id'];

      let user = await UserService.findByEmail(req);
      if (user.status !== 200) {
        logError({ message: "User not found", transactionId: transactionid, serviceId: serviceid });
        return res.status(user.status).json({ message: user.message });
      }
      logInfo({ message: "User found", transactionId: transactionid, serviceId: serviceid });
      return res.status(user.status).json(user);
    } catch (error) {
      logError({ message: error.message, transactionId: transactionid, serviceId: serviceid });
      return res.status(500).json({ message: "Internal Server Error" });
    }
  }


}


export default new UserController();