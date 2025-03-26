
import * as httpStatus from "../../../config/constants/httpStatus.js";
import UserException from "../exception/UserException.js";
import UserRepository from "../repository/userRepository.js";

import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";
import * as secrets from "../../../config/constants/secrets.js";

class UserService {

  async findByEmail(req) {
    try {
      const { email } = req.params;
      const { authUser } = req;
      this.validateRequestData(email);
      let user = await UserRepository.findByEmail(email);
      this.validateUserNotFound(user);
      this.validateAuthenticatedUser(user,authUser);

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
      return {
        status: error?.status ? error.status : httpStatus.INTERNAL_SERVER_ERROR,
        message: error.message
      };
    }
  }

  validateRequestData(email) {
    if (!email) {
      throw new UserException(httpStatus.BAD_REQUEST, "User email is required");
    }
  }
  validateUserNotFound(user) {
    if (!user) {
      throw new UserException(httpStatus.NOT_FOUND, "User was not found");
    }
  }

  validateAuthenticatedUser(user, authUser){
    if(!authUser || user.id !== authUser.id){
      throw new UserException(httpStatus.UNAUTHORIZED, "You cannot see this user data.");
    }
  }



  async getAccessToken(req) {
    try {
      const { email, password } = req.body;
      this.validadeAccessTokenData(email, password);
      let user = await UserRepository.findByEmail(email);
      this.validateUserNotFound(user);
      await this.validadePassword(password, user.password);
      const authUser = { id: user.id, email: user.email };
      const accessToken = jwt.sign(authUser, secrets.API_SECRET, { expiresIn: "1d" });

      return {
        status: httpStatus.OK,
        accessToken: accessToken,
      };
    } catch (error) {
      console.error(error.message)
      return {
        status: error?.status ? error.status : httpStatus.INTERNAL_SERVER_ERROR,
        message: error.message
      };
    }


  }

  validadeAccessTokenData(email, password) {
    if (!email || !password) {
      throw new UserException(httpStatus.UNAUTHORIZED, "Email and password are required");
    }
  }

  async validadePassword(password, hashPassword) {
    if (!await bcrypt.compare(password, hashPassword)) {
      throw new UserException(httpStatus.UNAUTHORIZED, "Invalid password");
    }
  }



}

export default new UserService();