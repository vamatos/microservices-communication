
import * as httpStatus from "../../../config/constants/httpStatus.js";
import UserException from "../exception/UserException.js";
import UserRepository from "../repository/UserRepository.js";

import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";
import * as secrets from "../../../config/constants/secrets.js";
import { logInfo, logError } from "../../../config/logger.js";

class UserService {

  async findByEmail(req) {
    try {
      const { email } = req.params;
      const { authUser } = req;
      this.validateRequestData(email);
      let user = await UserRepository.findByEmail(email);
      this.validateUserNotFound(user);
      this.validateAuthenticatedUser(user, authUser);

      return {
        status: httpStatus.OK,
        user: {
          id: user.id,
          name: user.name,
          email: user.email
        }
      };

    } catch (error) {
      console.error(error.message)
      logError({ message: error.message, transactionId: req.headers.transactionid, serviceId: req.headers.serviceid });
      return {
        status: error?.status ? error.status : httpStatus.INTERNAL_SERVER_ERROR,
        message: error.message
      };
    }
  }

  validateRequestData(email) {
    if (!email) {
      throw new UserException("User email is required", httpStatus.BAD_REQUEST);
    }
  }
  validateUserNotFound(user) {
    if (!user) {
      throw new UserException( "User was not found",httpStatus.NOT_FOUND);
    }
  }

  validateAuthenticatedUser(user, authUser) {
    if (!authUser || user.id !== authUser.id) {
      throw new UserException("You cannot see this user data.", httpStatus.BAD_REQUEST);
    }
  }



  async getAccessToken(req) {
    try {

      const { email, password } = req.body;
      const transactionid = req.headers['x-transaction-id'];
      const serviceid = req.headers['x-service-id'];
      this.validadeAccessTokenData(email, password);

      let user = await UserRepository.findByEmail(email);
      this.validateUserNotFound(user);

      await this.validadePassword(password, user.password);

      const authUser = { id: user.id, name: user.name, email: user.email };
      const accessToken = jwt.sign(authUser, secrets.API_SECRET, { expiresIn: "10d" });

      return {
        status: httpStatus.OK,
        accessToken: accessToken,
      };
    } catch (error) {
      logError({ message: error.message, transactionId: transactionid, serviceId: serviceid });
      return {
        status: error?.status ? error.status : httpStatus.INTERNAL_SERVER_ERROR,
        message: error.message
      };
    }


  }

  validadeAccessTokenData(email, password) {
    if (!email || !password) {
      throw new UserException("Email and password are required", httpStatus.BAD_REQUEST);
    }
  }

  async validadePassword(password, hashPassword) {
    if (!await bcrypt.compare(password, hashPassword)) {
      throw new UserException("Invalid password",httpStatus.BAD_REQUEST);
    }
  }



}

export default new UserService();